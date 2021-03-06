package main.semantics_L3;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class XmlFileAttributes {

	public static int getDepth(Element element) {
		Node parent = element.getParentNode();
		int depth = 0;

		while (parent != null && parent.getNodeType() == Node.ELEMENT_NODE) {
			depth++;
			parent = parent.getParentNode();
		}

		return depth;
	}
	public static int numChild(int nodeNumber, String original, String nodename, Dtree tree) throws InputFileException {
		Dnode dnoeud = tree.getNode(nodeNumber);
		NodeList listen = dnoeud.refDomNode.getChildNodes();
		int compteur = 0;
		for (int i = 0; i < listen.getLength(); i++) {
			Node n = listen.item(i);
			if (n.getNodeName().equals(nodename)) {
				compteur++;
			}
		}
		return compteur;

	}
	public ArrayList<NodeChanged> addChanging(ArrayList<NodeChanged> modif, BrowseDelta bd, Similarity sim) throws InputFileException, IOException {
		// modif est de la forme [key nodenumber et value [cgt,fromto]]
		Dtree treeorig = bd.getTreeorig();
		Dtree treemodif = bd.getTreemodif();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {
				if (nCh.getNodetype().equals("table-wrap") || nCh.getNodetype().equals("table")
						|| nCh.getNodetype().equals("fig") || nCh.getNodetype().equals("figures")) {
					continue;
				}
			}
			if (nCh.hasPourcentage()) {
				continue;

			}
			int nbmodif = 0;
			if (nCh.hasChangelist()) {
				for (ChangeObject cOb : nCh.getChangelist()) {
					if (cOb.getChangement().equals("move") || cOb.getChangement().equals("delete")
							|| cOb.getChangement().equals("upgrade") || cOb.getChangement().equals("downgrade")
							|| cOb.getChangement().equals("insert") || cOb.getChangement().equals("merge")
							|| cOb.getChangement().equals("split") || cOb.getChangement().equals("ref-insert")
							|| cOb.getChangement().equals("ref-update") || cOb.getChangement().equals("ref-delete")) {
						nbmodif += 1;
					}
				}
				if (nCh.getChangelist().size() > 0) {
					if (nbmodif == nCh.getChangelist().size()) {
						continue;
					}

				}
			}
			NodeChanged nCh1 = nCh;

//			if (key.equals(""))
//				continue;
			int nodenumber = nCh.getNodenumberA();
			int nodenumberB = 0;
			if (nCh.hasNodenumberB()) {
				nodenumberB = nCh.getNodenumberB();
			}
			// On prend le noeud du xml origine et on le met dans la liste changemnet
			if (nodenumber == -1)
				continue;
			Dnode dnodeorig = treeorig.getNode(nodenumber);
			Node noeud = dnodeorig.refDomNode;

			// **
			if (noeud.getNodeType() == Node.ELEMENT_NODE) {
				Element eElementorg = (Element) noeud;
				// Element eElementorg = (Element) noeud;

				// On prend le noeud de modif et on le met dans la liste changemnet
				Dnode dnodemodif = treemodif.getNode(nodenumberB);
				Node noeudmodif = dnodemodif.refDomNode;

				// **
				// Node eElementmodif = noeudmodif;
				if (noeudmodif.getNodeType() == Node.ELEMENT_NODE) {

					String txt = nodeSpace(noeud).replaceAll("( )+", " ");
					String txtmodif = nodeSpace(noeudmodif).replaceAll("( )+", " ");
					nCh = (NodeChanged) sim.score(txt.trim(), txtmodif.trim(),nCh);
					// Element eElementorg = (Element) noeud;
					nCh.setDepth(Integer.toString(getDepth(eElementorg)));
					// on ajoute cette liste a notre hashmap modif
					modif.set(modif.indexOf(nCh1), nCh);
				}
			}

		}
		return modif;

	}


	public String nodeSpace(Node noeud) {
		if (noeud.getNodeType() == Node.ELEMENT_NODE) {
			Element eElementmodif1 = (Element) noeud;
			String textcontent1 = eElementmodif1.getTextContent();
			if (noeud.hasChildNodes()) {
				NodeList corglist = noeud.getChildNodes();
				String txt = "";
				for (int i = 0; i < corglist.getLength(); i++) {
					if (corglist.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) corglist.item(i);
						txt += e.getTextContent();
					}
				}
				if (!textcontent1.equals(txt) && textcontent1.contains(txt)) {

					for (int i = 0; i < corglist.getLength(); i++) {
						textcontent1.replace(txt, " " + nodeSpace(corglist.item(i)));
					}
				} else if (textcontent1.equals(txt)) {

					textcontent1 = "";
					for (int i = 0; i < corglist.getLength(); i++) {
						textcontent1 += nodeSpace(corglist.item(i));
					}
				}
			}
			textcontent1 = textcontent1.replaceAll("( )+", " ");

			return textcontent1 + " ";
		}
		return "";
	}
	public ArrayList<NodeChanged> propSimilarity(ArrayList<NodeChanged> modif, BrowseDelta bd, Similarity sim) throws InputFileException, IOException {

		Dtree treeorig = bd.getTreeorig();
		Dtree treemodif = bd.getTreemodif();
		ArrayList<NodeChanged> modif1 = new ArrayList<NodeChanged>();
		for (NodeChanged nc : modif) {
			if (nc.hasNodeT()) {

				if (nc.getNodetype().equals("table-wrap") || nc.getNodetype().equals("table")
						|| nc.getNodetype().equals("fig") || nc.getNodetype().equals("figure")) {
					continue;
				}
			}
			modif1.add(nc);

		}
		for (NodeChanged nC : modif1) {

			if (nC.hasPourcentage()) {
				Dnode dnoeud = treeorig.getNode(nC.getNodenumberA());
				Node noeudorig = dnoeud.refDomNode;
				double size1 = 0;
				if (noeudorig.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) noeudorig;
					size1 = el.getTextContent().length();
				}
				while (noeudorig.getParentNode().getParentNode() != null) {
					int pap = dnoeud.getPosFather();
					dnoeud = treeorig.getNode(pap);
					noeudorig = dnoeud.refDomNode;
					if ((modif1.stream().anyMatch(o -> o.getNodenumberA() == pap))) {
						break;

					}

					if (!(modif.stream().anyMatch(o -> o.getNodenumberA() == pap))) {
						NodeChanged nCh = new NodeChanged(pap);
						if (noeudorig.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) noeudorig;
							if (e.hasAttribute("id")) {
								nCh.setId(e.getAttribute("id"));
							}
							if (e.getFirstChild().getNodeName() == "title") {
								if (noeudorig.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
									Element elt = (Element) noeudorig.getFirstChild();
									nCh.setTitle(elt.getTextContent());
								}
							}
							double size2 = e.getTextContent().length();
							if (nC.getJaccard()!= null) {
								double jaccard = nC.getJaccard();
								jaccard = 1 - ((size1 / size2) * (1 - (jaccard / 100)));
								jaccard = (double) Math.round(jaccard * 1000) / 10;
								nCh.setJaccard(Math.abs(jaccard));
							}
							if (nC.getSimilartext()!=null) {
								double simi = nC.getSimilartext();
								simi = 1 - ((size1 / size2) * (1 - (simi / 100)));
								simi = (double) Math.round(simi * 1000) / 10;
								nCh.setSimilartext(Math.abs(simi));
							}
							if (nC.getSimitextword()!=null) {

								double simW = nC.getSimitextword();
								simW = 1 - ((size1 / size2) * (1 - (simW / 100)));
								simW = (double) Math.round(simW * 1000) / 10;
								nCh.setSimitextword(Math.abs(simW));

							}
							if (nC.getTf()!=null) {
								double tf =nC.getTf();
								tf = 1 - ((size1 / size2) * (1 - (tf / 100)));
								tf = (double) Math.round(tf * 1000) / 10;
								nCh.setTf(Math.abs(tf));
							}
							if(nC.hasTopicmodel()){
								double tm = nC.getTopicModel();
								tm = 1 - ((size1 / size2) * (1 - (tm / 100)));
								tm = (double) Math.round(tm * 1000) / 10;
								nCh.setTopicModel(Math.abs(tm));

							}
							nCh.setNodetype(e.getNodeName());
							nCh.setDepth(Integer.toString(getDepth(e)));

							modif.add(nCh);

						}

					} else if (modif.stream().anyMatch(o -> o.getNodenumberA() == pap)
							&& !(modif1.stream().anyMatch(o -> o.getNodenumberA() == pap))) {
						NodeChanged noch = modif.stream().filter(a -> a.getNodenumberA() == pap)
								.collect(Collectors.toList()).get(0);
						if (noch.hasPourcentage()) {
							modif.remove(noch);
							if (noeudorig.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) noeudorig;
								if (e.hasAttribute("id")) {
									noch.setId(e.getAttribute("id"));
								}
								if (e.getFirstChild().getNodeName() == "title") {
									if (noeudorig.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
										Element elt = (Element) noeudorig.getFirstChild();
										noch.setTitle(elt.getTextContent());
									}
								}
								noch.setDepth(Integer.toString(getDepth(e)));

								double size2 = e.getTextContent().length();
								if (nC.getJaccard()!=null) {
									double jaccard = nC.getJaccard();
									double jaccard1 = noch.getJaccard();
									jaccard = (jaccard1 / 100) - ((size1 / size2) * (1 - (jaccard / 100)));
									jaccard = (double) Math.round(jaccard * 1000) / 10;
									noch.setJaccard(Math.abs(jaccard));

								}
								if (nC.getSimilartext()!=null) {
									double simi = nC.getSimilartext();
									double sim1 = noch.getSimilartext();
									simi = (sim1 / 100) - ((size1 / size2) * (1 - (simi / 100)));
									simi = (double) Math.round(simi * 1000) / 10;
									noch.setSimilartext(Math.abs(simi));

								}
								if (nC.getSimitextword()!=null) {
									double simW = nC.getSimitextword();
									double simW1 = noch.getSimitextword();
									simW = (simW1 / 100) - ((size1 / size2) * (1 - (simW / 100)));
									simW = (double) Math.round(simW * 1000) / 10;
									noch.setSimitextword(Math.abs(simW));

								}
								if (nC.getTf()!=null) {
									double tf =nC.getTf();
									double tf1 = noch.getTf();
									tf = (tf1 / 100) - ((size1 / size2) * (1 - (tf / 100)));
									tf = (double) Math.round(tf * 1000) / 10;
									noch.setTf(Math.abs(tf));
								}

								modif.add(noch);

							}
						}
					}
				}
			} else if (!nC.hasPourcentage()) {
				if (nC.getChangelist().get(0).getChangement().contains("-update")
						|| nC.getChangelist().get(0).getChangement().contains("style")) {
					if (!nC.isA()) {
						nC.setNodenumberA(Integer.parseInt(nC.getChangelist().get(0).getNnBRef()));
					}
					Dnode dnoeud = treeorig.getNode(nC.getNodenumberA());
					Node noeudorig = dnoeud.refDomNode;
					int pap1 = dnoeud.indexKey;
					while (noeudorig.getNodeName() != "article") {
						int vrai = dnoeud.indexKey;
						dnoeud = treeorig.getNode(vrai);
						noeudorig = dnoeud.refDomNode;
						if ((modif1.stream().anyMatch(o -> o.getNodenumberA() == vrai))) {
							if (vrai != pap1) {
								break;
							}

						}
						if (!(modif.stream().anyMatch(o -> o.getNodenumberA() == vrai))) {
							NodeChanged nCh = new NodeChanged(vrai);
							if (noeudorig.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) noeudorig;
								if (e.hasAttribute("id")) {
									nCh.setId(e.getAttribute("id"));
								}
								if (e.getFirstChild().getNodeName() == "title") {
									if (noeudorig.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
										Element elt = (Element) noeudorig.getFirstChild();
										nCh.setTitle(elt.getTextContent());
									}
								}
								nCh.setNodetype(noeudorig.getNodeName());
								nCh.setDepth(Integer.toString(getDepth(e)));
								modif.add(nCh);
							}

						} else {
							NodeChanged noch = modif.stream().filter(a -> a.getNodenumberA() == vrai)
									.collect(Collectors.toList()).get(0);
							modif.remove(noch);
							if (noeudorig.getNodeType() == Node.ELEMENT_NODE) {
								Element e = (Element) noeudorig;
								noch.setDepth(Integer.toString(getDepth(e)));
								if (e.hasAttribute("id")) {
									noch.setId(e.getAttribute("id"));
								}
								if (e.getFirstChild().getNodeName() == "title") {
									if (noeudorig.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
										Element elt = (Element) noeudorig.getFirstChild();
										noch.setTitle(elt.getTextContent());
									}
								}
								modif.add(noch);

							}
						}

						int pap = dnoeud.getPosFather();
						dnoeud = treeorig.getNode(pap);
						noeudorig = dnoeud.refDomNode;
					}

				}
				continue;
			}

		}
		return modif;

	}

	public ArrayList<NodeChanged> affichageChanging(ArrayList<NodeChanged> modif, BrowseDelta bd)
			throws InputFileException {
		Dtree treeorig = bd.getTreeorig();
		Dtree treemodif = bd.getTreemodif();
		for (NodeChanged nCh : modif) {
			if (nCh.hasNodeT()) {

				if (nCh.getNodetype().equals("table-wrap") || nCh.getNodetype().equals("table")
						|| nCh.getNodetype().equals("fig") || nCh.getNodetype().equals("figures")) {
					continue;
				}
			}
			if (nCh.hasChangelist()) {
				ChangeObject cOb = nCh.getChangelist().get(0);
				if (cOb.hasOp() || cOb.getChangement().equals("insert")) {
					if (cOb.isTo()) {
						Dnode dnodemodif = treemodif.getNode(nCh.getNodenumberA());
						Node noeud = dnodemodif.refDomNode;
						if (noeud.getNodeType() == Node.ELEMENT_NODE) {
							Element eElementmodif = (Element) noeud;
							String name = eElementmodif.getNodeName();
							if (eElementmodif.hasAttribute("id")) {
								nCh.setId(eElementmodif.getAttribute("id"));
							}
							if (eElementmodif.getFirstChild().getNodeName() == "title") {
								if (noeud.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
									Element elt = (Element) noeud.getFirstChild();
									nCh.setTitle(elt.getTextContent());
								}
							}
							nCh.setNodetype(name);
						}
						continue;
					}
				}
				Dnode dnodeorig = treeorig.getNode(nCh.getNodenumberA());
				Node noeud = dnodeorig.refDomNode;
				if (noeud.getNodeType() == Node.ELEMENT_NODE) {
					Element eElementorg = (Element) noeud;
					String name = eElementorg.getNodeName();
					if (eElementorg.hasAttribute("id")) {
						nCh.setId(eElementorg.getAttribute("id"));
					}
					if (eElementorg.getFirstChild().getNodeName() == "title") {
						if (noeud.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
							Element elt = (Element) noeud.getFirstChild();
							nCh.setTitle(elt.getTextContent());
						}
					}
					nCh.setNodetype(name);
				}
			}
		}
		return modif;
	}


}
