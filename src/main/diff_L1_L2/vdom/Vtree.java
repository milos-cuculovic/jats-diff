/*****************************************************************************************
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

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.vdom;

import main.diff_L1_L2.exceptions.InputFileException;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.Vector;

/** Vtree abstract class
 * @author schirinz 
 * 
 *      
 */
public abstract class Vtree<T> extends DOMDocument {

	Logger logger = Logger.getLogger(getClass());

	public Vector<T> nodeList = new Vector<T>(); // Vnode Vector
	public int numNode = 0; // counter of present nodes

	/**
	 * Constructor
	 * 
	 * @param fileXML
	 *            XML file path
	 * @param ltrim
	 *            (true) remove left trailing whitespaces
	 * @param rtrim
	 *            (true) remove right trailing whitespaces
	 * @param collapse
	 *            (true) collapses internal whitespaces
	 * @param emptynode
	 *            (true) if set doesn't consider textnodes only containing
	 *            whitespaces
	 * @param commentnode
	 *            (true) if set doesn't consider comment nodes
	 * @throws InputFileException
	 *             Raised if not able to build the Vtree
	 */
	public Vtree(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML);

		//System.out.println("Creating Vtree on document:" + fileXML);

		strongNormalize(ltrim, rtrim, collapse, emptynode, commentnode);

		//System.out.println("TopDown Visit Start");
		// Compute base attributes
		visitaTopDown(root, 0, -1, 0);
	}

	/**
	 * Returns number of nodes
	 * 
	 * @return number of nodes
	 */
	public int count() {
		return nodeList.size();
	}

	/**
	 * Return the index node
	 * 
	 * @param index
	 *            index
	 * @return node in index position
	 */
	public T getNode(int index) {
		return nodeList.get(index);
	}

	/**
	 * Abstract method to be implemented by the class extending Vtree
	 * 
	 * @param ownerVtree
	 *            owner Tree
	 * @param DOMnode
	 *            DOM node of reference
	 * @param indexKey
	 *            access key for the node
	 * @param posFather
	 *            access key for the parent node
	 * @param posLikeChild
	 *            position as child
	 * @return nuovo nodo del tipo opportuno
	 */
	public abstract T newNode(Object ownerVtree, Node DOMnode,
			Integer indexKey, Integer posFather, Integer posLikeChild);

	/**
	 * Calculate base attribute of the node. Inherited attributes
	 * 
	 * @param node
	 *            current DOM node
	 * @param index
	 *            node index
	 * @param posFather
	 *            parent node index
	 * @param posLikeChild
	 *            position of the node as child
	 */
	private void visitaTopDown(Node node, int index, int posFather,
			int posLikeChild) {
		if (node != null) {
			index = numNode;
			nodeList.add(index,
					newNode(this, node, numNode, posFather, posLikeChild));
			numNode++;

			if (node.hasChildNodes())
				for (int i = 0; i < node.getChildNodes().getLength(); i++)
					visitaTopDown(node.getChildNodes().item(i), numNode, index,
							i);

		}
	}

}