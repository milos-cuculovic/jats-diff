/** ***************************************************************************************
 *
 *   This file is part of jats-diff project.
 *
 *   jats-diff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   jats-diff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **************************************************************************************** */
package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import java.util.Vector;
import java.util.HashMap;
import main.diff_L1_L2.phases.common.Match;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import java.util.Collections;
import java.util.Iterator;
import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.relation.Fragment;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei documenti
 */
public class FindDowngradeOld extends Phase {

    protected Node left, right;
    protected List<NodeVO> initA = new ArrayList<NodeVO>();
    protected List<NodeVO> initB = new ArrayList<NodeVO>();
    protected HashMap<String, NodesVO> nodesA = new HashMap<String, NodesVO>();
    protected HashMap<String, NodesVO> nodesB = new HashMap<String, NodesVO>();
    protected int parentA = 0;
    protected int parentB = 0;
    public static final String[] TAGS = new String[]{"p", "sec"};
    public boolean findSectionA = false;
    public boolean findSectionB = false;
    protected List<String> AInfinum = new ArrayList<>();
    protected List<String> BInfinum = new ArrayList<>();

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public FindDowngradeOld(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
            Nconfig cfg) {
        super(SearchField, Rel, Ta, Tb, cfg);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see ndiff.phases.Phase#compute()
     */
    @Override
    public void compute() throws ComputePhaseException {
        try {
            logger.info("START FIND DOWNGRADE");

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            findPossibleDowngeadeNodes();
            logger.info("END");
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            throw new ComputePhaseException("Find Downgrade");
        }
    }

    protected void findPossibleDowngeadeNodes() {
        try {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
            SF.StartFieldProcess(Field.LOCALITY);
            Field processField;

            while ((processField = SF.nextField()) != null) {
                findA.set(1, 0);
                findB.set(1, 0);

                String valueA = "";
                String valueAId = "";
                Interval newAInterval = null;
                Interval newBInterval = null;
                String parentNodeNameA = "";

                List<Node> diffA = new ArrayList<>();

                int postFatherIndexB = B.getNode(processField.yRef.inf).posFather;
                int postFatherIndexBLength = B.getNode(processField.yRef.inf).posFather + B.getNode(B.getNode(processField.yRef.inf).posFather).getNumChildSubtree();
                for (int i = postFatherIndexB; i <= postFatherIndexBLength; i++) {
                    int countExists = 0;
                    boolean existsSecId = true;

                    //becasue processField return parent not where is changes then iterate just for children
                    if (B.getNode(i).refDomNode.getNodeName().equals("sec")
                            && A.getNode(B.getNode(i).posFather).refDomNode.getNodeName().equals("sec")) {
                        //logger.info("BB1: " + B.getNode(i).getRefDomNode().getAttributes().getNamedItem("id")); //debug
                        String subSectionBContent = B.getNode(i).refDomNode.getTextContent();
                        String regExpA = "^(\\d+\\.\\d+\\.\\s)(.*)";
                        Pattern pA = Pattern.compile(regExpA);
                        Matcher mA = pA.matcher(subSectionBContent);
                        subSectionBContent = mA.replaceAll("$2");

//                        //this part found which node from A not exists in B
                        int postFatherIndexA = A.getNode(processField.xRef.inf).posFather;
                        int postFatherIndexALength = A.getNode(processField.xRef.inf).posFather + A.getNode(A.getNode(processField.xRef.inf).posFather).getNumChildSubtree();
                        for (int j = postFatherIndexA; j <= postFatherIndexALength; j++) {
                            if (A.getNode(j).refDomNode.getNodeName().equals("sec")
                                    && !A.getNode(A.getNode(j).posFather).refDomNode.getNodeName().equals("sec")) {
                                Node nodeTmpA = A.getNode(j).getRefDomNode();
                                Node nodeTmpB = B.getNode(i).getRefDomNode();
                                logger.info("AA1: " + A.getNode(j).getRefDomNode().getAttributes().getNamedItem("id") + "node ind" + j);
                                if (nodeTmpA.isEqualNode(nodeTmpB)) {
                                    countExists++;

                                }
                            }
                        }
                        if (countExists > 0) {
                            existsSecId = true;
                        } else {
                            existsSecId = false;
                        }
                        if (!existsSecId) {
                            int postFatherRepeatIndexA = A.getNode(processField.xRef.inf).posFather;
                            int postFatherRepeatALength = A.getNode(processField.xRef.inf).posFather + A.getNode(A.getNode(processField.xRef.inf).posFather).getNumChildSubtree();
                            //again iterate B but trough parent tree and try to find parent sec that contains same content as non existsin subsec from A
                            for (int repeatA = postFatherRepeatIndexA; repeatA <= postFatherRepeatALength; repeatA++) {

                                if (A.getNode(repeatA).refDomNode.getNodeName().equals("sec")
                                        && !A.getNode(A.getNode(repeatA).posFather).refDomNode.getNodeName().equals("sec")) {
                                    String sectionAContent = A.getNode(repeatA).refDomNode.getTextContent();
                                    String regExp = "^(\\d+\\.\\s)(.*)";
                                    Pattern p = Pattern.compile(regExp);
                                    Matcher m = p.matcher(sectionAContent);
                                    sectionAContent = m.replaceAll("$2");

                                    if (sectionAContent.equals(subSectionBContent)) {

                                        newAInterval = new Interval(repeatA, repeatA);
                                        newBInterval = new Interval(i, i);

                                        findA.set(newAInterval);
                                        findB.set(newBInterval);

                                        R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.DOWNGRADE);

                                        if ((findA.inf > processField.xRef.inf && A.getNode(processField.xRef.inf).refDomNode.getNodeName().equals("sec"))
                                                || (findB.inf > processField.yRef.inf && B.getNode(processField.yRef.inf).refDomNode.getNodeName().equals("sec"))) {
                                            Interval tmpIntervalA = new Interval(processField.xRef.inf, processField.xRef.inf);
                                            Interval tmpIntervalB = new Interval(processField.yRef.inf, processField.yRef.inf);
                                            R.addFragment(tmpIntervalA, tmpIntervalB, A.getNode(tmpIntervalA.inf).weight, Relation.NO);
                                            SF.subField(tmpIntervalA, tmpIntervalB, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
                                        }

                                        for (int l = findA.inf; l <= findA.inf + A.getNode(findA.inf).getNumChildSubtree(); l++) {
                                            A.getNode(l).inRel = Relation.DOWNGRADE;
                                        }

                                        for (int l = findB.inf; l <= findB.inf + B.getNode(findB.inf).getNumChildSubtree(); l++) {
                                            B.getNode(l).inRel = Relation.DOWNGRADE;
                                        }

                                        if ((findA.inf == processField.xRef.inf && A.getNode(processField.xRef.inf).refDomNode.getNodeName().equals("sec"))
                                                || findB.inf == processField.yRef.inf && B.getNode(processField.yRef.inf).refDomNode.getNodeName().equals("sec")) {

                                            SF.subField(findA, findB, Field.LOCALITY, Field.LOCALITY, Field.LOCALITY, Field.LOCALITY);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        }

    }

}
