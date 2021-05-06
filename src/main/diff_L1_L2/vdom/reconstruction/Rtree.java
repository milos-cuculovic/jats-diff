/*****************************************************************************************
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

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.vdom.reconstruction;

import main.diff_L1_L2.vdom.Vtree;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.metadelta.AOperation;
import main.diff_L1_L2.metadelta.Operation;
import main.diff_L1_L2.metadelta.SOperation;
import main.diff_L1_L2.metadelta.TOperation;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import java.util.Vector;

/**
 * @author Mike
 * 
 *         Extends DOM to insert properties and method as to help the elaboration
 *         of the document.
 * 
 */
public class Rtree extends Vtree<Rnode> {

	/**
	 * Wrapper containing information about splitting a text node
	 * @author Mike 
	 *         
	 */
	class Xpoint {
		public Text nodeToSplit;
		public Integer offsetToSplit;
		public Node refNode;

		/**
		 * Constructor
		 * 
		 * @param node
		 * @param offsetToSplit
		 * @param refNode
		 */
		Xpoint(Node node, Integer offsetToSplit, Node refNode) {
			this.nodeToSplit = (Text) node;
			this.offsetToSplit = offsetToSplit;
			this.refNode = refNode;
		}

		public String show() {
			String ret = "";
			ret += "pos:" + offsetToSplit + " \n"
					+ nodeToSplit.getTextContent();
			return ret;
		}

	}

	Logger logger = Logger.getLogger(getClass().getName());
	public static final String NDIFF_NAMESPACE = "http://diff.cs.unibo.it/jndiff";

	public static final String NDIFF_PREFIX = "ndiff";
	private Integer counter; // Per inserimento ricorsivo

	private int nodeDeleted = 0;

	// Lista dei nodi attualmente editati
	public Vector<Integer> editingNode = new Vector<Integer>();

	/**
	 * Constructor - Calls Vtree constructor and builds the base structure
	 * , then computes the new specific attributes for the reconstruction phase
	 * 
	 * @param fileXML
	 *            XML file path
	 * @param ltrim
	 *            if set to true deletes all the left trailing whitespaces in a text node
	 * @param rtrim
	 *            if set to true deletes all the right trailing whitespaces in a text node
	 * @param collapse
	 *             if set to true collapses the internal whitespace of a text node
	 * @param emptynode
	 *            if set to true text nodes containing only whitespaces
	 *            are not taken into account during the Dtree building phase
	 * @param commentnode
	 *            if set to true comment nodes are not taken into account
     *			  during the Dtree building phase
	 */
	public Rtree(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML, ltrim, rtrim, collapse, emptynode, commentnode);
		Attr attr = DOM.createAttribute("xmlns:" + NDIFF_PREFIX);
		attr.setValue(NDIFF_NAMESPACE);
		root.setAttributeNode(attr);
	}

	/**
	 * Insert of a single node
	 * 
	 * @param nn
	 *            Key of the node to insert
	 * @param at
	 *            Key of the parent node on which to append child
	 * @param pos
	 *            position of the node as the child
	 * @param children
	 *            number of children to adopt
	 * @param content
	 *            node to insert
	 */
	public void WRAP(Integer nodenumber, Integer at, Integer pos, Integer children, String nodename, Node content) {

		System.out.println(" WRAP nodenumber:" + nodenumber + " at:" + at + " pos:" + pos
				+ " children:" + children);

		Node toIns = DOM.importNode(content, false);
		Node father = getNode(at).refDomNode;
		Node refNode = findChildPosition(father, pos);
		
		//marking up node
		Element markup = (Element) toIns;
		markup.setAttributeNS(NDIFF_PREFIX, NDIFF_PREFIX+":"+ Operation.STATUS_ATTR, SOperation.WRAP_OP_VALUE);
		markup.setAttributeNS(NDIFF_PREFIX, NDIFF_PREFIX+":"+SOperation.AT_ATTR, at.toString() );
		markup.setAttributeNS(NDIFF_PREFIX, NDIFF_PREFIX+":"+SOperation.POS_ATTR, pos.toString() );
		markup.setAttributeNS(NDIFF_PREFIX, NDIFF_PREFIX+":"+SOperation.CHILDREN_ATTR, children.toString() );

		if (refNode != null)
			father.insertBefore(toIns, refNode);
		else
			father.appendChild(toIns);

		addNodeList(nodenumber, at, toIns);

		// Adotta figli dal contesto
		if (children > 0)
			adoptChild(at, nodenumber, children);
		
		//propagate changes
		propagationModify(father);
	}

	/**
	 * Adds to nodeList the subtree settled in content
	 * 
	 * @param nn
	 *            position from where to start inserting node in the indexing
	 * @param at
	 *            Parent to be node of the nodes to be inserted
	 * @param content
	 *            root node of the DOM part to insert
	 */
	private void addNodeList(Integer nn, Integer at, Node content) {
		counter = nn;
		recursiveAddNodeList(at, content);
	}

	/**
	 * Look for children to adopt. During the adoption deleted nodes
	 * are taken into account
	 * 
	 * @param fnode
	 *            Parent node of the current node
	 * @param node
	 *            Current node
	 * @param children
	 *            Number of children to adopt
	 */
	private void adoptChild(Integer fnode, Integer node, Integer children) {

		// System.out.println("Search Adopt Child fnode:"+fnode+" node:"+node+" children:"+children);
		if (getNode(fnode).isNew == 1)
			adoptChild(getNode(fnode).getPosFather(), fnode, children);

		// System.out.println("Adopt Child fnode:"+fnode+" node:"+node+" children:"+children);

		Node nextSibling;
		Node child = getNode(node).refDomNode;
		int i = 0;
		while (i < children) {

			nextSibling = child.getNextSibling();
			child.appendChild(nextSibling);
			if (!isMarkupHowDelete(nextSibling))
				i++;

		}
	}

	/**
	 * change the value of a node attribute
	 * 
	 * @param nn
	 *            index of the node in which there is an attribute to be removed
	 * @param name
	 *            Attribute's name which value has to be changed
	 * @param newValue
	 *            New attribute's value
	 */
	public void ATTCHANGE(Integer nodenumber, String name, String newValue, String oldValue, String operation) {
		System.out.println(" ATTCHANGE nn:" + nodenumber + " name:" + name + " newValue:"+ newValue);
		
		Element editNode = (Element) getNode(nodenumber).refDomNode;
		editNode.setAttribute(name, newValue);
		
		//create markup node
		Element markup = DOM.createElementNS(NDIFF_NAMESPACE, AOperation.ELEMENT_NAME);
		markup.setPrefix(NDIFF_PREFIX);
		markup.setAttribute(AOperation.NEWVALUE_ATTR, newValue);
		markup.setAttribute(AOperation.OLDVALUE_ATTR, oldValue);
		markup.setAttribute(AOperation.NAME_ATTR, name);
		markup.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodenumber.toString());
		markup.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodenumber.toString());
		markup.setAttribute(AOperation.OPERATION_ATTR, operation);
		Node parentNode = editNode.getParentNode();
		parentNode.insertBefore(markup,editNode);
		
		//notify something changed
		propagationModify(parentNode);
		
	}

	/**
	 * Insert an attribute in a node
	 * 
	 * @param nn
	 *            Node index
	 * @param name
	 *            Attribute's name to insert
	 * @param value
	 *            Value of the new attribute
	 */
	public void ATTINSERT(Integer nodenumber, String name, String newValue, String operation) {
		System.out.println(" ATTINSERT nn:" + nodenumber + " name:" + name + " value:" + newValue);
		Element editNode = (Element) getNode(nodenumber).refDomNode;
		editNode.setAttribute(name, newValue);
		
		//markup node
		Element markup = DOM.createElementNS(NDIFF_NAMESPACE, AOperation.ELEMENT_NAME);
		markup.setPrefix(NDIFF_PREFIX);
		markup.setAttribute(AOperation.NEWVALUE_ATTR, newValue);
		markup.setAttribute(AOperation.NAME_ATTR, name);
		markup.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodenumber.toString() );
		markup.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodenumber.toString() );
		markup.setAttribute(AOperation.OPERATION_ATTR, operation);
		Node parentNode = editNode.getParentNode();
		parentNode.insertBefore(markup,editNode);
		
		//notify something changed
		propagationModify(parentNode);
	}

	/**
	 * Remove an attribute
	 * 
	 * @param nn
	 *            Node index
	 * @param name
	 *            Attribute's name to be removed
	 */
	public void ATTDELETE(Integer nodenumber, String name, String operation) {
		System.out.println(" ATTDELETE nn:" + nodenumber + " name:" + name);
		Element editNode = (Element) getNode(nodenumber).refDomNode;
		editNode.removeAttribute(name);
		
		//markup node
		Element markup = DOM.createElementNS(NDIFF_NAMESPACE, AOperation.ELEMENT_NAME);
		markup.setPrefix(NDIFF_PREFIX);
		markup.setAttribute(AOperation.NAME_ATTR, name);
		markup.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodenumber.toString() );
		markup.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodenumber.toString() );
		markup.setAttribute(AOperation.OPERATION_ATTR, operation);
		Node parentNode = editNode.getParentNode();
		parentNode.insertBefore(markup,editNode);
		
		//notify something changed
		propagationModify(parentNode);
	}

	/**
	 * Checks if the node is in a editing state. If not
	 * it will put it in an editing state
	 * 
	 * @param nn
	 *            node index to be checked
	 */
	private void checkEditing(Integer nn) {
		if (!getNode(nn).isEditing) {
			getNode(nn).isEditing = true;
			editingNode.add(nn);

			Node father = getNode(nn).refDomNode.getParentNode();

			Node editing = DOM.createElementNS(NDIFF_NAMESPACE, TOperation.EDITING_VALUE);
			editing.setPrefix(NDIFF_PREFIX);

			father.insertBefore(editing, getNode(nn).refDomNode);
			editing.appendChild(getNode(nn).refDomNode);

			getNode(nn).refDomNode = editing;
		}
	}

	/**
	 * Close the nodes in an editing states
	 */
	public void closeEditing() {
		Node editing;
		Node father;

		for (int i = 0; i < editingNode.size(); i++) {
			editing = getNode(editingNode.get(i)).refDomNode;
			father = editing.getParentNode();

			while (editing.hasChildNodes()) {
				father.insertBefore(editing.getFirstChild(), editing);
			}

			father.removeChild(editing);
		}

	}

	/**
	 * Deletes a substring of a textnode
	 * 
	 * @param nn
	 *            Index of the text node to be modified
	 * @param pos
	 *            initial Offset of substring to delete
	 * @param length
	 *            length of text to be deleted
	 */
	public void DEL(Integer nn, Integer pos, Integer length) {
		System.out.println(" DEL nn:" + nn + " pos:" + pos + " length:" + length);
		checkEditing(nn);

		propagationModify(getNode(nn).refDomNode.getParentNode());

		// compute references as to split the node
		Xpoint splitPoint = findPosToSplit(getNode(nn).refDomNode,
				(pos - getNode(nn).countDel));
		getNode(nn).countDel += length;

		// System.out.println(splitPoint.show());
		

		/* Substring to remove */
		String textToDelete = splitPoint.nodeToSplit.substringData(
				splitPoint.offsetToSplit, length);
				
		/* creating context of insertion */
		Node nodeLeft = null;
		Node nodeRight = null;

		/* Left node */
		if (splitPoint.offsetToSplit > 0) {
			// System.out.println("nodeToSplit.substringData(0,  "+lengthToSplit+");");
			String contentLeft = splitPoint.nodeToSplit.substringData(0,
					splitPoint.offsetToSplit);
			// System.out.println(contentLeft);

			nodeLeft = DOM.createTextNode(contentLeft);
		}

		/* right node */
		if ((splitPoint.offsetToSplit + length) < splitPoint.nodeToSplit
				.getLength()) {

			// System.out.println("nodeToSplit.substringData("+(lengthToSplit+length)+",  "+nodeToSplit.getLength()+");");
			String contentRight = splitPoint.nodeToSplit.substringData(
					splitPoint.offsetToSplit + length,
					splitPoint.nodeToSplit.getLength());
			// System.out.println(contentRight);
			nodeRight = DOM.createTextNode(contentRight);
		}

		Node nodeDelete = DOM.createTextNode(textToDelete);
		Element markup = DOM.createElementNS(NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT);
		markup.setAttribute(Operation.STATUS_ATTR, Operation.DELETED_VALUE);
		markup.setPrefix(NDIFF_PREFIX);
		markup.appendChild(nodeDelete);

		if (nodeLeft != null)
			getNode(nn).refDomNode.insertBefore(nodeLeft,
					splitPoint.nodeToSplit);
		getNode(nn).refDomNode.insertBefore(markup, splitPoint.nodeToSplit);

		if (nodeRight != null)
			getNode(nn).refDomNode.insertBefore(nodeRight,
					splitPoint.nodeToSplit);

		getNode(nn).refDomNode.removeChild(splitPoint.nodeToSplit);
	}

	/**
	 * Remove a subtree
	 * 
	 * @param nn
	 *            key of the root of the subtree to remove
	 * @param nodecount
	 *            Number of children belonging to the subtree to be removed
	 * @param move
	 *            notify if the deletion is to mark as a move operation
	 */
	public void DELETE(Integer nn, Integer nodecount, String move) {

		System.out.println(" DELETE nn:" + nn + "(" + (nn - nodeDeleted)
				+ ") nodecount:" + nodecount);

		nn -= nodeDeleted;
		nodeDeleted += nodecount;

		Node toDelete = getNode(nn).getRefDomNode();
		Node father = toDelete.getParentNode();

		if (toDelete.getNodeType() != Node.ELEMENT_NODE) {

			// Creating wrapper node 
			Element markup = DOM.createElementNS(NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT);
			markup.setPrefix(NDIFF_PREFIX);
			if (move == null) {
				markup.setAttribute(Operation.STATUS_ATTR, SOperation.DELETED_VALUE);
			} else {
				markup.setAttribute(Operation.STATUS_ATTR, SOperation.MOVEDTO_VALUE);
				markup.setAttribute(SOperation.IDREF_ATTR, move);
			}

			// changing document structure
			father.insertBefore(markup, toDelete);
			markup.appendChild(toDelete);

			propagationModify(markup.getParentNode());

		} else { // Node to be deleted is an element

			if (move == null) {
				Attr attr = DOM.createAttributeNS(NDIFF_NAMESPACE, Operation.STATUS_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(SOperation.DELETED_VALUE);
				((Element) toDelete).setAttributeNode(attr);
			} else {
				Attr attr = DOM.createAttributeNS(NDIFF_NAMESPACE, Operation.STATUS_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(SOperation.MOVEDTO_VALUE);
				((Element) toDelete).setAttributeNode(attr);

				attr = DOM.createAttributeNS(NDIFF_NAMESPACE, SOperation.IDREF_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(move);
				((Element) toDelete).setAttributeNode(attr);
			}

			propagationModify(toDelete.getParentNode());
		}

		subNodeList(nn, nodecount);
	}

	/**
	 * Remove the node with the specified key
	 * 
	 * @param nn
	 *            Key of the node to be removed. node's children will be
	 *            adopted by the node's parent
	 */
	public void UNWRAP(Integer nodenumber, String nodename) {

		System.out.println(" UNWRAP nn:" + nodenumber + "(" + (nodenumber - nodeDeleted) + ")");

		nodenumber -= nodeDeleted;
		nodeDeleted++;

		Node removeNode = getNode(nodenumber).getRefDomNode();
		Node father = removeNode.getParentNode();
		
		//creating markup node
		Element markupFather = DOM.createElementNS(NDIFF_NAMESPACE, SOperation.UNWRAP_ELEMENT);
		markupFather.setPrefix(NDIFF_PREFIX);
		markupFather.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodenumber.toString() );
		markupFather.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodenumber.toString() );
		markupFather.setAttribute(SOperation.NODE_NAME_ATTR, nodename );

		//inserting markup node in the document
		father.insertBefore(markupFather, removeNode);
		
		while (removeNode.hasChildNodes()) {
			Node insertedNode = father.insertBefore(removeNode.getFirstChild(), removeNode);
			markupFather.appendChild(insertedNode);
		}
		
		father.removeChild(removeNode);

		subNodeList(nodenumber, 1);
		
		//notify something changed
		propagationModify(father);
	}

	/**
	 * Calculating the insertion position not taking into account the nodes
	 * marked as deleted
	 * 
	 * @param father
	 *            Father of the node to be inserted
	 * @param pos
	 *            Node's position
	 * @return Right node of the node to be inserted, or null if the node to be inserted
	 *         is the last one
	 */
	private Node findChildPosition(Node father, Integer pos) {

		NodeList childList = father.getChildNodes();

		// System.out.println("debug:"+father.getChildNodes().getLength());

		int count = 0;
		Node refNode = null;

		for (int i = 0; i < childList.getLength(); i++) {

			// System.out.println("debug:"+childList.item(i).toString());

			if (count == pos)
				refNode = childList.item(i);
			if (!isMarkupHowDelete(childList.item(i)))
				count++;
		}

		return refNode;
	}

	/**
	 * Calculate the node and the node's internal offset on which to apply the split
	 * 
	 * @param tmp
	 *            Node to be splitted
	 * @param offset
	 *           Offset on which to apply the split
	 * @return References to be able to split the node
	 */
	private Xpoint findPosToSplit(Node tmp, Integer offset) {
		Integer nChild = -1;
		Integer offsetCount = 0;
		Integer explorer = 0;
		Node visitNode;

		do {

			nChild++;
			offsetCount = explorer;
			visitNode = tmp.getChildNodes().item(nChild);

			if (!isMarkupHowDelete(visitNode)) {

				explorer += visitNode.getTextContent().length();

				/*
				 * if(visitNode.getNodeType() == Node.ELEMENT_NODE) explorer +=
				 * ((Text)visitNode.getFirstChild()).getLength(); else explorer
				 * += ((Text)visitNode).getLength();
				 */

			}

		} while ((explorer < offset)
				&& (nChild < (tmp.getChildNodes().getLength() - 1)));

		Node nodeToSplit = null;
		Node refNode = null;
		Integer offsetToSplit = (offset - offsetCount);

		// if the node is the last child
		if (nChild < tmp.getChildNodes().getLength()) {

			nodeToSplit = tmp.getChildNodes().item(nChild);
			// If the node to be splitted is a markup node, the node won't be splitted
			// instead a new node will be inserted before the node to be splitted
			if (isMarkup(nodeToSplit)) {
				refNode = nodeToSplit;
				nodeToSplit = null;
			}

		} else {
			// Creating a new node and making a tail insert
			nodeToSplit = null;
			refNode = null;
		}

		return new Xpoint(nodeToSplit, offsetToSplit, refNode);
	}

	/**
	 * Insert a substring inside a text node
	 * 
	 * @param nn
	 *            Index of the node in which to insert text
	 * @param pos
	 *            Insertion offset
	 * @param length
	 *            length of the inserted text
	 * @param content
	 *            Text to be inserted
	 */
	public void INS(Integer nn, Integer pos, Integer length, Node content) {
		System.out.println(" INS nn:" + nn + " pos:" + pos + " length:" + length);
		checkEditing(nn);

		propagationModify(getNode(nn).refDomNode.getParentNode());

		// Computing references to split the node
		Xpoint splitPoint = findPosToSplit(getNode(nn).refDomNode, pos);

		/* Creating markup node */
		Node nodeInsert = DOM.adoptNode(content.cloneNode(true));

		Element markup = DOM.createElementNS(NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT);
		markup.setAttribute(Operation.STATUS_ATTR, Operation.INSERTED_VALUE);
		markup.setPrefix(NDIFF_PREFIX);

		markup.appendChild(nodeInsert);

		/* if the node is the last one */
		if (splitPoint.nodeToSplit == null) {

			getNode(nn).refDomNode.insertBefore(markup, splitPoint.refNode);

		} else {

			Node nodeLeft = null;
			Node nodeRight = null;

			/* Left node */
			if (splitPoint.offsetToSplit > 0) {
				String contentLeft = splitPoint.nodeToSplit.substringData(0,
						splitPoint.offsetToSplit);
				nodeLeft = DOM.createTextNode(contentLeft);
			}

			/* Right node */
			if (splitPoint.offsetToSplit < splitPoint.nodeToSplit.getLength()) {
				String contentRight = splitPoint.nodeToSplit.substringData(
						splitPoint.offsetToSplit,
						splitPoint.nodeToSplit.getLength());
				nodeRight = DOM.createTextNode(contentRight);
			}

			if (nodeLeft != null)
				getNode(nn).refDomNode.insertBefore(nodeLeft,
						splitPoint.nodeToSplit);
			getNode(nn).refDomNode.insertBefore(markup, splitPoint.nodeToSplit);
			if (nodeRight != null)
				getNode(nn).refDomNode.insertBefore(nodeRight,
						splitPoint.nodeToSplit);
			getNode(nn).refDomNode.removeChild(splitPoint.nodeToSplit);
		}

	}

	/**
	 * Insert a subtree
	 * 
	 * @param nn
	 *           Key of the root of the subtree to insert
	 * @param at
	 *            Key of the parent node on which to append the child
	 * @param pos
	 *            Position of the node as the child
	 * @param nodeCount
	 *            Number of node contained in the subtree
	 * @param content
	 *            subtree to be inserted
	 * @param move
	 *            notify if the insertion is to mark as a move operation
	 */
	public void INSERT(Integer nodenumber, Integer at, Integer pos, Integer nodeCount,
			Node content, String move) {

		System.out.println(" INSERT nn:" + nodenumber + " at:" + at + " pos:" + pos
				+ " nodeCount:" + nodeCount);

		boolean wrapper = false;

		Node toIns = DOM.importNode(content, true);
		Node father = getNode(at).refDomNode;

		Element markup;

		// if the node is not an element mark as a wrapper
		if (toIns.getNodeType() != Node.ELEMENT_NODE) {
			Element tmp = DOM.createElementNS(NDIFF_NAMESPACE, TOperation.TEXT_OPERATION_ELEMENT);
			tmp.setPrefix(NDIFF_PREFIX);
			tmp.appendChild(toIns);
			markup = tmp;
			wrapper = true;
		} else
			markup = (Element) toIns;

		if (move == null) {

			if (!wrapper) {
				Attr attr = DOM.createAttributeNS(NDIFF_NAMESPACE, Operation.STATUS_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(Operation.INSERTED_VALUE);
				markup.setAttributeNode(attr);
			} else
				markup.setAttribute(Operation.STATUS_ATTR, Operation.INSERTED_VALUE);

		} else {

			if (!wrapper) {
				Attr attr = DOM.createAttributeNS(NDIFF_NAMESPACE, Operation.STATUS_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(SOperation.MOVEDFROM_VALUE);
				markup.setAttributeNode(attr);

				attr = DOM.createAttributeNS(NDIFF_NAMESPACE, SOperation.ID_ATTR);
				attr.setPrefix(NDIFF_PREFIX);
				attr.setValue(move);
				markup.setAttributeNode(attr);
			} else {
				markup.setAttribute(Operation.STATUS_ATTR, SOperation.MOVEDFROM_VALUE);
				markup.setAttribute(SOperation.ID_ATTR, move);
			}

		}

		Node refNode = findChildPosition(father, pos);
		if (refNode != null)
			father.insertBefore(markup, refNode);
		else
			father.appendChild(markup);

		addNodeList(nodenumber, at, toIns);
		propagationModify(markup.getParentNode());
	}

	/**
	 * Check if a node is a markup node
	 * 
	 * @param node
	 *            Node to be checked
	 * @return returns true if it is a markup node
	 */
	public boolean isMarkup(Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			if (node.getPrefix() != null) {
				if ((node.getPrefix().equals(NDIFF_PREFIX)))
					return true;
			}

			if (((Element) node).hasAttribute(NDIFF_PREFIX + ":"+ Operation.STATUS_ATTR))
				return true;

		}
		return false;
	}

	/**
	 * Check if a node is marked as removed. Nodes are marked as removed
	 * if they have one of the following attributes: 
	 * ndiff:status="deleted" 
	 * ndiff:status="movedTo"
	 * ndiff:status="deleted"
	 * ndiff:status="movedTo"
	 * 
	 * @param node
	 *            node to be checked
	 * @return returns true if the node is marked as removed
	 */
	public boolean isMarkupHowDelete(Node node) {

		System.out.println(" *** Test node:" + node.getNodeName());

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element tmp = (Element) node;

			System.out.println("Cast to Element node  prefix:" + tmp.getPrefix()
					+ " name:" + tmp.getNodeName());
			System.out.println("Attribute status:" + tmp.getAttribute(Operation.STATUS_ATTR));
			System.out.println("Attribute ndiff:status:"
					+ tmp.getAttribute(NDIFF_PREFIX + ":"+ Operation.STATUS_ATTR));

			// test sui nodi wrapper segnati come mossi
			if (tmp.getPrefix() != null) {
				if ((tmp.getPrefix().equals(NDIFF_PREFIX))
						&& (tmp.getAttribute(Operation.STATUS_ATTR).equals(SOperation.DELETED_VALUE)))
					return true;

				if ((tmp.getPrefix().equals(NDIFF_PREFIX))
						&& (tmp.getAttribute(Operation.STATUS_ATTR).equals(SOperation.MOVEDTO_VALUE)))
					return true;
			} else {
				if ((tmp.getAttribute(NDIFF_PREFIX + ":"+ Operation.STATUS_ATTR)
						.equals(SOperation.DELETED_VALUE)))
					return true;

				if ((tmp.getAttribute(NDIFF_PREFIX + ":"+ Operation.STATUS_ATTR)
						.equals(SOperation.MOVEDTO_VALUE)))
					return true;
			}

		}

		System.out.println("No deleted");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.vdom.Vtree#newNode(java.lang.Object, org.w3c.dom.Node,
	 * java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Rnode newNode(Object ownerVtree, Node refDomNode, Integer indexKey,
			Integer posFather, Integer posLikeChild) {
		return new Rnode(ownerVtree, refDomNode, indexKey, posFather,
				posLikeChild);
	}

	/**
	 * Propagates the modification (Bottom-up)
	 * 
	 * @param node
	 *            Node on which to start the propagation
	 */
	private void propagationModify(Node node) {
		Element eNode = (Element) node;
		
		if( !eNode.hasAttributeNS(NDIFF_PREFIX, Operation.STATUS_ATTR) ){
			Attr attr = DOM.createAttributeNS(NDIFF_NAMESPACE, Operation.STATUS_ATTR);
			attr.setPrefix(NDIFF_PREFIX);
			attr.setValue(Operation.MODIFIED_VALUE);
			((Element) node).setAttributeNode(attr);
		}

		if (node != root)
			propagationModify(node.getParentNode());
	}

	/**
	 * Insert the element via a left pre-visit(use counter
	 * for the insertion)
	 * 
	 * @param at
	 *            Parent node position
	 * @param node
	 *            Node to be inserted in the indexing
	 */
	private void recursiveAddNodeList(Integer at, Node node) {

		Rnode newNode = new Rnode();
		newNode.refDomNode = node;
		newNode.isNew = 1;
		newNode.indexKey = counter;
		newNode.posFather = at;
		nodeList.insertElementAt(newNode, counter);

		counter++;

		if (node.hasChildNodes())
			for (int i = 0; i < node.getChildNodes().getLength(); i++)
				recursiveAddNodeList(newNode.indexKey, node.getChildNodes()
						.item(i));
	}

	/**
	 * Remove the indexed nodes from start, for length positions
	 * 
	 * @param start
	 *            Starting index from where to start removing nodes
	 * @param length
	 *            Number of nodes to be removed from the indexing
	 */
	private void subNodeList(int start, int length) {
		for (int i = 0; i < length; i++) {
			nodeList.removeElementAt(start);
		}
	}

}
