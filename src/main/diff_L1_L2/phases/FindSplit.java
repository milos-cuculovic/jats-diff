/** ***************************************************************************************
 *
 *   This file is part of JNdiff project.
 *
 *   JNdiff is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by
 *   the Free Software Foundation; either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   JNdiff is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU LESSER GENERAL PUBLIC LICENSE for more details.
 *
 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
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
import static main.diff_L1_L2.phases.FindMerge.TAGS;
import main.diff_L1_L2.relation.Fragment;
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
    protected static final String[] TAGS = new String[]{"p", "sec"};

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

            //swich delte/insert findSplit or split similar old way
            findSplit();
            //split();

            logger.info("END");
            stopWatch.stop();
            logger.error("Split : " + stopWatch.toString());

        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    protected void split() {

        Field processField;
        SF.StartFieldProcess(Field.NO);
        List<Interval> listIntervalA = new ArrayList<>();
        List<Interval> listIntervalB = new ArrayList<>();
        while ((processField = SF.nextField()) != null) {
            for (int i = processField.xRef.inf; i <= processField.xRef.sup; i++) {
                List<Dnode> listPossibleMerge = new ArrayList<>();

                if (ArrayUtils.indexOf(TAGS, A.getNode(i).getRefDomNode().getNodeName()) > -1) {
//                    logger.info("name: " + B.getNode(i).getRefDomNode().getNodeName() + " " + B.getNode(i).getRefDomNode().getTextContent());
                    int parentNode = A.getNode(i).getPosFather();
                    String content = A.getNode(i).getRefDomNode().getTextContent();
                    //potencial split
                    for (Dnode b : B.nodeList) {

                        if (A.getNode(i).getRefDomNode().getNodeName().equals(b.getRefDomNode().getNodeName())
                                && !A.getNode(i).getRefDomNode().getTextContent().equals(b.getRefDomNode().getTextContent())
                                && A.getNode(i).posFather.equals(b.posFather)) {
                            if (content.contains(b.getRefDomNode().getTextContent())) {
                                listPossibleMerge.add(b);
                            } else {
                                Jaccard jw = new Jaccard();
                                if (b.getPosFather() == parentNode && jw.similarity(content, b.getRefDomNode().getTextContent()) >= 0.40) {
                                    listPossibleMerge.add(b);
                                }
                            }
                        }

                    }

                    if (listPossibleMerge.size() > 1) {
                        String bText = "";
                        for (Dnode n : listPossibleMerge) {
                            String t = n.getRefDomNode().getTextContent().trim();
                            bText = bText.concat(t);
                        }
                        Jaccard jw = new Jaccard();

                        if (jw.similarity(content, bText) >= 0.90 || content.equals(bText)) {

                            Interval findA = new Interval(A.getNode(i).getIndexKey(), A.getNode(i).getIndexKey());
                            for (Dnode n : listPossibleMerge) {
                                Interval findB = new Interval(n.getIndexKey(), n.getIndexKey());
                                R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.SPLIT);
                                listIntervalA.add(findA);
                                listIntervalB.add(findB);
                                for (int l = findA.inf; l <= findA.sup; l++) {
                                    A.getNode(l).inRel = Relation.SPLIT;
                                }
                                for (int l = findB.inf; l <= findB.sup; l++) {
                                    B.getNode(l).inRel = Relation.SPLIT;
                                }
//                                SF.subField(findA, findB, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
                            }

                        }

                    }
                }

            }
            this.clearIntervals(listIntervalA, listIntervalB);
        }

//        System.exit(0);
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
        if (deleteTextList.equals(insertTextList)) {
//            logger.info("SPLIT FOUND");
//            System.exit(0);
//            logger.info(insertText);
            for (Dnode insert : insertedNodes) {
                Interval findB = new Interval(insert.getIndexKey(), insert.getIndexKey());
                R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.SPLIT);
                for (int l = findA.inf; l <= findA.sup; l++) {
                    A.getNode(l).inRel = Relation.SPLIT;
                }
                for (int l = findB.inf; l <= findB.sup; l++) {
                    B.getNode(l).inRel = Relation.SPLIT;
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

    /**
     *
     * @param node
     *
     * @return
     */
    public String getChildTextContent(Node node) {
        String result = "";

        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (!node.getChildNodes().item(i).getNodeName().equals("title")) {
                result = result + node.getChildNodes().item(i).getTextContent();
            }
        }

        return result;
    }

    public void setParentANodes(int indexA) {
        Dnode nodeA = A.getNode(indexA);

        if (nodeA.posFather != 0) {
//            logger.info(nodeA.posFather);
            nodeA.inRel = Relation.SPLIT;

//            R.addFragment(new Interval(indexA, indexA + nodeA.numChildSubtree), new Interval(indexB, indexB + nodeB.numChildSubtree), nodeA.weight, Relation.MERGE);
            setParentANodes(nodeA.posFather);
        }
    }

    public void setParentBNodes(int indexB) {
        Dnode nodeB = B.getNode(indexB);

        if (nodeB.posFather != 0) {
//            logger.info(nodeB.posFather);
            nodeB.inRel = Relation.SPLIT;

//            R.addFragment(new Interval(indexA, indexA + nodeA.numChildSubtree), new Interval(indexB, indexB + nodeB.numChildSubtree), nodeA.weight, Relation.MERGE);
            setParentBNodes(nodeB.posFather);
        }
    }

    /**
     *
     * @param inf
     * @param sup
     * @param domNode
     * @return
     */
    protected Interval getRangeA(int inf, int sup, Node domNode) {
        Interval interval = new Interval(1, 0);
        String domNodeString = getNodeXmlString(domNode);

        for (int i = inf; i <= sup; i++) {

            String refNodeString = getNodeXmlString(A.getNode(i).refDomNode);

            if (domNodeString.equals(refNodeString)) {

                return new Interval(i, i);

            }

        }
//        logger.info("GET B RANGE : " + interval.show());
        return interval;
    }

    /**
     *
     * @param inf
     * @param sup
     * @param domNode
     * @return
     */
    protected Interval getRangeB(int inf, int sup, Node domNode) {
        Interval interval = new Interval(1, 0);
        String domNodeString = getNodeXmlString(domNode);

        for (int i = inf; i <= sup; i++) {

            String refNodeString = getNodeXmlString(B.getNode(i).refDomNode);

            if (domNodeString.equals(refNodeString)) {

                return new Interval(i, i);

            }

        }
//        logger.info("GET B RANGE : " + interval.show());
        return interval;
    }

    protected String getNodeXmlString(Node node) {
        try {
            Document document
                    = node.getOwnerDocument().getImplementation().createDocument("", "fake", null);
            Element copy = (Element) document.importNode(node, true);
            document.importNode(node, false);
            document.removeChild(document.getDocumentElement());
            document.appendChild(copy);
            DOMImplementation domImpl = document.getImplementation();
            DOMImplementationLS domImplLs = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
            LSSerializer serializer = domImplLs.createLSSerializer();
            serializer.getDomConfig().setParameter("format-pretty-print", false);

            return serializer.writeToString(document);
        } catch (Exception e) {
            return "";
        }

    }
}
