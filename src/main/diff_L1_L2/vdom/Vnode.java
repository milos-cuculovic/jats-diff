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

package main.diff_L1_L2.vdom;

import org.w3c.dom.Node;

/** Vnode Class
 * @author schirinz
 */
public class Vnode {

	public Object ownerVtree; // Reference to the Vtree of belonging
	public Integer indexKey; // Vnode index
	public Node refDomNode; // Reference to the DOM node
	public Integer posFather; // Position of the father node
	public Integer posLikeChild; // Child node position

	/**
	 * Constructor
	 */
	public Vnode() {
	}

	/**
	 * Vnode Constructor
	 * 
	 * @param indexKey
	 *            Node's index to be used as access key
	 * @param refDomNode
	 *			  DOM node reference
	 * @param posFather
	 *            Father node position
	 * @param posLikeChild
	 *            position of the node as child
	 */
	public Vnode(Integer indexKey, Node refDomNode, Integer posFather,
			Integer posLikeChild) {
		setIndexKey(indexKey);
		setRefDomNode(refDomNode);
		setPosFather(posFather);
		setPosLikeChild(posLikeChild);
	}

	/**
	 * Return node's index
	 * 
	 * @return node's index
	 */
	public Integer getIndexKey() {
		return indexKey;
	}

	/**
	 * Return the Vtree which owns the node
	 * 
	 * @return Vtree
	 */
	public Object getOwnerVtree() {
		return ownerVtree;
	}

	/**
	 * Returns the index of the parent node
	 * 
	 * @return index of the parent node
	 */
	public Integer getPosFather() {
		return posFather;
	}

	/**
	 * Returns index position as child
	 * 
	 * @return index position as child
	 */
	public Integer getPosLikeChild() {
		return posLikeChild;
	}

	/**
	 * Returns the DOM at which the node is linked 
	 * 
	 * @return DOM at which the node is linked 
	 */
	public Node getRefDomNode() {
		return refDomNode;
	}

	/**
	 * Set the node index
	 * 
	 * @param indexKey
	 *            node index
	 */
	public void setIndexKey(Integer indexKey) {
		this.indexKey = indexKey;
	}

	/**
	 * Set the Vtree object to which the node belongs
	 * 
	 * @param ownerVtree
	 *            Vtree owner
	 */
	public void setOwnerVtree(Object ownerVtree) {
		this.ownerVtree = ownerVtree;
	}

	/**
	 * Set the index of the parent node
	 * 
	 * @param posFather
	 *            index of the parent node
	 */
	public void setPosFather(Integer posFather) {
		this.posFather = posFather;
	}

	/**
	 * Set the index position as child node
	 * 
	 * @param posLikeChild
	 *            index as child
	 */
	public void setPosLikeChild(Integer posLikeChild) {
		this.posLikeChild = posLikeChild;
	}

	/**
	 * Set the DOM node to which node is linked to
	 * 
	 * @param refDomNode
	 *            DOM node
	 */
	public void setRefDomNode(Node refDomNode) {
		this.refDomNode = refDomNode;
	}

}
