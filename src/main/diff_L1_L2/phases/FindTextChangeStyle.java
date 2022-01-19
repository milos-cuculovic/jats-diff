/** ***************************************************************************************
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
 *
 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **************************************************************************************** */
package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.Iterator;
import main.diff_L1_L2.vdom.diffing.Dnode;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import main.diff_L1_L2.core.Ndiff;
import main.diff_L1_L2.vo.MoveText;
import main.diff_L1_L2.vo.MoveTextData;
import main.diff_L1_L2.vo.NodeDeltaStyle;
import main.diff_L1_L2.vo.TextChange;
import main.diff_L1_L2.vo.TextChangeData;
import java.util.ListIterator;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;

/**
 * @author Sasa Simic
 *
 *
 */
public class FindTextChangeStyle extends Phase {

    //found array dat by jats-diff
    public List<List<Dnode>> foundNodeUpdate = new ArrayList<>();
    public List<Dnode> foundNodeInsert = new ArrayList<>();
    public List<Dnode> foundNodeDelete = new ArrayList<>();

    MoveTextData moveTextData = new MoveTextData();
    public int maxSimilarity;
    TextChangeData textChangeData;

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public FindTextChangeStyle(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
            Nconfig cfg) {
        super(SearchField, Rel, Ta, Tb, cfg);
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see ndiff.phases.Phase#compute()
     */
    @Override
    public void compute() throws ComputePhaseException {
        try {
            textChangeData = TextChangeData.getInstance();
            findStyleChanges();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected void findStyleChanges() {
        try {
            //call function initChanges prepare three list foundNodeUpdate/foundNodeInsert/foundNodeDelete
            this.initChanges();

            //observe changes in the text - updates (style, for update points no insert or delete node)
            this.getTextStyleChanges();
            //show|hide debug info about movetext
            this.debugStyleChanges();

            //set text change fragments
            this.setTextChangeFragment();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void initChanges() {
        // Text nodes existing on A and not existing on B are deleted nodes
        try {
            Vector<Interval> dom = SF.getIntervalsOnX();
            Interval toProcess;
            for (int i = 0; i < dom.size(); i++) {
                toProcess = dom.get(i);

                for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                    if (A.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
                        foundNodeDelete.add(A.getNode(k));
                    }
                }
            }

            // Text nodes existing on B and not existing on A are inserted nodes
            Vector<Interval> cod = SF.getIntervalsOnY();
            for (int i = 0; i < cod.size(); i++) {
                toProcess = cod.get(i);

                for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                    if (B.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
                        foundNodeInsert.add(B.getNode(k));
                    }
                }
            }

            // Deleted text nodes followed by insert and a given similarity are updated nodes
            List<Dnode> removeDeleteTmp = new ArrayList<>();
            List<Dnode> removeInsertTmp = new ArrayList<>();
            for (int i = 0; i < foundNodeDelete.size(); i++) {
                maxSimilarity = cfg.getIntPhaseParam(Nconfig.FindUpdate, "level");
                Dnode tmpAUpdate = null;
                Dnode tmpBUpdate = null;
                Boolean detectUpdate = Boolean.FALSE;
                if (foundNodeDelete.get(i).refDomNode.getNodeType() == Node.TEXT_NODE) {
                    tmpBUpdate = null;
                    for (int j = 0; j < foundNodeInsert.size(); j++) {

                        if (foundNodeInsert.get(j).refDomNode.getNodeType() == Node.TEXT_NODE) {
                            int tmpSimilarity = foundNodeDelete.get(i).getSimilarity(foundNodeInsert.get(j));
                            if (tmpSimilarity > maxSimilarity) {
                                maxSimilarity = tmpSimilarity;
                                tmpAUpdate = foundNodeDelete.get(i);
                                tmpBUpdate = foundNodeInsert.get(j);
                                detectUpdate = Boolean.TRUE;
                            }
                        }
                    }
                }
                //prepare update list in list nodes
                if (detectUpdate == Boolean.TRUE) {
                    removeDeleteTmp.add(tmpAUpdate);
                    removeInsertTmp.add(tmpBUpdate);
                    List<Dnode> tmpList = new ArrayList<>();
                    tmpList.add(tmpAUpdate);
                    tmpList.add(tmpBUpdate);
                    foundNodeUpdate.add(tmpList);
                }
            }

            //check and delete no necessary insert and delete
            for (Dnode r : removeInsertTmp) {
                if (foundNodeInsert.contains(r)) {
                    foundNodeInsert.remove(r);
                }
            }
            for (Dnode r : removeDeleteTmp) {
                if (foundNodeDelete.contains(r)) {
                    foundNodeDelete.remove(r);
                }
            }
            removeInsertTmp.clear();
            removeDeleteTmp.clear();

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find deltas using java-diff-utils
     */
    public void getTextStyleChanges() {
        Vector<NodeDeltaStyle> deltaChangesA = new Vector<>();
        Vector<NodeDeltaStyle> deltaChangesB = new Vector<>();

        // For each foundNodeUpdate:
        for (int i = 0; i < foundNodeUpdate.size(); i++) {
            Dnode a = A.getNode(foundNodeUpdate.get(i).get(0).getIndexKey());
            Dnode b = B.getNode(foundNodeUpdate.get(i).get(1).getIndexKey());
            String aText = a.refDomNode.getTextContent(); //.replaceAll("\n\r", "").replaceAll("\n", "").replaceAll("\r", "");
            String bText = b.refDomNode.getTextContent(); //.replaceAll("\n\r", "").replaceAll("\n", "").replaceAll("\r", "");

            //make array of strings split by space, try diffing whole words
            List<String> listOfTextA = new ArrayList<String>(Arrays.asList(aText.split("\\s+")));
            List<String> listOfTextB = new ArrayList<String>(Arrays.asList(bText.split("\\s+")));

            String patterOpenTags = Ndiff.prepareRexExpPaternAsString(false);
            String patterCloseTags = Ndiff.prepareRexExpPaternAsString(true);
            listOfTextA = this.tuneUpListOfText(listOfTextA, patterOpenTags, patterCloseTags, 0);
            listOfTextB = this.tuneUpListOfText(listOfTextB, patterOpenTags, patterCloseTags, 0);
            //use java-diff-utils to try find changes in text (return type action CHANGE, INSERT, DELETE position, source text, target text
            Patch<String> patch = DiffUtils.diff(listOfTextA, listOfTextB);

            AbstractDelta delta;

            //Collect all deltas for insert and delete and add in temporary array
            for (int pathIndex = 0; pathIndex < patch.getDeltas().size(); pathIndex++) {

                delta = patch.getDeltas().get(pathIndex);
                String targetText = "";
                String sourceText = "";
                logger.info("DELTA CHANGES : " + delta);
                String action = delta.getType().toString();
                //Pattern pattern = Pattern.compile("(_\\|.+?\\|_)(.*?)(_\\|\\/.+?\\|_)");
                Pattern pattern = Pattern.compile("(_\\|.+?\\|_)(.*|.*?)(_\\|\\/.+?\\|_)");
                Matcher matcherSource;
                Matcher matcherTarget;

                sourceText = "";
                //concatenate all changes from array how to get one full string 1 INFO - #### SOURCE: _|italic|_Yersinia 2#### SOURCE: enterocolitica_|/italic|_
                for (int c = 0; c < delta.getSource().getLines().size(); c++) {
                    //it is necessary becsaue have situation as
//                    sourceText += delta.getSource().getLines().get(c).toString() + " ";
//                    if (Character.isWhitespace(delta.getSource().getLines().get(c).toString().charAt(0))) {
//                        sourceText += delta.getSource().getLines().get(c).toString().substring(1, delta.getSource().getLines().get(c).toString().length()) + " ";
//                    } else {
                    sourceText += delta.getSource().getLines().get(c).toString().trim() + " ";
//                    }
                }
                sourceText = sourceText.trim();

                matcherSource = pattern.matcher(sourceText);

                while (matcherSource.find()) {
                    NodeDeltaStyle nodeDelta = new NodeDeltaStyle();
                    nodeDelta.setA(a);
                    nodeDelta.setB(b);
                    nodeDelta.setAction(action);
                    nodeDelta.setContent(matcherSource.group(1) + matcherSource.group(2) + matcherSource.group(3));
                    nodeDelta.setRawText(matcherSource.group(2));
                    nodeDelta.setOpenTag(matcherSource.group(1));
                    nodeDelta.setCloseTag(matcherSource.group(3));
                    deltaChangesA.add(nodeDelta);
                }

                targetText = "";
                for (int t = 0; t < delta.getTarget().getLines().size(); t++) {

//                    if (Character.isWhitespace(delta.getTarget().getLines().get(t).toString().charAt(0))) {
//                        targetText += delta.getTarget().getLines().get(t).toString().substring(1, delta.getTarget().getLines().get(t).toString().length()) + " ";
//                    } else {
                    targetText += delta.getTarget().getLines().get(t).toString().trim() + " ";
//                    }

                }
                targetText = targetText.trim();

                matcherTarget = pattern.matcher(targetText);

                while (matcherTarget.find()) {
                    //logger.info("CHANGE TARGET MACHERR: " + targetText);

                    NodeDeltaStyle nodeDelta = new NodeDeltaStyle();
                    nodeDelta.setA(a);
                    nodeDelta.setB(b);
                    nodeDelta.setAction(action);
                    nodeDelta.setContent(matcherTarget.group(1) + matcherTarget.group(2) + matcherTarget.group(3));
                    nodeDelta.setRawText(matcherTarget.group(2));
                    nodeDelta.setOpenTag(matcherTarget.group(1));
                    nodeDelta.setCloseTag(matcherTarget.group(3));
                    deltaChangesB.add(nodeDelta);
                }

                //this part is needed to delete all matched words with style edits
                Iterator<NodeDeltaStyle> deltaAItr = deltaChangesA.iterator();
                Iterator<NodeDeltaStyle> deltaBItr;
                while (deltaAItr.hasNext()) {
                    NodeDeltaStyle nA = deltaAItr.next();
                    deltaBItr = deltaChangesB.iterator();
                    while (deltaBItr.hasNext()) {
                        NodeDeltaStyle nB = deltaBItr.next();
                        //logger.info("TEST XXX" + nA.content);
                        //logger.info("TEST XXX" + nB.content);
                        if (nB.action.equals(nA.action) && nB.content.equals(nA.content)) {

                            deltaBItr.remove();
                            deltaAItr.remove();
                            //deltaAItr = deltaChangesA.iterator();
                            //deltaBItr = deltaChangesB.iterator();
                        }
                    }
                }

                //logger.info(deltaChangesA);
                //logger.info(deltaChangesB);
                if (deltaChangesA.size() > 0 && deltaChangesB.size() > 0) {
                    for (NodeDeltaStyle tmpNA : deltaChangesA) {
                        TextChange textChanges = new TextChange();
                        textChanges.setAction(TextChangeData.ACTION_UPDATE_STYLE_FROM);
                        textChanges.setNodeA(tmpNA.a);
                        textChanges.setNodeB(tmpNA.b);
                        textChanges.setTextSourceOrig(sourceText);
                        textChanges.setTextTargetOrig(targetText);
                        textChanges.setTextSource(tmpNA.getContent());
                        textChanges.setPositionFrom(tmpNA.a.getRefDomNode().getNodeValue().indexOf(tmpNA.getContent()));
                        textChangeData.add(textChanges);
                    }

                    for (NodeDeltaStyle tmpNB : deltaChangesB) {
                        TextChange textChanges = new TextChange();
                        textChanges.setAction(TextChangeData.ACTION_UPDATE_STYLE_TO);
                        textChanges.setNodeA(tmpNB.a);
                        textChanges.setNodeB(tmpNB.b);
                        textChanges.setTextSourceOrig(sourceText);
                        textChanges.setTextTargetOrig(targetText);
                        textChanges.setTextTarget(tmpNB.getContent());
                        textChanges.setPositionTo(tmpNB.b.getRefDomNode().getNodeValue().indexOf(tmpNB.getContent()));
                        textChangeData.add(textChanges);

                    }
                } else if (deltaChangesA.size() > 0 && deltaChangesB.size() == 0) {

                    for (NodeDeltaStyle tmpNA : deltaChangesA) {
                        TextChange textChanges = new TextChange();
                        textChanges.setAction(TextChangeData.ACTION_DELETE_STYLE);
                        textChanges.setNodeA(tmpNA.a);
                        textChanges.setNodeB(tmpNA.b);
                        textChanges.setTextSource(tmpNA.getContent());
                        textChanges.setPositionFrom(tmpNA.a.getRefDomNode().getNodeValue().indexOf(tmpNA.getContent()));
                        textChangeData.add(textChanges);
                    }
                } else if (deltaChangesA.size() == 0 && deltaChangesB.size() > 0) {
                    for (NodeDeltaStyle tmpNB : deltaChangesB) {
                        TextChange textChanges = new TextChange();
                        textChanges.setAction(TextChangeData.ACTION_INSERT_STYLE);
                        textChanges.setNodeA(tmpNB.a);
                        textChanges.setNodeB(tmpNB.b);
                        textChanges.setTextTarget(tmpNB.getContent());
                        textChanges.setPositionTo(tmpNB.b.getRefDomNode().getNodeValue().indexOf(tmpNB.getContent()));
                        textChangeData.add(textChanges);

                    }
                }
                deltaChangesA.removeAllElements();
                deltaChangesB.removeAllElements();

            }
            //ENABLE this points if need
            //foundNodeUpdate.get(i).get(0).refDomNode.setNodeValue(foundNodeUpdate.get(i).get(0).refDomNode.getNodeValue().replaceAll("(_\\|.+?\\|_)(.*?)(_\\|\\/.+?\\|_)", ""));
            //foundNodeUpdate.get(i).get(1).refDomNode.setNodeValue(foundNodeUpdate.get(i).get(1).refDomNode.getNodeValue().replaceAll("(_\\|.+?\\|_)(.*?)(_\\|\\/.+?\\|_)", ""));

        }
//      logger.info(textChangeData.getAllTextChanges());
//      System.exit(0);

    }

    /**
     * setTextChangeFragment
     */
    public void setTextChangeFragment() {
        List<List<Interval>> listProcessedInterval = new ArrayList<>();
        Interval findA = new Interval(1, 0);
        Interval findB = new Interval(1, 0);
        findA.set(1, 0);
        findB.set(1, 0);
        String textNode;
        for (TextChange textChange : textChangeData.getAllTextChanges()) {
            int infA = textChange.getNodeA().getIndexKey();
            int infB = textChange.getNodeB().getIndexKey();

            Interval newAInterval = new Interval(infA, infA);
            Interval newBInterval = new Interval(infB, infB);
            List<Interval> tmpListInterval = new ArrayList<>();
            findA.set(newAInterval);
            findB.set(newBInterval);

            tmpListInterval.add(findA);
            tmpListInterval.add(findB);
            if (!listProcessedInterval.contains(tmpListInterval)) {
                listProcessedInterval.add(tmpListInterval);
            }
            switch (textChange.getAction()) {
                case TextChangeData.ACTION_UPDATE_STYLE_TO:

                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.UPDATE_STYLE_TO);

                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.UPDATE_STYLE_TO;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.UPDATE_STYLE_TO;

                    }

                    String textNodeB = textChange.getNodeB().getRefDomNode().getNodeValue();
                    StringBuffer bufB = new StringBuffer(textNodeB);
                    int startB = textChange.getPositionTo();
                    int endB = startB + textChange.getTextTarget().length();
                    String insertTextWithoutTag = Ndiff.clearEncodedTags(textChange.getTextTarget());
                    bufB.replace(startB, endB, insertTextWithoutTag);
                    textNodeB = bufB.toString();
                    textChange.getNodeB().getRefDomNode().setNodeValue(textNodeB);

                    break;
                case TextChangeData.ACTION_UPDATE_STYLE_FROM:
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.UPDATE_STYLE_FROM);

                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.UPDATE_STYLE_FROM;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.UPDATE_STYLE_FROM;
                    }

                    String textNodeA = textChange.getNodeA().getRefDomNode().getNodeValue();
                    StringBuffer bufA = new StringBuffer(textNodeA);
                    int startA = textChange.getPositionFrom();
                    int endA = startA + textChange.getTextSource().length();
                    insertTextWithoutTag = Ndiff.clearEncodedTags(textChange.getTextSource());
                    bufA.replace(startA, endA, insertTextWithoutTag);
                    textNodeA = bufA.toString();
                    textChange.getNodeA().getRefDomNode().setNodeValue(textNodeA);

                    break;
                case TextChangeData.ACTION_INSERT_STYLE:
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.INSERT_STYLE);
                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.INSERT_STYLE;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.INSERT_STYLE;
                    }

                    textNodeB = textChange.getNodeB().getRefDomNode().getNodeValue();

                    bufB = new StringBuffer(textNodeB);
                    startB = textChange.getPositionTo();
                    endB = startB + textChange.getTextTarget().length();
                    insertTextWithoutTag = Ndiff.clearEncodedTags(textChange.getTextTarget());
                    bufB.replace(startB, endB, insertTextWithoutTag);
                    textNodeB = bufB.toString();
                    textChange.getNodeB().getRefDomNode().setNodeValue(textNodeB);

                    textNodeA = textChange.getNodeA().getRefDomNode().getNodeValue();
                    textChange.getNodeA().getRefDomNode().setNodeValue(textNodeA);

                    break;
                case TextChangeData.ACTION_DELETE_STYLE:
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.DELETE_STYLE);
                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.DELETE_STYLE;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.DELETE_STYLE;
                    }

                    textNodeA = textChange.getNodeA().getRefDomNode().getNodeValue();
                    bufA = new StringBuffer(textNodeA);
                    startA = textChange.getPositionFrom();
                    endA = startA + textChange.getTextSource().length();
                    insertTextWithoutTag = Ndiff.clearEncodedTags(textChange.getTextSource());
                    bufA.replace(startA, endA, insertTextWithoutTag);

                    textNodeA = bufA.toString();
                    textChange.getNodeA().getRefDomNode().setNodeValue(textNodeA);

                    break;
                case TextChangeData.ACTION_MOVE_TEXT_FROM:
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.MOVETEXT_FROM);
                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.MOVETEXT_FROM;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.MOVETEXT_FROM;
                    }

                    break;
                case TextChangeData.ACTION_MOVE_TEXT_TO:
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.MOVETEXT_TO);
                    for (int l = findA.inf; l <= findA.sup; l++) {
                        A.getNode(l).inRel = Relation.MOVETEXT_TO;
                    }

                    for (int l = findB.inf; l <= findB.sup; l++) {
                        B.getNode(l).inRel = Relation.MOVETEXT_TO;
                    }

                    break;

                default:
                    logger.info("There are no text changes action");
            }
        }

    }

    /**
     * preview style changes
     */
    public void debugStyleChanges() {
        List<TextChange> mtd = textChangeData.getAllTextChanges();
        for (TextChange mt : mtd) {
            logger.info(mt);
        }
    }

    /**
     * preview move text
     */
    public void debugMoveChanges() {
        List<MoveText> mtd = moveTextData.getAllMoveChanges();
        for (MoveText mt : mtd) {
            logger.info(mt);
        }
    }

    /**
     * preview changes
     */
    public void debugChanges() {
        for (int i = 0; i < foundNodeInsert.size(); i++) {
            logger.info("---FOUND Insert: " + foundNodeInsert.get(i).refDomNode);
        }
        for (int i = 0; i < foundNodeDelete.size(); i++) {
            logger.info("---FOUND Delete: " + foundNodeDelete.get(i).refDomNode);
        }
        for (int i = 0; i < foundNodeUpdate.size(); i++) {
            logger.info("---FOUND Update A: " + foundNodeUpdate.get(i).get(0).refDomNode);
            logger.info("---FOUND Update B: " + foundNodeUpdate.get(i).get(1).refDomNode);
        }
    }

    /**
     * Tune Up List Of Text
     * NOTE this function work in first level
     *
     * @param listOfText
     * @return
     */
    public List<String> tuneUpListOfText(List<String> listOfText, String patterOpenTags, String patterCloseTags, int startIndex) {
        String tmpStr = "";
        int index = -1;
        int i = startIndex;
        ListIterator<String> iterator = listOfText.listIterator(startIndex);

        while (iterator.hasNext()) {

            String str = iterator.next();
            boolean startTagsExists = Pattern.compile(patterOpenTags).matcher(str).find();
            boolean endTagsExists = Pattern.compile(patterCloseTags).matcher(str).find();

            if (startTagsExists && !endTagsExists) {
                Matcher m = Pattern.compile(patterOpenTags).matcher(str);
                while (m.find()) {
                    patterCloseTags = m.group(0).replaceAll("_\\|", "_\\|/");
                }
                index = i;

            }

            if (index > -1) {
                //comment next block if recursion will have problem  -- start
//                if (i > index) {
//                    startTagsExists = Pattern.compile(patterOpenTags).matcher(str).find();
//                    if (startTagsExists) {
//                        tuneUpListOfText(listOfText, patterOpenTags, patterCloseTags, i);
//                    }
//                }
                // -- end

                tmpStr = tmpStr.concat(str).concat(" ");
                if (i > index) {
                    iterator.remove();
                }
            }

            if (!startTagsExists && endTagsExists) {
                if (index > -1) {
                    listOfText.set(index, tmpStr);
                }
                index = -1;
                tmpStr = "";
            }
            i++;
        }

        return listOfText;
    }

    /**
     * Tune Up List Of Text
     * NOTE this function work in first level
     * ORIG NO DELETE YET
     *
     * @param listOfText
     * @return
     */
//    public List<String> tuneUpListOfText(List<String> listOfText, String patterOpenTags, String patterCloseTags, int startIndex) {
//        String tmpStr = "";
//        int index = -1;
//        int i = startIndex;
//        ListIterator<String> iterator = listOfText.listIterator(startIndex);
//
//        while (iterator.hasNext()) {
//
//            String str = iterator.next();
//            boolean startTagsExists = Pattern.compile(patterOpenTags).matcher(str).find();
//            boolean endTagsExists = Pattern.compile(patterCloseTags).matcher(str).find();
//
//            if (startTagsExists && !endTagsExists) {
//                index = i;
//
//            }
//
//            if (index > -1) {
//                //comment next block if recursion will have problem  -- start
////                if (i > index) {
////                    startTagsExists = Pattern.compile(patterOpenTags).matcher(str).find();
////                    if (startTagsExists) {
////                        tuneUpListOfText(listOfText, patterOpenTags, patterCloseTags, i);
////                    }
////                }
//                // -- end
//
//                tmpStr = tmpStr.concat(str).concat(" ");
//                if (i > index) {
//                    iterator.remove();
//                }
//            }
//
//            if (!startTagsExists && endTagsExists) {
//                if (index > -1) {
//                    listOfText.set(index, tmpStr);
//                }
//                index = -1;
//                tmpStr = "";
//            }
//            i++;
//        }
//
//        return listOfText;
//    }
}
