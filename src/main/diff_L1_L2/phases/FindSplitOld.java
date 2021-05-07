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
import main.diff_L1_L2.relation.Fragment;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 * documenti
 */
public class FindSplitOld extends Phase {

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
    public FindSplitOld(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            logger.info("START FINDE SPLIT");

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            findPossibleSplitNodes();
            logger.info("END");
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    protected void findPossibleSplitNodes() {
        try {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
            SF.StartFieldProcess(Field.LOCALITY);
            Field processField;

            while ((processField = SF.nextField()) != null) {
                findA.set(1, 0);
                findB.set(1, 0);
//                if (B.getNode(processField.yRef.inf).inRel == Relation.MERGE) {
//                    //protect if already merge
//                    continue;
//                }
//                logger.info("A  k " + processField.xRef.show());
//                logger.info("B  k" + processField.yRef.show() + "-------->" + B.getNode(processField.yRef.sup).getRefDomNode().getParentNode().getNodeName());
                if ((B.getNode(processField.yRef.sup).refDomNode.getNodeType() == Node.TEXT_NODE)
                        || A.getNode(processField.xRef.inf).getRefDomNode().getChildNodes().getLength() < B.getNode(processField.yRef.inf).getRefDomNode().getChildNodes().getLength()) {

//                    logger.info("A  " + processField.xRef.show() + " " + A.getNode(processField.xRef.sup).getRefDomNode().getNodeName() + " " + A.getNode(processField.xRef.sup).getRefDomNode().getChildNodes().getLength());
//                    logger.info("B " + processField.yRef.show() + " " + B.getNode(processField.yRef.sup).getRefDomNode().getChildNodes().getLength());
                    Vnode nodeA;
                    Vnode nodeB;
                    Interval processIntervalA;
                    Interval processIntervalB;

                    if (B.getNode(processField.yRef.sup).refDomNode.getNodeType() == Node.TEXT_NODE) {
                        //if node type text two node above (paren.parent) is necessary to keep the logic of the children for all cases
                        int indexA = A.getNode(A.getNode(processField.xRef.sup).posFather).posFather;
                        int indexB = B.getNode(B.getNode(processField.yRef.sup).posFather).posFather;
                        nodeA = A.getNode(indexA);
                        nodeB = B.getNode(indexB);

                        processIntervalA = new Interval(nodeA.getIndexKey(), nodeA.getIndexKey());
                        processIntervalB = new Interval(nodeB.getIndexKey(), nodeB.getIndexKey());

                    } else {

                        nodeA = A.getNode(processField.xRef.inf);
                        nodeB = B.getNode(processField.yRef.inf);
                        processIntervalA = new Interval(processField.xRef.inf, processField.xRef.inf);
                        processIntervalB = new Interval(processField.yRef.inf, processField.yRef.inf);
                    }
                    findAndCalculateSplit(processIntervalA, processIntervalB, nodeA, nodeB);
                }
                nodesA.clear();
                nodesB.clear();
            }

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * @param processIntervalA
     * @param processIntervalB
     * @param nodeA
     * @param nodeB
     */
    protected void findAndCalculateSplit(Interval processIntervalA, Interval processIntervalB, Vnode nodeA, Vnode nodeB) {

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

        for (String key : nodesA.keySet()) {
            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);
//			logger.info("COUNT A key: " + key + nodesA.get(key).count);
//			logger.info("COUNT B key: " + key + nodesB.get(key).count);
            if (nodesA.containsKey(key) && nodesB.containsKey(key) && nodesA.get(key).count < nodesB.get(key).count) {
//                logger.info("COUNT A key: " + key + nodesA.get(key).count);
//                logger.info("COUNT B key: " + key + nodesB.get(key).count);

                for (int i = 0; i < nodesA.get(key).nodes.size(); i++) {
                    String childTextContentB = "";
                    String childTextContentA = getChildTextContent(nodesA.get(key).nodes.get(i).node);
                    List<NodeVO> tmpMatchedList = new ArrayList<>();
                    for (int j = 0; j < nodesB.get(key).nodes.size(); j++) {
                        String childTextContent = getChildTextContent(nodesB.get(key).nodes.get(j).node);
                        if (nodesA.get(key).nodes.get(i).node.getNodeType() != Node.TEXT_NODE
                                && !nodesA.get(key).nodes.get(i).node.getTextContent().equals(nodesB.get(key).nodes.get(j).node.getTextContent())
                                && nodesA.get(key).nodes.get(i).node.getTextContent().contains(childTextContent)) {
                            tmpMatchedList.add(nodesB.get(key).nodes.get(j));
                            if (nodesB.get(key).nodes.get(j).node.getNodeName().equalsIgnoreCase("sec")) {
                                childTextContentB = childTextContentB.concat(childTextContent);
                            } else {
                                childTextContentB = childTextContentB.concat(childTextContent).concat(" ");
                            }
                        }
                    }
                    int tmpFindBInf = 0;
                    int tmpFindBSup = 0;
                    if (tmpMatchedList.size() > 1 && childTextContentA.equals(childTextContentB.trim())) {
                        logger.info("A NODE SPLIT MATCHED: " + nodesA.get(key).nodes.get(i).node.getNodeName() + " points" + nodesA.get(key).nodes.get(i).I.show());
                        Interval intervalA = getRangeA(nodesA.get(key).nodes.get(i).I.inf, nodesA.get(key).nodes.get(i).I.sup, nodesA.get(key).nodes.get(i).node);
                        findA.set(intervalA);
                        for (int k = 0; k < tmpMatchedList.size(); k++) {

                            logger.info("B SPLIT NODE MATCHED " + tmpMatchedList.get(k).node.getNodeName() + " points" + nodesB.get(key).nodes.get(k).I.show());
                            Interval intervalB = getRangeB(nodesB.get(key).nodes.get(k).I.inf, nodesB.get(key).nodes.get(k).I.sup, tmpMatchedList.get(k).node);
                            if (k == 0) {
                                tmpFindBInf = intervalB.inf;
                            }
                            if (k == tmpMatchedList.size() - 1) {
                                tmpFindBSup = intervalB.sup;
                            }
                            findB.set(intervalB);

                            R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.SPLIT);
                            //next line is not necesary
//                                SF.subPoint(findA.inf, findB.inf, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
                            for (int l = findA.inf; l <= findA.sup; l++) {
                                A.getNode(l).inRel = Relation.SPLIT;
                            }
                            for (int l = findB.inf; l <= findB.sup; l++) {
                                B.getNode(l).inRel = Relation.SPLIT;
                            }
                            //may be need if need set parent relation.no or relation.equal
                            setParentANodes(A.getNode(findA.inf).posFather);
                            setParentBNodes(B.getNode(findB.inf).posFather);

                        }
                        if (tmpFindBInf > 0 && tmpFindBSup > 0 && findB.inf > 0 && findB.sup > 0 && tmpFindBSup > tmpFindBInf && findB.sup > findB.inf) {
                            SF.subField(findA, new Interval(tmpFindBInf, tmpFindBSup), Field.NO, Field.LOCALITY,
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
