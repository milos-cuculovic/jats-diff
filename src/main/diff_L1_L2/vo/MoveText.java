/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import main.diff_L1_L2.vdom.diffing.Dnode;

/**
 *
 * @author Sammy Guergachi <sguergachi at gmail.com>
 */
public class MoveText {

	Integer positionFrom;
	Integer positionTo;
	String text;
	Dnode nodeA;
	Dnode nodeB;

	public Integer getPositionFrom() {
		return positionFrom;
	}

	public void setPositionFrom(Integer positionFrom) {
		this.positionFrom = positionFrom;
	}

	public Integer getPositionTo() {
		return positionTo;
	}

	public void setPositionTo(Integer positionTo) {
		this.positionTo = positionTo;
	}

	public Dnode getNodeA() {
		return nodeA;
	}

	public void setNodeA(Dnode nodeA) {
		this.nodeA = nodeA;
	}

	public Dnode getNodeB() {
		return nodeB;
	}

	public void setNodeB(Dnode nodeB) {
		this.nodeB = nodeB;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "text: " + this.text
			+ " | position-from: " + this.positionFrom
			+ " | position-to: " + this.positionTo;
	}

}
