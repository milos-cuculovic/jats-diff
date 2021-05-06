/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

/**
 *
 * @author sasa simic
 */
public class StyleMatcherData implements Cloneable {

    String content;
    Integer position;
//    String operation;
    String extendContent;
    String preContent;
    String tag;
    String endTag;
    String localContent;
    String styleTag;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

//    public String getOperation() {
//        return operation;
//    }
//
//    public void setOperation(String operation) {
//        this.operation = operation;
//    }
    public String getExtendContent() {
        return extendContent;
    }

    public void setExtendContent(String extendContent) {
        this.extendContent = extendContent;
    }

    public String getPreContent() {
        return preContent;
    }

    public void setPreContent(String preContent) {
        this.preContent = preContent;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLocalContent() {
        return localContent;
    }

    public void setLocalContent(String localContent) {
        this.localContent = localContent;
    }

    public String getEndTag() {
        return endTag;
    }

    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    public String getStyleTag() {
        return styleTag;
    }

    public void setStyleTag(String styleTag) {
        this.styleTag = styleTag;
    }

    @Override
    public String toString() {
        return "\n-->[Content: " + this.content
                + " \nPosition: " + this.position
                + " \nPre content: " + this.preContent
                + " \nExtend content: " + this.extendContent
                + " \nTag: " + this.tag + "]<--\n";
    }

    /**
     *
     * @return StyleMatcherData
     */
    public StyleMatcherData cloneObject() {
        StyleMatcherData styleMatcherData = null;
        try {
            styleMatcherData = (StyleMatcherData) super.clone();
        } catch (CloneNotSupportedException e) {

//			logger.info("ERROR LINE: " + e.getStackTrace()[0].getLineNumber()
//				+ " FILE: " + e.getStackTrace()[0].getFileName()
//				+ " Message: " + e.getMessage());
        }
        return styleMatcherData;
    }

}
