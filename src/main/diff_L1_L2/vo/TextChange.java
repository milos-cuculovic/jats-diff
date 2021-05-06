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
public class TextChange implements Cloneable {

    Integer positionFrom;
    Integer positionTo;
    String textSource;
    String textTarget;
    String textSourceOrig;
    String textTargetOrig;
    String action;
    Dnode nodeA;
    Dnode nodeB;
    Dnode parentNodeA;
    Dnode parentNodeB;

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

    public String getTextSource() {
        return textSource;
    }

    public void setTextSource(String textSource) {
        this.textSource = textSource;
    }

    public String getTextTarget() {
        return textTarget;
    }

    public void setTextTarget(String textTarget) {
        this.textTarget = textTarget;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Dnode getParentNodeA() {
        return parentNodeA;
    }

    public void setParentNodeA(Dnode parentNodeA) {
        this.parentNodeA = parentNodeA;
    }

    public Dnode getParentNodeB() {
        return parentNodeB;
    }

    public void setParentNodeB(Dnode parentNodeB) {
        this.parentNodeB = parentNodeB;
    }

    public String getTextSourceOrig() {
        return textSourceOrig;
    }

    public void setTextSourceOrig(String textSourceOrig) {
        this.textSourceOrig = textSourceOrig;
    }

    public String getTextTargetOrig() {
        return textTargetOrig;
    }

    public void setTextTargetOrig(String textTargetOrig) {
        this.textTargetOrig = textTargetOrig;
    }

    @Override
    public String toString() {
        return " | action: " + this.action
                + " | text from: " + this.textSource
                + " | text to: " + this.textTarget
                + " | text from orig: " + this.textSourceOrig
                + " | text to orig: " + this.textTargetOrig
                + " | position-from: " + this.positionFrom
                + " | position-to: " + this.positionTo + "\n";

    }

    /**
     *
     * @return TextChange
     */
    public TextChange cloneObject() {
        TextChange textChange = null;
        try {
            textChange = (TextChange) super.clone();
        } catch (CloneNotSupportedException e) {

//            logger.info("ERROR LINE: " + e.getStackTrace()[0].getLineNumber()
//                    + " FILE: " + e.getStackTrace()[0].getFileName()
//                    + " Message: " + e.getMessage());
        }
        return textChange;
    }
}
