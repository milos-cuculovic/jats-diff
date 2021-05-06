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
import main.diff_L1_L2.phases.common.Match;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;

import java.util.Vector;

/**
 * @author Mike Crea le associazioni di uguaglianza tra i due documenti in input.
 *
 */
public class Partition extends Phase {

	/**
	 * Costruttore
	 *
	 * @param SearchField Campi di ricerca rimasti in NxN
	 * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
	 * @param Ta Dtree relativo al documento originale
	 * @param Tb Dtree relativo al documento modificato
	 * @param cfg Nconfig relativo alla configurazione del Diff
	 */
	public Partition(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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

//				logger.info("Partition(Call)\t X:" + processField.xRef.show()
//						+ "\t Y:" + processField.yRef.show());
				findA.set(1, 0);
				findB.set(1, 0);

				LCCS(processField.xRef, processField.yRef, findA, findB);

//				logger.info("Partition(Return)\t X:" + findA.show() + "\t Y:"
//					+ findB.show());
				if (findA.size() > 0) {
					// Togli i nodi dallo spazio di ricerca
					SF.subField(findA, findB, Field.NO, Field.LOCALITY,
						Field.LOCALITY, Field.NO);

					// Inserisci i nodi nella struttura Relation(posso
					// effetuuare inserimento separato, in modo da utilizzare
					// singoli sottoalberi)
					int k = 0;
					int child;
					while (k < findA.size()) {
						child = A.getNode(findA.inf + k).getNumChildSubtree();
						R.addFragment(new Interval(findA.inf + k, findA.inf + k
							+ child), new Interval(findB.inf + k, findB.inf
							+ k + child), A.getNode(findA.inf + k).weight,
							Relation.EQUAL);
						k += A.getNode(findA.inf + k).getNumChildSubtree() + 1;
					}

					// Cambia appartenenza dei nodi
					for (int i = findA.inf; i <= findA.sup; i++) {
						A.getNode(i).inRel = Relation.EQUAL;
					}
					for (int i = findB.inf; i <= findB.sup; i++) {
						B.getNode(i).inRel = Relation.EQUAL;
					}
				}
			}

			logger.info("END");
		} catch (Exception e) {
			throw new ComputePhaseException("Partition");
		}
	}

	/**
	 * Parte dall'inizio degli intervalli e va avanti finche' il contenuto dei nodi e uguale, si ferma quando arriva alla fine degli
	 * intervalli
	 *
	 * @param intA Intervallo di ricerca sul documento A
	 * @param intB Intervallo di ricerca sul documento B
	 * @return Il match trovato tra i due intervalli
	 */
	protected Match explorer(Interval intA, Interval intB) {
		Match m = new Match(0, 0);
		while ((intA.inf + m.length <= intA.sup)
			&& (intA.inf + m.length
			+ A.getNode(intA.inf + m.length).getNumChildSubtree() <= intA.sup)
			&& (intB.inf + m.length <= intB.sup)
			&& (intB.inf + m.length
			+ B.getNode(intB.inf + m.length).getNumChildSubtree() <= intB.sup)
			&& ((A.getNode(intA.inf + m.length).getHashTree()).equals(B
				.getNode(intB.inf + m.length).getHashTree()))) {

			m.weight += A.getNode(intA.inf + m.length).getWeight();
			m.length += B.getNode(intB.inf + m.length).getNumChildSubtree() + 1;
		}

		// Tolgo ultimo incremento del while
		m.length--;
		return m;
	}

	/**
	 * Restituisce la sottosequenza continua comune massima negli intervalli esaminati
	 *
	 * @param intA - Intervallo da esaminare sul documento A
	 * @param intB - Intervallo da esaminare sul documento B
	 * @param findA - Intervallo trovato uguale nello spazio esaminato(size <=0 se nn viene trovata nessuna corrispondenza) @param findB -
	 * Intervallo trovato uguale n ello spazio esaminato(size <=0 se nn viene trovata nessuna corrispondenza)
	 */
	protected void LCCS(Interval intA, Interval intB, Interval findA,
		Interval findB) {
		Interval tmp_int_A = new Interval(0, 0);
		Interval tmp_int_B = new Interval(0, 0);

		findA.set(1, 0);
		findB.set(1, 0);
		int max_match = 0;

		Vector<Integer> cl;

		for (int i = intA.inf; i <= intA.sup; i++) {

			cl = B.getClass(A.getNode(i).getHashTree());
			if (cl != null) {
				for (int k = 0; k < cl.size(); k++) {
					int j = cl.get(k);
					if (intB.isIntro(j)) {
						tmp_int_A.set(i, intA.sup);
						tmp_int_B.set(j, intB.sup);

						Match tmp = explorer(tmp_int_A, tmp_int_B);

						if (tmp.weight > max_match) {
							max_match = tmp.weight;
							findA.set(i, i + tmp.length);
							findB.set(j, j + tmp.length);
						}
					}
				}
			}
		}
	}

}
