/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import com.github.difflib.patch.AbstractDelta;
import main.diff_L1_L2.vdom.diffing.Dnode;

/**
 * Temporary class for Deltas and nodes
 *
 * @author sasha
 *
 */
public class NodeDeltaStyle {

    public String content;
    public String rawText;
    public String openTag;
    public String closeTag;
    public String action;
    public int positionStart;
    public int positionEnd;
    public Dnode a;
    public Dnode b;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getOpenTag() {
        return openTag;
    }

    public void setOpenTag(String openTag) {
        this.openTag = openTag;
    }

    public String getCloseTag() {
        return closeTag;
    }

    public void setCloseTag(String closeTag) {
        this.closeTag = closeTag;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Dnode getA() {
        return a;
    }

    public void setA(Dnode a) {
        this.a = a;
    }

    public Dnode getB() {
        return b;
    }

    public void setB(Dnode b) {
        this.b = b;
    }

    public int getPositionStart() {
        return positionStart;
    }

    public void setPositionStart(int positionStart) {
        this.positionStart = positionStart;
    }

    public int getPositionEnd() {
        return positionEnd;
    }

    public void setPositionEnd(int positionEnd) {
        this.positionEnd = positionEnd;
    }

    @Override
    public String toString() {
        return " content: " + this.content
                + " rawText: " + this.rawText
                + " openTag: " + this.openTag
                + " closeTag: " + this.closeTag
                + " action: " + this.action + "\n";
    }
}
