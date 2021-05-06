/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.relation.Field;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author sasa simic <simic@mdpi.com>
 */
public class NodeHandler {

	private String nodeName;
	private String currentNode;
	private String prevNode;
	private List<String> nextNode;
	private Field processField;
	Dnode node;
	private Boolean exclude = false;
	private int relationNodeInf;
	private int relationNodeSup;

	public NodeHandler() {
		this.nextNode = new ArrayList<>();
	}

	public String getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(String currentNode) {
		this.currentNode = currentNode;
	}

	public String getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(String prevNode) {
		this.prevNode = prevNode;
	}

	public List<String> getNextNode() {
		return nextNode;
	}

	public void setNextNode(List<String> nextNode) {
		this.nextNode = nextNode;
	}

	public void addNextNode(String nextNodeValue) {
		if (!nextNodeValue.isEmpty() && !this.nextNode.contains(nextNodeValue)) {
			this.nextNode.add(nextNodeValue);
		}

	}

	public Dnode getNode() {
		return node;
	}

	public void setNode(Dnode node) {
		this.node = node;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Field getProcessField() {
		return processField;
	}

	public void setProcessField(Field processField) {
		this.processField = processField;
	}

	public Boolean getExclude() {
		return exclude;
	}

	public void setExclude(Boolean exclude) {
		this.exclude = exclude;
	}

	public int getRelationNodeInf() {
		return relationNodeInf;
	}

	public void setRelationNodeInf(int relationNodeInf) {
		this.relationNodeInf = relationNodeInf;
	}

	public int getRelationNodeSup() {
		return relationNodeSup;
	}

	public void setRelationNodeSup(int relationNodeSup) {
		this.relationNodeSup = relationNodeSup;
	}

	@Override
	public String toString() {
		return "Node Hadler OBJECT-> [\n"
			+ "\t| Node Name: " + this.nodeName
			+ "\t| Current node: " + this.currentNode
			+ "\t| Next node: " + this.nextNode
			+ "\t| Prev node: " + this.prevNode
			+ "\t| relation inf: " + this.relationNodeInf
			+ "\t| relation sup: " + this.relationNodeSup
			+ "\t| Exclude: " + this.exclude
			+ "\t| Dnode " + this.node.getRefDomNode() + "\n]";

	}

}
