package main.semantics_L3;

import main.diff_L1_L2.exceptions.InputFileException;

import java.util.*;
import java.util.stream.Collectors;

public class TextChange {
    Similarity sim = new Similarity();

    public ArrayList<NodeChanged> objupdate(ArrayList<NodeChanged> modif) {
        modif = update(modif, "text-update");
        modif = update(modif, "ref-update");
        modif = update(modif, "sec-update");
        modif = update(modif, "figure-update");
        modif = update(modif, "table-update");
        return modif;

    }

    public ArrayList<NodeChanged> update(ArrayList<NodeChanged> modif, String changeName) {
        for (NodeChanged nCh : modif) {
            if (nCh.hasChangelist()) {
                int i = 0;
                for (ChangeObject cO : nCh.getChangelist()) {
                    if (cO.getChangement().equals(changeName)) {
                        i++;
                    }
                }
                if (i > 1) {
                    ChangeObject co = new ChangeObject();
                    int size = nCh.getChangelist().size();
                    int del = 0;
                    for (int k = 0; k < size; k++) {
                        ChangeObject cO = nCh.getChangelist().get(k - del);
                        if (cO.getChangement().equals(changeName)) {
                            cO.setNodenumA(co.getNodenumA());
                            cO.setNodenumB(co.getNodenumB());
                            co.setChangement(changeName);
                            nCh.getChangelist().remove(cO);
                            del++;
                        }
                    }
                    nCh.getChangelist().add(co);
                }
            }
        }
        return modif;
    }

    public ArrayList<NodeChanged> movetext1(ArrayList<NodeChanged> modif, BrowseDelta bd) throws InputFileException {
        // listtext key: listword value :
        HashMap<ArrayList<String>, ArrayList<ArrayList<String>>> listtext = new HashMap<ArrayList<String>, ArrayList<ArrayList<String>>>();
        for (ChangeObject co : bd.getChangeList()) {
            if (co.getChangement().equals("text-update") || co.getChangement().equals("delete")
                    || co.getChangement().equals("insert")) {
                if (co.hasText()) {
                    String texte = co.getTextContent();
                    texte = texte.replaceAll("\\p{Punct}", " $0 ");
                    ArrayList<String> listword = sim.listWords(texte);
                    // y verifie si on a deja cette clef ou non
                    int y = 0;
                    ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
                    Collections.sort(listword);
                    for (ArrayList<String> key : listtext.keySet()) {
                        if (sim.similarTextword1(listword, key) > 95 || listword.containsAll(key)||key.containsAll(listword)) {
                            y = 1;
                            info.addAll(listtext.get(key));
                            ArrayList<String> TextContent = new ArrayList<String>();
                            if (co.getChangement().equals("text-update")
                                    || co.getChangement().equals("text-style-update")) {
                                TextContent.add(co.getNodenumA());
                            } else {
                                if (co.hasNodenumberA()) {
                                    TextContent.add(co.getNodenumA());
                                } else {
                                    TextContent.add(co.getNodenumB());
                                }
                            }
                            if (co.hasOp()) {
                                TextContent.add(co.getOp());
                            } else {
                                if (co.getChangement().contains("delete")) {
                                    TextContent.add("text-deleted");
                                } else if (co.getChangement().contains("insert")) {
                                    TextContent.add("text-inserted");
                                }
                            }
                            TextContent.add(co.getChangement());
                            TextContent.add(texte);
                            info.add(TextContent);
                            if (key.size() >= listword.size()) {
                                listtext.put(key, info);
                                break;
                            }
                            if (key.size() < listword.size()) {
                                listtext.put(listword, info);
                                listtext.remove(key);
                                break;
                            }
                        }
                    }
                    // sortie for
                    if (y == 0) {
                        ArrayList<String> textContent = new ArrayList<String>();
                        if (co.getChangement().equals("text-update")) {
                            textContent.add(co.getNodenumA());
                        } else {
                            if (co.hasNodenumberA()) {
                                textContent.add(co.getNodenumA());
                            } else {
                                textContent.add(co.getNodenumB());
                            }
                        }
                        if (co.hasOp()) {
                            textContent.add(co.getOp());
                        } else {
                            if (co.getChangement().contains("delete")) {
                                textContent.add("text-deleted");
                            } else if (co.getChangement().contains("insert")) {
                                textContent.add("text-inserted");
                            }
                        }

                        textContent.add(co.getChangement());
                        textContent.add(texte);

                        info.add(textContent);
                        listtext.put(listword, info);
                    }
                }
            }
        }
        // retirer le delete insert inutile
        // on parcour listtext
        HashMap<ArrayList<String>, ArrayList<ArrayList<String>>> listtext2 = listtext;
        ArrayList<ArrayList<String>> remove = new ArrayList<ArrayList<String>>();

        for (ArrayList<String> lista : listtext.keySet()) {
            // parcour les listes d'un m�me changement

            for (int indexl = 0; indexl <= listtext.get(lista).size(); indexl++) {

                if (listtext.get(lista).size() > indexl) {
                    ArrayList<String> l = listtext.get(lista).get(indexl);
                    for (int indexl1 = indexl; indexl1 <= listtext.get(lista).size(); indexl1++) {
                        if (listtext.get(lista).size() > indexl1) {
                            ArrayList<String> l1 = listtext.get(lista).get(indexl1);
                            if (l != l1
                                    && (l.get(3).contains(l1.get(3)) || l1.get(3).contains(l.get(3)))) {
                                if (l.get(1) == "text-deleted") {
                                    if (listtext2.containsKey(lista)) {

                                        if (listtext2.get(lista).contains(l)) {
                                            String txt = listtext2.get(lista).get(indexl1).get(3).replace(l.get(3), "");
                                            listtext2.get(lista).get(indexl1).set(3, txt);
                                            listtext2.get(lista).remove(l);
                                            remove.add(l);

                                        }
                                    }
                                } else {
                                    if (listtext2.containsKey(lista)) {

                                        if (listtext2.get(lista).contains(l1)) {
                                            String txt = listtext2.get(lista).get(indexl).get(2).replace(l1.get(2),
                                                    "");
                                            listtext2.get(lista).get(indexl).set(2, txt);
                                            listtext2.get(lista).remove(l1);
                                            remove.add(l1);
                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

// maintenant que la liste est toute belle on met dans modif
        listtext = listtext2;

        for (
                ArrayList<String> key : listtext.keySet()) {
            // donc il ya 2modif du meme texte
            if (listtext.get(key).size() >= 2) {
                if (listtext.get(key).get(0).get(1).contains("text-deleted")
                        && listtext.get(key).get(1).get(1).contains("text-inserted")) {
                    String nA = listtext.get(key).get(0).get(0);
                    String nB = listtext.get(key).get(1).get(0);
                    if (!modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(nA))) {
                        continue;
                    }
                    NodeChanged nChA = modif.stream().filter(a -> a.getNodenumberA() == Integer.parseInt(nA))
                            .collect(Collectors.toList()).get(0);
                    for (ChangeObject cOb : nChA.getChangelist()) {
                        if (cOb.getChangement().equals("text-update")) {
                            cOb.setOp("text-move-from");
                            cOb.setNodenumB(nB);
                            break;
                        }
                    }
                    if (!modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(nA))) {
                        NodeChanged nChB = modif.stream().filter(a -> a.getNodenumberA() == Integer.parseInt(nB))
                                .collect(Collectors.toList()).get(0);

                        for (ChangeObject cOb : nChB.getChangelist()) {
                            if (cOb.getChangement().equals("text-update")) {
                                cOb.setOp("text-move-to");
                                cOb.setNodenumB(nA);
                                break;
                            }
                        }
                    }
                }
                if (listtext.get(key).get(1).get(1).contains("text-deleted")
                        && listtext.get(key).get(0).get(1).contains("text-inserted")) {
                    String nB = listtext.get(key).get(0).get(0);
                    String nA = listtext.get(key).get(1).get(0);
                    if (!modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(nA))) {
                        continue;
                    }
                    NodeChanged nChA = modif.stream().filter(a -> a.getNodenumberA() == Integer.parseInt(nA))
                            .collect(Collectors.toList()).get(0);
                    for (ChangeObject cOb : nChA.getChangelist()) {
                        if (cOb.getChangement().equals("text-update")) {
                            cOb.setOp("text-move-from");
                            cOb.setNodenumB(nB);//doute
                            break;
                        }
                    }
                    if (modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(nB))) {
                        List<NodeChanged> templist = modif.stream().filter(a -> a.getNodenumberA() == Integer.parseInt(nB))
                                .collect(Collectors.toList());
                        NodeChanged nChB = new NodeChanged(Integer.parseInt(nB));
                        for (NodeChanged nc:templist){
                            if (!nc.isA()){
                                nChB=nc;
                                for (ChangeObject cOb : nChB.getChangelist()) {
                                    if (cOb.getChangement().equals("text-update")) {
                                        cOb.setOp("text-move-to");
                                        cOb.setNodenumB(nA);
                                        break;
                                    }
                                    else if (cOb.getChangement().equals("insert")){
                                        cOb.setChangement("text-update");
                                        cOb.setOp("text-move-to");
                                        cOb.setNodenumB(nA);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    }
                }
                for (ArrayList<String> lista : listtext.get(key)) {
                    if (modif.stream().anyMatch(o -> o.getNodenumberA() == Integer.parseInt(lista.get(0)))) {
                        NodeChanged nCh = modif.stream()
                                .filter(a -> a.getNodenumberA() == Integer.parseInt(lista.get(0)))
                                .collect(Collectors.toList()).get(0);
                        for (ChangeObject cOb : nCh.getChangelist()) {
                            if (lista.get(2).equals(cOb.getChangement())) {
                                if (lista.get(2).equals("insert") || lista.get(1).equals("text-inserted")) {
                                    cOb.setChangement("text-update");
                                    cOb.setOp("text-move-to");
                                }
                                if (lista.get(2) == "delete" || lista.get(1) == "text-deleted") {
                                    cOb.setChangement("text-update");
                                    cOb.setOp("text-move-from");
                                }
                            }
                        }
                    }
                }
            }
        }
        for (
                ArrayList<String> r : remove) {
            System.out.println(r.get(0));
            NodeChanged nCh = modif.stream().filter(a -> a.getNodenumberA() == Integer.parseInt(r.get(0)))
                    .collect(Collectors.toList()).get(0);
            modif.remove(nCh);
            for (ChangeObject cOb : nCh.getChangelist()) {
                if (cOb.getChangement().equals("delete") || cOb.getOp().equals("text-delete")) {
                    nCh.getChangelist().remove(cOb);
                    break;
                }
            }
            if (nCh.getChangelist().size()!=0){
                modif.add(nCh);
            }
        }

        return modif;

}

}