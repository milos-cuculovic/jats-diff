/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.vo;

import java.util.ArrayList;
import java.util.List;
import main.diff_L1_L2.vdom.diffing.Dnode;

/**
 *
 * @author sasha
 */
public class TextChangeData {

    public static final String ACTION_INSERT_STYLE = "INSERT_STYLE";
    public static final String ACTION_DELETE_STYLE = "DELETE_STYLE";
    public static final String ACTION_UPDATE_STYLE_TO = "UPDATE_STYLE_TO";
    public static final String ACTION_UPDATE_STYLE_FROM = "UPDATE_STYLE_FROM";
    public static final String ACTION_UPDATE_TEXT_TO = "UPDATE_TEXT_TO";
    public static final String ACTION_UPDATE_TEXT_FROM = "UPDATE_TEXT_FROM";
    public static final String ACTION_INSERT_TEXT = "INSERT_TEXT";
    public static final String ACTION_DELETE_TEXT = "DELETE_TEXT";
    public static final String ACTION_MOVE_TEXT_FROM = "MOVE_TEXT_FROM";
    public static final String ACTION_MOVE_TEXT_TO = "MOVE_TEXT_TO";

    private static TextChangeData instance = null;

    List<TextChange> textChangeData;

    public TextChangeData() {
        textChangeData = new ArrayList<>();

    }

    public static TextChangeData getInstance() {
        if (instance == null) {
            instance = new TextChangeData();
        }

        return instance;
    }

    public void add(TextChange textChange) {
        if (!textChangeData.contains(textChange)) {
            textChangeData.add(textChange);
        }

    }

    public void remove(TextChange textChange) {
        if (textChangeData.contains(textChange)) {
            textChangeData.remove(textChange);
        }

    }

    public List<TextChange> getAllTextChanges() {
        return textChangeData;
    }

    public int size() {
        return textChangeData.size();
    }

    public TextChange get(int key) {
        return textChangeData.get(key);
    }

    public TextChange findElementByNodeBandAction(String action, Dnode node) {

        TextChange result = textChangeData.stream()
                .filter(textChange -> node.getIndexKey().intValue() == textChange.nodeB.getIndexKey().intValue()
                && action.equals(textChange.getAction()))
                .findAny()
                .orElse(null);
        return result;

    }

    public TextChange findElementByNodeAandAction(String action, Dnode node) {
//		logger.info("AAAAAAAAAA" + action + node);
//		for (TextChange t : textChangeData) {
//			logger.info(t.action + " " + t.textSource + " " + t.textTarget + " " + t.getNodeA().indexKey + " " + t.getNodeB().getIndexKey());
//		}
        TextChange result = textChangeData.stream()
                .filter(textChange -> node.getIndexKey().intValue() == textChange.nodeA.getIndexKey().intValue()
                && action.equals(textChange.getAction()))
                .findAny()
                .orElse(null);
        return result;

    }

    //check is insert raw text object exists as text-move object
    public TextChange isInsertMoveObject(TextChange insertObj) {
        TextChange result = textChangeData.stream()
                .filter(textChange -> insertObj.textTarget.equals(textChange.getTextSource())
                && insertObj.textTarget.equals(textChange.getTextTarget())
                && textChange.action.equals(TextChangeData.ACTION_MOVE_TEXT_TO))
                .findAny()
                .orElse(null);
        return result;
    }

    public TextChange isDeleteMoveObject(TextChange deleteObj) {
        TextChange result = textChangeData.stream()
                .filter(textChange -> deleteObj.textSource.equals(textChange.getTextSource())
                && deleteObj.textSource.equals(textChange.getTextTarget())
                && textChange.action.equals(TextChangeData.ACTION_MOVE_TEXT_FROM))
                .findAny()
                .orElse(null);
        return result;
    }

    public TextChange isUpdateMoveObject(TextChange updateObj) {
        TextChange result = textChangeData.stream()
                .filter(textChange -> updateObj.textTarget.equals(textChange.getTextTarget())
                && textChange.action.equals(TextChangeData.ACTION_MOVE_TEXT_TO))
                .findAny()
                .orElse(null);
        return result;
    }

    public Boolean contains(TextChange obj) {
        if (textChangeData.contains(obj)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
