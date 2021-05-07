package main.semantics_L3;

import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Vector;


public class AtRectif {
    public Dnode atcalcul(Dtree treea, Dtree treeb, String cond, String specobj, Dnode da, Dnode db, BrowseDelta bd,
                          String pathb) throws InputFileException {
        Dnode dspec = da;
        while (!dspec.refDomNode.getNodeName().equals(specobj)) {
            dspec = treea.getNode(dspec.getPosFather());
        }
        Node nspec = dspec.refDomNode;
        if (nspec.getNodeType() == Node.ELEMENT_NODE) {
            Element espec = (Element) nspec;
            if (espec.hasAttribute("id")) {
                String id = espec.getAttribute("id");
                Vector<Dnode> nl = treeb.nodeList;
                Dnode dbfather = db;
                for (Dnode dn : nl) {
                    if (dn.refDomNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) dn.refDomNode;
                        if (e.hasAttribute("id")) {
                            if (e.getAttribute("id").equals(id)) {
                                dbfather = dn;
                                if(cond.equals(specobj)) {
                                    return dbfather;
                                }
                                break;
                            }
                        }
                    }
                }
                int nc = bd.nodecounter(dbfather.getIndexKey(), pathb, treeb);
                int min = dbfather.indexKey;
                int max = min + nc;
                for (Dnode dn : nl) {
                    if (dn.indexKey > min && dn.indexKey < max) {
                        if (dn.refDomNode.getNodeName().equals(cond)) {
                            db = dn;
                            break;
                        }
                    }
                }
            }
            else {
                Vector<Dnode> nl = treeb.nodeList;
                for (Dnode dn : nl) {
                    if(dn.refDomNode.getNodeName().equals(specobj)) {
                        return dn;
                    }
                }
            }
        }

        return db;

    }
}

