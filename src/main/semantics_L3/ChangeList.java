package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChangeList {

	// changeMap transform the changeObjectList into a NodechangeList
	public ArrayList<NodeChanged> changetoList(BrowseDelta bd) throws InputFileException {
		int actualNodeNumberDiff = 0;
		int refnn = 0;
		Dtree treeorig = bd.getTreeorig();
		Dtree treemodif = bd.getTreemodif();
		for (ChangeObject change : bd.getChangeList()) {
			int nodenumber;
			int nodenumberA = -1;
			int nodenumberB = -1;
			if (change.hasNodenumberA()) {
				int nna = Integer.parseInt(change.getNnARef()) + refnn;
				change.setNnARef(Integer.toString(nna));
				nodenumberA = Integer.parseInt(change.getNodenumA());
				nodenumber = nodenumberA;
			} else {
				int nnb = Integer.parseInt(change.getNnBRef()) - refnn;
				change.setNnBRef(Integer.toString(nnb));
				nodenumberB = Integer.parseInt(change.getNodenumB());
				nodenumber = nodenumberB;
			}
			if (change.hasNodecount()) {

				if (change.getChangement().equals("delete")) {
					actualNodeNumberDiff += Integer.parseInt(change.getNodecount());
					refnn += Integer.parseInt(change.getNodecount());

				} else if (change.hasOp()) {
					if (change.getOp().contains("To")) {
						actualNodeNumberDiff -= Integer.parseInt(change.getNodecount());
						refnn -= Integer.parseInt(change.getNodecount());
					} else if (change.getOp().contains("From")) {
						actualNodeNumberDiff += Integer.parseInt(change.getNodecount());
						refnn += Integer.parseInt(change.getNodecount());
					}
				} else {// insert
					actualNodeNumberDiff -= Integer.parseInt(change.getNodecount());
					refnn -= Integer.parseInt(change.getNodecount());
				}

			}
			if (actualNodeNumberDiff != 0) {
				int i = 0;
			}
			for (ChangeObject tempco : bd.getChangeList()) {

				if (tempco.isTo()) {
					if (Integer.parseInt(tempco.getOldatA()) > nodenumber) {
						int newat = Integer.parseInt(tempco.getAtA()) + actualNodeNumberDiff;
						tempco.setAtA(Integer.toString(newat));
					}
				}
				if (tempco.isFrom()) {
					if (Integer.parseInt(tempco.getOldatB()) > nodenumber) {
						int newatB = Integer.parseInt(tempco.getAtB()) - actualNodeNumberDiff;
						tempco.setAtB(Integer.toString(newatB));
					}
				}
			}
			actualNodeNumberDiff = 0;
		}

		ArrayList<NodeChanged> modif = new ArrayList<NodeChanged>();

		// calcul nnaref and nnbref

		for (ChangeObject co : bd.getChangeList()) {
			if (co.getChangement().contains("text-style")) {
				if (!co.hasNodenumB()) {
					co.setNodenumB(co.getNnARef());
				}
				if (!co.hasNodenumberA()) {
					co.setNodenumA(co.getNnBRef());
				}
			}
			ChangeObject change = new ChangeObject();
			change = co;
			if (co.isXref()) {
				if (!co.hasOp()) {
					co.setOp("");
				}
				String xrorig = "";
				String xr = "";
				if (co.getOp().contains("deleted")) {
					Dnode dn = treeorig.getNode(Integer.parseInt(co.getNodenumA()));
					Node n = dn.refDomNode;
					Element e = (Element) n.getParentNode();
					xr = co.getTextContent();
					xrorig = e.getTextContent();
					while (!xr.contains("ref-type") || xr.indexOf("rid") < xr.indexOf("ref-type")) {
						xr = xrorig.substring(xrorig.indexOf(xr) - 1);
						xrorig = e.getTextContent();

					}
					xr = xr.substring(xr.indexOf("ref-type"));
				} else {
					Dnode dn = treemodif.getNode(Integer.parseInt(co.getNodenumB()));
					Node n = dn.refDomNode;
					Element e = (Element) n.getParentNode();
					xr = co.getTextContent();
					xrorig = e.getTextContent();
					if (!(xr.indexOf("-|xref") == 0)) {
						while (!xr.contains("ref-type") || xr.indexOf("rid") < xr.indexOf("ref-type")) {
							xr = xrorig.substring(xrorig.indexOf(xr) - 1);
							xrorig = e.getTextContent();

						}
						xr = xr.substring(xr.indexOf("ref-type"));
					} else {
						xr = xr.substring(xr.indexOf("-|xref") + 6);
					}
				}
				if (xr.contains("Figure")) {
					change.setCitable("figure");
				} else if (xr.contains("bibr")) {
					change.setCitable("ref");
				} else if (xr.contains("table")) {
					change.setCitable("table");
				} else {
					change.setCitable("sec");
				}
				xr = xr.substring(xr.indexOf("rid") + 5);
				xr = xr.substring(0, xr.indexOf("\"|-"));

				Pattern pattern = Pattern.compile("\\d+");
				Matcher matcher = pattern.matcher(xr);
				matcher.find();
				change.setLabel(Integer.parseInt(matcher.group()));
				change.setLabSec(xr);
				if (co.getOp().equals("")) {
					co.setOp(null);
				}
			}
			int nodenumber;
			int nodenumberA = -1;
			int nodenumberB = -1;
			if (change.hasNodenumberA()) {
				nodenumberA = Integer.parseInt(change.getNodenumA());
				nodenumber = nodenumberA;
				if (change.getChangement().contains("text-style")) {
					Dnode noeudmodif = treemodif.getNode(nodenumberA);
					change.setNodenumB(Integer.toString(noeudmodif.getPosFather()));
				}
			} else {
				nodenumberB = Integer.parseInt(change.getNodenumB());
				nodenumber = nodenumberB;
			}
			if (change.getChangement().contains("text-style") && change.hasNnBref()) {
				change.setNodenumA(change.getNnBRef());
				Dnode noeud = treeorig.getNode(Integer.parseInt(change.getNodenumA()));
				change.setNodenumA(Integer.toString(noeud.getPosFather()));
			}

			// node number difference calculation

			if (modif.stream().anyMatch(o -> o.getNodenumberA() == nodenumber)) {
				List<NodeChanged> templist = modif.stream().filter(a -> a.getNodenumberA() == nodenumber)
						.collect(Collectors.toList());
				if ((templist.stream().anyMatch(a -> a.isA() == true) && nodenumberA != -1)
						|| (templist.stream().anyMatch(a -> a.isA() == false) && nodenumberB != -1)) {
					NodeChanged noch = new NodeChanged(nodenumber);
					if (nodenumberA != -1) {
						noch = templist.stream().filter(a -> a.isA() == true).collect(Collectors.toList()).get(0);
						modif.remove(noch);
						if (change.hasNodenumB()) {
							noch.setNodenumberB(Integer.parseInt(change.getNodenumB()));
						}
					} else {
						noch = templist.stream().filter(a -> a.isA() == false).collect(Collectors.toList()).get(0);
						modif.remove(noch);
					}

					if (change.isFrom() || change.isTo()) {
						if (change.hasFirstChildid()) {
							noch.setLabelSec(change.getFirstChildid());

							if (change.isRef()) {

								noch.setLabelRef(change.getFirstChildid());
							}
						}
					}

					noch.getChangelist().add(change);
					modif.add(noch);
				} else {
					NodeChanged noch = new NodeChanged(nodenumber);
					if (nodenumberA != -1) {
						noch.setA(true);
						if (change.hasNodenumB()) {
							noch.setNodenumberB(Integer.parseInt(change.getNodenumB()));
						}
					} else {
						noch.setA(false);
					}
					if (change.isFrom() || change.isTo()) {
						if (change.hasFirstChildid()) {
							noch.setLabelSec(change.getFirstChildid());

							if (change.isRef()) {

								noch.setLabelRef(change.getFirstChildid());
							}
						}
					}
					ArrayList<ChangeObject> listmodif = new ArrayList<ChangeObject>();
					listmodif.add(change);
					noch.setChangelist(listmodif);
					modif.add(noch);
				}
			} else {
				NodeChanged noch = new NodeChanged(nodenumber);
				if (nodenumberA != -1) {
					noch.setA(true);
					if (change.hasNodenumB()) {
						noch.setNodenumberB(Integer.parseInt(change.getNodenumB()));
					}
				} else {
					noch.setA(false);
					noch.setNodenumberB(Integer.parseInt(change.getNodenumB()));
				}
				if (change.isFrom() || change.isTo()) {
					if (change.hasFirstChildid()) {
						noch.setLabelSec(change.getFirstChildid());

						if (change.isRef()) {

							noch.setLabelRef(change.getFirstChildid());
						}
					}
				}
				ArrayList<ChangeObject> listmodif = new ArrayList<ChangeObject>();
				listmodif.add(change);
				noch.setChangelist(listmodif);
				modif.add(noch);
			}
		}

		for (int b = 0; b < modif.size(); b++) {
			NodeChanged nCh = modif.get(b);
			for (ChangeObject cObj : nCh.getChangelist()) {
				if (!cObj.hasOp()) {
					cObj.setOp("");
				}
			}
		}
		for (int b = 0; b < modif.size(); b++) {
			NodeChanged nCh = modif.get(b);

			if (nCh.getChangelist().stream().anyMatch(a -> a.isXref())) {
				List<ChangeObject> colisFil = nCh.getChangelist().stream().filter(a -> a.isXref())
						.collect(Collectors.toList());

				List<ChangeObject> colis = colisFil.stream().filter(a -> a.getOp().equals("text-deleted"))
						.collect(Collectors.toList());

				for (int i = 0; i < colis.size(); i++) {
					for (int k = i + 1; k < colis.size(); k++) {

						ChangeObject cOo = linkCO(colis.get(i), colis.get(k));
						if (cOo != null) {
							nCh.getChangelist().remove(colis.get(i));
							nCh.getChangelist().remove(colis.get(k));
							nCh.getChangelist().add(cOo);

						}
					}
				}
				List<ChangeObject> colisIns = colisFil.stream().filter(a -> a.getOp().equals("text-inserted"))
						.collect(Collectors.toList());

				for (int i = 0; i < colisIns.size(); i++) {
					for (int k = i + 1; k < colisIns.size(); k++) {
						if (linkCO(colisIns.get(i), colisIns.get(k)) != null) {
							nCh.getChangelist().remove(colisIns.get(i));
							nCh.getChangelist().remove(colisIns.get(k));
							nCh.getChangelist().add(linkCO(colisIns.get(i), colisIns.get(k)));

						}
					}
				}

			}

		}
		// si c le nnb test est le meme quun nna et que le nna est lui meme true on met
		// enseöble TextContentddate et update attribute

//SEOARER CA A FONNNNND

		return modif;
	}

	public ChangeObject linkCO(ChangeObject co1, ChangeObject co2) {
		String textContent1 = co1.getTextContent();
		String textContent2 = co2.getTextContent();
		String fin = "";
		ChangeObject co = new ChangeObject();
		co.setChangement(co1.getChangement());
		co.setNodenumA(co1.getNodenumA());
		co.setNodenumB(co1.getNodenumB());
		co.setXref(co1.isXref());
		co.setOp(co1.getOp());
		co.setCitable(co1.getCitable());
		if ((textContent1.contains("|/xref|") && textContent2.contains("-|xref"))
				|| (textContent2.contains("|/xref|") && textContent1.contains("-|xref"))) {
			String deb = "";
			int pos = 0;
			int finalpos = 0;
			if (textContent1.contains("|/xref|")) {
				deb = textContent2;
				pos = Integer.parseInt(co2.getPos());
				finalpos = Integer.parseInt(co1.getPos());
				fin = textContent1;
			} else {
				deb = textContent1;
				pos = Integer.parseInt(co1.getPos());
				finalpos = Integer.parseInt(co2.getPos());
				fin = textContent2;
			}
			if (finalpos - pos - deb.length() > 0 && finalpos - pos - deb.length() < 5) {
				co.setTextContent(deb + fin);
				deb = deb.substring(deb.indexOf("ref-type") + 8);
				co.setLabSec(deb.substring(deb.indexOf("rid") + 4, deb.indexOf("|-Section")));
				return co;
			}
		}
		return null;
	}

	public ArrayList<Dnode> condAt(Dnode d1, Dnode d2, Dtree tree1, Dtree tree2) {
		Dnode pap1 = tree1.getNode(d1.getPosFather());
		Dnode pap2 = tree2.getNode(d2.getPosFather());
		Node npap1 = pap1.refDomNode;
		Node npap2 = pap2.refDomNode;
		while (!(npap1.getNodeType() == Node.ELEMENT_NODE)) {
			d1 = pap1;
			pap1 = tree1.getNode(d1.getPosFather());
			npap1 = pap1.refDomNode;
		}
		while (!(npap2.getNodeType() == Node.ELEMENT_NODE)) {
			d2 = pap2;
			pap2 = tree2.getNode(d2.getPosFather());
			npap2 = pap2.refDomNode;
		}
		Element e1 = (Element) npap1;
		Element e2 = (Element) npap2;
		int p2index = pap2.indexKey;
		ArrayList<Dnode> listDnode = new ArrayList<Dnode>();
		while (pap1.indexKey != 0) {
			while (pap2.indexKey != 0) {
				if (e1.getNodeName().equals(e2.getNodeName())) {

					if (e1.hasAttribute("id") && e2.hasAttribute("id")) {

						if (e1.getAttribute("id").equals(e2.getAttribute("id"))) {
							listDnode.add(pap1);
							listDnode.add(pap2);
							listDnode.add(d1);
							return listDnode;
						}
					} else {
						listDnode.add(pap1);
						listDnode.add(pap2);
						listDnode.add(d1);
						return listDnode;
					}
				}
				pap2 = tree2.getNode(pap2.posFather);
				e2 = (Element) pap2.refDomNode;

				System.out.println("1" + e1.getNodeName() + e1.getAttribute("id"));
				System.out.println("2" + e2.getNodeName() + e2.getAttribute("id"));
			}
			d1 = pap1;
			pap2 = tree2.getNode(p2index);
			npap2 = pap2.refDomNode;
			e2 = (Element) npap2;
			pap1 = tree1.getNode(pap1.posFather);
			e1 = (Element) pap1.refDomNode;
		}
		return null;

	}
}
