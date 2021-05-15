package main.semantics_L3;

import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
	@SuppressWarnings("static-access")
	public static void main(String args[])
			throws ParserConfigurationException, SAXException, IOException, InputFileException, TransformerException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		String filesParam = null;
		String[] filesList = null;
		boolean fileExists;
		boolean doDiff = false;
		boolean doSemantics = false;

		if (args.length >= 5 && args[0].equals("-L")) {
			if (args[1].equals("diff")) {
				doDiff = true;
				filesParam = args[2] + " " + args[3] + " " + args[4];
			} else if (args[1].equals("semantics")) {
				if(args.length == 6) {
					doSemantics = true;
					filesParam = args[2] + " " + args[3] + " " + args[4];
				}
				else {
					System.err.println("Not enough arguments, for -L semantics, please specify both the <delta.xml> and <semantics.xml> files");
					System.exit(1);
				}

			} else if(args.length == 6) {
				doDiff = true;
				doSemantics = true;
				filesParam = args[2] + " " + args[3] + " " + args[4] + " " + args[5];
			}

			filesList = new String[1];
			filesList[0] = filesParam;

			for (String files : filesList) {
				String[] fileSplit = files.split("\\s+");
				File f = new File(fileSplit[0]);
				String path = f.getParent() + "/";

				if(doDiff) {
					main.diff_L1_L2.ui.Main jnDiff = new main.diff_L1_L2.ui.Main();

					System.out.println("*********** Start: " + path + " ***********");

					System.out.println("jats-diff START");
					String[] paramsJnDiff = { "-d", fileSplit[0], fileSplit[1], fileSplit[2] };
					jnDiff.main(paramsJnDiff);
					System.out.println("jats-diff DONE: " + fileSplit[2]);
				}

				if(doSemantics) {
					System.out.println("change-semantics START");
					// Build Document
					Document document = builder.parse(new File(fileSplit[2]));

					// Normalize the XML Structure;
					document.getDocumentElement().normalize();
					// Build Document
					Document documentO = builder.parse(new File(fileSplit[0]));
					// Normalize the XML Structure;
					documentO.getDocumentElement().normalize();

					Document documentM = builder.parse(new File(fileSplit[1]));
					// Normalize the XML Structure;
					documentM.getDocumentElement().normalize();

					// Element root = document.getDocumentElement();
					AttributeParse att = new AttributeParse();
					File origXML = new File(fileSplit[0]);
					File newXML = new File(fileSplit[1]);
					BrowseDelta bD = new BrowseDelta(document, path + "tmp_" + origXML.getName(), path + "tmp_" + newXML.getName());

					ArrayList<NodeChanged> modif = null;

					boolean jaccard = true;
					boolean simtext = false;
					boolean simtextW = true;
					modif = att.toolChange(bD, jaccard, simtext, simtextW);
					modif.sort(Comparator.comparing(NodeChanged::getNodenumberA));
					File semantics = new File(fileSplit[3]);
					semantics.delete();

					System.out.println("change-semantics DONE: " + fileSplit[3]);

					for (NodeChanged nc : modif) {
						String indent = "";
						if (nc.getDepth() != null) {
							for (int i = 0; i < Integer.parseInt(nc.getDepth()); i++) {
								indent += "  ";
							}
						}

						PrintStream o = new PrintStream(new FileOutputStream(fileSplit[3], true));
						PrintStream console = System.out;

						PrintStream printStreamList[] = new PrintStream[1];
						//PrintStream printStreamList[] = new PrintStream[2];
						printStreamList[0] = o;
						//printStreamList[1] = console;

						for (int i = 0; i < printStreamList.length; i++) {

							System.setOut(printStreamList[i]);
							System.out.println("");

							System.out.println(indent + nc.getNodenumberA() + " - " + nc.getNodetype());
							if (nc.hasInit()) {
								System.out.println(indent + "* " + nc.getInit());
								System.out.println(indent + "* " + nc.getModified());
								System.out.println(indent + "* " + nc.getFinall());
							}
							if (nc.hasid()) {
								System.out.println(indent + "* " + nc.getId());
							}
							if (nc.hasTitle()) {
								System.out.println(indent + "* " + nc.getTitle());
							}
							if (nc.hasDepth()) {
								System.out.println(indent + "* depth: " + nc.getDepth());
							}
							if (nc.hasChangelist()) {
								for (ChangeObject cOb : nc.getChangelist()) {
									System.out.println(indent + "* " + cOb.getChangement());
									if (cOb.hasOp()) {
										System.out.println(indent + "* " + cOb.getOp());

									}
									if (cOb.hasTableChange()) {
										for (TableChange tc : cOb.getTablechange()) {
											System.out.println("        * " + tc.toString());
										}
									}
								}
							}
							if (nc.hasJaccard()) {
								System.out.println(indent + "* " + nc.getJaccard());
							}
							if (nc.hasSimilartext()) {
								System.out.println(indent + "* " + nc.getSimilartext());
							}
							if (nc.hasSimilartextW()) {
								System.out.println(indent + "* " + nc.getSimitextword());
							}
						}
					}
				}
			}

		}
		else {
			System.err.println("Not enough arguments, please specify -L <diff / semantics / all> <delta.xml> and/or <semantics.xml>");
			System.exit(1);
		}
	}
}