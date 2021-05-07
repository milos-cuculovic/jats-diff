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
import org.w3c.dom.Element;

/**
 * operations on attributes wrapper
 * @author schirinz 
 */
public class AOperation extends Operation {

	public String attName; // Attribute name to delete or create
	public String newValue; // attribute new name
	public String oldValue; // attribute old name
	
	//all Elements and Attributes names gathered all here to modularize everything
	public final static String ELEMENT_NAME = "update-attribute";
	public final static String OPERATION_ATTR = "op";
	public final static String OLDVALUE_ATTR = "oldvalue";
	public final static String NEWVALUE_ATTR =  "newvalue";
	public final static String NAME_ATTR = "name";
	
	public final static String INSERT_OPERATION = "insert-attr";
	public final static String DELETE_OPERATION = "delete-attr";
	public final static String CHANGE_OPERATION = "change-attr";

	/**
	 * Operation on attributes constructor
	 * 
	 * @param type
	 *            operation Type
	 * @param nodeA
	 *            node the operation is referring to (original document)
	 * @param nodeB
	 *            node the operation is referring to (modified document)
	 * @param attName
	 *            Name of the attribute (modified/inserted/deleted)
	 * @param newValue
	 *            new value of the attribute
	 * @param oldValue
	 *            old value of the attribute
	 */
	public AOperation(byte type, Dnode nodeA, Dnode nodeB, String attName,
			String newValue, String oldValue) {
		setBaseInfo(type, nodeA, nodeB);
		this.attName = attName;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.metadelta.Operation#dump(vdom.DOMDocument)
	 */
	@Override
	public void dump(DOMDocument Ndelta) {
		Element newOp;
		switch (type) {

		case DELETE_ATT:
			newOp = Ndelta.DOM.createElement(ELEMENT_NAME);
			newOp.setAttribute(OPERATION_ATTR,DELETE_OPERATION);
			if(nodeA != null) {
				newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
			}
			if(nodeB != null) {
				newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			}
			newOp.setAttribute(NAME_ATTR, attName);
			newOp.setAttribute(OLDVALUE_ATTR, oldValue);
			Ndelta.root.appendChild(newOp);
			break;

		case INSERT_ATT:
			newOp = Ndelta.DOM.createElement(ELEMENT_NAME);
			newOp.setAttribute(OPERATION_ATTR,INSERT_OPERATION);
			if(nodeA != null) {
				newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
			}
			if(nodeB != null) {
				newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			}
			newOp.setAttribute(NAME_ATTR, attName);
			newOp.setAttribute(NEWVALUE_ATTR, newValue);
			Ndelta.root.appendChild(newOp);
			break;

		case CHANGE_VALUE_ATT:
			newOp = Ndelta.DOM.createElement(ELEMENT_NAME);
			newOp.setAttribute(OPERATION_ATTR, CHANGE_OPERATION);
			if(nodeA != null) {
				newOp.setAttribute(NODE_NUMBER_A_ATTR, nodeA.indexKey.toString());
			}
			if(nodeB != null) {
				newOp.setAttribute(NODE_NUMBER_B_ATTR, nodeB.indexKey.toString());
			}
			newOp.setAttribute(NAME_ATTR, attName);
			newOp.setAttribute(OLDVALUE_ATTR, oldValue);
			newOp.setAttribute(NEWVALUE_ATTR, newValue);
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
		case CHANGE_VALUE_ATT:
			ret = "ChaneAttValue indexNodeA:" + nodeA.indexKey.toString()
					+ " indexNodeB:" + nodeB.indexKey.toString() + "  Node:"
					+ attName + "  NewValue:" + newValue + " OldVAlue:"
					+ oldValue;
			break;

		case INSERT_ATT:
			ret = "InsertAtt indexNodeA:" + nodeA.indexKey.toString()
					+ " indexNodeB:" + nodeB.indexKey.toString() + "  Node:"
					+ attName + "  NewValue:" + newValue;
			break;

		case DELETE_ATT:
			ret = "DeleteAtt indexNodeA:" + nodeA.indexKey.toString()
					+ "  indexNodeB:" + nodeB.indexKey.toString() + "  Node:"
					+ attName + "  NewValue:" + oldValue;
			break;
		}

		return ret;
	}
}
