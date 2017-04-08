package de.markusziller.alns.utils;

import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.entities.Solution;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.jboss.serial.io.JBossObjectInputStream;
import org.jboss.serial.io.JBossObjectOutputStream;

import java.io.*;
import java.sql.*;
import java.util.Iterator;

public class PersistenceUtil {

    private Connection con = null;

    // private PersistenceUtil db;

    public PersistenceUtil() {

    }

    public Long persistToDatabase(Solution is, Instance i) throws Exception {
        connectToDB();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO thrp.solutions (solution, computed, instance) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setLong(2, System.currentTimeMillis());
            ps.setLong(3, i.getDBPrimaryKey());

            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                // ObjectOutputStream oout = new ObjectOutputStream(bout);
                JBossObjectOutputStream objOut = new JBossObjectOutputStream(bout);
                objOut.writeObject(is);
                objOut.close();
                byte[] asBytes = bout.toByteArray();

                ps.setObject(1, asBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                Long key = rs.getLong(1);
                return key;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDB();
        }
        throw new Exception();

    }

    public Long persistToDatabase(Instance i) throws SQLException {

        connectToDB();

        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO thrp.instances (instance, created, uuid) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);

            ps.setLong(2, System.currentTimeMillis());
            ps.setString(3, i.getUid());

            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                // ObjectOutputStream oout = new ObjectOutputStream(bout);
                JBossObjectOutputStream objOut = new JBossObjectOutputStream(bout);
                objOut.writeObject(i);
                objOut.close();
                byte[] asBytes = bout.toByteArray();

                ps.setObject(1, asBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs != null && rs.next()) {
                Long key = rs.getLong(1);
                return key;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDB();
        }

        throw new SQLException();
    }

    public Instance getInstanceFromDatabase(String uuid) {

        connectToDB();

        Instance i = null;
        try {
            Statement stmt;
            stmt = con.createStatement();

            String query = "SELECT instance FROM `thrp`.`instances` where uuid = '" + uuid + "';";

            ResultSet res = stmt.executeQuery(query);

            res.next();

            byte[] buf = res.getBytes(1);
            if (buf != null) {
                ObjectInputStream objectIn = new JBossObjectInputStream(new ByteArrayInputStream(buf));

                // new ObjectInputStream(new ByteArrayInputStream(buf));

                // jois = new JBossObjectInputStream(objectIn);
                i = (Instance) objectIn.readObject();
                objectIn.close();
                // i.setDBPrimaryKey(id);
                return i;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDB();
        }
        return i;
    }

    public Instance getInstanceFromDatabase(Long id) {

        connectToDB();

        Instance i = null;
        try {
            Statement stmt;
            stmt = con.createStatement();

            String query = "SELECT instance FROM `thrp`.`instances` where id = '" + id + "';";

            ResultSet res = stmt.executeQuery(query);

            res.next();

            byte[] buf = res.getBytes(1);
            if (buf != null) {
                ObjectInputStream objectIn = new JBossObjectInputStream(new ByteArrayInputStream(buf));

                // new ObjectInputStream(new ByteArrayInputStream(buf));

                // jois = new JBossObjectInputStream(objectIn);
                i = (Instance) objectIn.readObject();
                objectIn.close();
                i.setDBPrimaryKey(id);
                return i;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDB();
        }
        return i;
    }

    // untested
    public Solution getSolutionFromDatabase(Long id) {

        connectToDB();
        Solution i = null;
        try {
            Statement stmt;
            stmt = con.createStatement();

            String query = "SELECT solution FROM `thrp`.`solutions` where id = '" + id + "';";

            ResultSet res = stmt.executeQuery(query);

            res.next();

            byte[] buf = res.getBytes(1);
            if (buf != null) {
                ObjectInputStream objectIn = new JBossObjectInputStream(new ByteArrayInputStream(buf));

                // new ObjectInputStream(new ByteArrayInputStream(buf));

                // jois = new JBossObjectInputStream(objectIn);
                i = (Solution) objectIn.readObject();
                objectIn.close();
//				i.setDBPrimaryKey(id);
                return i;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDB();
        }
        return i;
    }

    public void persistToFilesystem(Instance i) throws IOException {
        persistToFilesystem(i, "instances", i.getUid() + "_" + System.currentTimeMillis());
    }

    public void persistToFilesystem(Solution s) throws IOException {
        persistToFilesystem(s, "solutions", s.getUid() + "_" + System.currentTimeMillis());
    }

    public Instance getInstanceFromFilesystem(Long id) throws IOException, ClassNotFoundException {

        Instance i = (Instance) getEntityFromFilesystem("instances", "" + id);
        i.setDBPrimaryKey(id);
        return i;
    }

    public Solution getSolutionFromFilesystem(Long id) throws IOException, ClassNotFoundException {

        Solution s = (Solution) getEntityFromFilesystem("solutions", "" + id);
        return s;
    }

    public Instance getInstanceFromFilesystem(String uuid) throws IOException, ClassNotFoundException {

        Instance i = (Instance) getEntityFromFilesystem("instances", uuid);
        return i;
    }

    public Solution getSolutionFromFilesystem(String uuid) throws IOException, ClassNotFoundException {

        Solution s = (Solution) getEntityFromFilesystem("solutions", "" + uuid);
        return s;
    }




    private void connectToDB() {
        try {
            if (con == null || con.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection("jdbc:mysql://localhost/thrp", XMLUtil.getProgramConfigXMLEntry("sqluser"), XMLUtil.getProgramConfigXMLEntry("sqlpw"));
            }
        } catch (SQLException | ClassNotFoundException | DocumentException e) {
            e.printStackTrace();
        }
    }

    private void closeConnectionToDB() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Object getEntityFromFilesystem(String subpath, String id) throws IOException, ClassNotFoundException {

        Iterator<File> it = FileUtils.iterateFiles(new File("./output/" + subpath + "/"), new String[]{"thrp"}, false);

        File f = null;

        while (it.hasNext()) {
            File next = it.next();
            if (next.getName().startsWith(id + "_")) {
                f = next;
                break;
            }
        }

        // String p = new
        // StringBuilder().append("./files/").append(subpath).append("/").append(filename
        // + ".thrp").toString();

        FileInputStream fin = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fin);
        JBossObjectInputStream jois = new JBossObjectInputStream(ois);
        Object o = jois.readObject();

        ois.close();
        jois.close();

        return o;

    }

    private void persistToFilesystem(Object i, String subpath, String filename) throws IOException {
        String p = "./output/" + subpath + "/" + filename + ".thrp";

        OutputStream file = new FileOutputStream(p);
        OutputStream buffer = new BufferedOutputStream(file);
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        JBossObjectOutputStream objOut = new JBossObjectOutputStream(output);

        objOut.writeObject(i);
        objOut.close();

    }

    public void persistToFilesystem(Solution[] ims) throws IOException {
        for (Solution solution : ims) {
            persistToFilesystem(solution);
        }

    }

}
