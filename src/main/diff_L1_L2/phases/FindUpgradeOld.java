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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.relation.Fragment;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei documenti
 */
public class FindUpgradeOld extends Phase {

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
    public FindUpgradeOld(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            logger.info("START FIND UPGRADE");

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            findPossibleUpgradeNodes();
            logger.info("END");
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            throw new ComputePhaseException("Find Upgrade");
        }
    }

    protected void findPossibleUpgradeNodes() {
        try {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
            SF.StartFieldProcess(Field.LOCALITY);
            Field processField;
            List<Interval> listIntervalA = new ArrayList<>();
            List<Interval> listIntervalB = new ArrayList<>();

            while ((processField = SF.nextField()) != null) {
                findA.set(1, 0);
                findB.set(1, 0);
//                if (A.getNode(processField.xRef.inf).inRel == Relation.MERGE) {
//                    //protect if already merge
//                    continue;
//                }
                String valueA = "";
                String valueAId = "";
                Interval newAInterval = null;
                Interval newBInterval = null;
                String parentNodeNameA = "";
                int ApostFatherTmp = 0;
//                logger.info("A  k " + processField.xRef.show() + "-------->" + B.getNode(processField.yRef.inf).getRefDomNode().getNodeName());
//                logger.info("B  k" + processField.yRef.show() + "-------->" + B.getNode(processField.yRef.inf).getRefDomNode().getNodeName());
                List<Node> diffA = new ArrayList<>();
                int postFatherIndexA = A.getNode(processField.xRef.inf).posFather;
                int postFatherIndexALength = A.getNode(processField.xRef.inf).posFather + A.getNode(A.getNode(processField.xRef.inf).posFather).getNumChildSubtree();
                for (int i = postFatherIndexA; i <= postFatherIndexALength; i++) {
                    int countExists = 0;
                    boolean existsSecId = true;

                    //becasue processField return parent not where is changes then iterate just for children
                    if (A.getNode(i).refDomNode.getNodeName().equals("sec")
                            && A.getNode(A.getNode(i).posFather).refDomNode.getNodeName().equals("sec")) {
                        String subSectionAContent = A.getNode(i).refDomNode.getTextContent();
                        String regExpA = "^(\\d+\\.\\d+\\.\\s)(.*)";
                        Pattern pA = Pattern.compile(regExpA);
                        Matcher mA = pA.matcher(subSectionAContent);
                        subSectionAContent = mA.replaceAll("$2");

                        existsSecId = false;

                        if (!existsSecId) {
                            int postFatherRepeatIndexB = B.getNode(processField.yRef.inf).posFather;
                            int postFatherRepeatIndexBLength = B.getNode(processField.yRef.inf).posFather + B.getNode(B.getNode(processField.yRef.inf).posFather).getNumChildSubtree();
//
                            //again iterate B but trough parent tree and try to find parent sec that contains same content as non existsin subsec from A
                            for (int repeatB = postFatherRepeatIndexB; repeatB <= postFatherRepeatIndexBLength; repeatB++) {

                                if (B.getNode(repeatB).refDomNode.getNodeName().equals("sec")
                                        && !B.getNode(B.getNode(repeatB).posFather).refDomNode.getNodeName().equals("sec")) {

                                    String sectionBContent = B.getNode(repeatB).refDomNode.getTextContent();
                                    String regExp = "^(\\d+\\.\\s)(.*)";
                                    Pattern p = Pattern.compile(regExp);
                                    Matcher m = p.matcher(sectionBContent);
                                    sectionBContent = m.replaceAll("$2");

                                    if (subSectionAContent.equals(sectionBContent)) {
                                        newAInterval = new Interval(i, i);
                                        newBInterval = new Interval(repeatB, repeatB);
                                        findA.set(newAInterval);
                                        findB.set(newBInterval);

                                        R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.UPGRADE);
                                        listIntervalA.add(findA);
                                        listIntervalB.add(findB);
//                                        if ((findA.inf > processField.xRef.inf && A.getNode(processField.xRef.inf).refDomNode.getNodeName().equals("sec"))
//                                                || (findB.inf > processField.yRef.inf && B.getNode(processField.yRef.inf).refDomNode.getNodeName().equals("sec"))) {
//                                        Interval tmpIntervalA = new Interval(processField.xRef.inf, processField.xRef.inf);
//                                        Interval tmpIntervalB = new Interval(processField.yRef.inf, processField.yRef.inf);
//                                        R.addFragment(tmpIntervalA, tmpIntervalB, A.getNode(tmpIntervalA.inf).weight, Relation.NO);
//                                        SF.subField(tmpIntervalA, tmpIntervalB, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
//                                        }
                                        for (int l = findA.inf; l <= findA.inf + A.getNode(findA.inf).getNumChildSubtree(); l++) {
                                            A.getNode(l).inRel = Relation.UPGRADE;
                                        }

                                        for (int l = findB.inf; l <= findB.inf + B.getNode(findB.inf).getNumChildSubtree(); l++) {
                                            B.getNode(l).inRel = Relation.UPGRADE;
                                        }
//                                        if (findA.inf > 1 && findB.inf > 1 && findA.sup > 0 && findB.sup > 0 && findA.sup > findA.inf && findB.sup > findB.inf) {
//                                        if ((findA.inf == processField.xRef.inf && A.getNode(processField.xRef.inf).refDomNode.getNodeName().equals("sec"))
//                                                || findB.inf == processField.yRef.inf && B.getNode(processField.yRef.inf).refDomNode.getNodeName().equals("sec")) {
//                                            SF.subField(findA, findB, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
//
//                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }

            // remove fields from insert
            this.clearIntervals(listIntervalA, listIntervalB);
//            System.exit(0);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     *
     * @param listIntervalA
     * @param listIntervalB
     */
    protected void clearIntervals(List<Interval> listIntervalA, List<Interval> listIntervalB) {

        Vector<Interval> cod = SF.getIntervalsOnY();
        for (int c = 0; c < cod.size(); c++) {
            Interval interval = cod.get(c);
            if (listIntervalB.contains(interval)) {
                SF.subField(interval, interval, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
            }
        }
        Vector<Interval> dom = SF.getIntervalsOnX();
        for (int d = 0; d < dom.size(); d++) {
            Interval interval = dom.get(d);
            if (listIntervalA.contains(interval)) {
                SF.subField(interval, interval, Field.LOCALITY, Field.LOCALITY, Field.LOCALITY, Field.LOCALITY);
            }
        }
    }
}
