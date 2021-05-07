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
import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import org.apache.commons.lang.time.StopWatch;
import main.diff_L1_L2.relation.Fragment;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.Cosine;
//import org.apache.commons.text.similarity.CosineDistance;
//import org.apache.commons.text.similarity.JaccardDistance;
//import org.apache.commons.text.similarity.JaroWinklerDistance;
//import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 * documenti
 */
public class FindMerge extends Phase {

    protected List<NodeVO> initA = new ArrayList<NodeVO>();
    protected List<NodeVO> initB = new ArrayList<NodeVO>();
    protected int parentA = 0;
    protected int parentB = 0;
    protected static final String[] TAGS = {"p", "sec"};

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public FindMerge(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("START FIND MERGE");
            //swich delte/insert findMerge or merge similar old way
//            findMerge();
            merge();

            logger.info("END");
            stopWatch.stop();
            logger.error("Merge : " + stopWatch.toString());
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    protected void merge() {

        Field processField;
        SF.StartFieldProcess(Field.LOCALITY);
        List<Interval> listIntervalA = new ArrayList<>();
        List<Interval> listIntervalB = new ArrayList<>();
        while ((processField = SF.nextField()) != null) {
            for (int i = processField.yRef.inf; i <= processField.yRef.sup; i++) {
                List<Dnode> listPossibleMerge = new ArrayList<>();

                // Only for p and sec tags
                if (ArrayUtils.indexOf(TAGS, B.getNode(i).getRefDomNode().getNodeName()) > -1) {
//                    logger.info("name: " + B.getNode(i).getRefDomNode().getNodeName() + " " + B.getNode(i).getRefDomNode().getTextContent());
                    int parentNode = B.getNode(i).getPosFather();
                    String content = B.getNode(i).getRefDomNode().getTextContent().trim();
                    //potencial merge
                    for (Dnode a : A.nodeList) {

                        if (B.getNode(i).getRefDomNode().getNodeName().equals(a.getRefDomNode().getNodeName())
                                && !B.getNode(i).getRefDomNode().getTextContent().equals(a.getRefDomNode().getTextContent())
                                && B.getNode(i).posFather.equals(a.posFather)) {

                            if (content.contains(a.getRefDomNode().getTextContent())) {
                                listPossibleMerge.add(a);
                            } else {
                                Jaccard jw = new Jaccard();
                                // Check if there is a function that evaluates the conteinence ration of one string in another
                                if (a.getPosFather() == parentNode && jw.similarity(content, a.getRefDomNode().getTextContent()) >= 0.45) {
                                    listPossibleMerge.add(a);
                                }
                            }
                        }

                    }

                    if (listPossibleMerge.size() > 1) {
                        String aText = "";
                        for (Dnode n : listPossibleMerge) {
                            String t = n.getRefDomNode().getTextContent().trim();
                            aText = aText.concat(t);
                        }
                        Jaccard jw = new Jaccard();

                        if (jw.similarity(content, aText) >= 0.90 || content.equals(aText)) {

                            Interval findB = new Interval(B.getNode(i).getIndexKey(), B.getNode(i).getIndexKey());
                            for (Dnode n : listPossibleMerge) {
                                Interval findA = new Interval(n.getIndexKey(), n.getIndexKey());
                                R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.MERGE);
                                listIntervalA.add(findA);
                                listIntervalB.add(findB);
                                for (int l = findA.inf; l <= findA.sup; l++) {
                                    A.getNode(l).inRel = Relation.MERGE;
                                }
                                for (int l = findB.inf; l <= findB.sup; l++) {
                                    B.getNode(l).inRel = Relation.MERGE;
                                }
//                                SF.subField(findA, findB, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);

                            }

                        }

                    }
                }

            }
            this.clearIntervals(listIntervalA, listIntervalB);
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
                SF.subField(interval, interval, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
            }
        }
    }

    protected void findMerge() {
        List<Dnode> foundNodeInsert;
        List<Dnode> foundNodeDelete;
        foundNodeDelete = populateDeleteNodes();
        foundNodeInsert = populateInsertNodes();

        findAndCalculateMerge(foundNodeDelete, foundNodeInsert);
    }

    protected void findAndCalculateMerge(List<Dnode> foundNodeDelete, List<Dnode> foundNodeInsert) {
        for (Dnode insert : foundNodeInsert) {
            List<Dnode> deletedNodes = new ArrayList<>();
            for (Dnode delete : foundNodeDelete) {
                if (A.getNode(insert.getPosFather()).getRefDomNode().getNodeName().equals(B.getNode(delete.getPosFather()).getRefDomNode().getNodeName())
                        && insert.getRefDomNode().getTextContent().contains(delete.getRefDomNode().getTextContent())
                        && insert.getRefDomNode().getTextContent().length() > delete.getRefDomNode().getTextContent().length()) {
                    deletedNodes.add(delete);
                }
            }

            if (!deletedNodes.isEmpty()) {
                parse(insert, deletedNodes);
            }

        }
    }

    /**
     *
     * @param delete
     * @param insertedNodes
     */
    protected void parse(Dnode insert, List<Dnode> deletedNodes) {
        String separator = " ";
        String insertText = insert.getRefDomNode().getTextContent();
        Interval findB = new Interval(insert.getIndexKey(), insert.getIndexKey());
        List<String> insertTextList = new ArrayList<String>(Arrays.asList(insertText.split(" ")));
        Collections.sort(insertTextList);

        String deleteText = "";
        for (Dnode delete : deletedNodes) {
            deleteText = deleteText.concat(delete.getRefDomNode().getTextContent());
            deleteText = deleteText.concat(separator);
        }
        deleteText = deleteText.trim();
        List<String> deleteTextList = new ArrayList<String>(Arrays.asList(deleteText.split(" ")));
        Collections.sort(deleteTextList);
        if (insertTextList.equals(deleteTextList)) {

            for (Dnode delete : deletedNodes) {
                Interval findA = new Interval(delete.getIndexKey(), delete.getIndexKey());
                R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.MERGE);
                for (int l = findA.inf; l <= findA.sup; l++) {
                    A.getNode(l).inRel = Relation.MERGE;
                }
                for (int l = findB.inf; l <= findB.sup; l++) {
                    B.getNode(l).inRel = Relation.MERGE;
                }
            }
        }

    }

    /**
     *
     * @return
     */
    protected List<Dnode> populateDeleteNodes() {

        List<Dnode> foundNodeDelete = new ArrayList<>();
        Vector<Interval> dom = SF.getIntervalsOnX();
        Interval toProcess;
        for (int i = 0; i < dom.size(); i++) {
            toProcess = dom.get(i);
            for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                if (ArrayUtils.indexOf(TAGS, A.getNode(k).refDomNode.getNodeName()) > -1) {
                    foundNodeDelete.add(A.getNode(k));
                }
            }
        }

        return foundNodeDelete;

    }

    protected List<Dnode> populateInsertNodes() {

        List<Dnode> foundNodeInsert = new ArrayList<>();
        Interval toProcess;
        // Text nodes existing on B and not existing on A are inserted nodes
        Vector< Interval> cod = SF.getIntervalsOnY();
        for (int i = 0; i < cod.size(); i++) {
            toProcess = cod.get(i);
            for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                if (ArrayUtils.indexOf(TAGS, B.getNode(k).refDomNode.getNodeName()) > -1) {
                    foundNodeInsert.add(B.getNode(k));
                }
            }
        }

        return foundNodeInsert;
    }

}
