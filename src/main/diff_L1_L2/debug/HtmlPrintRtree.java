package main.diff_L1_L2.debug;

import main.diff_L1_L2.vdom.reconstruction.Rnode;
import main.diff_L1_L2.vdom.reconstruction.Rtree;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Mike Crea un file XML con le informazioni per il debug Tipi di nodo:
 *         0 : nodo normale 1 : nodo insert di markup 2 : nodo delete di markup
 *         3 : nodo editing
 */
public class HtmlPrintRtree {

	private Rtree rtree;
	private int contaVisita;
	private PrintWriter out;
	Logger logger = Logger.getLogger(getClass());

	public boolean isMarkupHowEditing(Node node) {

		if ((node.getNodeType() == Node.ELEMENT_NODE)
				&& (node.getPrefix() != null)) {

			if (node.getPrefix().equals(Rtree.NDIFF_PREFIX)
					&& node.getNodeName().equals(
							Rtree.NDIFF_PREFIX + ":editing"))
				return true;
		}

		return false;
	}

	public boolean isMarkupHowInsert(Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element tmp = (Element) node;

			// main.diff_L1_L2.test sui nodi wrapper segnati come mossi
			if (tmp.getPrefix() != null) {
				if ((tmp.getPrefix().equals(Rtree.NDIFF_PREFIX))
						&& (tmp.getAttribute("status").equals("inserted")))
					return true;

				if ((tmp.getPrefix().equals(Rtree.NDIFF_PREFIX))
						&& (tmp.getAttribute("status").equals("moved"))
						&& tmp.hasAttribute("id"))
					return true;
			} else {
				if ((tmp.getAttribute(Rtree.NDIFF_PREFIX + ":status")
						.equals("inserted")))
					return true;

				if ((tmp.getAttribute(Rtree.NDIFF_PREFIX + ":status")
						.equals("moved"))
						&& (tmp.hasAttribute(Rtree.NDIFF_PREFIX + ":id")))
					return true;
			}

		}
		return false;
	}

	public void print(Rtree rtree, String file) {
		//System.out.println(" Dump Rtree on " + file);

		try {
			FileWriter f = new FileWriter(file);
			out = new PrintWriter(f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		this.rtree = rtree;
		out.println("<?xml-stylesheet type=\"text/xsl\" href=\"../../xslt/vtree.xsl\" encoding=\"UTF-8\"?>");
		out.println("<tree>");

		contaVisita = 0;
		visitaNodo(rtree.root, false, false, false);

		out.println("</tree>");

		// chiusura del file
		out.close();
	}

	/**
	 * Discesa ricorsiva dell'albero XML
	 * 
	 * @param node
	 *            nodo attualmente visitato
	 * @param deleted
	 *            true se è stato trovato il markup di delete
	 * @param inserted
	 *            true se è stato trovato il markup di insert
	 * @param isEditing
	 *            true se il nodo è in editing
	 */
	private void visitaNodo(Node node, boolean deleted, boolean inserted,
			boolean isEditing) {

		if (node != null) {

			String nodeName = node.getNodeName();
			// Aggiungi attributi al nome del nodo
			NamedNodeMap att = node.getAttributes();
			if (att != null)
				for (int i = 0; i < att.getLength(); i++) {
					nodeName += " " + att.item(i).getNodeName() + "=&quot;"
							+ att.item(i).getNodeValue() + "&quot;";
				}

			// Caso in cui il nodo NON sia "markato"
			if (!rtree.isMarkup(node) && !deleted && !inserted && !isEditing) {

				Rnode tmp = rtree.getNode(contaVisita);

				out.println("<subtree id=\"" + System.nanoTime() + "\" num=\""
						+ contaVisita + "\" name=\"" + nodeName + "\" type=\""
						+ 0 + "\">");
				out.println("<attrib>");
				out.println("<att name=\"indexKey:\" value=\""
						+ tmp.getIndexKey() + "\" />");
				out.println("<att name=\"posFather:\" value=\""
						+ tmp.getPosFather() + "\" />");
				out.println("<att name=\"posLikeChild:\" value=\""
						+ tmp.getPosLikeChild() + "\" />");
				out.println("<att name=\"isNew:\" value=\"" + tmp.isNew
						+ "\" />");
				out.println("<att name=\"isEditing:\" value=\"" + tmp.isEditing
						+ "\" />");
				out.println("</attrib>");

				contaVisita++;
			}
			// Caso in cui il nodo sia markato o un suo predecessore sia markato
			else {

				// Se è il nodo di markup, lo scrivo e aggiorno le variabili per
				// la discesa
				if (rtree.isMarkup(node)) {
					int type = 0;
					String num = "-";

					if (rtree.isMarkupHowDelete(node)) {
						deleted = true;
						type = 2;
					} else if (isMarkupHowInsert(node)) {
						inserted = true;
						type = 1;
					} else if (isMarkupHowEditing(node)) {
						isEditing = true;
						type = 3;
						num = String.valueOf(contaVisita);
						contaVisita++;
					}
					out.println("<subtree id=\"" + System.nanoTime()
							+ "\" num=\"" + num + "\" name=\"" + nodeName
							+ "\" type=\"" + type + "\">");
				} else {// Caso in cui un antenato sia markato
					if (deleted)
						out.println("<subtree id=\"" + System.nanoTime()
								+ "\" num=\"-\" name=\"" + nodeName
								+ "\" type=\"" + 2 + "\">");
					else if (inserted && !isEditing) {
						out.println("<subtree id=\"" + System.nanoTime()
								+ "\" num=\"" + contaVisita + "\" name=\""
								+ nodeName + "\" type=\"" + 1 + "\">");
						contaVisita++;
					} else if (!deleted && !inserted && isEditing) {
						out.println("<subtree id=\"" + System.nanoTime()
								+ "\" num=\"" + "-" + "\" name=\"" + nodeName
								+ "\" type=\"" + 3 + "\">");
					} else
						out.println("<subtree id=\"" + System.nanoTime()
								+ "\" num=\"" + "-" + "\" name=\"" + nodeName
								+ "\" type=\"" + 1 + "\">");
				}
			}

			if (node.getNodeType() != Node.ELEMENT_NODE) {
				out.println("<content>    " + node.getTextContent()
						+ "    </content>");
			}

			// Chiamata ricorsiva
			if (node.hasChildNodes())
				for (int i = 0; i < node.getChildNodes().getLength(); i++)
					visitaNodo(node.getChildNodes().item(i), deleted, inserted,
							isEditing);

			out.println("</subtree>");

		}
	}
}
