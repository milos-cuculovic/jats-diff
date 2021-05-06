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

package main.diff_L1_L2.vdom.diffing;

import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.core.ATTdiff;
import main.diff_L1_L2.core.TXTdiff;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.relation.Fragment;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;

import java.util.HashMap;

/**
 * DOM node extension to be used during diffing phase
 * @author Mike
 */
public class Dnode extends Vnode {

	public Integer numChildSubtree; // total number of children in the subtree
	public Integer inRel; // Relation type of the node

	// partition phase
	public Integer weight = 1; // Peso del sottoalbero radicato - Peso del testo
	// content
	public String hashNode = null; // Contiene un valore hash che calcolato sul
	// node
	public String hashTree = null; // Contiene un valore hash calcolato sul nodo
	// e sul suo sottoalbero

	// findUpdate phase
	public Sequence Sequence; // In case of context node it is the sequence
	public HashMap<Integer, Likeness> likeness; // Similarity vector

	// findMerge phase
	public HashMap<Integer, Containess> containess; // Similarity vector

	// propagation phase
	public HashMap<String, Fragment> fragmentList = new HashMap<String, Fragment>();
	public String hashFragment; // hash only on node fragments

	// deltaderive phase
	public Integer insOnMe = 0; // number of nodes to be inserted

	/**
	 * Constructor
	 *
	 * @param ownerVtree
	 *            Dtree owner of the node
	 * @param refDomNode
	 *            Reference DOM node
	 * @param indexKey
	 *            Node index
	 * @param posFather
	 *            Node parent index
	 * @param posLikeChild
	 *            Position of the node as child
	 */
	public Dnode(Object ownerVtree, Node refDomNode, int indexKey,
			int posFather, int posLikeChild) {
		super(indexKey, refDomNode, posFather, posLikeChild);
		setOwnerVtree(ownerVtree);
		containess = new HashMap<Integer, Containess>();
		likeness = new HashMap<Integer, Likeness>();
		numChildSubtree = 0;
		inRel = Relation.NO;
	}

	/**
	 * computer similarity and metadelta between the two Vnodes
	 * nodeB
	 *
	 * @param nodeB
	 *            node to be compared with
	 */
	private void computeLikeness(Dnode nodeB) {
		if ((this.refDomNode.getNodeType() == Node.TEXT_NODE)
				&& (nodeB.refDomNode.getNodeType() == Node.TEXT_NODE)) {
			likeness.put(nodeB.indexKey, TXTdiff.compute(this, nodeB));
		}

		if ((this.refDomNode.getNodeType() == Node.ELEMENT_NODE)
				&& (nodeB.refDomNode.getNodeType() == Node.ELEMENT_NODE)) {
			likeness.put(nodeB.indexKey, ATTdiff.compute(this, nodeB));
		}
	}

	/**
	 * Computes METADELTA between nodeA and nodeB
	 *
	 * @param nodeB
	 *           Target Dnode
	 * @return new METADELTA
	 */
	public METAdelta getDeltaLikeness(Dnode nodeB) {
		if (!likeness.containsKey(nodeB.indexKey))
			computeLikeness(nodeB);
		return likeness.get(nodeB.indexKey).MINIdelta;
	}

	/**
	 * Return METADELTA between the node and NodeB
	 *
	 * @param indexB
	 *           Index of the node in the modified Document
	 * @return new METADELTA
	 */
	public METAdelta getDeltaLikeness(int indexB) {
		return likeness.get(indexB).MINIdelta;
	}

	/**
	 * Returns node's hash
	 *
	 * @return node's hash
	 */
	public String getHashNode() {
		return hashNode;
	}

	/**
	 * Returns subtree hash
	 *
	 * @return subtree hash
	 */
	public String getHashTree() {
		return hashTree;
	}

	/**
	 * Returns subtree total nodes number
	 *
	 * @return subtree total nodes number
	 */
	public Integer getNumChildSubtree() {
		return numChildSubtree;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see vdom.Vnode#getOwnerVtree()
	 */
	@Override
	public Dtree getOwnerVtree() {
		return (Dtree) ownerVtree;
	}

	/**
	 * Returns similarity between the node and NodeB, if not present in cache
	 * compute it and add it to cache
	 *
	 * @param nodeB
	 *           dNode to be compared with
	 * @return Similiraty result
	 */
	public int getSimilarity(Dnode nodeB) {
		if (!likeness.containsKey(nodeB.indexKey))
			computeLikeness(nodeB);
		return likeness.get(nodeB.indexKey).similarity;
	}

	/**
	 * Returns subtree weight
	 *
	 * @return subtree weight
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * Set hash value of the node
	 *
	 * @param hashNode
	 *           node's hash value
	 */
	public void setHashNode(String hashNode) {
		this.hashNode = hashNode;
	}

	/**
	 * Set hash value of the subtree
	 *
	 * @param hashTree
	 *            Hash value to be set
	 */
	public void setHashTree(String hashTree) {
		this.hashTree = hashTree;
	}

	/**
	 * Set subtree total number of contained nodes
	 *
	 * @param numChildSubtree
	 *           Total number of contained nodes
	 */
	public void setNumChildSubtree(Integer numChildSubtree) {
		this.numChildSubtree = numChildSubtree;
	}

	/**
	 * Set subtree weight
	 *
	 * @param weight
	 *            Subtree weight
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
