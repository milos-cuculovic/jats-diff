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
package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import main.diff_L1_L2.phases.common.Match;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import java.util.Collections;
import java.util.Iterator;
import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.relation.Fragment;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import main.diff_L1_L2.phases.MoveTextVO;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.Chunk;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import main.diff_L1_L2.vdom.diffing.Hash;
import main.diff_L1_L2.core.Ndiff;
import static main.diff_L1_L2.core.Ndiff.EXCLUDE_STYLE_TAG;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.vo.NodeHandler;
import main.diff_L1_L2.vo.StyleMatcherData;
import main.diff_L1_L2.vo.MoveText;
import main.diff_L1_L2.vo.MoveTextData;
import main.diff_L1_L2.vo.NodeDelta;
import main.diff_L1_L2.vo.TextChange;
import main.diff_L1_L2.vo.TextChangeData;

/**
 * @author Sasa Simic
 *
 *
 */
public class FindTextMove extends Phase {

	//found array dat by jndiff
	public List<List<Dnode>> foundNodeUpdate = new ArrayList<>();
	public List<Dnode> foundNodeInsert = new ArrayList<>();
	public List<Dnode> foundNodeDelete = new ArrayList<>();
	//define two temporary list of AbstracDelta object for compare text
	List<NodeDelta> textDataInsertTmp = new ArrayList<>();
	List<NodeDelta> textDataDeleteTmp = new ArrayList<>();
	List<NodeDelta> textDataChangeTmp = new ArrayList<>();
	MoveTextData moveTextData = new MoveTextData();
	public int maxSimilarity = 30;
	public int minCharsMoveText = 10;
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
	public FindTextMove(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
			logger.info("START FIND TEXTMOVE CHANGES");
//			findTextMoveChanges();
			logger.info("END");
		} catch (Exception e) {
			logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber() + " Message: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}
	}

	protected void findTextMoveChanges() {
		try {

			//call function initChanges prepare three list foundNodeUpdate/foundNodeInsert/foundNodeDelete
			this.initChanges();
			//show|hide debug info about changes
//			this.debugChanges();

			//observe changes in the text - updates ( movetext for update points no insert or delete node)
			//1. call and get deltas
			this.getDeltas();

			//2. call move text
			this.getTextMoveChanges();
			//show|hide debug info about movetext
//			this.debugMoveChanges();

			//set text change fragments
			this.setTextChangeFragment();

//			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

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
		for (TextChange textChange : textChangeData.getAllTextChanges()) {
			int infA = textChange.getNodeA().getIndexKey();
			int infB = textChange.getNodeB().getIndexKey();

			int infParentA = textChange.getNodeA().getPosFather();
			int infParentB = textChange.getNodeB().getPosFather();
			int supParentA = infParentA + A.getNode(infParentA).numChildSubtree;
			int supParentB = infParentB + B.getNode(infParentB).numChildSubtree;

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
	 * Find move text
	 */
	public void getTextMoveChanges() {
		//		logger.info(moveTextDataDeleteTmp);
		//compare two array insert and delete text and try find is have move
		for (NodeDelta deleteObj : textDataDeleteTmp) {
			List<String> deletes = null;
			deletes = deleteObj.delta.getSource().getLines();
			String deleteString = "";

			for (int s = 0; s < deletes.size(); s++) {
				deleteString += deletes.get(s).toString() + " ";

			}

			int changePositionFrom = deleteObj.a.getRefDomNode().getNodeValue().indexOf(deleteString.trim());
			for (NodeDelta insertObj : textDataInsertTmp) {
				List<String> inserts = null;
				String insertString = "";
				inserts = insertObj.delta.getTarget().getLines();

				for (int t = 0; t < inserts.size(); t++) {
					insertString += inserts.get(t).toString() + " ";

				}
				int changePositionTo = deleteObj.b.getRefDomNode().getNodeValue().indexOf(insertString.trim());
				//if there are matches delete and insert (same text delete and insert) for the same text then it can be move
				// this comersion work for string grater length 10 chars
				//works for some cases if the situation is clear
//                logger.info("delete " + deleteString);
//                logger.info("insert " + insertString);
//                logger.info("================");
				if (deleteString.equals(insertString) && changePositionFrom != changePositionTo) {
					Dnode aTmp = deleteObj.a;
					Dnode bTmp = insertObj.b;
					TextChange mt = new TextChange();
					mt.setNodeA(aTmp);
					mt.setNodeB(bTmp);
					mt.setParentNodeA(A.getNode(aTmp.posFather));
					mt.setParentNodeB(B.getNode(bTmp.posFather));
					mt.setAction(TextChangeData.ACTION_MOVE_TEXT_TO);
					mt.setPositionTo(changePositionTo);
					mt.setPositionFrom(changePositionFrom);
					mt.setTextSource(deleteString); //both insert string and delete string are same
					mt.setTextTarget(insertString);
					TextChange mtTmp = mt.cloneObject(); //second case for from/to
					mtTmp.setAction(TextChangeData.ACTION_MOVE_TEXT_FROM);
					textChangeData.add(mt);
					textChangeData.add(mtTmp);
					String textValueA = mt.getNodeA().getRefDomNode().getNodeValue();
					StringBuilder textSourceValue = new StringBuilder(textValueA);
//					textSourceValue.delete(changePositionFrom, changePositionFrom + deleteString.length() - 1);
					mt.getNodeA().getRefDomNode().setNodeValue(textSourceValue.toString());

					String textValueB = mt.getNodeB().getRefDomNode().getNodeValue();
					StringBuilder textTargetValue = new StringBuilder(textValueB);
//					textTargetValue.delete(changePositionTo, changePositionTo + insertString.length() - 1);
					mt.getNodeB().getRefDomNode().setNodeValue(textTargetValue.toString());

				}
			}
		}
	}

	/**
	 * Find deltas
	 */
	public void getDeltas() {

		for (int i = 0; i < foundNodeUpdate.size(); i++) {
//			Dnode aT = foundNodeUpdate.get(i).get(0);
//			Dnode bT = foundNodeUpdate.get(i).get(1);
			Dnode a = A.getNode(foundNodeUpdate.get(i).get(0).getIndexKey());
			Dnode b = B.getNode(foundNodeUpdate.get(i).get(1).getIndexKey());
			String aText = a.refDomNode.getTextContent();
			String bText = b.refDomNode.getTextContent();

			//remove all what is tag replacement with content in tag, how try find move text
//            for (String var : Ndiff.EXCLUDE_STYLE_TAG) {
//                aText = aText.replaceAll("\\_\\_\\|(.+?)\\|\\_\\_", "$1");
//                bText = bText.replaceAll("\\_\\_\\|(.+?)\\|\\_\\_", "$1");
//            }
			//make array of strings split by space, try diffing whole words
			List<String> listOfTextA = new ArrayList<String>(Arrays.asList(aText.split(" ")));
			List<String> listOfTextB = new ArrayList<String>(Arrays.asList(bText.split(" ")));

			//use java-diff-utils to try find changes in text (return type action CHANGE, INSERT, DELETE position, source text, target text
			Patch<String> patch = DiffUtils.diff(listOfTextA, listOfTextB);

			AbstractDelta delta;

			//Collect all deltas for insert and delete and add in temporary array
			for (int pathIndex = 0; pathIndex < patch.getDeltas().size(); pathIndex++) {
				delta = patch.getDeltas().get(pathIndex);
//				logger.info(delta + "->" + delta.getType().toString());
				String action = delta.getType().toString();
				//insert in text is enough for find move text
				if (action.equals("INSERT")) {
					NodeDelta nodeDelta = new NodeDelta();
					nodeDelta.a = a;
					nodeDelta.b = b;
					nodeDelta.delta = delta;
					textDataInsertTmp.add(nodeDelta);

//                    logger.info("DELTA INSERT source" + delta.getSource().getLines().toString());
//                    logger.info("DELTA INSERT target" + delta.getTarget().getLines().toString());
				}

				if (action.equals("DELETE")) {
					NodeDelta nodeDelta = new NodeDelta();
					nodeDelta.a = a;
					nodeDelta.b = b;
					nodeDelta.delta = delta;
					textDataDeleteTmp.add(nodeDelta);

//					logger.info("DELTA DELETE source" + delta.getSource().getLines());
//					logger.info("DELTA DELETE target" + delta.getTarget().getLines());
				}

				if (action.equals("CHANGE")) {

					NodeDelta nodeDelta = new NodeDelta();
					nodeDelta.a = a;
					nodeDelta.b = b;
					nodeDelta.delta = delta;
					textDataChangeTmp.add(nodeDelta);

				}

			}
		}

	}

	public void initChanges() {
		try {
			Vector<Interval> dom = SF.getIntervalsOnX();
			Interval toProcess;
			for (int i = 0; i < dom.size(); i++) {
				toProcess = dom.get(i);

				for (int k = toProcess.inf; k <= toProcess.sup; k++) {
//                    if (k + A.getNode(k).getNumChildSubtree() <= toProcess.sup) {
					if (A.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
						foundNodeDelete.add(A.getNode(k));
//						logger.info(A.getNode(k).hashTree + " " + A.getNode(k).hashNode + " A DELETE" + A.getNode(k).refDomNode);
					}
//					k += A.getNode(k).getNumChildSubtree();
				}
			}

			Vector<Interval> cod = SF.getIntervalsOnY();
			for (int i = 0; i < cod.size(); i++) {
				toProcess = cod.get(i);

				for (int k = toProcess.inf; k <= toProcess.sup; k++) {
//                    if (k + B.getNode(k).getNumChildSubtree() <= toProcess.sup) {
					if (B.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
						foundNodeInsert.add(B.getNode(k));
//						logger.info(B.getNode(k).hashTree + " " + B.getNode(k).hashNode + " B INSERT" + B.getNode(k).refDomNode);
					}
//					k += B.getNode(k).getNumChildSubtree();
				}
			}

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

}
