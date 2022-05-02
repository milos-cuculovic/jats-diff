package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TabFigure {


	NodeParents np = new NodeParents();
	AtRectif at = new AtRectif();

	public ArrayList<NodeChanged> specObjtreatment(ArrayList<NodeChanged> modif, BrowseDelta bd, String specobj,
												   String cgt, Similarity sim) throws InputFileException, IOException {

		ArrayList<NodeChanged> delcit = new ArrayList<NodeChanged>();
		ArrayList<NodeChanged> tab = np.tabMaker(specobj, modif, delcit, bd);
		if (tab.size() == 0) {
			return modif;
		}
		Dtree treem = bd.getTreemodif();
		Dtree tree = bd.getTreeorig();
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
								}
							}
						}
					}
				}
			}
		}
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
										}
									}
								}
							}
						}
					}
				}
			}
		}
		for (NodeChanged d : del) {
			modif.remove(d);
		}
		for (NodeChanged nCh : tab) {
			modif.remove(nCh);
			TableChange tc = new TableChange();
			if (!nCh.isA()) {
				boolean isTo = false;
				del = new ArrayList<NodeChanged>();
				int nnb = nCh.getNodenumberA();
				Dnode noeud = treem.getNode(nnb);
				int pa = noeud.getIndexKey();
				Integer da = null;
				Node n = noeud.refDomNode;
				Dnode noeudA = tree.getNode(0);
				if (n.getNodeName().equals(specobj)) {
					nCh.setNodetype(specobj);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) n;
						nCh.setId(e.getAttribute("id"));
					}
					ChangeObject co = nCh.getChangelist().get(0);
					Dnode noeudB = treem.getNode(Integer.parseInt(co.getNodenumB()));
					noeudA = tree.getNode(at.atcalcul(Integer.parseInt(co.getAtB()), bd,  true));
					nCh.setAtB(Integer.parseInt(co.getAtB()));
					nCh.setAtA(noeudA.indexKey);
					isTo = true;
				} else {
					while (!n.getNodeName().equals(specobj)) {
						if (n.getNodeName().equals("caption") || n.getNodeName().equals("table")
								|| n.getNodeName().equals("table-wrap-foot")) {
							ChangeObject co = nCh.getChangelist().get(0);
							Dnode noeudB = treem.getNode(Integer.parseInt(co.getAtB()));
							noeudA = tree.getNode(at.atcalcul(Integer.parseInt(co.getAtB()), bd,  true));
							nCh.setAtB(noeud.getIndexKey());
							nCh.setAtA(noeudA.getIndexKey());
							da = noeudA.indexKey;
							noeudB = treem.getNode(pa);
							if (n.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) n;
								if (noeudA.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eA = (Element) noeudA.refDomNode;
									tc = (TableChange) sim.score(e.getTextContent(), eA.getTextContent(),tc);
									tc.setName(n.getNodeName());
									pa = noeud.posFather;
									noeud = treem.getNode(pa);
									da = noeudA.posFather;
									noeudA = tree.getNode(da);
									n = noeud.refDomNode;
								}
							}
							break;
						}
						pa = noeud.posFather;
						noeud = treem.getNode(pa);
						n = noeud.refDomNode;
					}

				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					nCh.setId(e.getAttribute("id"));
				}
				nCh.setNodenumberB(pa);
				nCh.setNodenumberA(pa);
				nCh.setNodetype(n.getNodeName());}
				int nodenumA;
				if (da != null) {
					nCh.setA(true);
					nCh.setNodenumberA(da);
					nodenumA = da;
				} else {
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
					if (cO.hasTableChange()) {
						ArrayList<TableChange> aT = cO.getTablechange();
						TableChange finalTc = tc;
						if (!(aT.stream().anyMatch(o -> o.getName() == finalTc.getName()))) {
							aT.add(tc);
							cO.setTablechange(aT);
						}
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
					if (cgt.equals("ref")) {
						if (isTo) {
							cO.setChangement("insert");
							cO.setAtA(Integer.toString(nCh.getAtA()));
							cO.setAtB(Integer.toString(nCh.getAtB()));
						} else {
							cO.setChangement("ref-edit");
							cO.setNodenumA(Integer.toString(nCh.getNodenumberA()));
						}
						cO.setTo(isTo);
					} else if (tc.getName() != null) {
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
				boolean isFrom = false;
				int nna = nCh.getNodenumberA();
				Dnode noeud = tree.getNode(nna);
				Node n = noeud.refDomNode;
				int da = noeud.getIndexKey();
				int count = noeud.getIndexKey();
				Integer pa = null;
				ChangeObject co = nCh.getChangelist().get(0);
				Dnode noeudB = treem.getNode(0);
				if (co.hasNodenumB()) {
					noeudB = treem.getNode(Integer.parseInt(co.getNodenumB()));
					nCh.setAtB(at.atcalcul(nna, bd,  false));
					nCh.setAtA(noeud.posFather);
					noeudB = treem.getNode(noeudB.posFather);
				} else {
					noeudB = treem.getNode(at.atcalcul(nna, bd,  false));;
					nCh.setAtB(noeudB.posFather);
					nCh.setAtA(Integer.parseInt(co.getAtA()));
				}
				if (n.getNodeName().equals(specobj)) {
					isFrom = true;
					nCh.setNodetype(specobj);
					if (n.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) n;
						nCh.setId(e.getAttribute("id"));
					}
				} else {
					while (!n.getNodeName().equals(specobj)) {
						if (n.getNodeName().equals("caption") || n.getNodeName().equals("table")
								|| n.getNodeName().equals("table-wrap-foot")) {
							Dnode noeudA = tree.getNode(nCh.getAtA());
							noeudB = treem.getNode(at.atcalcul(nCh.getAtA(), bd,  false));
							da = noeudA.indexKey;
							pa = noeudB.indexKey;
							da = count;
							noeudA = tree.getNode(da);
							nCh.setAtB(noeudB.posFather);
							nCh.setAtA(noeud.posFather);
							if (n.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) n;

								if (noeudB.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eB = (Element) noeudB.refDomNode;
									tc = (TableChange) sim.score(e.getTextContent(), eB.getTextContent(),tc);
									tc.setName(n.getNodeName());


								}
							}
							da = noeudA.getPosFather();
							n = tree.getNode(da).refDomNode;
							pa = noeudB.getPosFather();
							noeudB = treem.getNode(pa);
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
					nCh.setNodenumberB(pa);
					nCh.setAtB(noe.posFather);
				}
				nCh.setNodenumberA(da);
				Dnode dna = treem.getNode(da);
				nCh.setAtA(dna.posFather);
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
					if (!n.getNodeName().equals("ref")) {
						TableChange finalTc1 = tc;
						if (!(aT.stream().anyMatch(o -> o.getName() == finalTc1.getName()))) {
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
					if (cgt.equals("ref")) {
						if (isFrom) {
							cO.setChangement("delete");
							cO.setAtA(Integer.toString(nCh.getAtA()));
							cO.setAtB(Integer.toString(nCh.getAtB()));
						} else {
							cO.setChangement("ref-edit");
							cO.setNodenumA(Integer.toString(nCh.getNodenumberA()));
						}
						cO.setFrom(isFrom);
					} else if (tc.getName() != null) {
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
				}
			}
		}
		for (NodeChanged d : del) {
			modif.remove(d);
		}
		if (specobj.equals("table-wrap") || specobj.equals("fig")) {
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
							nCh = (NodeChanged) sim.score(e.getTextContent(), em.getTextContent(), nCh);
							nCh.setAtA(dn.getPosFather());
							nCh.setAtB(dnm.getPosFather());
							nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e)));
							nCh.setDepth(Integer.toString(XmlFileAttributes.getDepth(e)));
							nnbpobj = nCh.getAtB();
						} else {
							if (!nCh.isA()) {
								nnbpobj = nCh.getAtB();
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
						if (noeudobj == -1 || noeudobj > nCh.getNodenumberA()) {
							if (!nCh.isA()) {
								noeudobjB = nCh.getNodenumberA();
							} else {
								noeudobj = nCh.getNodenumberA();
							}
						}
					}
				}
			}
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
				if (nnbobj != null) {
					NodeChanged nc = new NodeChanged(noeudobj - 1);
					noeudobj = dn.getPosFather();
					nc.setModified("Modified: " + Integer.toString(compmodifobj));
					int childinit = XmlFileAttributes.numChild(noeudobj, bd.getOrignal(), specobj, bd.getTreeorig());
					nc.setInit("Initial: " + Integer.toString(childinit));
					nc.getChangelist();
					if (nnbpobj != null) {
						int childfin = XmlFileAttributes.numChild(nnbpobj, bd.getModified(), specobj, bd.getTreemodif());
						nc.setFinall("Final: " + Integer.toString(childfin));
						nSec.setNodenumberB(nnbpobj);
					} else {
						nc.setFinall("Final: " + Integer.toString(childinit - compmodifobj));
						nSec.setNodenumberB(nnbobj);
					}
					Dnode dnodesec = tree.getNode(nSec.getNodenumberA());
					Dnode dnodemsec = treem.getNode(nSec.getNodenumberB());
					Element esec = (Element) dnodesec.refDomNode;
					Element esecm = (Element) dnodemsec.refDomNode;
					nc = (NodeChanged) sim.score(esec.getTextContent(), esecm.getTextContent(),nc);
					nc.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
					nSec.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
					nc.setNodetype(cgt);
					nc.setNodenumberA(noeudobj);
					nc.setNodenumberB(nnbpobj);
					modif.add(nc);
				} else if (nnbpobj != null) {
					NodeChanged nc = new NodeChanged(noeudsec + 1);
					noeudobj = dn.getPosFather();
					nc.setModified("Modified: " + Integer.toString(compmodifobj));
					int childinit = XmlFileAttributes.numChild(noeudobj, bd.getOrignal(), specobj, bd.getTreeorig());
					nc.setInit("Initial: " + Integer.toString(childinit));
					nc.getChangelist();
					int childfin = XmlFileAttributes.numChild(nnbpobj, bd.getModified(), specobj, bd.getTreemodif());
					nc.setFinall("Final: " + Integer.toString(childfin));
					nSec.setNodenumberB(nnbpobj);

					Dnode dnodesec = tree.getNode(nSec.getNodenumberA());
					Dnode dnodemsec = treem.getNode(nSec.getNodenumberB());
					Element esec = (Element) dnodesec.refDomNode;
					Element esecm = (Element) dnodemsec.refDomNode;
					nc = (NodeChanged) sim.score(esec.getTextContent(), esecm.getTextContent(),nc);
					nc.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
					nSec.setDepth(Integer.toString(XmlFileAttributes.getDepth(esec)));
					nc.setNodetype(cgt);
					nc.setNodenumberA(noeudobj);
					nc.setNodenumberB(nnbpobj);
					modif.add(nc);
				}

			}
			int nna = nSec.getNodenumberA();
			if (!(modif.stream().anyMatch(o -> o.getNodenumberA() == nna))) {
				modif.add(nSec);
			}
		}
//		else {
//			References ref = new References();
//			modif = ref.findRef(modif, bd, jaccard, simitext, simtextW, specobj);
//		}
		return modif;
	}

	public ArrayList<NodeChanged> findTabFig(ArrayList<NodeChanged> modif, BrowseDelta bd, Similarity sim) throws InputFileException, IOException {
		modif = specObjtreatment(modif, bd, "fig", "figure", sim);
		modif = specObjtreatment(modif, bd, "table-wrap", "table", sim);
		return modif;
	}
}