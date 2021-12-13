package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class References {

	NodeParents np = new NodeParents();
	public ArrayList<NodeChanged> labelCondi(ArrayList<NodeChanged> modif) {
		ArrayList<String> labList = new ArrayList<String>();
		ArrayList<NodeChanged> del = new ArrayList<NodeChanged>();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (nCh.getNodetype().equals("ref")) {
					if (nCh.hasLabelRef()) {
						if (nCh.getChangelist().get(0).isFrom()) {
							labList.add(nCh.getLabelRef() + "-");
						} else if (nCh.getChangelist().get(0).isTo()) {
							labList.add(nCh.getLabelRef() + "+");
						}
					}
				}
			}
		}
		for (NodeChanged nCh : modif) {
			if (!nCh.getChangelist().get(0).hasOp()) {
				nCh.getChangelist().get(0).setOp("");
			}
			if (nCh.getChangelist().get(0).getChangement().contains("ref")) {
				if (nCh.getChangelist().size() == 2) {
					// ca veut dire insert delete TextContentd
					ChangeObject ch1 = nCh.getChangelist().get(0);

					ChangeObject ch2 = nCh.getChangelist().get(1);
					int news;
					int orig;
					if (ch1.getChangement().contains("insert") || ch1.getOp().contains("insert")) {

						news = ch1.getLabel();
						orig = ch2.getLabel();
					} else {

						news = ch2.getLabel();
						orig = ch1.getLabel();
					}
					int diff = orig - news;
					int compt = 0;
					int z = 0;
					for (String l : labList) {
						Pattern pattern = Pattern.compile("\\d+");
						Matcher matcher = pattern.matcher(l);
						matcher.find();
						if (Integer.parseInt(matcher.group()) < orig || Integer.parseInt(matcher.group()) < news) {
							z += 1;
							if (l.substring(l.length() - 1).equals("+")) {
								compt -= 1;
							} else if (l.substring(l.length() - 1).equals("-")) {
								compt += 1;
							}
						}
					}
					if (z != 0) {
						if (compt == diff) {
							del.add(nCh);
						}
					}
				} else if (nCh.getChangelist().size() == 1
						&& (nCh.getChangelist().get(0).getChangement().contains("delete")
						|| nCh.getChangelist().get(0).getOp().contains("delete"))) {
					for (String l : labList) {
						Pattern pattern = Pattern.compile("\\d+");
						Matcher matcher = pattern.matcher(l);
						matcher.find();
						ChangeObject ch = nCh.getChangelist().get(0);
						if (Integer.parseInt(matcher.group()) == ch.getLabel()
								&& l.substring(l.length() - 1).equals("-")) {
							del.add(nCh);
						}
					}
				}
				// öodified et original

			}
			if (nCh.getChangelist().get(0).getOp().equals("")) {
				nCh.getChangelist().get(0).setOp(null);
			}
		}
		for (NodeChanged d : del) {
			modif.remove(d);
		}

		return modif;

	}

	public ArrayList<NodeChanged> refNeg(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
		ArrayList<Integer> labList = new ArrayList<Integer>();
		ArrayList<NodeChanged> del = new ArrayList<NodeChanged>();
		Dtree tree = bd.getTreeorig();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (nCh.getNodetype().equals("ref")) {
					if (nCh.hasLabelRef()) {
						Pattern pattern = Pattern.compile("\\d+");
						Matcher matcher = pattern.matcher(nCh.getLabelRef());
						matcher.find();
						labList.add(Integer.parseInt(matcher.group()));
					}
				}

			}
		}
		for (NodeChanged nCh : modif) {
			for (ChangeObject co : nCh.getChangelist()) {
				if (co.getChangement().contains("ref")) {
					if (!co.hasOp()) {
						co.setOp("");
					}
					if (nCh.getChangelist().size() == 1
							&& (co.getChangement().contains("delete") || co.getOp().contains("delete"))) {
						Dnode dn = tree.getNode(nCh.getNodenumberA());
						Node n = dn.refDomNode;
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) n;
							if (e.getTextContent().contains("-|xref")) {
								String xr = e.getTextContent();

								while (xr.contains("ref-type")) {
									xr = xr.substring(xr.indexOf("ref-type") + 8);
									Pattern pattern = Pattern.compile("\\d+");
									Matcher matcher = pattern.matcher(xr);
									matcher.find();
									int label = Integer.parseInt(matcher.group());
									if (Math.abs(label - co.getLabel()) == 1) {
										if (labList.contains(label)) {
											del.add(nCh);
											continue;
										}
									}

								}
							}
						}

						NodeList nl = n.getChildNodes();
						for (int i = 0; i < nl.getLength(); i++) {
							if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) nl.item(i);
								if (e.getNodeName().equals("xref")) {
									int label = Integer.parseInt(e.getTextContent());
									if (Math.abs(label - co.getLabel()) == 1) {
										if (labList.contains(label)) {
											del.add(nCh);
										}
									}
								}
							}
						}
					}
					if (co.getOp().equals("")) {
						co.setOp(null);
					}
					break;
				}
			}
		}

		for (NodeChanged d : del) {
			modif.remove(d);
		}

		return modif;

	}


	public ArrayList<NodeChanged> findRef(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
		ArrayList<NodeChanged> tab = new ArrayList<NodeChanged>();
		Dtree tree = bd.getTreeorig();
		Dtree treem = bd.getTreemodif();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (nCh.getNodetype().equals("ref")) {
					tab.add(nCh);
				}
			}
		}
		if (tab.size() == 0) {
			return modif;
		}
		Dnode dnoeudB = null;
		Dnode dnoeudA = null;
		for (NodeChanged nCh : tab) {
			ChangeObject cO = nCh.getChangelist().get(0);
			if (cO.hasNodenumB()) {
				dnoeudB = treem.getNode(Integer.parseInt(cO.getNodenumB()));
				int papB = dnoeudB.posFather;
				dnoeudB = treem.getNode(papB);
				if (cO.hasAtA()) {
					dnoeudA = tree.getNode(Integer.parseInt(cO.getAtA()));
				} else {
					dnoeudA = tree.getNode(Integer.parseInt(cO.getNodenumA()));
					int papA = dnoeudA.posFather;
					dnoeudA = tree.getNode(papA);
				}
			} else {
				dnoeudA = tree.getNode(Integer.parseInt(cO.getNodenumA()));
				int papA = dnoeudA.posFather;
				dnoeudA = tree.getNode(papA);
				if (cO.hasAtB()) {
					int papB = Integer.parseInt(cO.getAtB());
					dnoeudB = treem.getNode(papB);
				} else {
					dnoeudB = treem.getNode(nCh.getNodenumberB());
					int papB = dnoeudB.getPosFather();
					dnoeudB = treem.getNode(papB);
				}
			}
		}
//			}
//		}
		Node nA = dnoeudA.refDomNode;
		if (!nA.getNodeName().equals("ref-list")) {
			nA = nA.getParentNode();
			dnoeudA = tree.getNode(dnoeudA.getPosFather());
		}
		NodeChanged nCh = new NodeChanged(dnoeudA.indexKey);
		int comp = XmlFileAttributes.numChild(dnoeudA.indexKey, bd.getOrignal(), "ref", bd.getTreeorig());
		nCh.setNodenumberB(dnoeudB.getIndexKey());
		nCh.setNodetype(nA.getNodeName());
		nCh.setA(true);
		int compB = XmlFileAttributes.numChild(dnoeudB.indexKey, bd.getModified(), "ref", bd.getTreemodif());
		nCh.setFinall("Final: " + Integer.toString(compB));
		nCh.setInit("Initial: " + Integer.toString(comp));
		nCh.setModified("Modified: " + Integer.toString(tab.size()));
		modif.add(nCh);
		return modif;

	}
}


