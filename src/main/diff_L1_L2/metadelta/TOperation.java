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

package main.diff_L1_L2.metadelta;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.vdom.diffing.Dnode;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Wrapper for text related operations
 * @author schirinz 
 */
public class TOperation extends Operation {

	Logger logger = Logger.getLogger(getClass());

	public Integer pStart; // start position of the text to be either removed or inserted
	public Integer length; // Length of such text
	
	//all Elements and Attributes names gathered all here to modularize everything
	public static final String TEXT_OPERATION_ELEMENT = "text-edit";
	public static final String TEXT_NODE_ELEMENT = "text-update";
	
	public static final String TEXT_OP_ATTR = "op";
	public final static String POS_ATTR = "pos";
	public final static String LENGTH_ATTR = "length";
	
	public final static String INSERT_OP_VALUE = "text-inserted";
	public final static String DELETE_OP_VALUE = "text-deleted";
	public static final String EDITING_VALUE = "editing";

	/**
	 * Constructor
	 * 
	 * @param type
	 *            Operation type
	 * @param nodeA
	 *            Original Document node
	 * @param nodeB
	 *            Modified Document node
	 * @param pStart
	 *            Text Offset from where to start the operation
	 * @param length
	 *            Length of the text
	 */
	public TOperation(byte type, Dnode nodeA, Dnode nodeB, int pStart,
			int length) {
		setBaseInfo(type, nodeA, nodeB);
		this.pStart = pStart;
		this.length = length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.metadelta.Operation#dump(vdom.DOMDocument)
	 */
	@Override
	public void dump(DOMDocument Ndelta) {

		Element newOp;
		Text content = null;

		switch (type) {

		case Operation.DELETE_TEXT:
			newOp = Ndelta.DOM.createElement(TEXT_NODE_ELEMENT);
			if(nodeA != null) {
				newOp.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
			}
			if(nodeB != null) {
				newOp.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			}
			newOp.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			newOp.setAttribute(POS_ATTR, pStart.toString());
			newOp.setAttribute(LENGTH_ATTR, length.toString());
			newOp.setAttribute(TEXT_OP_ATTR, DELETE_OP_VALUE );

			content = (Text) nodeA.refDomNode;
			newOp.appendChild(Ndelta.DOM.createTextNode(content.substringData(
					pStart, length)));
			Ndelta.root.appendChild(newOp);
			break;

		case Operation.INSERT_TEXT:
			newOp = Ndelta.DOM.createElement(TEXT_NODE_ELEMENT);
			if(nodeA != null) {
				newOp.setAttribute(Operation.NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
			}
			if(nodeB != null) {
				newOp.setAttribute(Operation.NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			}
			newOp.setAttribute(POS_ATTR, pStart.toString());
			newOp.setAttribute(LENGTH_ATTR, length.toString());
			newOp.setAttribute(TEXT_OP_ATTR, INSERT_OP_VALUE );

			content = (Text) nodeB.refDomNode;
			newOp.appendChild(Ndelta.DOM.createTextNode(content.substringData(
					pStart, length)));
			Ndelta.root.appendChild(newOp);
			break;
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

		case Operation.DELETE_TEXT:
			ret = "DeleteText  indexNodeA:" + nodeA.indexKey.toString()
					+ " indexNodeB:" + nodeB.indexKey.toString() + "  pStart:"
					+ pStart + "  length:" + length;
			break;

		case Operation.INSERT_TEXT:
			ret = "InsertText indexnodeA:" + nodeA.indexKey.toString()
					+ " indexNodeB:" + nodeB.indexKey.toString() + "  pStart:"
					+ pStart + "  length:" + length;
			break;

		}

		return ret;
	}

}
