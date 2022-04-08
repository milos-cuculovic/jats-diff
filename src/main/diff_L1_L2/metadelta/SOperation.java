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
package main.diff_L1_L2.metadelta;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.vo.MoveTextData;
import main.diff_L1_L2.vo.MoveText;
import main.diff_L1_L2.vo.TextChangeData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import main.diff_L1_L2.vo.TextChange;
import main.diff_L1_L2.core.Ndiff;

/**
 * Wrapper for operations on the document's structure
 *
 * @author schirinz
 */
public class SOperation extends Operation {

    Logger logger = Logger.getLogger(SOperation.class);
    String IDmove;
    String IDmerge;
    String IDsplit;
    String IDupgrade;
    String IDdowngrade;
    String IDmovetext;
    String IDinsertstyle;
    String IDdeletestyle;
    String IDupdatestyle;
    String IDinsertstyletext;
    String IDdeletestyletext;
    String IDupdatestyletextto;
    String IDupdatestyletextfrom;
    String IDupdatestyleFrom;
    String IDupdatestyleTo;
    TextChange textChange;

    //all Elements and Attributes names gathered all here to modularize everything
    public static final String UNWRAP_ELEMENT = "unwrap"; //same for delta
    public static final String WRAP_ELEMENT_DELTA = "wrap";
    public static final String INSERT_ELEMENT = "insert";
    public static final String DELETE_ELEMENT = "delete";
    public static final String MOVE_ELEMENT = "move";
    public static final String MERGE_ELEMENT = "merge";
    public static final String SPLIT_ELEMENT = "split";
    public static final String UPGRADE_ELEMENT = "upgrade";
    public static final String DOWNGRADE_ELEMENT = "downgrade";
    public static final String MOVETEXT_ELEMENT = "move-text";
    public static final String INSERT_STYLE_ELEMENT = "text-style-insert";
    public static final String INSERT_STYLE_TEXT_ELEMENT = "text-insert";
    public static final String DELETE_STYLE_ELEMENT = "text-style-delete";
    public static final String DELETE_STYLE_TEXT_ELEMENT = "delete-text";
    public static final String UPDATE_STYLE_ELEMENT = "text-style-update";
    public static final String UPDATE_STYLE_TEXT_ELEMENT = "text-update";

    public static final String NODE_NAME_ATTR = "nodename";
    public static final String AT_ATTR = "at";
    public static final String NODECOUNT_ATTR = "nodecount";
    public static final String POS_ATTR = "pos";
    public static final String CHILDREN_ATTR = "children";
    public static final String ID_ATTR = "id";
    public static final String IDREF_ATTR = "idref";
    public static final String OPERATION_ATTR = "op";
    public static final String MOVEID_ATTR = "move";
    public static final String MERGEID_ATTR = "merge";
    public static final String SPLITID_ATTR = "split";
    public static final String UPGRADEID_ATTR = "upgrade";
    public static final String DOWNGRADEID_ATTR = "downgrade";
    public static final String MOVEDTEXTID_ATTR = "movetext";
    public static final String INSERTSTYLE_ATTR = "insertstyle";
    public static final String DELETESTYLE_ATTR = "deletestyle";
    public static final String UPDATESTYLE_ATTR = "updatestyle";

    public static final String WRAP_OP_VALUE = "wrapping";
    public static final String MOVEDTO_VALUE = "movedTo";
    public static final String MOVEDFROM_VALUE = "movedFrom";
    public static final String MERGEDTO_VALUE = "mergedTo";
    public static final String MERGEDFROM_VALUE = "mergedFrom";
    public static final String NODE_POSFATHER_ATTR = "posFather";
    public static final String SPLITEDTO_VALUE = "splitedTo";
    public static final String SPLITEDFROM_VALUE = "splitedFrom";
    public static final String UPGRADEDTO_VALUE = "upgradedTo";
    public static final String UPGRADEDFROM_VALUE = "upgradedFrom";
    public static final String DOWNGRADEDTO_VALUE = "downgradedTo";
    public static final String DOWNGRADEDFROM_VALUE = "downgradedFrom";
    public static final String MOVETEXTTO_VALUE = "movetextTo";
    public static final String MOVETEXTFROM_VALUE = "movetextFrom";
    public static final String INSERTSTYLE_VALUE = "insert-style";
    public static final String DELETESTYLE_VALUE = "delete-style";
    public static final String UPDATESTYLE_VALUE_FROM = "update-style-from";
    public static final String UPDATESTYLE_VALUE_TO = "update-style-to";
    public static final String UPDATESTYLETEXT_VALUE_TO = "update-text-to";
    public static final String UPDATESTYLETEXT_VALUE_FROM = "update-text-from";
    public static final String MOVE_TEXT_IN_NODE = "movetext";

    /**
     * Costructor
     *
     * @param type operation type
     * @param indexNodeA Node's index of the operation in the A Vtree
     * @param indexNodeB Node's index of the operation in the B Vtree
     * @param indexFatherB Index of the parent node which the operation is
     * referring to
     */
    /**
     * Costructor
     *
     * @param type Operation Type
     * @param nodeA Original document node
     * @param nodeB Modified document node
     */
    public SOperation(byte type, Dnode nodeA, Dnode nodeB) {
        setBaseInfo(type, nodeA, nodeB);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see ndiff.metadelta.Operation#dump(vdom.DOMDocument)
     */
    @Override
    public void dump(DOMDocument Ndelta) {

        Element newOp = null;

        TextChangeData textChangeData = TextChangeData.getInstance();
        switch (type) {

            case INSERT_TREE:
                newOp = Ndelta.DOM.createElement(INSERT_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
                newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
                newOp.setAttribute(NODECOUNT_ATTR, nodeB.numChildSubtree.toString());
                // newOp.appendChild(Ndelta.DOM.adoptNode(
                // nodeB.refDomNode.cloneNode(true)));
//				updateTextChildNodesRecursion(nodeB.getRefDomNode());
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case INSERT_NODE:
                newOp = Ndelta.DOM.createElement(SOperation.INSERT_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
                newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
                newOp.setAttribute(NODE_NAME_ATTR, nodeB.refDomNode.getNodeName());
                newOp.setAttribute(CHILDREN_ATTR, ((Integer) (nodeB.refDomNode
                        .getChildNodes().getLength() - nodeB.insOnMe)).toString());
                // newOp.appendChild(Ndelta.DOM.adoptNode(
                // nodeB.refDomNode.cloneNode(false)));
//				updateTextChildNodesRecursion(nodeB.getRefDomNode().getParentNode());
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, false));
                Ndelta.root.appendChild(newOp);
                break;

            case DELETE_TREE:
                newOp = Ndelta.DOM.createElement(DELETE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR,
                        ((Integer) (nodeA.numChildSubtree + 1)).toString());
                // newOp.appendChild(Ndelta.DOM.adoptNode(
                // nodeA.refDomNode.cloneNode(true)));
//				nodeA.refDomNode.setNodeValue(Ndiff.encodeTags(nodeA.refDomNode.getNodeValue()));
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case DELETE_NODE:
                newOp = Ndelta.DOM.createElement(DELETE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODE_NAME_ATTR, nodeA.refDomNode.getNodeName());
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, false));
                Ndelta.root.appendChild(newOp);
                break;

            case MOVE_TO:
                newOp = Ndelta.DOM.createElement(MOVE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR,
                        ((Integer) (nodeA.numChildSubtree + 1)).toString());
                newOp.setAttribute(MOVEID_ATTR, IDmove);
                newOp.setAttribute(OPERATION_ATTR, MOVEDTO_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case MOVE_FROM:
                newOp = Ndelta.DOM.createElement(MOVE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
                newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
                newOp.setAttribute(NODECOUNT_ATTR, nodeB.numChildSubtree.toString());
                newOp.setAttribute(MOVEID_ATTR, IDmove);
                newOp.setAttribute(OPERATION_ATTR, MOVEDFROM_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case MERGE_TO:
                newOp = Ndelta.DOM.createElement(MERGE_ELEMENT);
                if (nodeA != null) {
                    //if(nodeA.getRefDomNode().getNodeName() != "#text") {
                    //    break;
                    //}
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    //if(nodeB.getRefDomNode().getNodeName() != "#text") {
                    //    break;
                    //å}
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
                newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
                newOp.setAttribute(ID_ATTR, IDmerge);
                newOp.setAttribute(OPERATION_ATTR, MERGEDTO_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case MERGE_FROM:
                newOp = Ndelta.DOM.createElement(MERGE_ELEMENT);
                if (nodeA != null) {
                    if(nodeA.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    if(nodeB.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(ID_ATTR, IDmerge);
                newOp.setAttribute(OPERATION_ATTR, MERGEDFROM_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case SPLIT_TO:
                newOp = Ndelta.DOM.createElement(SPLIT_ELEMENT);
                if (nodeA != null) {
                    if(nodeA.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    if(nodeB.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(AT_ATTR, nodeB.posFather.toString());
                newOp.setAttribute(POS_ATTR, nodeB.posLikeChild.toString());
                newOp.setAttribute(ID_ATTR, IDsplit);
                newOp.setAttribute(OPERATION_ATTR, SPLITEDTO_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            case SPLIT_FROM:
                newOp = Ndelta.DOM.createElement(SPLIT_ELEMENT);
                if (nodeA != null) {
                    if(nodeA.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    if(nodeB.getRefDomNode().getNodeName() == "#text") {
                        break;
                    }
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(ID_ATTR, IDsplit);
                newOp.setAttribute(OPERATION_ATTR, SPLITEDFROM_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);
                break;

            //UPGRADE
            case UPGRADE_TO:
                newOp = Ndelta.DOM.createElement(UPGRADE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR,
                        ((Integer) (nodeB.numChildSubtree + 1)).toString());
                newOp.setAttribute(OPERATION_ATTR, UPGRADEDTO_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
                Ndelta.root.appendChild(newOp);

                break;

            case UPGRADE_FROM:
                newOp = Ndelta.DOM.createElement(UPGRADE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR, nodeA.numChildSubtree.toString());
                newOp.setAttribute(OPERATION_ATTR, UPGRADEDFROM_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);

                break;

            //DOWNGRADE
            case DOWNGRADE_TO:
                newOp = Ndelta.DOM.createElement(DOWNGRADE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR,
                        ((Integer) (nodeB.numChildSubtree + 1)).toString());
                newOp.setAttribute(OPERATION_ATTR, DOWNGRADEDTO_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeB.refDomNode, true));
                Ndelta.root.appendChild(newOp);

                break;

            case DOWNGRADE_FROM:
                newOp = Ndelta.DOM.createElement(DOWNGRADE_ELEMENT);
                if (nodeA != null) {
                    newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                }
                if (nodeB != null) {
                    newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                }
                newOp.setAttribute(NODECOUNT_ATTR, nodeA.numChildSubtree.toString());
                newOp.setAttribute(OPERATION_ATTR, DOWNGRADEDFROM_VALUE);
                newOp.appendChild(Ndelta.DOM.importNode(nodeA.refDomNode, true));
                Ndelta.root.appendChild(newOp);

                break;

            case MOVETEXT_TO:
                textChange = textChangeData.findElementByNodeBandAction(TextChangeData.ACTION_MOVE_TEXT_TO, nodeB);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(MOVETEXT_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeB.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, MOVEDTO_VALUE);
                    newOp.setAttribute("text-position-to", textChange.getPositionTo().toString());
                    newOp.appendChild(Ndelta.DOM.createCDATASection(textChange.getTextTarget()));
                    Ndelta.root.appendChild(newOp);
                    textChangeData.remove(textChange);
                }
                break;

            case MOVETEXT_FROM:
                textChange = textChangeData.findElementByNodeAandAction(TextChangeData.ACTION_MOVE_TEXT_FROM, nodeA);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(MOVETEXT_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeA.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, MOVEDFROM_VALUE);
                    newOp.setAttribute("text-position-from", textChange.getPositionFrom().toString());
                    newOp.appendChild(Ndelta.DOM.createCDATASection(textChange.getTextSource()));
                    Ndelta.root.appendChild(newOp);
                    //remove becasue findElementBy .. use first element found
                    textChangeData.remove(textChange);
                }
                break;

            case INSERT_STYLE:

                textChange = textChangeData.findElementByNodeBandAction(TextChangeData.ACTION_INSERT_STYLE, nodeB);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(INSERT_STYLE_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeB.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, INSERTSTYLE_VALUE);
                    newOp.setAttribute("text-position-insert-tag", textChange.getPositionTo().toString());
                    newOp.appendChild(Ndelta.DOM.createTextNode(textChange.getTextTarget()));
                    Ndelta.root.appendChild(newOp);
                    textChangeData.remove(textChange);
                }
                break;
//
            case DELETE_STYLE:
                textChange = textChangeData.findElementByNodeAandAction(TextChangeData.ACTION_DELETE_STYLE, nodeA);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(DELETE_STYLE_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeA.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, DELETESTYLE_VALUE);
                    newOp.setAttribute("text-position-delete-tag", textChange.getPositionFrom().toString());
                    newOp.appendChild(Ndelta.DOM.createCDATASection(textChange.getTextSource()));
                    Ndelta.root.appendChild(newOp);
                    textChangeData.remove(textChange);
                }
                break;

            case UPDATE_STYLE_TO:
                textChange = textChangeData.findElementByNodeBandAction(TextChangeData.ACTION_UPDATE_STYLE_TO, nodeB);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(UPDATE_STYLE_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeB.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, UPDATESTYLE_VALUE_TO);
                    newOp.setAttribute("text-position-update-tag", textChange.getPositionTo().toString());
                    newOp.appendChild(Ndelta.DOM.createCDATASection(textChange.getTextTarget()));
                    Ndelta.root.appendChild(newOp);
                    textChangeData.remove(textChange);
                }
                break;

            case UPDATE_STYLE_FROM:
                textChange = textChangeData.findElementByNodeAandAction(TextChangeData.ACTION_UPDATE_STYLE_FROM, nodeA);
                if (textChange != null) {
                    newOp = Ndelta.DOM.createElement(UPDATE_STYLE_ELEMENT);
                    if (nodeA != null) {
                        newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
                    }
                    if (nodeB != null) {
                        newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
                    }
                    newOp.setAttribute(NODECOUNT_ATTR, ((Integer) (nodeA.numChildSubtree + 1)).toString());
                    newOp.setAttribute(OPERATION_ATTR, UPDATESTYLE_VALUE_FROM);
                    newOp.setAttribute("text-position-update-tag", textChange.getPositionFrom().toString());
                    newOp.appendChild(Ndelta.DOM.createCDATASection(textChange.getTextSource()));
                    Ndelta.root.appendChild(newOp);
                    textChangeData.remove(textChange);
                }
                break;
            //next operations INSERT_STYLE_TEXT/DELETE_STYLE_TEXT/UPDATE_STYLE_TEXT
            //case if exists changes in move text change or style text cahnges then check and set raw text changes
        }
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see ndiff.metadelta.Operation#show()
     */
    @Override
    public String show() {
        String ret = "";

        switch (type) {

            case DELETE_TREE:
                ret = "DeleteTree  indexNodeA:" + nodeA.indexKey.toString();
                break;

            case INSERT_TREE:
                ret = "InsertTree  indexNodeB:" + nodeB.indexKey.toString()
                        + "  IndexFatherB:" + nodeB.posFather.toString();
                break;

            case DELETE_NODE:
                ret = "DeleteNode  indexNodeA:" + nodeA.indexKey.toString();
                break;

            case INSERT_NODE:
                ret = "InsertNode  indexNodeB:" + nodeB.indexKey.toString()
                        + "  IndexFatherB:" + nodeB.posFather.toString();
                break;

            case MOVE_TO:
                ret = "MoveTo   indexNodeA:" + nodeA.indexKey.toString()
                        + "  indexNodeB:" + nodeB.indexKey.toString();
                break;

            case MOVE_FROM:
                ret = "MoveFrom   indexNodeA:" + nodeA.indexKey.toString()
                        + "  indexNodeB:" + nodeB.indexKey.toString()
                        + "  IndexFatherB:" + nodeB.posFather.toString();
                break;

        }

        return ret;
    }

    /**
     *
     * @param node
     * @return
     */
    public void updateTextChildNodesRecursion(Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node newNode = node.getChildNodes().item(i);
            if (newNode.hasChildNodes()) {

                updateTextChildNodesRecursion(newNode);
                if (newNode.getNodeType() == Node.TEXT_NODE) {
                    logger.info("TEst");
                    System.exit(0);
                    node.setNodeValue(Ndiff.encodeTags(node.getNodeValue()));
                }
            } else {
//				logger.info(node.ge);
//				System.exit(0);
                if (node.getNodeType() == Node.TEXT_NODE) {
                    logger.info("TEst");
                    System.exit(0);
                    node.setNodeValue(Ndiff.encodeTags(node.getNodeValue()));
                }
            }
        }

    }

    /**
     *
     * @param node
     * @return
     */
    public Node removeChildNodesRecursion(Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node newNode = node.getChildNodes().item(i);
            if (newNode.hasChildNodes()) {
                removeChildNodesRecursion(newNode);
                node.removeChild(newNode);
            } else {
                node.getParentNode().removeChild(node);
            }
        }
        return node;
    }
}
