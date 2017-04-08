package de.markusziller.alns.utils;

import de.markusziller.alns.entities.Instance;
import de.markusziller.alns.entities.Route;
import de.markusziller.alns.entities.Solution;
import de.markusziller.alns.entities.Therapist;
import de.markusziller.alns.entities.jobs.*;
import de.markusziller.alns.instancegenerator.metrics.IMetric;
import org.apache.logging.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

public class XMLUtil {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(XMLUtil.class);
    private static final String SYSTEM_CONFIG_PATH = "config" + File.separator + "system-config.xml";
    private static final String PROGRAM_CONFIG_PATH = "config" + File.separator + "program-config.xml";
    private static final String INSTANCE_LOG_PATH = "logs" + File.separator + "instancelog.xml";
    private static final String COMPEXP_LOG_PATH = "logs" + File.separator + "comp-experiment.xml";


    public static void logSolomon(Instance i) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(COMPEXP_LOG_PATH);

        Element entry = generateSolomonInstanceEntry(i);

        doc.getRootElement().add(entry);

        writeDocument(doc, COMPEXP_LOG_PATH);

    }

    public static void backupComputationExperimentResults(String appendix) throws IOException {

        String name = COMPEXP_LOG_PATH.replace(".xml", "-" + appendix + ".xml");

        while ((new File(name)).exists()) {
            name = name.replace(".xml", "-" + appendix + ".xml");
        }
        Files.copy(new File(COMPEXP_LOG_PATH).toPath(), new File(name).toPath());
    }

    public static void logSolomon(Solution s) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(COMPEXP_LOG_PATH);

        String uuid = s.getInstance().getUid();
        XPath xpathSelector = DocumentHelper.createXPath("document/instance[@uuid='" + uuid + "']");

        Element entry = (Element) xpathSelector.selectNodes(doc).get(0);

        Element proc = entry.element("solutions");

        Element solution = DocumentHelper.createElement("solution");
        solution.addAttribute("timestamp", timestamp());
        solution.addAttribute("uuid", "" + s.getUid());
        solution.addAttribute("costs", "" + s.getCostFitness());

        Element results = generateResultElement(s);
        Element config = generateConfigElement(s);
        Element routes = generateRoutesElement(s);

        addAll(solution, results, config, routes);

        proc.add(solution);

        writeDocument(doc, COMPEXP_LOG_PATH);

    }

    private static Element generateRoutesElement(Solution is) {
        Element routes = DocumentHelper.createElement("routes");

        TreeSet<Therapist> tset = new TreeSet<>(Comparators.THERAPIST_ID_ASCENDING);
        tset.addAll(is.getInstance().getTherapists());

        for (Therapist t : tset) {
            if (is.isActive(t)) {
                Element th = DocumentHelper.createElement("vehicle");
                th.addAttribute("id", "" + t.getId());

                for (Route r : is.getRoutes().get(t)) {
                    for (de.markusziller.alns.entities.Node n : r.getN()) {
                        if (n.getJob() instanceof BreakJob || n.getJob() instanceof TreatmentJob) {
                            Element room = DocumentHelper.createElement("room");
                            room.setText("" + n.getRoom().getId());

                            if (n.getJob() instanceof WardJob) {
                                room.addAttribute("i_r", "" + ((WardJob) n.getJob()).getRoom().getId());
                            }
                            if (n.getJob() instanceof OutpatientJob) {
                                room.addAttribute("i_r", "r_0");
                            }
                            if (n.getJob() instanceof ICUJob) {
                                room.addAttribute("i_r", "" + n.getRoom().getId());
                            }
                            room.addAttribute("start", "" + n.getStart());
                            room.addAttribute("end", "" + n.getEnd());
                            th.add(room);
                        }

                    }
                }

                routes.add(th);
            }
        }

        return routes;
    }

    private static void writeDocument(Document doc, String destination) throws IOException {

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(destination), format);
        writer.write(doc);
        writer.close();
    }

    private static Element generateResultElement(Solution is) {
        Element results = DocumentHelper.createElement("results");

        // Element s_uuid = DocumentHelper.createElement("uuid");
        // s_uuid.setText("" + is.getUid());
        Element costs = DocumentHelper.createElement("costs");
        costs.setText("" + is.getCostFitness());

        Element activeVehicles = DocumentHelper.createElement("usedVehicles");
        activeVehicles.setText("" + is.activeVehicles());

        Element time = DocumentHelper.createElement("time");
        time.setText("" + OutputUtil.formatMillis(is.getT_S_compl()));
        Element uns_job = DocumentHelper.createElement("unscheduledJobs");
        uns_job.setText("" + is.getUnscheduledJobs().size());
        addAll(results, costs, activeVehicles, time, uns_job);
        return results;
    }

    private static Element generateConfigElement(Solution is) {
        Element config = DocumentHelper.createElement("config");

        return config;
    }


    private static Element generateSolomonInstanceEntry(Instance i) {

        Element settings = DocumentHelper.createElement("settings");

        Element name = DocumentHelper.createElement("name");
        Element gamma = DocumentHelper.createElement("gamma");
        Element rho = DocumentHelper.createElement("rho");

        name.setText(i.getSolomonInstance().getName());
        gamma.setText("" + i.getSolomonInstance().getGamma());
        rho.setText("" + i.getSolomonInstance().getRho());

        addAll(settings, name, gamma, rho);

        Element thp = DocumentHelper.createElement("therapists");
        Element rm = DocumentHelper.createElement("rooms");

        Element ward = DocumentHelper.createElement("ward");
        ward.setText("" + i.getWards().size());
        Element tc = DocumentHelper.createElement("tc");
        tc.setText("" + i.getTcs().size());
        Element icu = DocumentHelper.createElement("icu");
        icu.setText("" + i.getIcus().size());

        addAll(rm, ward, tc, icu);

        Element jb = DocumentHelper.createElement("jobs");

        Element wardj = DocumentHelper.createElement("ward");
        wardj.setText("" + i.getWardjobs().size());
        Element outj = DocumentHelper.createElement("out");
        outj.setText("" + i.getOutjobs().size());
        Element icuj = DocumentHelper.createElement("icu");
        icuj.setText("" + i.getIcujobs().size());

        addAll(jb, wardj, outj, icuj);

        Element qlf = DocumentHelper.createElement("qualifications");

        thp.setText(i.getTherapists().size() + "");
        rm.addAttribute("total", i.getRooms().size() + "");
        jb.addAttribute("total", i.getJobs().size() + "");
        qlf.setText(i.getQualifications().size() + "");

        Element dim = DocumentHelper.createElement("dimensions");

        addAll(dim, thp, rm, jb, qlf);
        // dim.add(thp);
        // dim.add(rm);
        // dim.add(jb);
        // dim.add(qlf);

        Element instance = DocumentHelper.createElement("instance");
        Element proc = DocumentHelper.createElement("solutions");

        addAll(instance, settings, dim, proc);
        // instance.add(dim);
        // instance.add(proc);
        instance.addAttribute("uuid", "" + i.getUid());
        instance.addAttribute("created", timestamp(i));
        return instance;
    }

    private static String getSystemConfigXMLEntry(String entry) throws DocumentException {

        SAXReader reader = new SAXReader();
        Document document = reader.read(SYSTEM_CONFIG_PATH);

        Element root = document.getRootElement().element(entry);
        return root.getStringValue();
    }

    public static String getProgramConfigXMLEntry(String entry) throws DocumentException {

        SAXReader reader = new SAXReader();
        Document document = reader.read(PROGRAM_CONFIG_PATH);

        Element root = document.getRootElement().element(entry);
        return root.getStringValue();
    }

    public static Integer getMaxNumberOfCPUCores() {
        try {
            return Math.min(Integer.valueOf(getSystemConfigXMLEntry("maxCPUCores")), Runtime.getRuntime().availableProcessors());
        } catch (NumberFormatException | DocumentException e) {
            log.error("Couldn't get max number of CPU Cores. Returning 1. Error: " + e.getMessage());
            return 1;
        }
    }

    public static boolean persistInstances() {
        try {
            return Integer.parseInt(getProgramConfigXMLEntry("persist")) > 0;
        } catch (NumberFormatException | DocumentException e) {
            return false;
        }
    }

    public static void addToHistory(Instance i) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(INSTANCE_LOG_PATH);

        Element entry = generateInstanceEntry(i);

        doc.getRootElement().add(entry);

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(INSTANCE_LOG_PATH), format);
        writer.write(doc);
        writer.close();

    }

    public static void addToHistory(Solution is) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(INSTANCE_LOG_PATH);

        String uuid = is.getInstance().getUid();
        XPath xpathSelector = DocumentHelper.createXPath("document/instance[@uuid='" + uuid + "']");
        Element entry = (Element) xpathSelector.selectNodes(doc).get(0);

        Element proc = entry.element("processings");

        Element processing = DocumentHelper.createElement("processing");

        Element s_uuid = DocumentHelper.createElement("uuid");
        s_uuid.setText("" + is.getUid());
        Element costs = DocumentHelper.createElement("costs");
        costs.setText("" + is.getCostFitness());
        Element time = DocumentHelper.createElement("time");
        time.setText("" + is.getT());
        Element uns_job = DocumentHelper.createElement("unscheduledJobs");
        uns_job.setText("" + is.getUnscheduledJobs().size());

        processing.addAttribute("timestamp", timestamp());
        processing.addAttribute("timelimit", "" + is.getConfig().getTimelimit());

        addAll(processing, costs, time, uns_job, s_uuid);

        // run.add(costs, time);
        proc.add(processing);

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(INSTANCE_LOG_PATH), format);
        writer.write(doc);
        writer.close();

    }

    private static String timestamp() {
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String ts = ft.format(now);
        return ts;
    }

    private static String timestamp(Instance i) {
        Date time = new Date(i.getTimestamp());
        SimpleDateFormat ft = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String ts = ft.format(time);
        return ts;
    }

    private static void addAll(Element parent, Element... children) {
        for (Element element : children) {
            parent.add(element);
        }
    }

    public static void addInfeasibilityEntry(Integer id) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(INSTANCE_LOG_PATH);

        XPath xpathSelector = DocumentHelper.createXPath("document/instance[@id='" + id + "']");
        Element entry = (Element) xpathSelector.selectNodes(doc).get(0);
        entry.addAttribute("infeasible", "true");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(INSTANCE_LOG_PATH), format);
        writer.write(doc);
        writer.close();

    }

    public static void addInfeasibilityEntry(String uid) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(INSTANCE_LOG_PATH);

        XPath xpathSelector = DocumentHelper.createXPath("document/instance[@uuid='" + uid + "']");
        Element entry = (Element) xpathSelector.selectNodes(doc).get(0);
        entry.addAttribute("infeasible", "true");

        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        XMLWriter writer = new XMLWriter(new FileOutputStream(INSTANCE_LOG_PATH), format);
        writer.write(doc);
        writer.close();

    }

    public static Element getInstanceEntry(Integer id) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(INSTANCE_LOG_PATH);

        XPath xpathSelector = DocumentHelper.createXPath("document/instance[@id='" + id + "']");
        Element e = (Element) xpathSelector.selectNodes(doc).get(0);

        return e;
    }

    private static Element generateInstanceEntry(Instance i) {
        Element thp = DocumentHelper.createElement("therapists");
        Element rm = DocumentHelper.createElement("rooms");
        Element jb = DocumentHelper.createElement("jobs");
        Element qlf = DocumentHelper.createElement("qualifications");

        thp.setText(i.getTherapists().size() + "");
        rm.setText(i.getRooms().size() + "");
        jb.setText(i.getJobs().size() + "");
        qlf.setText(i.getQualifications().size() + "");

        Element dim = DocumentHelper.createElement("dimensions");
        Element metrics = DocumentHelper.createElement("metrics");

        for (IMetric im : IMetric.metrics) {

            try {
                Element el = DocumentHelper.createElement(im.getAbbreviation());
                el.setText(im.compute(i) + "");
                metrics.add(el);
            } catch (Exception e) {
                Element el = DocumentHelper.createElement(im.getAbbreviation());
                el.setText("N.A.");
                metrics.add(el);
            }
        }

        addAll(dim, thp, rm, jb, qlf);
        // dim.add(thp);
        // dim.add(rm);
        // dim.add(jb);
        // dim.add(qlf);

        Element instance = DocumentHelper.createElement("instance");
        Element proc = DocumentHelper.createElement("processings");

        addAll(instance, dim, metrics, proc);
        // instance.add(dim);
        // instance.add(proc);
        instance.addAttribute("id", "" + i.getDBPrimaryKey());
        instance.addAttribute("uuid", "" + i.getUid());
        instance.addAttribute("timestamp", timestamp());
        return instance;
    }

    public static void logSolomon(Solution[] ims) throws DocumentException, IOException {
        for (Solution solution : ims) {
            logSolomon(solution);
        }
    }
}
