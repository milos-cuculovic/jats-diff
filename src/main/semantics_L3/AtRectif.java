package main.semantics_L3;

import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Vector;

//at.atcalcul(nCh.getAtB(), bd,  nCh.getChangelist().get(0).isTo())
public class AtRectif {
    public int at_calcul(int oldat, BrowseDelta bd,boolean isto) throws InputFileException {
        int atA =oldat;
        int actualNodeNumberDiff = 0;
        int newat = oldat;
        for (ChangeObject tempco : bd.getChangeList()) {
            if (tempco.hasNodecount()) {
                if (tempco.getChangement().equals("delete")) {
                    actualNodeNumberDiff += Integer.parseInt(tempco.getNodecount());


                } else if (tempco.hasOp()) {
                    if (tempco.getOp().contains("To")) {
                        actualNodeNumberDiff -= Integer.parseInt(tempco.getNodecount());

                    } else if (tempco.getOp().contains("From")) {
                        actualNodeNumberDiff += Integer.parseInt(tempco.getNodecount());

                    }
                } else {// insert
                    actualNodeNumberDiff -= Integer.parseInt(tempco.getNodecount());
                }
                if (isto) {
                    if (oldat> Integer.parseInt(tempco.getAtB())) {
                        newat += actualNodeNumberDiff;

                    }
                }
                if (!isto) {
                    if (oldat > Integer.parseInt(tempco.getAtA())) {
                        newat -= actualNodeNumberDiff;
                    }
                }else {// insert
                    actualNodeNumberDiff -= Integer.parseInt(tempco.getNodecount());
                }
                }

            actualNodeNumberDiff = 0;
            }
        return newat;
    }

}


