/*****************************************************************************************
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

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with jats-diff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 *         documenti
 * 
 */
public class FindMove extends Phase {

	Integer range;

	/**
	 * Constructor
	 * 
	 * @param SearchField
	 *            Research fields left in NxN
	 * @param Rel
	 *            Relationships detected between document nodes
	 * @param Ta
	 *            Dtree related to the original document
	 * @param Tb
	 *            Dtree related to the modified document
	 * @param cfg
	 *            Nconfig related to the Diff configuration
	 */
	public FindMove(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
			// Distance calculation relative to the configuration file
			range = (A.numNode + B.numNode) / 2;
			range = (range * cfg.getIntPhaseParam(Nconfig.FindMove, "range")) / 100;
			logger.info("START with range:" + range);

			Interval findA = new Interval(1, 0);
			Interval findB = new Interval(1, 0);

			Field processField;
			SF.StartFieldProcess(Field.NO);
			while ((processField = SF.nextField()) != null) {

				logger.info("FindMove(Call)   \t X:" + processField.xRef.show()
						+ " \t Y:" + processField.yRef.show());

				findA.set(1, 0);
				findB.set(1, 0);

				FindMaxMove(processField.xRef, processField.yRef, findA, findB);

				logger.info("FindMove(Return)\t X:" + findA.show() + "\t Y:"
						+ findB.show());

				if (findA.size() > 0) {
					// Remove nodes from the search space
					SF.subField(findA, findB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					// Insert nodes in the Relation structure
					R.addFragment(findA, findB, A.getNode(findA.inf).weight,
							Relation.MOVE);

					// Change membership of nodes
					for (int i = findA.inf; i <= findA.sup; i++)
						A.getNode(i).inRel = Relation.MOVE;
					for (int i = findB.inf; i <= findB.sup; i++)
						B.getNode(i).inRel = Relation.MOVE;
				}
			}
			logger.info("END");
		} catch (Exception e) {
			throw new ComputePhaseException("FindMove");
		}
	}

	/**
	 * Cerca eventuali sottoalberi spostati nello spazio definito da intA e intB
	 * 
	 * @param intA
	 *            Intervallo di ricerca sul documento Originale
	 * @param intB
	 *            Intervallo di ricerca sul documento Modificato
	 * @param findA
	 *            Match trovato sul documento originale
	 * @param findB
	 *            Match trovato sul documento modificato
	 */
	private void FindMaxMove(Interval intA, Interval intB, Interval findA,
			Interval findB) {

		for (int i = intA.inf; i <= intA.sup; i++) {
			for (int j = intB.inf; j <= intB.sup; j++) {
				if ((Math.abs(i - j) <= range)
					&& A.getNode(i).getHashTree().equals(B.getNode(j).getHashTree())
					&& (i + A.getNode(i).getNumChildSubtree() <= intA.sup)
					&& (j + B.getNode(j).getNumChildSubtree() <= intB.sup)) {

					if (A.getNode(i).getNumChildSubtree() > findA.size()) {
						findA.set(i, i + A.getNode(i).getNumChildSubtree());
						findB.set(j, j + B.getNode(j).getNumChildSubtree());
					}
				}
			}
		}
	}
}
