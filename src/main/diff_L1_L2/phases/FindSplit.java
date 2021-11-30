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

import info.debatty.java.stringsimilarity.Jaccard;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.LSSerializer;
import java.util.Collections;
import main.diff_L1_L2.vdom.diffing.Dnode;
import org.apache.commons.lang.time.StopWatch;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 * documenti
 */
public class FindSplit extends Phase {

    protected List<NodeVO> initA = new ArrayList<NodeVO>();
    protected List<NodeVO> initB = new ArrayList<NodeVO>();
    protected int parentA = 0;
    protected int parentB = 0;

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public FindSplit(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            logger.info("START FIND SPLIT");

            findSplit();

            logger.info("END");
            stopWatch.stop();
            logger.error("Split : " + stopWatch.toString());

        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
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

    protected void findSplit() {
        List<Dnode> foundNodeInsert;
        List<Dnode> foundNodeDelete;
        foundNodeDelete = populateDeleteNodes();
        foundNodeInsert = populateInsertNodes();

        findAndCalculateSplit(foundNodeDelete, foundNodeInsert);
    }

    protected void findAndCalculateSplit(List<Dnode> foundNodeDelete, List<Dnode> foundNodeInsert) {
        for (Dnode delete : foundNodeDelete) {
            List<Dnode> insertedNodes = new ArrayList<>();
            for (Dnode insert : foundNodeInsert) {
                if (A.getNode(delete.getPosFather()).getRefDomNode().getNodeName().equals(B.getNode(insert.getPosFather()).getRefDomNode().getNodeName())
                        && delete.getRefDomNode().getTextContent().contains(insert.getRefDomNode().getTextContent())
                        && delete.getRefDomNode().getTextContent().length() > insert.getRefDomNode().getTextContent().length()) {
                    insertedNodes.add(insert);
                }
            }

            if (!insertedNodes.isEmpty()) {
                parse(delete, insertedNodes);
            }
        }
    }

    /**
     *
     * @param delete
     * @param insertedNodes
     */
    protected void parse(Dnode delete, List<Dnode> insertedNodes) {
        String separator = " ";
        String deleteText = delete.getRefDomNode().getTextContent();
        Interval findA = new Interval(delete.getIndexKey(), delete.getIndexKey());
        List<String> deleteTextList = new ArrayList<String>(Arrays.asList(deleteText.split(" ")));
        Collections.sort(deleteTextList);
        String insertText = "";
        for (Dnode insert : insertedNodes) {
            insertText = insertText.concat(insert.getRefDomNode().getTextContent());
            insertText = insertText.concat(separator);
        }
        insertText = insertText.trim();
        List<String> insertTextList = new ArrayList<String>(Arrays.asList(insertText.split(" ")));
        Collections.sort(insertTextList);

        // Similarity 95%
        Jaccard jc = new Jaccard();
        if (deleteTextList.equals(insertTextList) || jc.similarity(deleteText, insertText) > 0.95) {
            for (Dnode insert : insertedNodes) {
                Interval findB = new Interval(insert.getIndexKey(), insert.getIndexKey());

                R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.SPLIT);
                for (int l = findA.inf; l <= findA.sup; l++) {
                    A.getNode(l).inRel = Relation.SPLIT;
                }

                for (int l = findB.inf; l <= findB.sup; l++) {
                    if(B.getNode(l).getRefDomNode().hasChildNodes()) {
                        B.getNode(l).inRel = Relation.SPLIT;
                    }
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
                foundNodeDelete.add(A.getNode(k));
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
                foundNodeInsert.add(B.getNode(k));
            }
        }

        return foundNodeInsert;
    }
}
