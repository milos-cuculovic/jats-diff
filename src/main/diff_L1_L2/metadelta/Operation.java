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
package main.diff_L1_L2.metadelta;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.vdom.diffing.Dnode;
import org.w3c.dom.Node;

/**
 * Abstract class defining operations which make the METADELTA
 *
 * @author Mike
 *
 */
public abstract class Operation {

	public static final byte DELETE_TREE = 0;
	public static final byte INSERT_TREE = 1;

	public static final byte DELETE_NODE = 2;
	public static final byte INSERT_NODE = 3;

	public static final byte MOVE_TO = 4;
	public static final byte MOVE_FROM = 5;

	public static final byte CONTEXT_MOVE_TO = 6;
	public static final byte CONTEXT_MOVE_FROM = 7;

	public static final byte INSERT_TEXT = 8;
	public static final byte DELETE_TEXT = 9;

	public static final byte INSERT_ATT = 10;
	public static final byte DELETE_ATT = 11;
	public static final byte CHANGE_VALUE_ATT = 12;

	public static final byte MERGE_TO = 13;
	public static final byte MERGE_FROM = 14;

	public static final byte SPLIT_TO = 15;
	public static final byte SPLIT_FROM = 16;

	public static final byte UPGRADE_TO = 17;
	public static final byte UPGRADE_FROM = 18;

	public static final byte DOWNGRADE_TO = 19;
	public static final byte DOWNGRADE_FROM = 20;

	public static final byte MOVETEXT_TO = 21;
	public static final byte MOVETEXT_FROM = 22;

	public static final byte INSERT_STYLE = 23;
	public static final byte UPDATE_STYLE_FROM = 24;
	public static final byte UPDATE_STYLE_TO = 25;
	public static final byte DELETE_STYLE = 26;

	public static final byte INSERT_STYLE_TEXT = 27;
	public static final byte DELETE_STYLE_TEXT = 28;
	public static final byte UPDATE_STYLE_TEXT_FROM = 29;
	public static final byte UPDATE_STYLE_TEXT_TO = 30;

	//Defining a standard name for all common attributes
	public static final String NODE_NUMBER_ATTR = "nodenumber";
	public static final String NODE_NUMBER_A_ATTR = "nodenumberA";
	public static final String NODE_NUMBER_B_ATTR = "nodenumberB";
	public static final String STATUS_ATTR = "status";

	//and for common values as well
	public static final String MODIFIED_VALUE = "modifiedd";
	public static final String INSERTED_VALUE = "inserted";
	public static final String DELETED_VALUE = "deleted";

	public byte type; // Operation type
	public Dnode nodeA; // Vnode referring on the document A operation
	public Dnode nodeB; // Vnode referring on the document B operation

	public Node refContent; // Reference to the Dom node

	/**
	 * Adds the xml operation structure and links it as the last child of the root element of the DOMdocument delta
	 *
	 * @param delta DOMdocument relativo al delta, a cui aggiungere l'operazione
	 */
	abstract public void dump(DOMDocument delta);

	/**
	 * Set base values of the operation
	 *
	 * @param type Operation type
	 * @param nodeA Original document node on which some operation applied
	 * @param nodeB Modified document node on which some operation applied
	 */
	public void setBaseInfo(byte type, Dnode nodeA, Dnode nodeB) {
		this.type = type;
		this.nodeA = nodeA;
		this.nodeB = nodeB;

	}

	/**
	 * Returns a string as to help the Debug phase, regarding information on the operation
	 *
	 * @return Returns a string representing the operation(Debug purpose)
	 */
	abstract public String show();

}
