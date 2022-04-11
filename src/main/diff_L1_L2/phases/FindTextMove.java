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
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import java.util.Vector;
import main.diff_L1_L2.vdom.diffing.Dnode;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.Chunk;
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

	public List<List<Dnode>> foundNodeUpdate = new ArrayList<>();
	public List<Dnode> foundNodeInsert = new ArrayList<>();
	public List<Dnode> foundNodeDelete = new ArrayList<>();
	List<NodeDelta> textDataInsertTmp = new ArrayList<>();
	List<NodeDelta> textDataDeleteTmp = new ArrayList<>();
	List<NodeDelta> textDataChangeTmp = new ArrayList<>();
	MoveTextData moveTextData = new MoveTextData();
	public int maxSimilarity = 30;
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
			// Commented as not yet tested - improvements needed
			//findTextMoveChanges();
			logger.info("END");
		} catch (Exception e) {
			logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber() + " Message: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		}
	}

	protected void findTextMoveChanges() {
		try {
			this.initChanges();

			//observe changes in the text - updates ( movetext for update points no insert or delete node)
			//1. call and get deltas
			this.getDeltas();

			//2. call move text
			this.getTextMoveChanges();

			//set text change fragments
			this.setTextChangeFragment();

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
					mt.setTextSource(deleteString); //both insert string and delete string are the same
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
			Dnode a = A.getNode(foundNodeUpdate.get(i).get(0).getIndexKey());
			Dnode b = B.getNode(foundNodeUpdate.get(i).get(1).getIndexKey());
			String aText = a.refDomNode.getTextContent();
			String bText = b.refDomNode.getTextContent();

			List<String> listOfTextA = new ArrayList<String>(Arrays.asList(aText.split(" ")));
			List<String> listOfTextB = new ArrayList<String>(Arrays.asList(bText.split(" ")));

			Patch<String> patch = DiffUtils.diff(listOfTextA, listOfTextB);

			AbstractDelta delta;

			//Collect all deltas for insert and delete and add in temporary array
			for (int pathIndex = 0; pathIndex < patch.getDeltas().size(); pathIndex++) {
				delta = patch.getDeltas().get(pathIndex);
				String action = delta.getType().toString();

				if (action.equals("INSERT")) {
					NodeDelta nodeDelta = new NodeDelta();
					nodeDelta.a = a;
					nodeDelta.b = b;
					nodeDelta.delta = delta;
					textDataInsertTmp.add(nodeDelta);
				}

				if (action.equals("DELETE")) {
					NodeDelta nodeDelta = new NodeDelta();
					nodeDelta.a = a;
					nodeDelta.b = b;
					nodeDelta.delta = delta;
					textDataDeleteTmp.add(nodeDelta);
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
					if (A.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
						foundNodeDelete.add(A.getNode(k));
					}
				}
			}

			Vector<Interval> cod = SF.getIntervalsOnY();
			for (int i = 0; i < cod.size(); i++) {
				toProcess = cod.get(i);

				for (int k = toProcess.inf; k <= toProcess.sup; k++) {
					if (B.getNode(k).refDomNode.getNodeType() == Node.TEXT_NODE) {
						foundNodeInsert.add(B.getNode(k));
					}
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
}
