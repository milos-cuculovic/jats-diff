package main.semantics_L3.domxml;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrowseDelta {
	private Document doc;
	private String original;
	private String modified;
	private Dtree treemodif;
	private Dtree treeorig;
	private ArrayList<ChangeObject> changeList = new ArrayList<ChangeObject>();

	public BrowseDelta(Document doc, String original, String modified) throws InputFileException {
		this.doc = doc;
		this.original = original;
		this.modified = modified;
		this.setChangeList(initializeList(doc, original, modified));
		gatherFROMTO();
	}

	private ArrayList<ChangeObject> initializeList(Document doc2, String original, String modified)
			throws InputFileException {
		this.treeorig=new Dtree(original, true, true, true, true, true);
		this.treemodif = new Dtree(modified, true, true, true, true, true);
		NodeList nod = this.doc.getElementsByTagName("ndiff");
		NodeList nList = nod.item(0).getChildNodes();
		ArrayList<ChangeObject> coList = new ArrayList<ChangeObject>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node node = nList.item(temp);
			ChangeObject change = new ChangeObject();
			change.setChangement(node.getNodeName());
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				if (node.getChildNodes().getLength() == 1) {
					if (node.getFirstChild().getNodeName().equals("label")) {
						continue;
					}
				}

				if (node.getChildNodes().getLength() == 1
						&& !(node.getChildNodes().item(0).getNodeType() == Node.ELEMENT_NODE)) {
					if (eElement.getTextContent().contains("-|xref") || eElement.getTextContent().contains("|/xref|")) {
						change.setXref(true);
					} else {
						change.setXref(false);
					}
				} else {
					change.setXref(false);
				}
				if ((eElement.getNodeName().equals("delete") && eElement.getTextContent().trim().isEmpty())
						|| (eElement.getNodeName().equals("insert") && eElement.getTextContent().trim().isEmpty())) {
					continue;
				}
				else {
					change.setTextContent(eElement.getTextContent());
					if (eElement.hasAttribute("nodenumberA")) {
						change.setNodenumA(eElement.getAttribute("nodenumberA"));
						change.setNnARef(eElement.getAttribute("nodenumberA"));

					}
					if (eElement.hasAttribute("nodenumberB")) {
						change.setNodenumB(eElement.getAttribute("nodenumberB"));
						change.setNnBRef(eElement.getAttribute("nodenumberB"));
					}
					if (eElement.hasAttribute("oldvalue")) {
						change.setOldvalue(eElement.getAttribute("oldvalue"));
					}
					if (eElement.hasAttribute("move")) {
						change.setMove(eElement.getAttribute("move"));
					}
					if (eElement.hasAttribute("newvalue")) {
						change.setNewvalue(eElement.getAttribute("newvalue"));
					}
					if (eElement.hasAttribute("op")) {
						change.setOp(eElement.getAttribute("op"));
					}
					if (((node.getNodeName().equals("upgrade") || node.getNodeName().equals("downgrade")
							|| node.getNodeName().equals("merge") || node.getNodeName().equals("split")
							|| node.getNodeName().equals("move")) && eElement.getAttribute("op").contains("From"))
							|| node.getNodeName().equals("delete")) {
						
						change.setFrom(true);
						int nc = nodecounter(Integer.parseInt(change.getNodenumA()), original,treeorig);
						change.setNodecount(Integer.toString(nc));
						int nnA = Integer.parseInt(change.getNodenumA());
						Dnode dnodeorig = treeorig.getNode(nnA);
						int nodeparnum = dnodeorig.posFather;
						if(eElement.hasAttribute("nodecount")) {
							if(eElement.getAttribute("nodecount").equals(0)) {
								change.setNodenumA(Integer.toString(nodeparnum));
							}
						}
						nnA = Integer.parseInt(change.getNodenumA());
						dnodeorig = treeorig.getNode(nnA);
						nodeparnum = dnodeorig.posFather;
						change.setAtA(Integer.toString(nodeparnum));
						change.setAtB(Integer.toString(nodeparnum));
						change.setOldatB(Integer.toString(nodeparnum));
						if (change.hasNodenumB()) {
							change.setNodenumB(null);
						}

					}
					if (((node.getNodeName().equals("upgrade") || node.getNodeName().equals("downgrade")
							|| node.getNodeName().equals("merge") || node.getNodeName().equals("split")
							|| node.getNodeName().equals("move")) && eElement.getAttribute("op").contains("To"))
							|| node.getNodeName().equals("insert")) {
						change.setTo(true);
						int nc = nodecounter(Integer.parseInt(change.getNodenumB()), modified,treemodif);
						change.setNodecount(Integer.toString(nc));
						int nnB = Integer.parseInt(change.getNodenumB());
						Dnode dnodemodif = treemodif.getNode(nnB);
						int nodeparnum = dnodemodif.posFather;
						if(eElement.hasAttribute("nodecount")) {
							String s=eElement.getAttribute("nodecount");
							if(s.equals("0")) {
								change.setNodenumB(Integer.toString(nodeparnum));
							}
						}
						nnB = Integer.parseInt(change.getNodenumB());
						dnodemodif = treemodif.getNode(nnB);
						nodeparnum = dnodemodif.posFather;
						change.setAtB(Integer.toString(nodeparnum));
						change.setAtA(Integer.toString(nodeparnum));
						change.setOldatA(Integer.toString(nodeparnum));
						if (change.hasNodenumberA()) {
							change.setNodenumA(null);
						}
					}

					if (eElement.hasAttribute("pos")) {
						change.setPos(eElement.getAttribute("pos"));
					}

					if (eElement.hasAttribute("action")) {
						change.setOp(eElement.getAttribute("action"));
					}
					if (change.getChangement().equals("text-update") || change.getChangement().contains("style")) {
						if (change.hasNodenumB()) {
							Dnode noeudmodif = treemodif.getNode(Integer.parseInt(change.getNodenumB()));
							change.setNodenumB(Integer.toString(noeudmodif.getPosFather()));
							change.setNnBRef(change.getNodenumB());
						}
						if (change.hasNodenumberA()) {
							Dnode noeud = treeorig.getNode(Integer.parseInt(change.getNodenumA()));
							change.setNodenumA(Integer.toString(noeud.getPosFather()));
							change.setNnARef(change.getNodenumA());
						}

					}
				}
				if (node.hasChildNodes()) {
					if (node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
						Element eChild = (Element) node.getFirstChild();
						if (eChild.hasAttribute("id")) {
							change.setFirstChildid(eChild.getAttribute("id"));

							if (eChild.getNodeName().equals("ref")) {

								change.setRef(true);
							}
						}
					}
				}
				if (change.hasNodenumberA()) {
					change.setPrincNodenum(change.getNodenumA());
				} else {
					change.setPrincNodenum(change.getNodenumB());
				}
				coList.add(change);
			}
		}
		return coList;

	}

	public int nodecounter(int nodenum, String path,Dtree tree) throws InputFileException {
		Dnode dnoeud = tree.getNode(nodenum);
		int n = nodenum;
		Node noeud = dnoeud.refDomNode;
		Node bro = noeud.getNextSibling();
		while (bro == null && noeud.getParentNode() != null) {
			bro = noeud.getParentNode().getNextSibling();
			noeud = noeud.getParentNode();
		}
		if (noeud.getParentNode() != null) {
			while (tree.getNode(nodenum).refDomNode != bro) {

				nodenum++;
			}
		} else {
			Vector<Dnode> nlist = tree.nodeList;
			Dnode fdno = nlist.get(nlist.size() - 1);

			while (tree.getNode(nodenum) != fdno) {

				nodenum++;
			}
		}
		nodenum -= n;
		return nodenum;

	}
	public void gatherFROMTO() throws NumberFormatException, InputFileException {

		HashMap<String, ArrayList<ChangeObject>> fromMap = new HashMap<String, ArrayList<ChangeObject>>();
		HashMap<String, ArrayList<ChangeObject>> toMap = new HashMap<String, ArrayList<ChangeObject>>();
		for (ChangeObject co :changeList) {
			if (co.isFrom()) {
				if (!co.hasOp()) {
					co.setOp("");
				}
				String parentA = co.getAtA();
				if (fromMap.get(parentA) == null) {
					ArrayList<ChangeObject> lisCo = new ArrayList<ChangeObject>();
					lisCo.add(co);
					fromMap.put(parentA, lisCo);
				} else {
					ArrayList<ChangeObject> lisCo = fromMap.get(parentA);
					lisCo.add(co);
					fromMap.put(parentA, lisCo);
				}
			} else if (co.isTo()) {
				String parentB = co.getAtB();
				if (toMap.get(parentB) == null) {
					ArrayList<ChangeObject> lisCo = new ArrayList<ChangeObject>();
					lisCo.add(co);
					toMap.put(parentB, lisCo);
				} else {
					ArrayList<ChangeObject> lisCo = toMap.get(parentB);
					lisCo.add(co);
					toMap.put(parentB, lisCo);
				}
			}
		}
		for (String key : fromMap.keySet()) {
			Dnode dn = treeorig.getNode(Integer.parseInt(key));
			if (fromMap.get(key).size() > 1) {
				if (dn.refDomNode.getChildNodes().getLength() == fromMap.get(key).size()) {
					String cgt1 = "";
					String cgt2 = "";
					String move="";
					for (ChangeObject cotemp : fromMap.get(key)) {
						if (cgt1.equals("") || cgt1.equals(cotemp.getChangement())) {
							cgt1 = cotemp.getChangement();
						} else if (cgt2.equals("") || cgt2.equals(cotemp.getChangement())) {
							cgt2 = cotemp.getChangement();
						} else {
							break;
						}
						if(cotemp.getChangement().equals("move")) {
							move=cotemp.getMove();
							Pattern pattern = Pattern.compile("\\d+");
							Matcher matcher = pattern.matcher(move);
							matcher.find();
							int from=Integer.parseInt(matcher.group());
							matcher.find();
							int to=Integer.parseInt(matcher.group());
							int diff=Integer.parseInt(key)-from;
							to=to+diff;
							move=key+"::"+to;
						}
						if (fromMap.get(key).indexOf(cotemp) == (fromMap.get(key).size() - 1)) {
							if (cgt1.equals("delete") || cgt2.equals("delete")) {
								if (!cgt1.equals("") && !cgt2.equals("")) {
									ChangeObject copar = new ChangeObject();
									if(cgt2.equals("delete")) {
										cgt2=cgt1;
									}
									copar.setChangement(cgt2);
									copar.setNodenumA(key);
									copar.setNnARef(key);
									copar.setAtA(Integer.toString(dn.getPosFather()));
									copar.setAtB(Integer.toString(dn.getPosFather()));
									copar.setOldatB(Integer.toString(dn.getPosFather()));
									copar.setFrom(true);
									int nc = nodecounter(Integer.parseInt(copar.getNodenumA()),original,
											treeorig);
									copar.setNodecount(Integer.toString(nc));
									copar.setOp(cgt2 + "dFrom");
									if(move!="") {
										copar.setMove(move);
									}
									for (ChangeObject codel : fromMap.get(key)) {
										getChangeList().remove(codel);
									}
									getChangeList().add(copar);
								}
							} else if (!cgt1.equals("") && !cgt2.equals("")) {
								continue;
							} else {
								ChangeObject copar = new ChangeObject();
								copar.setChangement(cgt1);
								copar.setNodenumA(key);
								if(move!="") {
									copar.setMove(move);
								}
								copar.setNnARef(key);
								copar.setAtA(Integer.toString(dn.getPosFather()));
								copar.setAtB(Integer.toString(dn.getPosFather()));
								copar.setOldatB(Integer.toString(dn.getPosFather()));
								copar.setFrom(true);
								int nc = nodecounter(Integer.parseInt(copar.getNodenumA()), original,
										treeorig);
								copar.setNodecount(Integer.toString(nc));
								copar.setOp(cgt1 + "dFrom");
								for (ChangeObject codel : fromMap.get(key)) {
									changeList.remove(codel);
								}
								changeList.add(copar);

							}
						}
					}
				}
			}
		}
		for (String key : toMap.keySet()) {
			Dnode dnmod = treemodif.getNode(Integer.parseInt(key));
			if (toMap.get(key).size() > 1) {
				if (dnmod.refDomNode.getChildNodes().getLength() == toMap.get(key).size()) {
					String cgt1 = "";
					String cgt2 = "";
					String move="";
					for (ChangeObject cotemp : toMap.get(key)) {
						if (cgt1.equals("") || cgt1.equals(cotemp.getChangement())) {
							cgt1 = cotemp.getChangement();
						} else if (cgt2 == "" || cgt2 == cotemp.getChangement()) {
							cgt2 = cotemp.getChangement();
						} else if (cgt1 != cotemp.getChangement()) {
							break;
						}
						if(cotemp.getChangement().equals("move")) {
							move=cotemp.getMove();
							Pattern pattern = Pattern.compile("\\d+");
							Matcher matcher = pattern.matcher(move);
							matcher.find();
							int from=Integer.parseInt(matcher.group());
							matcher.find();
							int to=Integer.parseInt(matcher.group());
							int diff=Integer.parseInt(key)-to;
							from=from+diff;
							move=from+"::"+key;
						}
						if (toMap.get(key).indexOf(cotemp) == (toMap.get(key).size() - 1)) {
							if (cgt1.equals("insert") || cgt2.equals("insert")) {
								if (cgt2.equals("insert")) {
									cgt2 = cgt1;
									cgt1 = "insert";
								}
								if (cgt2 != "") {
									ChangeObject copar = new ChangeObject();
									copar.setChangement(cgt2);
									copar.setNodenumB(key);
									copar.setNnBRef(key);
									copar.setMove(move);
									copar.setAtA(Integer.toString(dnmod.getPosFather()));
									copar.setOldatA(Integer.toString(dnmod.getPosFather()));
									copar.setAtB(Integer.toString(dnmod.getPosFather()));
									copar.setTo(true);
									int nc = nodecounter(Integer.parseInt(copar.getNodenumB()), modified,
											treemodif);
									copar.setNodecount(Integer.toString(nc));
									copar.setOp(cgt2 + "dTo");
									for (ChangeObject codel : toMap.get(key)) {
										changeList.remove(codel);

									}
									changeList.add(copar);
								}
							} else if (!cgt1.equals("") && !cgt2.equals("")) {
								continue;
							} else {
								ChangeObject copar = new ChangeObject();
								copar.setChangement(cgt1);
								copar.setNodenumB(key);
								copar.setNnBRef(key);
								copar.setMove(move);
								copar.setAtA(Integer.toString(dnmod.getPosFather()));
								copar.setOldatA(Integer.toString(dnmod.getPosFather()));
								copar.setAtB(Integer.toString(dnmod.getPosFather()));
								copar.setTo(true);
								int nc = nodecounter(Integer.parseInt(copar.getNodenumB()), modified,
										treemodif);
								copar.setNodecount(Integer.toString(nc));
								copar.setOp(cgt1 + "dTo");
								for (ChangeObject codel : toMap.get(key)) {
									changeList.remove(codel);
								}
								getChangeList().add(copar);
							}
						}
					}
				}
			}
		}
	}

	public ArrayList<ChangeObject> getChangeList() {
		return changeList;
	}

	public void setChangeList(ArrayList<ChangeObject> changeList) {
		this.changeList = changeList;
	}

	public String getOrignal() {
		return original;
	}
	public Document getDoc() {
		return doc;
	}
	public void setOrignal(String orignal) {
		this.original = orignal;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public Dtree getTreemodif() {
		return treemodif;
	}

	public void setTreemodif(Dtree treemodif) {
		this.treemodif = treemodif;
	}

	public Dtree getTreeorig() {
		return treeorig;
	}

	public void setTreeorig(Dtree treeorig) {
		this.treeorig = treeorig;
	}

}
