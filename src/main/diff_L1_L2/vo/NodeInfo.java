/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import main.diff_L1_L2.vdom.diffing.Dnode;

/**
 *
 * @author sasha
 */
public class NodeInfo {

	public Dnode node;
	public int relationInf;
	public int relationSup;
	public int index;

	public Dnode getNode() {
		return node;
	}

	public void setNode(Dnode node) {
		this.node = node;
	}

	public int getRelationInf() {
		return relationInf;
	}

	public void setRelationInf(int relationInf) {
		this.relationInf = relationInf;
	}

	public int getRelationSup() {
		return relationSup;
	}

	public void setRelationSup(int relationSup) {
		this.relationSup = relationSup;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
