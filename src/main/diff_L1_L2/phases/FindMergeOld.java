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

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 * documenti
 */
public class FindMergeOld extends Phase {

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
    public FindMergeOld(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            logger.info("START FINDE MERGE");

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            findPossibleMergeNodes();
            logger.info("END");
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            throw new ComputePhaseException("Find Merge");
        }
    }

    protected void findPossibleMergeNodes() {
        try {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
            SF.StartFieldProcess(Field.LOCALITY);
            Field processField;

            while ((processField = SF.nextField()) != null) {
                findA.set(1, 0);
                findB.set(1, 0);
//                if (A.getNode(processField.xRef.inf).inRel == Relation.MERGE) {
//                    //protect if already merge
//                    continue;
//                }
//                logger.info("A  k " + processField.xRef.show());
//                logger.info("B  k" + processField.yRef.show() + "-------->" + B.getNode(processField.yRef.sup).getRefDomNode().getParentNode().getNodeName());
                if ((A.getNode(processField.xRef.sup).refDomNode.getNodeType() == Node.TEXT_NODE)
                        || A.getNode(processField.xRef.inf).getRefDomNode().getChildNodes().getLength() > B.getNode(processField.yRef.inf).getRefDomNode().getChildNodes().getLength()) {

                    logger.info("A  " + processField.xRef.show() + " " + A.getNode(processField.xRef.sup).getRefDomNode().getNodeName() + " " + A.getNode(processField.xRef.sup).getRefDomNode().getChildNodes().getLength());
                    logger.info("B " + processField.yRef.show() + " " + B.getNode(processField.yRef.sup).getRefDomNode().getChildNodes().getLength());
                    Vnode nodeA;
                    Vnode nodeB;
                    Interval processIntervalA;
                    Interval processIntervalB;

                    if (A.getNode(processField.xRef.sup).refDomNode.getNodeType() == Node.TEXT_NODE) {
                        //if node type text two node above (paren.parent) is necessary to keep the logic of the children for all cases
                        int indexA = A.getNode(A.getNode(processField.xRef.sup).posFather).posFather;
                        int indexB = B.getNode(B.getNode(processField.yRef.sup).posFather).posFather;
                        if (indexA > 0 && indexB > 0) {
                            nodeA = A.getNode(indexA);
                            nodeB = B.getNode(indexB);

                            processIntervalA = new Interval(nodeA.getIndexKey(), nodeA.getIndexKey());
                            processIntervalB = new Interval(nodeB.getIndexKey(), nodeB.getIndexKey());
                            findAndCalculateMerge(processIntervalA, processIntervalB, nodeA, nodeB);
                        }

                    } else {

                        nodeA = A.getNode(processField.xRef.inf);
                        nodeB = B.getNode(processField.yRef.inf);
                        processIntervalA = new Interval(processField.xRef.inf, processField.xRef.inf);
                        processIntervalB = new Interval(processField.yRef.inf, processField.yRef.inf);
                        findAndCalculateMerge(processIntervalA, processIntervalB, nodeA, nodeB);
                    }

                }
                nodesA.clear();
                nodesB.clear();

            }

        } catch (Exception e) {
            throw e;
        }
//        System.exit(0);
    }

    /**
     *
     * @param processIntervalA
     * @param processIntervalB
     * @param nodeA
     * @param nodeB
     */
    protected void findAndCalculateMerge(Interval processIntervalA, Interval processIntervalB, Vnode nodeA, Vnode nodeB) {

        NodeList childNodesA = A.getNode(processIntervalA.inf).getRefDomNode().getChildNodes();
        NodeList childNodesB = B.getNode(processIntervalB.inf).getRefDomNode().getChildNodes();
        //try find count for each node in segment
        for (int i = 0; i < childNodesA.getLength(); i++) {
            String nodeName = childNodesA.item(i).getNodeName();

            if (nodesA.containsKey(nodeName)) {

                NodeVO nodeVo = new NodeVO();
                nodeVo.node = childNodesA.item(i);
                nodeVo.I = new Interval(processIntervalA.inf, processIntervalA.inf + A.getNode(processIntervalA.inf).numChildSubtree);
                NodesVO nodesVO = nodesA.get(nodeName);
                nodesVO.count = nodesVO.count + 1;
                nodesVO.nodes.add(nodeVo);
            } else {
                NodeVO nodeVo = new NodeVO();
                nodeVo.node = childNodesA.item(i);
                nodeVo.I = new Interval(processIntervalA.inf, processIntervalA.sup + A.getNode(processIntervalA.inf).numChildSubtree);
                NodesVO nodesVO = new NodesVO();
                nodesVO.count = 1;
                nodesVO.nodes.add(nodeVo);
                nodesA.put(nodeName, nodesVO);

            }

        }

        for (int i = 0; i < childNodesB.getLength(); i++) {
            String nodeName = childNodesB.item(i).getNodeName();
            if (nodesB.containsKey(nodeName)) {

                NodeVO nodeVo = new NodeVO();
                nodeVo.node = childNodesB.item(i);
                nodeVo.I = new Interval(processIntervalB.inf, processIntervalB.inf + B.getNode(processIntervalB.inf).numChildSubtree);
                NodesVO nodesVO = nodesB.get(nodeName);
                nodesVO.count = nodesVO.count + 1;
                nodesVO.nodes.add(nodeVo);
            } else {
                NodeVO nodeVo = new NodeVO();
                nodeVo.node = childNodesB.item(i);
                nodeVo.I = new Interval(processIntervalB.inf, processIntervalB.inf + B.getNode(processIntervalB.inf).numChildSubtree);
                NodesVO nodesVO = new NodesVO();
                nodesVO.count = 1;
                nodesVO.nodes.add(nodeVo);
                nodesB.put(nodeName, nodesVO);

            }
        }

        for (String key : nodesB.keySet()) {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
//            logger.info("COUNT A key: " + key + nodesA.get(key).count);
//            logger.info("COUNT B key: " + key + nodesB.get(key).count);
            //potential node for merge if number of same node in A grater then number of same Node in B
            if (nodesA.containsKey(key) && nodesB.containsKey(key) && nodesA.get(key).count > nodesB.get(key).count) {
//                logger.info("COUNT A key: " + key + nodesA.get(key).count);
//                logger.info("COUNT B key: " + key + nodesB.get(key).count);

                for (int i = 0; i < nodesB.get(key).nodes.size(); i++) {
                    String childTextContentA = "";
                    String childTextContentB = getChildTextContent(nodesB.get(key).nodes.get(i).node);

                    List<NodeVO> tmpMatchedList = new ArrayList<>();

                    for (int j = 0; j < nodesA.get(key).nodes.size(); j++) {
                        String childTextContent = getChildTextContent(nodesA.get(key).nodes.get(j).node);

                        if (nodesB.get(key).nodes.get(i).node.getNodeType() != Node.TEXT_NODE
                                && !nodesB.get(key).nodes.get(i).node.getTextContent().equals(nodesA.get(key).nodes.get(j).node.getTextContent())
                                && nodesB.get(key).nodes.get(i).node.getTextContent().contains(childTextContent)) {
                            tmpMatchedList.add(nodesA.get(key).nodes.get(j));
                            if (nodesA.get(key).nodes.get(j).node.getNodeName().equalsIgnoreCase("sec")) {
                                childTextContentA = childTextContentA.concat(childTextContent);
                            } else {
                                childTextContentA = childTextContentA.concat(childTextContent).concat(" ");
                            }
                        }
//
                    }

                    int tmpFindAInf = 0;
                    int tmpFindASup = 0;
                    if (tmpMatchedList.size() > 1 && childTextContentB.equals(childTextContentA.trim())) {
//                        logger.info("B NODE MATCHED: " + nodesB.get(key).nodes.get(i).node.getNodeName() + " points" + nodesB.get(key).nodes.get(i).I.show());
                        Interval intervalB = getRangeB(nodesB.get(key).nodes.get(i).I.inf, nodesB.get(key).nodes.get(i).I.sup, nodesB.get(key).nodes.get(i).node);
                        findB.set(intervalB);
                        for (int k = 0; k < tmpMatchedList.size(); k++) {

//                            logger.info("A MERGE NODE MATCHED " + tmpMatchedList.get(k).node.getNodeName() + " points" + nodesA.get(key).nodes.get(k).I.show());
                            Interval intervalA = getRangeA(nodesA.get(key).nodes.get(k).I.inf, nodesA.get(key).nodes.get(k).I.sup, tmpMatchedList.get(k).node);
                            if (k == 0) {
                                tmpFindAInf = intervalA.inf;
                            }
                            if (k == tmpMatchedList.size() - 1) {
                                tmpFindASup = intervalA.sup;
                            }
                            findA.set(intervalA);

                            R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.MERGE);
                            //next line is not necesary
//                                SF.subPoint(findA.inf, findB.inf, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
                            for (int l = findA.inf; l <= findA.sup; l++) {
                                A.getNode(l).inRel = Relation.MERGE;
                            }
                            for (int l = findB.inf; l <= findB.sup; l++) {
                                B.getNode(l).inRel = Relation.MERGE;
                            }
                            //may be need if need set parent relation.no or relation.equal
                            setParentANodes(A.getNode(findA.inf).posFather);
                            setParentBNodes(B.getNode(findB.inf).posFather);

                        }
                        //this part is debatable but should be exists
                        if (tmpFindAInf > 0 && tmpFindASup > 0 && findB.inf > 0 && findB.sup > 0 && tmpFindASup > tmpFindAInf && findB.sup > findB.inf) {
                            SF.subField(new Interval(tmpFindAInf, tmpFindASup), findB, Field.NO, Field.LOCALITY,
                                    Field.LOCALITY, Field.NO);
                        }
                    }
                }
            }

        }
        nodesA.clear();
        nodesB.clear();

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
            nodeA.inRel = Relation.MERGE;

//            R.addFragment(new Interval(indexA, indexA + nodeA.numChildSubtree), new Interval(indexB, indexB + nodeB.numChildSubtree), nodeA.weight, Relation.MERGE);
            setParentANodes(nodeA.posFather);
        }
    }

    public void setParentBNodes(int indexB) {
        Dnode nodeB = B.getNode(indexB);

        if (nodeB.posFather != 0) {
//            logger.info(nodeB.posFather);
            nodeB.inRel = Relation.MERGE;

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
