package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class NodeParents {	Similarity sim = new Similarity();
	AtRectif at = new AtRectif();

	public ArrayList<NodeChanged> tabMaker(String specobj, ArrayList<NodeChanged> modif, ArrayList<NodeChanged> delcit,
										   BrowseDelta bd) {
		ArrayList<NodeChanged> tab = new ArrayList<NodeChanged>();
		Dtree treem = bd.getTreemodif();
		Dtree tree = bd.getTreeorig();
		for (NodeChanged nCh : modif) {
			if (!nCh.isA()) {
				int nnb = nCh.getNodenumberA();
				Dnode noeud = treem.getNode(nnb);
				Node n = noeud.refDomNode;
				if (n.getNodeName().equals(specobj)) {
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) n;
						NodeList nlist = elmt.getElementsByTagName("label");
						nCh.setNodetype(specobj);
						if (nlist.item(0).getNodeType() == Node.ELEMENT_NODE) {
							elmt = (Element) nlist.item(0);
							nCh.setLabelObj(elmt.getTextContent());
						}
					}
					tab.add(nCh);
					continue;
				}
				if (n.getNodeName().equals("label")) {
					delcit.add(nCh);
					continue;
				}
				while (n.getParentNode().getParentNode() != null) {
					n = n.getParentNode();
					if (n.getNodeName().equals(specobj)) {
						tab.add(nCh);
						break;
					}
				}
			} else {
				int nna = nCh.getNodenumberA();
				Dnode noeud = tree.getNode(nna);
				Node n = noeud.refDomNode;
				if (n.getNodeName().equals(specobj)) {
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) n;
						NodeList nlist = elmt.getElementsByTagName("label");
						nCh.setNodetype(specobj);
						if (nlist.item(0).getNodeType() == Node.ELEMENT_NODE) {
							elmt = (Element) nlist.item(0);
							nCh.setLabelObj(elmt.getTextContent());
						}
					}
					tab.add(nCh);
					continue;
				}
				if (n.getNodeName().equals("label")) {
					delcit.add(nCh);
					continue;
				}
				while (n.getParentNode().getParentNode() != null) {
					n = n.getParentNode();
					if (n.getNodeName().equals(specobj)) {
						tab.add(nCh);
						break;
					}
				}
			}
		}
		return tab;
	}

	public ArrayList<NodeChanged> findNoeudPar(ArrayList<NodeChanged> modif, BrowseDelta bd, boolean jaccard,
											   boolean simitext, boolean simtextW) throws InputFileException {
		Dtree treeorig = bd.getTreeorig();
		Dtree treemodif = bd.getTreemodif();
		ArrayList<ArrayList<String>> stocknoeud = new ArrayList<ArrayList<String>>();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (!nCh.hasChangelist()) {
					continue;
				}
				if (nCh.getNodetype().equals("table-wrap") || nCh.getNodetype().equals("table")
						|| nCh.getNodetype().equals("fig") || nCh.getNodetype().equals("figures")
						|| nCh.getNodetype().equals("ref")) {
					for (ChangeObject cOb : nCh.getChangelist()) {
						if (cOb.hasOp() || cOb.getChangement().equals("delete")
								|| cOb.getChangement().equals("ref-delete") || cOb.getChangement().equals("insert")
								|| cOb.getChangement().equals("ref-insert")) {

							if (cOb.isFrom() || cOb.getChangement().equals("ref-delete")) {
								// from ca va �tre move upgrade downgrade merge egalement mais ca ne verra
								// aucune difference...
								// pb merge a voir
								int nnA = nCh.getNodenumberA();
								Dnode dnodeorig = treeorig.getNode(nnA);
								int nodeparnum = dnodeorig.posFather;
								Node noeud = treeorig.getNode(nodeparnum).refDomNode;
								if (noeud.getNodeType() == Node.ELEMENT_NODE) {
									Element e = (Element) noeud;
									if (!nCh.hasDepth()) {
										nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e) + 1));
									}

								}

							} else if (cOb.isTo() || cOb.getChangement().equals("ref-insert")) {
								int nnB=nCh.getNodenumberB();
								Dnode dnodemodif=treemodif.getNode(nnB);
								if (dnodemodif.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
									Element e = (Element) dnodemodif.refDomNode;
									if (!nCh.hasDepth()) {
										nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e) ));
									}
								}
							}
						}
					}

					continue;
				}
			}
			for (ChangeObject cOb : nCh.getChangelist()) {
				if (cOb.hasOp() || cOb.getChangement().equals("delete") || cOb.getChangement().equals("ref-delete")
						|| cOb.getChangement().equals("insert") || cOb.getChangement().equals("ref-insert")) {
					if (!cOb.hasOp()) {
						cOb.setOp("");
					}
					if (cOb.getOp().contains("From") || cOb.getChangement().equals("delete")
							|| cOb.getChangement().equals("ref-delete")) {
						// from ca va �tre move upgrade downgrade merge egalement mais ca ne verra
						// aucune difference...
						// pb merge a voir
						ArrayList<String> listfrom = new ArrayList<String>();
						int nnA = Integer.parseInt(cOb.getNodenumA());
						Dnode dnodeorig = treeorig.getNode(nnA);
						int nodeparnum = dnodeorig.posFather;
						Node noeud = treeorig.getNode(nodeparnum).refDomNode;
						if (noeud.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) noeud;
							if (!nCh.hasDepth()) {
								nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e) + 1));
							}
							listfrom.add(e.getTextContent());
							int nodeparent;
							if (cOb.hasNodenumB()) {
								int nnB = Integer.parseInt(cOb.getNodenumB());
								Dnode dnodemodif = treemodif.getNode(nnB);
								nodeparent = dnodemodif.posFather;
							} else {
								nodeparent = Integer.parseInt(cOb.getAtB());
							}
							Node noeudmodif = treemodif.getNode(nodeparent).refDomNode;
							if (noeudmodif.getNodeType() == Node.ELEMENT_NODE) {
								Element e1 = (Element) noeudmodif;
								listfrom.add(e1.getTextContent());
								listfrom.add(Integer.toString(nodeparnum));
								listfrom.add(e.getNodeName());
								listfrom.add(Integer.toString(XmlFileAttributes.getDepth(e)));
								if (e.hasAttribute("id")) {
									listfrom.add(e.getAttribute("id"));
								}
								if (e.getFirstChild().getNodeName() == "title") {
									if (noeud.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
										Element elt = (Element) noeud.getFirstChild();
										listfrom.add(elt.getTextContent());
									}
								}
								stocknoeud.add(listfrom);
							}
						}

					} else if (cOb.isTo() || cOb.getChangement().equals("ref-insert")) {
						ArrayList<String> listfrom = new ArrayList<String>();
						int nnA = Integer.parseInt(cOb.getAtA());
						Dnode dnodeorig = treeorig.getNode(nnA);
						int nnb = Integer.parseInt(cOb.getNodenumB());
						Dnode dnodemodif = treemodif.getNode(nnb);
						if (dnodemodif.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
							Element emod = (Element) dnodemodif.refDomNode;
							if (!nCh.hasDepth()) {
								nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(emod)));
							}
						}
						if (dnodeorig.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) dnodeorig.refDomNode;
							if (!nCh.hasDepth()) {
								nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e) + 1));
							}
							listfrom.add(e.getTextContent());

							int nnbparent = dnodemodif.getPosFather();
							dnodemodif = treemodif.getNode(nnbparent);
							if (dnodemodif.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
								Element e1 = (Element) dnodemodif.refDomNode;
								listfrom.add(e1.getTextContent());
								listfrom.add(Integer.toString(nnA));
								listfrom.add(e.getNodeName());
								listfrom.add(Integer.toString(XmlFileAttributes.getDepth(e)));
								if (e.hasAttribute("id")) {
									listfrom.add(e.getAttribute("id"));
								}
								if (e.getFirstChild().getNodeName() == "title") {
									if (dnodemodif.refDomNode.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
										Element elt = (Element) dnodemodif.refDomNode.getFirstChild();
										listfrom.add(elt.getTextContent());
									}
								}
								stocknoeud.add(listfrom);
							}
						}

					}
					if (cOb.getOp().equals("")) {
						cOb.setOp(null);
					}
				}
			}

		} // element 0 original element 1 modified element 2 nodenumber
		for (ArrayList<String> element : stocknoeud) {

			if (!(modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(element.get(2))))) {

				NodeChanged nCh = new NodeChanged(Integer.parseInt(element.get(2)));
				ArrayList<String> scores = sim.score(element.get(0), element.get(1), jaccard, simitext, simtextW);
				nCh.setJaccard(scores.get(0));
				nCh.setSimilartext(scores.get(1));
				nCh.setSimitextword(scores.get(2));
				nCh.setNodetype(element.get(3));
				nCh.setDepth(element.get(4));
				if (element.size() > 5) {
					nCh.setId(element.get(5));
				}
				if (element.size() > 6) {
					nCh.setTitle(element.get(6));
				}
				// on ajoute cette liste a notre hashmap modif
				modif.add(nCh);
			}
		}
		return modif;

	}

	public ArrayList<NodeChanged> specFather1(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
		Dtree treem = bd.getTreemodif();
		Dtree tree = bd.getTreeorig();
		ArrayList<NodeChanged> modif1 = new ArrayList<NodeChanged>();
		for (NodeChanged nc : modif) {
			modif1.add(nc);

		}
		for (NodeChanged nCh : modif1) {
			if (!nCh.isA()) {
				int nnb = nCh.getNodenumberA();
				String cgt = nCh.getChangelist().get(0).changement;
				Dnode noeud = treem.getNode(nnb);
				Dnode noeudA = tree.getNode(0);
				String eltCit = noeud.refDomNode.getNodeName();
				// Dnode noeuda = null;
				boolean isT = true;
				Node n = noeud.refDomNode;
				if (n.getNodeName().equals("ref")) {
					modif.remove(nCh);
					nCh.setNodetype(n.getNodeName());
					Node nolab = n.getFirstChild();
					noeudA = at.atcalcul(treem, tree, "ref-list", "ref-list", noeud, noeudA, bd, bd.getOrignal());
					nCh.setAtA(noeudA.indexKey);
					if (nolab.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) nolab;
						nCh.setLabelRef(elmt.getTextContent());
					}
					if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
						modif.add(nCh);
					}
					continue;
				} else {
					while (n.getParentNode().getParentNode() != null) {
						int pap = noeud.getPosFather();
						noeud = treem.getNode(pap);
						n = noeud.refDomNode;
						if (noeud.refDomNode.getNodeName().equals("ref")) {
							modif.remove(nCh);
							nCh.setNodetype(noeud.refDomNode.getNodeName());
							nCh.setNodenumberA(pap);
							ArrayList<ChangeObject> lcO = new ArrayList<ChangeObject>();
							ChangeObject co = new ChangeObject();
							if (noeud.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
								noeudA = at.atcalcul(treem, tree, n.getNodeName(), n.getNodeName(), noeud, noeudA, bd,
										bd.getOrignal());
								co.setAtA(Integer.toString(noeud.getPosFather()));

							}
							nCh.setNodenumberB(nCh.getNodenumberA());
							nCh.setNodenumberA(noeudA.indexKey);
							nCh.setA(true);
							co.setNodenumB(Integer.toString(pap));
							co.setNodenumA(Integer.toString(noeudA.indexKey));
							if (noeudA.refDomNode.getNodeName().equals("ref")) {
								if (eltCit.equals("element-citation")) {
									co.setChangement(cgt);
									Node nolab = noeudA.refDomNode.getFirstChild();
									if (n.getNodeType() == Node.ELEMENT_NODE) {
										Element elmt = (Element) nolab;
										nCh.setLabelRef(elmt.getTextContent());
									}
								} else {
									co.setChangement("ref-edit");
								}
							}
							co.setTo(isT);
							lcO.add(co);
							nCh.setChangelist(lcO);
							if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
								modif.add(nCh);
							}
						}
					}
				}
			} else {
				int nna = nCh.getNodenumberA();
				String cgt = nCh.getChangelist().get(0).changement;
				Dnode noeud = tree.getNode(nna);
				String eltCit = noeud.refDomNode.getNodeName();
				Node n = noeud.refDomNode;
				Dnode noeudB = null;
				boolean isF = true;
				if (n.getNodeName().equals("ref")) {
					modif.remove(nCh);
					nCh.setNodetype(n.getNodeName());
					Node nolab = n.getFirstChild();
					noeudB = at.atcalcul(tree, treem, "ref-list", "ref-list", noeud, noeudB, bd, bd.getModified());
					nCh.setAtB(noeudB.indexKey);
					if (nolab.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) nolab;
						nCh.setLabelRef(elmt.getTextContent());
					}
					if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
						modif.add(nCh);
					}
					continue;
				} else {
					while (n.getParentNode().getParentNode() != null) {
						int pap = noeud.getPosFather();
						noeud = tree.getNode(pap);
						n = noeud.refDomNode;
						if (n.getNodeName().equals("ref")) {
							modif.remove(nCh);
							nCh.setNodenumberA(pap);
							noeudB = at.atcalcul(tree, treem, "ref", "ref", noeud, noeudB, bd, bd.getOrignal());
							nCh.setNodenumberB(noeudB.indexKey);
							nCh.setNodetype(n.getNodeName());
							ArrayList<ChangeObject> lcO = new ArrayList<ChangeObject>();
							ChangeObject co = new ChangeObject();
							co.setFrom(isF);
							co.setAtB(Integer.toString(noeudB.posFather));
							nCh.setAtB(noeudB.posFather);
							if (eltCit.equals("element-citation")) {
								Node nolab = n.getFirstChild();
								if (nolab.getNodeType() == Node.ELEMENT_NODE) {
									Element elmt = (Element) nolab;
									nCh.setLabelRef(elmt.getTextContent());
								}
								co.setChangement(cgt);
							} else {
								co.setChangement("ref-edit");
							}
							co.setNodenumA(Integer.toString(pap));
							lcO.add(co);
							nCh.setChangelist(lcO);
							if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
								modif.add(nCh);
							}
						}
					}

				}
			}
		}
		return modif;

	}

	public ArrayList<NodeChanged> specFather(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
		Dtree treem = bd.getTreemodif();
		Dtree tree = bd.getTreeorig();
		ArrayList<NodeChanged> modif1 = new ArrayList<NodeChanged>();
		for (NodeChanged nc : modif) {
			modif1.add(nc);
		}
		for (NodeChanged nCh : modif1) {
			if (!nCh.isA()) {
				int nnb = nCh.getNodenumberA();
				String cgt = nCh.getChangelist().get(0).changement;
				Dnode noeud = treem.getNode(nnb);
				String eltCit = noeud.refDomNode.getNodeName();
				Dnode noeuda = null;
				Integer papA = null;
				boolean isT = true;
				Node n = noeud.refDomNode;
				if (n.getNodeName().equals("disp-formula") || n.getNodeName().equals("inline-formula")
						|| n.getNodeName().equals("contrib") ) {
					modif.remove(nCh);
					nCh.setNodetype(n.getNodeName());
					Node nolab = n.getFirstChild();
					if (nolab.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) nolab;
						nCh.setLabelRef(elmt.getTextContent());
					}
					if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
						modif.add(nCh);
					}
					break;
				} else {
					while (n.getParentNode().getParentNode() != null) {
						int pap = noeud.getPosFather();
						if (papA == null) {
							papA = Integer.parseInt(nCh.getChangelist().get(0).getAtA());
							noeuda = tree.getNode(papA);
						} else {
							papA = noeuda.posFather;
							noeuda = tree.getNode(papA);
						}
						noeud = treem.getNode(pap);
						n = noeud.refDomNode;
						if (noeuda.refDomNode.getNodeName().equals("disp-formula")
								|| noeuda.refDomNode.getNodeName().equals("inline-formula")
								|| noeuda.refDomNode.getNodeName().equals("contrib")) {
							modif.remove(nCh);
							nCh.setNodetype(noeuda.refDomNode.getNodeName());
							nCh.setNodenumberA(papA);
							ArrayList<ChangeObject> lcO = new ArrayList<ChangeObject>();

							ChangeObject co = new ChangeObject();
							co.setAtA(nCh.getChangelist().get(0).getAtA());
							if (n.getNodeName().equals("disp-formula") || n.getNodeName().equals("inline-formula")
									|| n.getNodeName().equals("ref") || n.getNodeName().equals("contrib")) {
								nCh.setNodenumberB(pap);
								co.setNodenumB(Integer.toString(pap));
							} else if (n.getParentNode().getNodeName().equals("ref")
									|| n.getParentNode().getNodeName().equals("disp-formula")
									|| n.getParentNode().getNodeName().equals("inline-formula")
									|| n.getParentNode().getNodeName().equals("contrib")) {
								nCh.setNodenumberB(noeud.getPosFather());
								co.setNodenumB(Integer.toString(pap));
							}
							if (noeuda.refDomNode.getNodeName().equals("ref")) {
								if (eltCit.equals("element-citation")) {
									co.setChangement(cgt);
									Node nolab = noeuda.refDomNode.getFirstChild();
									if (n.getNodeType() == Node.ELEMENT_NODE) {
										Element elmt = (Element) nolab;
										nCh.setLabelRef(elmt.getTextContent());
									}
								} else {
									co.setChangement("ref-edit");
								}
							} else if (noeuda.refDomNode.getNodeName().equals("contrib")) {
								co.setChangement("contrib-edit");
							} else {
								co.setChangement("formula-edit");
							}
							co.setTo(isT);
							co.setNodenumA(Integer.toString(papA));
							lcO.add(co);
							nCh.setChangelist(lcO);
							if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {

								modif.add(nCh);
							}
						}
					}
				}
			} else {
				int nna = nCh.getNodenumberA();
				String cgt = nCh.getChangelist().get(0).changement;
				Dnode noeud = tree.getNode(nna);
				String eltCit = noeud.refDomNode.getNodeName();
				Node n = noeud.refDomNode;
				Dnode noeudB = null;
				Integer papB = null;
				boolean isF = true;
				if (n.getNodeName().equals("disp-formula") || n.getNodeName().equals("inline-formula")
						|| n.getNodeName().equals("contrib")) {
					modif.remove(nCh);
					nCh.setNodetype(n.getNodeName());
					Node nolab = n.getFirstChild();
					if (nolab.getNodeType() == Node.ELEMENT_NODE) {
						Element elmt = (Element) nolab;
						nCh.setLabelRef(elmt.getTextContent());
					}
					if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
						modif.add(nCh);
					}
					break;
				} else {

					if (nCh.getChangelist().get(0).hasNodenumB()) {
						papB = nCh.getNodenumberB();
						noeudB = treem.getNode(papB);
						isF = false;
					}
					while (n.getParentNode().getParentNode() != null) {
						int pap = noeud.getPosFather();
						if (papB == null) {
							papB = Integer.parseInt(nCh.getChangelist().get(0).getAtB());
							noeudB = treem.getNode(papB);
						} else if (noeudB.refDomNode.getNodeName().equals("inline-formula")
								|| noeudB.refDomNode.getNodeName().equals("disp-formula")
								|| noeudB.refDomNode.getNodeName().equals("contrib")
								|| noeudB.refDomNode.getNodeName().equals("article")) {

						} else {
							papB = noeudB.posFather;
							noeudB = treem.getNode(papB);
						}
						noeud = tree.getNode(pap);
						n = n.getParentNode();
						if (n.getNodeName().equals("disp-formula") || n.getNodeName().equals("inline-formula")
								||n.getNodeName().equals("contrib")) {
							modif.remove(nCh);
							nCh.setNodenumberA(pap);
							nCh.setNodetype(n.getNodeName());
							ArrayList<ChangeObject> lcO = new ArrayList<ChangeObject>();
							ChangeObject co = new ChangeObject();
							co.setFrom(isF);
							if (nCh.getChangelist().get(0).hasAtB()) {
								co.setAtB(nCh.getChangelist().get(0).getAtB());
							}
							if (noeudB.refDomNode.getNodeName().equals("inline-formula")
									|| noeudB.refDomNode.getNodeName().equals("disp-formula")
									|| noeudB.refDomNode.getNodeName().equals("contrib")) {
								nCh.setNodenumberB(papB);
								co.setNodenumB(Integer.toString(papB));
							} else if (noeudB.refDomNode.getParentNode().getNodeName().equals("inline-formula")
									|| noeudB.refDomNode.getParentNode().getNodeName().equals("disp-formula")
									|| noeudB.refDomNode.getParentNode().getNodeName().equals("contrib")) {

								nCh.setNodenumberB(noeudB.getPosFather());
								co.setNodenumB(Integer.toString(noeudB.getPosFather()));
							}
							if (n.getNodeName().equals("contrib")) {
								co.setChangement("contrib-edit");
							} else {
								co.setChangement("formula-edit");
							}
							co.setNodenumA(Integer.toString(pap));
							lcO.add(co);
							nCh.setChangelist(lcO);
							if (!modif.stream().anyMatch(o -> o.getNodenumberA() == nCh.getNodenumberA())) {
								modif.add(nCh);
							}
						}
					}
				}
			}
		}

		return modif;
	}

}
