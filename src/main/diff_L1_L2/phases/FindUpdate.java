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
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei documenti
 */
public class FindUpdate extends Phase {

	/**
	 * Costruttore
	 *
	 * @param SearchField Campi di ricerca rimasti in NxN
	 * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
	 * @param Ta Dtree relativo al documento originale
	 * @param Tb Dtree relativo al documento modificato
	 * @param cfg Nconfig relativo alla configurazione del Diff
	 */
	public FindUpdate(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
			logger.info("START");

			Interval findA = new Interval(1, 0);
			Interval findB = new Interval(1, 0);

			Field processField;
			SF.StartFieldProcess(Field.LOCALITY);
			while ((processField = SF.nextField()) != null) {

//				logger.info("FindUpdate(Call)  \t X:"
//						+ processField.xRef.show() + " \t Y:"
//						+ processField.yRef.show());
				findA.set(1, 0);
				findB.set(1, 0);

				FindMaxUpdate(processField.xRef, processField.yRef, findA,
					findB);

//				logger.info("FindUpdate(Return)\t X:" + findA.show() + "\t Y:"
//						+ findB.show());
				if (findA.size() > 0) {
					// Togli i nodi dallo spazio di ricerca
					SF.subField(findA, findB, Field.NO, Field.LOCALITY,
						Field.LOCALITY, Field.NO);
					// Inserisci i nodi nella struttura Relation
					R.addFragment(findA, findB, A.getNode(findA.inf).weight,
						Relation.UPDATE);

					// Cambia appartenenza dei nodi
					for (int i = findA.inf; i <= findA.sup; i++) {
						A.getNode(i).inRel = Relation.UPDATE;
					}
					for (int i = findB.inf; i <= findB.sup; i++) {
						B.getNode(i).inRel = Relation.UPDATE;
					}
				}
			}

			logger.info("END");
		} catch (Exception e) {
			throw new ComputePhaseException("FindUpdate");
		}
	}

	/**
	 * Trova il nodo di testo con "somiglianza" massima nello spazio specificato da intA e intB
	 *
	 * @param intA Intervallo di ricerca sul documento A
	 * @param intB Intervallo di ricerca sul documento B
	 * @param findA Nodo trovato sul documento A (in questo caso l'intervallo avrà dimensione al massimo 1)
	 * @param findB Nodo trovato sul docuemnto B (in questo caso l'intervallo avrà dimensione al massimo 1)
	 */
	private void FindMaxUpdate(Interval intA, Interval intB, Interval findA,
		Interval findB) {
		Node tmpA;
		Node tmpB;

		/* If max is a percentage, I set the reference threshold */
		int max = cfg.getIntPhaseParam(Nconfig.FindUpdate, "level");

		for (int i = intA.inf; i <= intA.sup; i++) {

			for (int j = intB.inf; j <= intB.sup; j++) {
				tmpA = A.getNode(i).getRefDomNode();
				tmpB = B.getNode(j).getRefDomNode();

				if ((tmpA.getNodeType() == Node.TEXT_NODE)
					&& (tmpB.getNodeType() == Node.TEXT_NODE)) {

					int tmp = A.getNode(i).getSimilarity(B.getNode(j));

					if (tmp > max) {
						findA.set(i, i);
						findB.set(j, j);

						max = tmp;
					}
				}
			}
		}
	}
}
