package main.diff_L1_L2.debug;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.vdom.reconstruction.Rtree;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Mike Crea una struttura di directory contenente le informazioni per fare Debug raccolte durante il calcolo del diff
 *
 * debug degug/xslt/xtree.xsl # xslt per visualizzare gli alberi per i debug debug/diffing/'phase'/dtreeA.xml : dtreeB.xml : sf.html: r.html
 * debug/diffing/debug.html debug/diffing/index.html
 *
 *
 */
public class Debug {

	static public boolean flag = false;

	static String debugPath = "src/main/diff_L1_L2/debug";
	static private PrintWriter indexDiffing = null; // file dei collegamenti per
	// diffing

	static private PrintWriter indexRecostruction = null; // file dei
	// collegamenti per
	// recostruction
	static private int dumpcount = 0;

	/**
	 * Chiude la fase di Debug, salvando i relativi file
	 */
	static public void close() {
		if (flag) {
			indexDiffing.println("</ul></body></html>");
			indexDiffing.close();
			indexRecostruction.println("</ul></body></html>");
			indexRecostruction.close();
		}
	}

	/**
	 * Crea il file debug.html per visualizzare il framset del diffing
	 */
	static public void diffFrameset() {

		if (flag) {
			try {
				FileWriter f = new FileWriter(debugPath + "/diffing/debug.html");
				PrintWriter out = new PrintWriter(f);
				out.println("<FRAMESET COLS=\"10%,45%,45%\">"
					+ "<FRAME SRC=\"index.html\"/>"
					+ "<FRAME SRC=\"normalize/dtreeA.xml\" name=\"visualA\" />"
					+ "<FRAME SRC=\"normalize/dtreeB.xml\" name=\"visualB\"/>"
					+ "</FRAMESET>");
				out.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findtextchangestyle(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findtextchangestyle", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findtextchangestyle/dtreeB.xml','visualB');window.open('findtextchangestyle/dtreeA.xml','visualA');return false\">Findtextchangestyle</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findtextchangestyle/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findtextchangestyle/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findmerge(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findmerge", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findmerge/dtreeB.xml','visualB');window.open('findmerge/dtreeA.xml','visualA');return false\">FindMerge</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findmerge/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findmerge/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	static public void diffing_findupgrade(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findupgrade", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findupgrade/dtreeB.xml','visualB');window.open('findupgrade/dtreeA.xml','visualA');return false\">FindUpgrade</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findupgrade/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findupgrade/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}

	}

	static public void diffing_finddowngrade(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("finddowngrade", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('finddowngrade/dtreeB.xml','visualB');window.open('finddowngrade/dtreeA.xml','visualA');return false\">FindDowngrade</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"finddowngrade/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"finddowngrade/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}

	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findsplit(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findsplit", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findsplit/dtreeB.xml','visualB');window.open('findsplit/dtreeA.xml','visualA');return false\">FindSplit</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findsplit/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findsplit/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findtextmove(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findtextmove", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findtextmove/dtreeB.xml','visualB');window.open('findtextmove/dtreeA.xml','visualA');return false\">FindMoveText</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findtextmove/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findtextmove/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findstyle(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("diffing_findstyle", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('diffing_findstyle/dtreeB.xml','visualB');window.open('diffing_findstyle/dtreeA.xml','visualA');return false\">FindStyle</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"diffing_findstyle/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"diffing_findstyle/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di findMove, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findmove(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findmove", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findmove/dtreeB.xml','visualB');window.open('findmove/dtreeA.xml','visualA');return false\">FindMove</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findmove/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findmove/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di findUpdate, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_findupdate(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("findupdate", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('findupdate/dtreeB.xml','visualB');window.open('findupdate/dtreeA.xml','visualA');return false\">FindUpdate</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"findupdate/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"findupdate/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le strutture dopo la fase di normalizzazione, e linka nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_normalize(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("normalize", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('normalize/dtreeB.xml','visualB');window.open('normalize/dtreeA.xml','visualA');return false\">Normalize</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"normalize/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"normalize/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di Partizionamento, linka nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_partition(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("partition", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('partition/dtreeB.xml','visualB');window.open('partition/dtreeA.xml','visualA');return false\">Partition</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"partition/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"partition/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa le struture dopo la fase di propagation, link nell'indice i relativi file
	 *
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void diffing_propagation(Dtree A, Dtree B, Relation R, NxN SF) {
		if (flag) {
			dump_diff_status("propagation", A, B, R, SF);
			indexDiffing
				.println("<li> <a href=\"#\" onclick=\"window.open('propagation/dtreeB.xml','visualB');window.open('propagation/dtreeA.xml','visualA');return false\">Propagation</a></li>"
					+ "<ul>"
					+ "<li> <a href=\"propagation/r.html\"  target=\"relation\">Relation</a> </li>"
					+ "<li> <a href=\"propagation/sf.html\" target=\"nxn\">NxN</a> </li>"
					+ "</ul>");
		}
	}

	/**
	 * Stampa lo stato delle varie strutture dati in formato html nella directory specificata
	 *
	 * @param path
	 * @param A
	 * @param B
	 * @param R
	 * @param SF
	 */
	static public void dump_diff_status(String path, Dtree A, Dtree B,
		Relation R, NxN SF) {
		try {
			File dir = new File(debugPath + "/diffing/" + path);
			if (!dir.exists()) {
				dir.mkdir();
			}
		} catch (Exception e) {
		}

		// Salvo stato delle diverse strutture
		(new HtmlPrintDtree()).print(A, debugPath + "/diffing/" + path
			+ "/dtreeA.xml");
		(new HtmlPrintDtree()).print(B, debugPath + "/diffing/" + path
			+ "/dtreeB.xml");
		(new HtmlPrintNxN()).print(SF, debugPath + "/diffing/" + path
			+ "/sf.html", true, true, false);
		(new HtmlPrintRelation()).print(R, debugPath + "/diffing/" + path
			+ "/r.html");
	}

	static public void recostruction_add_step(Rtree rtree, String optype,
		String nn) {
		if (flag) {
			new HtmlPrintRtree().print(rtree, debugPath
				+ "/recostruction/dump/rtree" + dumpcount + ".xml");

			indexRecostruction
				.println("<li> <a href=\"#\" onclick=\"window.open('dump/rtree"
					+ (dumpcount - 1)
					+ ".xml#"
					+ nn
					+ "','visualA');window.open('dump/rtree"
					+ dumpcount
					+ ".xml#"
					+ nn
					+ "','visualB');return false\">"
					+ dumpcount
					+ ")"
					+ optype + "(" + nn + ")</a></li>");
			dumpcount++;
		}
	}

	/**
	 * Inizializza le struture per cominciare a fare Debug
	 */
	static public void start() {
		if (flag) {
			try {

				// Crea directory per il diffing
				File dir = new File(debugPath + "/diffing");
				if (!dir.exists()) {
					dir.mkdir();
				}

				// Crea frameset
				diffFrameset();

				// Crea indice per diffing
				FileWriter f = new FileWriter(debugPath + "/diffing/index.html");
				indexDiffing = new PrintWriter(f);

				// Crea directory per recostruction
				dir = new File(debugPath + "/recostruction");
				if (!dir.exists()) {
					dir.mkdir();
				}

				// Crea directory per mantenere le diverse versioni
				dir = new File(debugPath + "/recostruction/dump");
				if (!dir.exists()) {
					dir.mkdir();
				}

				// Crea indice per recostruction
				FileWriter f2 = new FileWriter(debugPath
					+ "/recostruction/index.html");
				indexRecostruction = new PrintWriter(f2);
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			indexDiffing
				.println("<html><head><title> Index Debug </title></head><body><ul>");
			indexRecostruction
				.println("<html><head><title> Index Debug </title></head><body><ul>");
		}
	}

}
