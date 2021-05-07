package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabFigure {
	Similarity sim=new Similarity();
	public ArrayList<NodeChanged> specObjtreatment(ArrayList<NodeChanged> modif, BrowseDelta bd, String specobj,
			String cgt, boolean jaccard, boolean simitext, boolean simtextW) throws InputFileException {
		ArrayList<NodeChanged> tab = new ArrayList<NodeChanged>();
		Dtree treem = bd.getTreemodif();
		Dtree tree = bd.getTreeorig();
		ArrayList<NodeChanged> delcit = new ArrayList<NodeChanged>();
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
				while (!n.getNodeName().equals("body") && !n.getNodeName().equals("front")
						&& !n.getNodeName().equals("back")) {
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
				while (!n.getNodeName().equals("body") && !n.getNodeName().equals("front")
						&& !n.getNodeName().equals("back")) {
					n = n.getParentNode();
					if (n.getNodeName().equals(specobj)) {
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							Element elmt = (Element) n;
							NodeList nlist = elmt.getElementsByTagName("label");
							if (nlist.item(0).getNodeType() == Node.ELEMENT_NODE) {
								elmt = (Element) nlist.item(0);
								nCh.setLabelObj(elmt.getTextContent());
							}
						}
						tab.add(nCh);
						break;
					}
				}
			}
		}
		if (tab.size() == 0) {
			return modif;
		}
		ArrayList<String> labListObj = new ArrayList<String>();
		for (NodeChanged nCh : tab) {
			if (nCh.hasLabelObj()) {
				if (nCh.getChangelist().get(0).isFrom()) {
					labListObj.add(nCh.getLabelObj() + "-");
				} else if (nCh.getChangelist().get(0).isTo()) {
					labListObj.add(nCh.getLabelObj() + "+");
				}
			}
		}
		for (NodeChanged nCh : modif) {
			if (nCh.hasChangelist()) {
				if (nCh.getChangelist().get(0).getChangement().contains(cgt)) {
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
						if (nCh.getChangelist().get(0).getChangement().equals(specobj)) {
							for (String l : labListObj) {
								Pattern pattern = Pattern.compile("\\d+");
								Matcher matcher = pattern.matcher(l);
								matcher.find();
								if (Integer.parseInt(matcher.group()) < orig
										|| Integer.parseInt(matcher.group()) < news) {
									z += 1;
									if (l.substring(l.length() - 1).equals("+")) {
										compt -= 1;
									} else if (l.substring(l.length() - 1).equals("-")) {
										compt += 1;
									}
								}
							}
						}
						if (z != 0) {
							if (compt == diff) {
								delcit.add(nCh);
							}
						}
					} else if (nCh.getChangelist().size() == 1
							&& (nCh.getChangelist().get(0).getChangement().contains("delete")
									|| nCh.getChangelist().get(0).getOp().contains("delete"))) {
						if (nCh.getChangelist().get(0).getChangement().contains(cgt)) {
							for (String l : labListObj) {
								Pattern pattern = Pattern.compile("\\d+");
								Matcher matcher = pattern.matcher(l);
								matcher.find();
								ChangeObject ch = nCh.getChangelist().get(0);
								if (Integer.parseInt(matcher.group()) == ch.getLabel()
										&& l.substring(l.length() - 1).equals("-")) {
									delcit.add(nCh);
								}}}}}}}
		for (NodeChanged d : delcit) {
			modif.remove(d);
		}
		ArrayList<Integer> labList = new ArrayList<Integer>();
		ArrayList<NodeChanged> del = new ArrayList<NodeChanged>();
		for (NodeChanged nCh : tab) {
			if (nCh.hasNodeT()) {
				if (nCh.hasLabelObj()) {
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(nCh.getLabelObj());
					matcher.find();
					labList.add(Integer.parseInt(matcher.group()));
				}
			}
		}
		// when its quote
		for (NodeChanged nCh : modif) {
			if (nCh.hasChangelist()) {
				if (nCh.getChangelist().get(0).getChangement().equals(specobj)) {
					if (nCh.getChangelist().size() == 1
							&& (nCh.getChangelist().get(0).getChangement().contains("delete")
									|| nCh.getChangelist().get(0).getOp().contains("delete"))) {
						Dnode dn = tree.getNode(nCh.getNodenumberA());
						Node n = dn.refDomNode;
						if (n.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) n;
							if (e.getTextContent().contains("-|xref")) {
								ChangeObject co = nCh.getChangelist().get(0);
								String xr = e.getTextContent();
								while (xr.contains("ref-type")) {
									xr = xr.substring(xr.indexOf("ref-type"));
									xr = xr.substring(xr.indexOf("|"));
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
									ChangeObject co = nCh.getChangelist().get(0);

									int label = Integer.parseInt(e.getTextContent());
									if (Math.abs(label - co.getLabel()) == 1) {
										if (labList.contains(label)) {
											del.add(nCh);
										}}}}}}}}}
		for (NodeChanged d : del) {
			modif.remove(d);
		}
		for (NodeChanged nCh : tab) {
			modif.remove(nCh);
			TableChange tc = new TableChange();

			if (!nCh.isA()) {
				del = new ArrayList<NodeChanged>();
				int nnb = nCh.getNodenumberA();
				Dnode noeud = treem.getNode(nnb);
				int pa = noeud.getIndexKey();
				Integer da = null;
				Node n = noeud.refDomNode;
				if (n.getNodeName().equals(specobj)) {
					nCh.setNodetype(specobj);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) n;
						nCh.setId(e.getAttribute("id"));
					}
					ChangeObject co = nCh.getChangelist().get(0);
					nCh.setAtB(Integer.parseInt(co.getAtB()));
					nCh.setAtA(Integer.parseInt(co.getAtA()));
				} else {
					while (!n.getNodeName().equals(specobj)) {

						if (n.getNodeName().equals("caption") || n.getNodeName().equals("table")
								|| n.getNodeName().equals("table-wrap-foot")) {
							ChangeObject co = nCh.getChangelist().get(0);
							Dnode noeudB = treem.getNode(Integer.parseInt(co.getAtB()));
							Dnode noeudA = tree.getNode(Integer.parseInt(co.getAtA()));
							nCh.setAtB(Integer.parseInt(co.getAtB()));
							nCh.setAtA(Integer.parseInt(co.getAtA()));
							while (noeudB != noeud) {
								da = noeudA.posFather;
								noeudA = tree.getNode(da);
								pa = noeudB.posFather;
								noeudB = treem.getNode(pa);
							}
							if (n.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) n;

								if (noeudA.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eA = (Element) noeudA.refDomNode;
									ArrayList<String> scores = sim.score(e.getTextContent(), eA.getTextContent(), jaccard,
											simitext, simtextW);

									tc.setName(n.getNodeName());
									tc.setJaccard(scores.get(0));
									tc.setSimilartext(scores.get(1));
									tc.setSimitextword(scores.get(2));
									pa = noeud.posFather;
									noeud = treem.getNode(pa);
									n = noeud.refDomNode;
								}
							}
							break;
						}
						pa = noeud.posFather;
						noeud = treem.getNode(pa);
						n = noeud.refDomNode;
					}
				}
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					nCh.setId(e.getAttribute("id"));
				}
				nCh.setNodenumberB(pa);
				nCh.setNodetype(n.getNodeName());
				int nodenumA;
				if (da != null) {
					Dnode noe = tree.getNode(da);
					da = noe.getPosFather();
					nCh.setNodenumberA(da);
					nodenumA = da;
				} else {
					nCh.setNodenumberA(pa);
					nodenumA = pa;
				}
				List<NodeChanged> modiff = modif.stream().filter(a -> a.hasChangelist()).collect(Collectors.toList());
				if (modiff.stream().anyMatch(o -> o.getNodenumberA() == nodenumA)) {
					NodeChanged nCh1 = modif.stream().filter(a -> a.getNodenumberA() == nodenumA)
							.collect(Collectors.toList()).get(0);
					modif.remove(nCh1);
					ArrayList<ChangeObject> aCo = nCh1.getChangelist();
					ChangeObject cO = aCo.get(0);
					aCo.remove(cO);
					nCh1.setNodetype(n.getNodeName());
					ArrayList<TableChange> aT = cO.getTablechange();
					if (!(aT.stream().anyMatch(o -> o.getName() == tc.getName()))) {
						aT.add(tc);
						cO.setTablechange(aT);
						cO.setChangement(specobj + "-edit");
						nCh1.setChangelist(aCo);
					}
					aCo.add(cO);
					modif.add(nCh1);
				} else {
					if (modif.stream().anyMatch(o -> o.getNodenumberA() == nodenumA)) {
						modif.remove(modif.stream().filter(a -> a.getNodenumberA() == nodenumA)
								.collect(Collectors.toList()).get(0));
					}
					ArrayList<ChangeObject> aCo = new ArrayList<ChangeObject>();
					ArrayList<TableChange> aT = new ArrayList<TableChange>();

					ChangeObject cO = new ChangeObject();
					if (tc.getName() != null) {
						aT.add(tc);
						cO.setNodenumA(Integer.toString(nCh.getNodenumberA()));
						cO.setTablechange(aT);
						if (n.getNodeName().equals("table-wrap")) {
							cO.setChangement("table-edit");
						} else {
							cO.setChangement(n.getNodeName());
						}
					} else {
						cO.setChangement("insert");
						cO.setTo(true);
						cO.setAtA(Integer.toString(nCh.getAtA()));
						cO.setAtB(Integer.toString(nCh.getAtB()));
					}
					aCo.add(cO);
					nCh.setChangelist(aCo);
					modif.add(nCh);
				}
			} else {
				del = new ArrayList<NodeChanged>();
				int nna = nCh.getNodenumberA();
				Dnode noeud = tree.getNode(nna);
				Node n = noeud.refDomNode;
				int da = noeud.getIndexKey();
				int count = noeud.getIndexKey();
				Integer pa = null;
				ChangeObject co = nCh.getChangelist().get(0);
				if (co.hasNodenumB()) {
					Dnode noeudB = treem.getNode(Integer.parseInt(co.getNodenumB()));
					nCh.setAtB(noeudB.getPosFather());
					nCh.setAtA(noeud.posFather);
				} else {
					nCh.setAtB(Integer.parseInt(co.getAtB()));
					nCh.setAtA(Integer.parseInt(co.getAtA()));
				}
				if (n.getNodeName().equals(specobj)) {
					nCh.setNodetype(specobj);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) n;
						nCh.setId(e.getAttribute("id"));
					}
				} else {
					while (!n.getNodeName().equals(specobj)) {
						if (n.getNodeName().equals("caption") || n.getNodeName().equals("table")) {
							Dnode noeudA = tree.getNode(nCh.getAtA());
							Dnode noeudB = treem.getNode(nCh.getAtB());
							da = noeudA.indexKey;
							while (noeudA != noeud) {
								pa = noeudB.posFather;
								noeudB = treem.getNode(pa);
								da = noeudA.posFather;
								noeudA = tree.getNode(da);
							}
							if (n.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) n;

								if (noeudB.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eB = (Element) noeudB.refDomNode;
									ArrayList<String> scores = sim.score(e.getTextContent(), eB.getTextContent(), jaccard,
											simitext, simtextW);
									tc.setName(n.getNodeName());
									tc.setJaccard(scores.get(0));
									tc.setSimilartext(scores.get(1));
									tc.setSimitextword(scores.get(2));
								}
							}
							da = noeudA.getPosFather();
							n = tree.getNode(da).refDomNode;
							break;
						}
						count = noeud.posFather;
						noeud = tree.getNode(count);
						n = noeud.refDomNode;
					}
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) n;
						nCh.setId(e.getAttribute("id"));
					}
				}
				if (pa != null) {
					Dnode noe = treem.getNode(pa);
					pa = noe.getPosFather();
					nCh.setNodenumberB(pa);
				}
				nCh.setNodenumberA(da);
				nCh.setNodetype(n.getNodeName());
				int nodenumA = da;
				List<NodeChanged> modiff = modif.stream().filter(a -> a.hasChangelist()).collect(Collectors.toList());
				if (modiff.stream().anyMatch(o -> o.getNodenumberA() == nodenumA)) {
					NodeChanged nCh1 = modif.stream().filter(a -> a.getNodenumberA() == nodenumA)
							.collect(Collectors.toList()).get(0);
					modif.remove(nCh1);
					ArrayList<ChangeObject> aCo = nCh1.getChangelist();
					ChangeObject cO = aCo.get(0);
					aCo.remove(cO);
					nCh1.setNodetype(n.getNodeName());
					ArrayList<TableChange> aT = cO.getTablechange();
					if (!(aT.stream().anyMatch(o -> o.getName() == tc.getName()))) {
						aT.add(tc);
						cO.setNodenumA(Integer.toString(nCh.getNodenumberA()));
						cO.setTablechange(aT);
						if (n.getNodeName().equals("table-wrap")) {
							cO.setChangement("table-edit");
						} else {
							cO.setChangement("fig-edit");
						}
						nCh1.setChangelist(aCo);
					}
					aCo.add(cO);
					modif.add(nCh1);
				}
				else {
					if (modif.stream().anyMatch(o -> o.getNodenumberA() == nodenumA)) {
						modif.remove(modif.stream().filter(a -> a.getNodenumberA() == nodenumA)
								.collect(Collectors.toList()).get(0));
					}
					ArrayList<ChangeObject> aCo = new ArrayList<ChangeObject>();
					ArrayList<TableChange> aT = new ArrayList<TableChange>();

					ChangeObject cO = new ChangeObject();
					if (tc.getName() != null) {
						aT.add(tc);
						cO.setNodenumA(Integer.toString(nCh.getNodenumberA()));
						cO.setTablechange(aT);
						if (n.getNodeName().equals("table-wrap")) {
							cO.setChangement("table-edit");
						} else {
							cO.setChangement("fig-edit");
						}
					} else {
						cO.setChangement("delete");
						cO.setFrom(true);
						cO.setAtA(Integer.toString(nCh.getAtA()));
						cO.setAtB(Integer.toString(nCh.getAtB()));
					}
					aCo.add(cO);
					nCh.setChangelist(aCo);
					modif.add(nCh);
				}}}
		for (NodeChanged d : del) {
			modif.remove(d);
		}
		int noeudobj = -1;
		int noeudobjB = -1;
		int compmodifobj = 0;
		Integer nnbobj = null;
		Integer nnbpobj = null;
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (nCh.getNodetype().equals(specobj)) {
					compmodifobj++;
					ChangeObject cO = nCh.getChangelist().get(0);
					if (cO.hasTableChange()) {
						Dnode dn = tree.getNode(nCh.getNodenumberA());
						Dnode dnm = treem.getNode(nCh.getNodenumberB());
						Node n = dn.refDomNode;
						Node nm = dnm.refDomNode;
						Element e = (Element) n;
						Element em = (Element) nm;
						ArrayList<String> scores = sim.score(e.getTextContent(), em.getTextContent(), jaccard, simitext,
								simtextW);
						nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e)));
						nCh.setJaccard(scores.get(0));
						nCh.setSimilartext(scores.get(1));
						nCh.setSimitextword(scores.get(2));
						nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e)));
					} else {
						if (!nCh.isA()) {
							Dnode dnm = treem.getNode(nCh.getNodenumberA());
							Node nm = dnm.refDomNode;
							Element em = (Element) nm;
							nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(em)));

						} else {
							Dnode dn = tree.getNode(nCh.getNodenumberA());
							Node n = dn.refDomNode;
							Element e = (Element) n;
							nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e)));
						}
						nnbobj = nCh.getAtB();
					}
					if (nCh.isA()) {
						nnbpobj = nCh.getNodenumberA();
					}
					if (noeudobj == -1 || noeudobj > nCh.getNodenumberA()) {
						if (nCh.isA()) {
							noeudobj = nCh.getNodenumberA();
						} else {
							noeudobj = nCh.getNodenumberA();
						}}}}}
		Dnode dn = null;
		int noeudsec = -1;
		NodeChanged nSec = new NodeChanged(noeudsec);
		if (noeudobj != -1) {
			dn = tree.getNode(noeudobj);
			noeudsec = dn.getPosFather();
			nSec = new NodeChanged(noeudsec);
			nSec.setA(true);

		} else if (noeudobjB != -1) {
			dn = treem.getNode(noeudobjB);
			noeudsec = dn.getPosFather();
			nSec = new NodeChanged(noeudsec);
			nSec.setA(false);
		}
		nSec.setNodetype("sec");
		nSec.setId("sec-type=\"display-objects\"");
		if (nnbobj != null || nnbpobj != null) {
			if (noeudobj != tree.numNode) {
				NodeChanged nc = new NodeChanged(noeudobj - 1);
				noeudobj = dn.getPosFather();
				nc.setModified("Modified: " + Integer.toString(compmodifobj));
				int childinit = XmlFileAttributes.numChild(noeudobj, bd.getOrignal(), specobj, bd.getTreeorig());
				nc.setInit("Initial: " + Integer.toString(childinit));
				nc.getChangelist();
				if (nnbpobj != null) {
					Dnode dnB = treem.getNode(nnbpobj);
					int pos = dnB.getPosFather();
					int childfin = XmlFileAttributes.numChild(pos, bd.getModified(), specobj, bd.getTreemodif());
					nc.setFinall("Final: " + Integer.toString(childfin));
					nSec.setNodenumberB(pos);
				} else {
					nc.setFinall("Final: " + Integer.toString(childinit - compmodifobj));
					nSec.setNodenumberB(nnbobj);
				}
				Dnode dnodesec = tree.getNode(nSec.getNodenumberA());
				Dnode dnodemsec = treem.getNode(nSec.getNodenumberB());
				Element esec = (Element) dnodesec.refDomNode;
				Element esecm = (Element) dnodemsec.refDomNode;
				ArrayList<String> scores = sim.score(esec.getTextContent(), esecm.getTextContent(), jaccard, simitext,
						simtextW);
				nc.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
				nSec.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
				nSec.setJaccard(scores.get(0));
				nSec.setSimilartext(scores.get(1));
				nSec.setSimitextword(scores.get(2));
				nc.setNodetype(cgt);
				modif.add(nc);
			}
		}
		modif.add(nSec);
		return modif;
	}
	public ArrayList<NodeChanged> findTabFig(ArrayList<NodeChanged> modif, BrowseDelta bd, boolean jaccard,
			boolean simitext, boolean simtextW) throws InputFileException {
		modif = specObjtreatment(modif, bd, "fig", "figure", jaccard, simitext, simtextW);
		modif = specObjtreatment(modif, bd, "table-wrap", "table", jaccard, simitext, simtextW);
		return modif;
	}
}