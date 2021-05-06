package main.semantics_L3.domxml;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Citables {
	public ArrayList<NodeChanged> setcitable(ArrayList<NodeChanged> modif) {
		ArrayList<NodeChanged> nchli = new ArrayList<NodeChanged>();
		ArrayList<NodeChanged> addnchli = new ArrayList<NodeChanged>();
		for (NodeChanged nCh : modif) {
			for (ChangeObject cObj : nCh.getChangelist()) {
				if (cObj.getChangement().equals("delete") || cObj.getChangement().equals("insert")
						|| cObj.getChangement().equals("text-update")) {
					if (cObj.isXref() && cObj.getChangement().equals("text-update")) {
						String citable = cObj.getCitable();
						if (modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(cObj.getNodenumA()))) {
							NodeChanged nCh1 = modif.stream()
									.filter(a -> a.getNodenumberA() == Integer.parseInt(cObj.getNodenumA()))
									.collect(Collectors.toList()).get(0);

							if (!nchli.contains(nCh1)) {
								int compteur = 0;
								for (ChangeObject co : nCh1.getChangelist()) {
									if (co.isXref()) {
										compteur++;
									}
								}
								if (compteur == 1) {
									String op = "";
									if (cObj.getOp().equals("text-deleted")) {
										op = ("delete");
									} else if (cObj.getOp().equals("text-inserted")) {
										op = ("insert");
									}
									NodeChanged nch = replaceCitable("-update", citable, cObj.getNodenumB(),
											cObj.getNodenumA(), cObj, nCh, op, false);
									nchli.add(nCh);
									addnchli.add(nch);
								} else {
									ChangeObject co = nCh1.getChangelist().get(0);
									for (ChangeObject c : nCh1.getChangelist()) {
										if (c.hasPos()) {
											int diff = Math.abs(
													Integer.parseInt(cObj.getPos()) - Integer.parseInt(c.getPos()));
											int diff1 = Math.abs(
													Integer.parseInt(cObj.getPos()) - Integer.parseInt(co.getPos()));
											if (diff1 >= diff) {
												co = c;
											}
										}
									}
									if (cObj != co) {
										NodeChanged nch1 = replaceCitable("-update", citable, cObj.getNodenumB(),
												cObj.getNodenumA(), cObj, nCh, cObj.getOp(), false);
										NodeChanged nch = replaceCitable("-update", citable, cObj.getNodenumB(),
												cObj.getNodenumA(), co, nch1, cObj.getOp(), false);
										nchli.add(nCh);
										nchli.add(nCh1);
										addnchli.add(nch);
										break;

									}
								}
							}
						}
					} else if (cObj.isXref() && cObj.getChangement().equals("insert")) {
						String citable = cObj.getCitable();
						List<NodeChanged> isAlist = modif.stream().filter(a -> !a.isA()).collect(Collectors.toList());
						if (isAlist.stream()
								.anyMatch(o -> o.getNodenumberA() == Integer.parseInt(cObj.getNodenumB()))) {
							NodeChanged nCh1 = modif.stream()
									.filter(a -> a.getNodenumberA() == Integer.parseInt(cObj.getNodenumB()))
									.collect(Collectors.toList()).get(0);
							if (!nchli.contains(nCh1)) {
								for (ChangeObject co : nCh1.getChangelist()) {
									if (co.isXref()) {
										NodeChanged nch = replaceCitable("-update", citable, cObj.getNodenumB(),
												cObj.getNnBRef(), cObj, nCh, "text-edit", true);
										nchli.add(nCh);
										nchli.add(nCh1);
										addnchli.add(nch);
										break;
									} // insert pure
									else {
										NodeChanged nch = replaceCitable("-insert", citable, cObj.getNodenumB(),
												cObj.getNnBRef(), cObj, nCh, "text-edit", true);
										nchli.add(nCh);
										nchli.add(nCh1);
										addnchli.add(nch);
										break;
									}
								}
							}
						} else {
							NodeChanged nch = replaceCitable("-insert", citable, cObj.getNodenumB(), cObj.getNnBRef(),
									cObj, nCh, "text-edit", true);
							nchli.add(nCh);
							addnchli.add(nch);
						}
					}
					// si c est delete
					else if (cObj.isXref() && cObj.getChangement().equals("delete")) {
						String citable = cObj.getCitable();
						ChangeObject cotxt = new ChangeObject();
						List<NodeChanged> isAlist = modif.stream().filter(a -> !a.isA()).collect(Collectors.toList());
						if (isAlist.stream()
								.anyMatch(o -> o.getNodenumberB() == Integer.parseInt(cObj.getNodenumA()))) {
							NodeChanged nCh1 = modif.stream()
									.filter(a -> a.getNodenumberB() == Integer.parseInt(cObj.getNnARef()))
									.collect(Collectors.toList()).get(0);
							if (!nchli.contains(nCh1)) {
								for (ChangeObject co : nCh1.getChangelist()) {
									// cela veut dire que c est une ref update

									if (co.isXref()) {
										NodeChanged nch = replaceCitable("-update", citable, cObj.getNnARef(),
												cObj.getNodenumB(), cObj, nCh, "text-edit", true);
										nchli.add(nCh);
										nchli.add(nCh1);
										addnchli.add(nch);
										break;
									} else {
										NodeChanged nch = replaceCitable("-delete", citable, cObj.getNnARef(),
												cObj.getNodenumB(), cObj, nCh, "text-edit", true);
										// juste del
										nchli.add(nCh);
										nchli.add(nCh1);
										addnchli.add(nch);
										break;
									}
								}
							}
						} else {
							NodeChanged nch = replaceCitable("-delete", citable, cObj.getNnARef(), cObj.getNodenumB(),
									cObj, nCh, "text-edit", true);
							nchli.add(nCh);
							addnchli.add(nch);
						}
					}
				}
			}
		}
		for (NodeChanged nCh : nchli) {
			modif.remove(nCh);
		}
		for (NodeChanged nCh : addnchli) {
			if (nCh.getChangelist().size() > 0) {
				modif.add(nCh);
			}
		}
		return modif;
	}

	public NodeChanged replaceCitable(String cgt, String citable, String nnB, String nnA, ChangeObject cObj,
			NodeChanged nCh, String op, boolean hasAt) {
		ChangeObject cotxt = new ChangeObject();
		cotxt.setChangement(citable + cgt);
		cotxt.setXref(true);
		cotxt.setLabSec(cObj.getLabSec());
		cotxt.setNodenumA(nnA);
		cotxt.setTextContent(cObj.getTextContent());
		cotxt.setNodenumB(nnB);
		cotxt.setCitable(citable);
		cotxt.setOp(op);
		if (hasAt) {
			cotxt.setAtA(cObj.getAtA());
			cotxt.setAtB(cObj.getAtB());
		}
		cotxt.setLabel(cObj.getLabel());
		NodeChanged nch = new NodeChanged(Integer.parseInt(nnA));
		nch.setChangelist(new ArrayList<ChangeObject>());
		if (hasAt) {
			nch.setAtA(Integer.parseInt(cotxt.getAtA()));
			nch.setAtB(Integer.parseInt(cotxt.getAtB()));
		}
		nch.getChangelist().addAll(nCh.getChangelist());
		nch.getChangelist().remove(cObj);
		nch.setNodenumberB(Integer.parseInt(nnB));
		nch.getChangelist().add(cotxt);
		return nch;
	}

	public ArrayList<NodeChanged> labSecfilt(ArrayList<NodeChanged> modif) {
		for (NodeChanged nCh : modif) {
			// to delete something in the for list
			int del = 0;
			int size = nCh.getChangelist().size();
			for (int i = 0; i < size; i++) {
				if (nCh.getChangelist().get(i - del).hasLabSec()) {
					String label = nCh.getChangelist().get(i - del).getLabSec();
					for (NodeChanged nCh1 : modif) {
						if (nCh1.hasLabelsec()) {
							if (label.contains(nCh1.getLabelSec()) || nCh1.getLabelSec().contains(label)) {
								if (nCh1.hasChangelist()) {
									if (nCh1.getChangelist().get(0).getChangement().equals("insert")) {
										continue;
									}
								}
								nCh.getChangelist().remove(nCh.getChangelist().get(i - del));
								del++;
							}
						}
					}
				}
			}
		}
		int size = modif.size();
		int del = 0;
		for (int i = 0; i < size; i++) {
			NodeChanged nCh = modif.get(i - del);
			if (nCh.getChangelist().size() == 0) {
				modif.remove(nCh);
				del++;
			}
		}
		return modif;
	}

	public ArrayList<NodeChanged> parasiteWord(ArrayList<NodeChanged> modif) {
		for (NodeChanged nCh : modif) {
			int size = nCh.getChangelist().size();
			if (nCh.getChangelist().size() != 1) {
				for (int i = 0; i < nCh.getChangelist().size(); i++) {
					ChangeObject co = nCh.getChangelist().get(i);
					if (co.isXref()) {
						String word = co.getTextContent();
						if (word.contains("-|xref")) {
							word = word.split("-|xref")[0];
							for (ChangeObject cO : nCh.getChangelist()) {
								if (cO.getChangement().equals("text-update")) {
									if (word.contains(cO.getTextContent())) {
										nCh.getChangelist().remove(cO);
										break;
									}
								}
							}
						}
						if (word.contains("-|/xref|-")) {
							word = word.split("-|/xref|-")[1];
							for (ChangeObject cO : nCh.getChangelist()) {
								if (cO.getChangement().equals("text-update")) {
									if (word.contains(cO.getTextContent())) {
										nCh.getChangelist().remove(cO);
										break;
									}
								}
							}
						}
					}
					if (size != nCh.getChangelist().size()) {
						break;
					}
				}
			}
		}
		return modif;
	}

	public ArrayList<NodeChanged> moveCitable(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
		Dtree tree = bd.getTreeorig();
		Dtree treem = bd.getTreemodif();
		HashMap<String, ArrayList<NodeChanged>> cit = new HashMap<String, ArrayList<NodeChanged>>();
		for (NodeChanged nch : modif) {
			for (ChangeObject co : nch.getChangelist()) {
				if (co.hasMove()) {
					if (cit.containsKey(co.getMove())) {
						cit.get(co.getMove()).add(nch);
						break;
					} else if (co.getMove() != null) {
						ArrayList<NodeChanged> anch = new ArrayList<>();
						anch.add(nch);
						cit.put(co.getMove(), anch);
					}
				}
			}
		}
		for (String key : cit.keySet()) {
			ArrayList<NodeChanged> anch = cit.get(key);
			for (NodeChanged nCh : anch) {
				modif.remove(nCh);
				Pattern pattern = Pattern.compile("\\d+");
				Matcher matcher = pattern.matcher(key);
				matcher.find();
				Dnode dn = tree.getNode(Integer.parseInt(matcher.group()));
				Node n = dn.refDomNode;
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;

					matcher.find();
					Dnode dnm = treem.getNode(Integer.parseInt(matcher.group()));
					Node nm = dnm.refDomNode;
					if (nm.getNodeType() == Node.ELEMENT_NODE) {
						Element em = (Element) nm;
						if (e.hasAttribute("id")) {
							nCh.setValueCitable(difference(e.getAttribute("id"), em.getAttribute("id")));
						}
					}
				}
				modif.add(nCh);
			}
		}
		return modif;
	}

	public ArrayList<NodeChanged> negQuote(ArrayList<NodeChanged> modif) throws InputFileException {
		ArrayList<NodeChanged> del = new ArrayList<NodeChanged>();
		ArrayList<NodeChanged> addi = new ArrayList<NodeChanged>();
		for (NodeChanged nCh : modif) {
			ArrayList<ChangeObject> coLis = new ArrayList<ChangeObject>();

			ArrayList<ChangeObject> secLis = new ArrayList<ChangeObject>();
			for (ChangeObject co : nCh.getChangelist()) {
				if (co.getChangement().equals("text-update")) {
					coLis.add(co);
				}
			}
			for (ChangeObject co : nCh.getChangelist()) {
				if (co.getChangement().equals("sec-update")) {
					secLis.add(co);
				}
			}
			String cgt = "";
			if (nCh.getChangelist().get(0).getChangement().equals("update-attribute")) {
				cgt = difference(nCh.getChangelist().get(0).getNewvalue(), nCh.getChangelist().get(0).getOldvalue());
			}
			if (coLis.size() == 2) {
				if (coLis.get(0).getOp().contains("insert")) {
					if (coLis.get(1).getOp().contains("delete")) {
						cgt = difference(coLis.get(0).getTextContent(), coLis.get(1).getTextContent());
					}
				}
				if (coLis.get(1).getOp().contains("insert")) {
					if (coLis.get(0).getOp().contains("delete")) {
						cgt = difference(coLis.get(1).getTextContent(), coLis.get(0).getTextContent());
					}
				}
			} else if (coLis.size() == 1) {
				if (coLis.get(0).getOp().contains("insert")) {
					cgt = "insert" + coLis.get(0).getTextContent();
				}

				if (coLis.get(0).getOp().contains("delete")) {
					cgt = "delete" + coLis.get(0).getTextContent();
				}
			}
			if (secLis.size() == 2) {
				if (secLis.get(0).getOp().contains("insert")) {
					if (secLis.get(1).getOp().contains("delete")) {
						cgt = difference(secLis.get(0).getTextContent(), secLis.get(1).getTextContent());
					}
				}
				if (secLis.get(1).getOp().contains("insert")) {
					if (secLis.get(0).getOp().contains("delete")) {
						cgt = difference(secLis.get(1).getTextContent(), secLis.get(0).getTextContent());
					}
				}
			} else if (secLis.size() == 1) {
				if (secLis.get(0).getOp().contains("insert")) {
					cgt = "insert" + secLis.get(0).getTextContent();
				}

				if (secLis.get(0).getOp().contains("delete")) {
					cgt = "delete" + secLis.get(0).getTextContent();
				}
			}
			if(secLis.isEmpty()) {
				continue;
			}
			if(coLis.isEmpty()) {
				continue;
			}
			for (int i = 0; i < modif.size(); i++) {
				NodeChanged nCh1 = modif.get(i);
				if (nCh1.hasCitVal()) {
					if (cgt.equals(nCh1.getValueCitable())) {
						NodeChanged nch = new NodeChanged(nCh.getNodenumberA());
						if (nCh.isA()) {
							nCh.setA(true);
						} else {
							nCh.setA(false);
						}
						nch.setChangelist(new ArrayList<ChangeObject>());
						nch.getChangelist().addAll(nCh.getChangelist());
						nch.getChangelist().removeAll(coLis);
						nch.getChangelist().removeAll(secLis);
						del.add(nCh);
						if (coLis.size() != 0) {
							addi.add(nch);
						} else if (secLis.size() != 0) {
							addi.add(nch);
						}
					}
				}
			}
		}
		for (NodeChanged d : del) {
			modif.remove(d);
		}
		for (NodeChanged a : addi) {
			if (a.getChangelist().size() > 0) {
				modif.add(a);
			}
		}

		return modif;
	}

	public String difference(String del, String ins) {
		String old = "";
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(del);
		Pattern pattern1 = Pattern.compile("\\d+");
		Matcher matcher1 = pattern1.matcher(ins);
		while (matcher.find()) {
			if (matcher1.find()) {
				if (!matcher.group().equals(matcher1.group())) {
					old += "echange" + matcher.group() + matcher1.group();
				}
			} else {
				old += "delete" + matcher.group();
			}
		}
		if (matcher1.find()) {
			old += "insert" + matcher1.group();
		}
		return old;
	}
}
