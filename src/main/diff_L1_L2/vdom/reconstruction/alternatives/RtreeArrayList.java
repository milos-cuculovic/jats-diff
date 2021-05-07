/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main.diff_L1_L2.vdom.reconstruction.alternatives;

import main.diff_L1_L2.vdom.reconstruction.Rtree;
import main.diff_L1_L2.exceptions.InputFileException;

import java.util.ArrayList;

/**
 * 
 * Alternativa alla classe Rtree, usa degli ArrayList al posto dei Vector Per
 * ora è utilizzata solo nei main.diff_L1_L2.test delle performance
 * 
 */
public class RtreeArrayList extends
        Rtree {

	public ArrayList<Integer> editingNode = new ArrayList<Integer>();

	public RtreeArrayList(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML, ltrim, rtrim, collapse, emptynode, commentnode);

	}

}
