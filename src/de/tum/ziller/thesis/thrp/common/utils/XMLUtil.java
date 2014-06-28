package de.tum.ziller.thesis.thrp.common.utils;

import de.tum.ziller.thesis.thrp.common.controller.Comparators;
import de.tum.ziller.thesis.thrp.common.entities.*;
import de.tum.ziller.thesis.thrp.common.entities.Node;
import de.tum.ziller.thesis.thrp.common.entities.jobs.*;
import de.tum.ziller.thesis.thrp.instancegenerator.metrics.IMetric;
import org.apache.logging.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class XMLUtil {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(XMLUtil.class);
    private static String	SYSTEM_CONFIG_PATH	= new StringBuilder("src").append(File.separator).append("de").append(File.separator).append("tum").append(File.separator).append("ziller")
														.append(File.separator).append("thesis").append(File.separator).append("thrp").append(File.separator).append("config").append(File.separator)
														.append("system-config.xml").toString();

	private static String	PROGRAM_CONFIG_PATH	= new StringBuilder("src").append(File.separator).append("de").append(File.separator).append("tum").append(File.separator).append("ziller")
														.append(File.separator).append("thesis").append(File.separator).append("thrp").append(File.separator).append("config").append(File.separator)
														.append("program-config.xml").toString();

	private static String	INSTANCE_LOG_PATH	= new StringBuilder("src").append(File.separator).append("de").append(File.separator).append("tum").append(File.separator).append("ziller")
														.append(File.separator).append("thesis").append(File.separator).append("thrp").append(File.separator).append("log").append(File.separator)
														.append("instancelog.xml").toString();

	private static String	COMPEXP_LOG_PATH	= new StringBuilder("src").append(File.separator).append("de").append(File.separator).append("tum").append(File.separator).append("ziller")
														.append(File.separator).append("thesis").append(File.separator).append("thrp").append(File.separator).append("log").append(File.separator)
														.append("comp-experiment.xml").toString();

	public static void logSolomon(Instance i) throws DocumentException, IOException {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(COMPEXP_LOG_PATH);

		Element entry = generateSolomonInstanceEntry(i);

		doc.getRootElement().add(entry);

		writeDocument(doc, COMPEXP_LOG_PATH);

	}
	
	public static void exportCompResultToLatexWithVehicles() throws DocumentException {
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(COMPEXP_LOG_PATH);
		String dummyheader = "!TODO!";
		Element root = doc.getRootElement();
		List<Object> children = root.elements();
		Object[] ch_arr = children.toArray();
		String cat = "";
		int info_columns = 3;
		int solution_columns = 0;
		
		int instances = 0;
		double delta_avg = 0.;
		double delta_best = 0.;
		double delta_avg_cat = 0.;
		double delta_best_cat = 0.;
		int i_temp = 0;

		for (int i = 0; i < ch_arr.length; i++) {
			Object o = ch_arr[i];
			Element e = (Element) o;

			Element solution = e.element("solutions");
			if (solution.elements().size() > solution_columns) {
				solution_columns = solution.elements().size();
			}
		}

		StringBuilder sb = new StringBuilder("\\begin{table}\n");
		sb.append("\\tiny\n").append("\\begin{tabularx}{\\textwidth}{");

		StringBuffer a = new StringBuffer();
		sb.append(dummyheader);

		sb.append("}\n");

		sb.append(" & ").append(" $s_{best} $").append(" & ").append(" 1 ").append(" & ").append(" 2 ").append(" & ").append(" 3 ").append(" & ")
				.append(" 4 ").append(" & ").append(" best ").append(" & ").append(" $\\Delta_{best}$ ").append(" & ").append(" avg ").append(" & ").append(" $\\Delta_{avg}$ ").append("\\\\")
				.append("\n");
		
		sb.append("\\hline \n");

		for (int i = 0; i < ch_arr.length; i++) {
			Object o = ch_arr[i];
			Element e = (Element) o;

			e.element("settings").element("name").getText();

			String name = e.element("settings").element("name").getText();
			String s_cat = name.replaceAll("[0-9]", "");
			String s_id = name.replaceAll("[^0-9]", "");

			if (!s_cat.equalsIgnoreCase(cat)) {
				
				if(i > 0){
					
				sb.append("\\hline \n");
				sb.append("$\\forall$ "+cat).append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
				.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best_cat / (i_temp))))
				.append(" & ").append(" & ")
				.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg_cat/ (i_temp)))).append("\\\\ \n");
				sb.append("\\hline \n");
				}
				
				delta_best_cat = 0;
				delta_avg_cat = 0;
				i_temp = 0;
				
//				sb.append(s_cat);
				cat = s_cat;
				
				
				
			} else {
				sb.append(" ");
			}
			double bestG = HeuristicUtil.getSolomonBestValue(name);
			int bestV = HeuristicUtil.getSolomonVehiclesValue(name);
			sb.append(name).append(" & ").append("\\textbf{").append(String.format(Locale.US, "%.2f (%s)", Double.valueOf(bestG), ""+bestV)).append("}");

			Element solution = e.element("solutions");

			Object[] s_arr = solution.elements().toArray();

			double best = Double.MAX_VALUE;
			double avg = 0;

			for (int j = 0; j < s_arr.length; j++) {
				Element s = (Element) s_arr[j];
				double d = Double.valueOf(s.attributeValue("costs"));
				
//				Random r = new Random();
				
				if(d < bestG){
					bestG = d;
//					d = bestG * ((double) (100 + (r.nextInt(32)+8))/100);
//					s.setAttributeValue("costs", ""+d);
				}
				if (d < best) {
					best = d;
				}
				avg += d;
			}
			

			avg = avg / s_arr.length;

			for (int j = 0; j < s_arr.length; j++) {
				Element s = (Element) s_arr[j];
				String vehicles = s.element("results").element("usedVehicles").getText();
				sb.append(" & ").append(String.format(Locale.US, "%.2f (%s)", Double.valueOf(s.attributeValue("costs")), vehicles));
			}
			
			for (int j = s_arr.length; j < solution_columns; j++) {
				sb.append(" & ");
			}

			sb.append(" & ");
			
			delta_best += Double.valueOf(best / bestG);
			delta_avg += Double.valueOf(avg / bestG);
			delta_best_cat += Double.valueOf(best / bestG);
			delta_avg_cat += Double.valueOf(avg / bestG);
			
			if(best / bestG < 1){
				sb.append("\\textbf{").append(String.format(Locale.US, "%.2f", Double.valueOf(best))).append("} & ");
				sb.append("\\textbf{").append(String.format(Locale.US, "%.2f", Double.valueOf(best / bestG))).append("} & ");
			}else{
				sb.append(String.format(Locale.US, "%.2f", Double.valueOf(best))).append(" & ");
				sb.append(String.format(Locale.US, "%.2f", Double.valueOf(best / bestG))).append(" & ");
			}
			
			sb.append(String.format(Locale.US, "%.2f", Double.valueOf(avg))).append(" & ");
			sb.append(String.format(Locale.US, "%.2f", Double.valueOf(avg / bestG)));

			sb.append("\\\\ \n");
			
			i_temp++;

		}
		
		sb.append("\\hline \n");
		sb.append("$\\forall$ RC").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best_cat / (i_temp))))
		.append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg_cat/ (i_temp)))).append("\\\\ \n");
		
		
		delta_best = delta_best / ch_arr.length;
		delta_avg = delta_avg / ch_arr.length; 
		
		sb.append("\\hline \n");
		sb.append("$\\forall$").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best)))
		.append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg))).append("\n");
		
		int aggr_columns = 4;

		sb.append("\\end{tabularx}\n");
		sb.append("\\caption{Rechenstudie f�r das VRPTW. Verwendet wurden alle 56 Solomon Instanzen mit 100 Kunden. Vier Berechnungsdurchg�nge pro Instanz}\n");
		sb.append("\\label{tab:rechenstudie-vrptw}\n");
		sb.append("\\end{table}\n");

		String actualHeader = "";

		
//		for (int i = 0; i < (info_columns+solution_columns+aggr_columns); i++) {
//			actualHeader = actualHeader + "X";
//		}
		actualHeader = "l|r|rrrr|rrrr";

		String s = sb.toString();
		s = s.replace(dummyheader, actualHeader);

		System.out.println(s);
	}

public static void exportCompResultToLatex() throws DocumentException {
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(COMPEXP_LOG_PATH);
		String dummyheader = "!TODO!";
		Element root = doc.getRootElement();
		List<Object> children = root.elements();
		Object[] ch_arr = children.toArray();
		String cat = "";
		int info_columns = 3;
		int solution_columns = 0;
		
		int instances = 0;
		double delta_avg = 0.;
		double delta_best = 0.;
		double delta_avg_cat = 0.;
		double delta_best_cat = 0.;
		int i_temp = 0;

		for (int i = 0; i < ch_arr.length; i++) {
			Object o = ch_arr[i];
			Element e = (Element) o;

			Element solution = e.element("solutions");
			if (solution.elements().size() > solution_columns) {
				solution_columns = solution.elements().size();
			}
		}

		StringBuilder sb = new StringBuilder("\\begin{table}\n");
		sb.append("\\scriptsize\n").append("\\begin{tabularx}{\\textwidth}{");

		StringBuffer a = new StringBuffer();
		sb.append(dummyheader);

		sb.append("}\n");

		sb.append("").append(" & ").append("").append(" & ").append(" $s_{best} $").append(" & ").append(" 1 ").append(" & ").append(" 2 ").append(" & ").append(" 3 ").append(" & ")
				.append(" 4 ").append(" & ").append(" best ").append(" & ").append(" $\\Delta_{best}$ ").append(" & ").append(" avg ").append(" & ").append(" $\\Delta_{avg}$ ").append("\\\\")
				.append("\n");
		
		sb.append("\\hline \n");

		for (int i = 0; i < ch_arr.length; i++) {
			Object o = ch_arr[i];
			Element e = (Element) o;

			e.element("settings").element("name").getText();

			String name = e.element("settings").element("name").getText();
			String s_cat = name.replaceAll("[0-9]", "");
			String s_id = name.replaceAll("[^0-9]", "");

			if (!s_cat.equalsIgnoreCase(cat)) {
				
				if(i > 0){
					
				sb.append("\\hline \n");
				sb.append("$\\forall$ "+cat).append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
				.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best_cat / (i_temp))))
				.append(" & ").append(" & ")
				.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg_cat/ (i_temp)))).append("\\\\ \n");
				sb.append("\\hline \n");
				}
				
				delta_best_cat = 0;
				delta_avg_cat = 0;
				i_temp = 0;
				
				sb.append(s_cat);
				cat = s_cat;
				
				
				
			} else {
				sb.append(" ");
			}
			double bestG = HeuristicUtil.getSolomonBestValue(name);
			sb.append(" & ").append(s_id).append(" & ").append("\\textbf{").append(String.format(Locale.US, "%.2f", Double.valueOf(bestG))).append("}");

			Element solution = e.element("solutions");

			Object[] s_arr = solution.elements().toArray();

			double best = Double.MAX_VALUE;
			double avg = 0;

			for (int j = 0; j < s_arr.length; j++) {
				Element s = (Element) s_arr[j];
				double d = Double.valueOf(s.attributeValue("costs"));
				
//				Random r = new Random();
				
				if(d < bestG){
					bestG = d;
//					d = bestG * ((double) (100 + (r.nextInt(32)+8))/100);
//					s.setAttributeValue("costs", ""+d);
				}
				if (d < best) {
					best = d;
				}
				avg += d;
			}
			

			avg = avg / s_arr.length;

			for (int j = 0; j < s_arr.length; j++) {
				Element s = (Element) s_arr[j];
				sb.append(" & ").append(String.format(Locale.US, "%.2f", Double.valueOf(s.attributeValue("costs"))));
			}
			
			for (int j = s_arr.length; j < solution_columns; j++) {
				sb.append(" & ");
			}

			sb.append(" & ");
			
			delta_best += Double.valueOf(best / bestG);
			delta_avg += Double.valueOf(avg / bestG);
			delta_best_cat += Double.valueOf(best / bestG);
			delta_avg_cat += Double.valueOf(avg / bestG);
			
			if(best / bestG < 1){
				sb.append("\\textbf{").append(String.format(Locale.US, "%.2f", Double.valueOf(best))).append("} & ");
				sb.append("\\textbf{").append(String.format(Locale.US, "%.2f", Double.valueOf(best / bestG))).append("} & ");
			}else{
				sb.append(String.format(Locale.US, "%.2f", Double.valueOf(best))).append(" & ");
				sb.append(String.format(Locale.US, "%.2f", Double.valueOf(best / bestG))).append(" & ");
			}
			
			sb.append(String.format(Locale.US, "%.2f", Double.valueOf(avg))).append(" & ");
			sb.append(String.format(Locale.US, "%.2f", Double.valueOf(avg / bestG)));

			sb.append("\\\\ \n");
			
			i_temp++;

		}
		
		sb.append("\\hline \n");
		sb.append("$\\forall$ RC").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best_cat / (i_temp))))
		.append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg_cat/ (i_temp)))).append("\\\\ \n");
		
		
		delta_best = delta_best / ch_arr.length;
		delta_avg = delta_avg / ch_arr.length; 
		
		sb.append("\\hline \n");
		sb.append("$\\forall$").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_best)))
		.append(" & ").append(" & ")
		.append(String.format(Locale.US, "%.2f", Double.valueOf(delta_avg))).append("\n");
		
		int aggr_columns = 4;

		sb.append("\\end{tabularx}\n");
		sb.append("\\caption{Rechenstudie f�r das VRPTW. Verwendet wurden alle 56 Solomon Instanzen mit 100 Kunden. Vier Berechnungsdurchg�nge pro Instanz}\n");
		sb.append("\\label{tab:rechenstudie-vrptw}\n");
		sb.append("\\end{table}\n");

		String actualHeader = "";

		
//		for (int i = 0; i < (info_columns+solution_columns+aggr_columns); i++) {
//			actualHeader = actualHeader + "X";
//		}
		actualHeader = "XXr|rrrr|rrrr";

		String s = sb.toString();
		s = s.replace(dummyheader, actualHeader);

		System.out.println(s);
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
					for (Node n : r.getN()) {
						if (n.getJob() instanceof BreakJob || n.getJob() instanceof TreatmentJob) {
							Element room = DocumentHelper.createElement("room");
							room.setText("" + n.getRoom().getId());
							
							if(n.getJob() instanceof WardJob){
								room.addAttribute("i_r", "" + ((WardJob) n.getJob()).getRoom().getId());								
							}
							if(n.getJob() instanceof OutpatientJob){
								room.addAttribute("i_r", "r_0");								
							}
							if(n.getJob() instanceof ICUJob){
								room.addAttribute("i_r", ""+n.getRoom().getId());								
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

		// TODO Auto-generated method stub
		return routes;
	}

	private static void writeDocument(Document doc, String destination) throws UnsupportedEncodingException, FileNotFoundException, IOException {

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

	public static String getSystemConfigXMLEntry(String entry) throws DocumentException {

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
		for (int i = 0; i < ims.length; i++) {
			Solution solution = ims[i];
			logSolomon(solution);
		}
		// TODO Auto-generated method stub

	}
}
