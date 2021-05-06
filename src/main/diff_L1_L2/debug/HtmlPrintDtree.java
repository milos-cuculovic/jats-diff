package main.diff_L1_L2.debug;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class HtmlPrintDtree {
	private main.diff_L1_L2.vdom.diffing.Dtree Dtree;
	private int contaVisita;
	private PrintWriter out;

	Logger logger = Logger.getLogger(getClass());

	public void print(Dtree Dtree, String file) {
		//System.out.println("Debug: printVtree su " + file);

		try {
			FileWriter f = new FileWriter(file);
			out = new PrintWriter(f);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		this.Dtree = Dtree;
		out.println("<?xml-stylesheet type=\"text/xsl\" href=\"../../xslt/vtree.xsl\" encoding=\"UTF-8\"?>");
		out.println("<tree>");

		contaVisita = 0;
		visitaNodo(Dtree.root);

		/*
		 * Inserisci un nodo finale fittizzio che contiene le classi e il loro
		 * contenuto
		 */
		/*
		 * out.println("<subtree caption=\"Classi\" icon=\"punto.gif\">");
		 * out.println("<subtree caption=\""+Dtree.classes.toString()+
		 * "\" icon=\"punto.gif\" />"); out.println("</subtree>");
		 */

		out.println("</tree>");

		// chiusura del file
		out.close();
	}

	private void visitaNodo(Node node) {
		if (node != null) {

			Dnode tmp = Dtree.getNode(contaVisita);

			String type = String.valueOf(tmp.inRel + 10);
			String nodeName = node.getNodeName();

			// Aggiungi attributi al nome del nodo
			NamedNodeMap att = tmp.refDomNode.getAttributes();
			if (att != null)
				for (int i = 0; i < att.getLength(); i++) {
					nodeName += " " + att.item(i).getNodeName() + "=&quot;"
							+ att.item(i).getNodeValue() + "&quot;";
				}

			// Intestazione nodo
			out.println("<subtree id=\"" + System.nanoTime() + "\" num=\""
					+ contaVisita + "\" name=\"" + nodeName + "\" type=\""
					+ type + "\">");

			// Attributi nodo
			out.println("<attrib>");
			out.println("<att name=\"indexKey:\" value=\"" + tmp.getIndexKey()
					+ "\" />");
			out.println("<att name=\"HashNode:\" value=\"" + tmp.getHashNode()
					+ "\" />");
			out.println("<att name=\"HashTree:\" value=\"" + tmp.getHashTree()
					+ "\" />");
			out.println("<att name=\"posFather:\" value=\""
					+ tmp.getPosFather() + "\" />");
			out.println("<att name=\"posLikeChild:\" value=\""
					+ tmp.getPosLikeChild() + "\" />");
			out.println("<att name=\"numChildSubtree:\" value=\""
					+ tmp.getNumChildSubtree() + "\" />");
			out.println("<att name=\"weight:\" value=\"" + tmp.getWeight()
					+ "\" />");
			out.println("<att name=\"hashFragment:\" value=\""
					+ tmp.hashFragment + "\" />");
			out.println("<att name=\"inRel:\" value=\"" + tmp.inRel + "\" />");
			out.println("<att name=\"insOnMe:\" value=\"" + tmp.insOnMe
					+ "\" />");
			out.println("</attrib>");

			// Frammenti nodo
			Iterator<String> it = tmp.fragmentList.keySet().iterator();
			String key;
			if (it != null) {
				out.println("<fragments>");
				while (it.hasNext()) {
					key = it.next();
					out.println("<fragment id=\"" + key + "\" />");
				}
				out.println("</fragments>");
			}

			// Contenuto nodo
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				out.println("<content>" + node.getTextContent() + "</content>");
			}

			contaVisita++;
			if (node.hasChildNodes())
				for (int i = 0; i < node.getChildNodes().getLength(); i++)
					visitaNodo(node.getChildNodes().item(i));

			out.println("</subtree>");
		}
	}

}
