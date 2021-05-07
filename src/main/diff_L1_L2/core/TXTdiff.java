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
package main.diff_L1_L2.core;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Likeness;
import main.diff_L1_L2.vdom.diffing.Sequence;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import org.apache.log4j.Logger;

import java.util.Vector;

/**
 * @author Mike Classe che si occupa di fare il diff del contenuto di nodi di testo
 */
public class TXTdiff {

	static Logger logger = Logger.getLogger("ndiff.TXTdiff");

	static NxN SF;
	static Sequence A;
	static Sequence B;

	static Dnode nodeA;
	static Dnode nodeB;

	static Double Common; // lunghezza del testo(in caratteri) comune alle
	// stringhe in input
	static METAdelta TXTdelta;

	/**
	 * Confronta le sequenze dei due Vnode, e inserisce il risultato MetaDelta, nel VnodeB, alla posizione indexA. Quindi nel VnodeB, ci
	 * sarà un vettore che contiene i MetaDelta relativi al confronto con determinati nodi del documento originale.
	 *
	 * @param nA Nodo del documento originale del quale si vuole fare il diff
	 * @param nB Nodo del documento modificato del quale si vuole fare il diff
	 * @return Somiglianza dei due nodi
	 */
	public static Likeness compute(Dnode nA, Dnode nB) {
		nodeA = nA;
		nodeB = nB;

		compute(nodeA.Sequence, nodeB.Sequence);
		createMiniDelta();

		return new Likeness(Common.intValue(), TXTdelta);
	}

	/**
	 * Effettua il diff tra due sequenze
	 *
	 * @param seqA Sequenza originale
	 * @param seqB Sequenza modificata
	 */
	public static void compute(Sequence seqA, Sequence seqB) {
		A = seqA;
		B = seqB;

		// logger.info(A.show(true));
		// logger.info(B.show(true));
		Common = 0.0;

		logger.info("START");

		SF = new NxN(A.length() - 1, B.length() - 1);

		Interval findA = new Interval(1, 0);
		Interval findB = new Interval(1, 0);

		Field processField;
		SF.StartFieldProcess(Field.LOCALITY);
		while ((processField = SF.nextField()) != null) {

//			logger.info("Partition(Call) \t X:" + processField.xRef.show()
//					+ "\t Y:" + processField.yRef.show());
			findA.set(1, 0);
			findB.set(1, 0);

			LCCS(processField.xRef, processField.yRef, findA, findB);

//			logger.info("Partition(Return) \t X:" + findA.show() + " \t Y:"
//				+ findB.show());
			if (findA.size() > 0) {
				// Togli i nodi dallo spazio di ricerca

				SF.subField(findA, findB, Field.NO, Field.LOCALITY,
					Field.LOCALITY, Field.NO);
				Common += (A.getOffset(findA.sup) + A.getElement(findA.sup)
					.length()) - A.getOffset(findA.inf);
			}
		}

		Common = Fs(new Double(nodeA.refDomNode.getTextContent().length()),
			new Double(nodeB.refDomNode.getTextContent().length()), Common);

		// new HtmlPrintNxN().print(SF, "../debug/t.html", true, true, true);
		logger.info("END");
	}

	/**
	 * Crea il METAdelta relativo al delta calcolato Il METAdelta viene aggiunto al VnodeB
	 */
	public static void createMiniDelta() {

		int pStart, length;

		// Esamino struttura dati SF
		TXTdelta = new METAdelta();

		Vector<Interval> dom = SF.getIntervalsOnX();
		Interval toProcess;
		for (int i = 0; i < dom.size(); i++) {

			toProcess = dom.get(i);

			pStart = A.getOffset(toProcess.inf);
			length = (A.getOffset(toProcess.sup) - pStart)
				+ A.getElement(toProcess.sup).length();

			// logger.info(" DELETE --> "+pStart+":"+length);
			TXTdelta.addDeleteTextOperation(nodeA, nodeB, pStart, length);
		}

		Vector<Interval> cod = SF.getIntervalsOnY();
		for (int i = 0; i < cod.size(); i++) {

			toProcess = cod.get(i);

			// logger.info(toProcess.show());
			pStart = B.getOffset(toProcess.inf);
			length = (B.getOffset(toProcess.sup) - pStart)
				+ B.getElement(toProcess.sup).length();

			// logger.info(" INSERT --> "+pStart+":"+length);
			TXTdelta.addInsertTextOperation(nodeA, nodeB, pStart, length);
		}
	}

	/**
	 * Ritorna la lunghezza della stringa comune presente nei due intervalli
	 *
	 * @param intA Intervallo di ricerca nella sequenza A
	 * @param intB Intervallo di ricerca nella sequenza B
	 * @return Dimensione della stringa comune uguale
	 */
	static Interval explorer(Interval intA, Interval intB) {
		int explorer = 0;
		Interval ret = new Interval(0, 0);
		while ((intA.inf + explorer <= intA.sup)
			&& (intB.inf + explorer <= intB.sup)
			&& ((A.getElement(intA.inf + explorer)).equals((B
				.getElement(intB.inf + explorer))))) {

			ret.sup += A.getElement(intA.inf + explorer).length();
			explorer++;

			ret.inf = explorer;
			/*
			 * BUG trovato e risolto problema di sforare gli intervalli di
			 * ricerca
			 */
 /*
			 * if((intA.inf + (explorer-1) <= intA.sup) && (intB.inf +
			 * (explorer-1) <= intB.sup)) ret.inf = explorer;
			 */

		}
		return ret;
	}

	/**
	 * Funziona che calcola la percentuale di somiglianza tra due testi
	 *
	 * @param la Lunghezza del testo nel documento originale
	 * @param lb Lunghezza del testo nel documento modificato
	 * @param c Lunghezza del testo comune
	 * @return Percentuale di somiglianza dei sue nodi
	 */
	static private Double Fs(Double la, Double lb, Double c) {
		Double coeffAdj = Math.max((la - c) / la, (lb - c) / lb);

		c = (c * 100) / Math.min(la, lb) * (1 - coeffAdj);
//		logger.info("Similarity(" + la + "," + lb + "," + Common + ") = " + c
//			+ "%");
		return c;
	}

	/**
	 * Returns the LCCS (Longest Common Continuous Subsequence) found in the specified search ranges
	 *
	 * @param intA Intervallo di ricerca nella sequenza A
	 * @param intB Intervallo di ricerca nella sequenza B
	 * @param findA Intervallo trovato nella sequenza A
	 * @param findB Intervallo trovato nella sequenza B
	 */
	static void LCCS(Interval intA, Interval intB, Interval findA,
		Interval findB) {
		Interval tmp_int_A = new Interval(0, 0);
		Interval tmp_int_B = new Interval(0, 0);
		findA.set(1, 0);
		findB.set(1, 0);
		int max_match = 0;

		Vector<Integer> cl;
		int match;

		Integer weight = 0;

		for (int i = intA.inf; i <= intA.sup; i++) {

			cl = B.getClass(A.getElement(i));

			if (cl != null) {
				for (int k = 0; k < cl.size(); k++) {
					int j = cl.get(k);
					if (intB.isIntro(j)) {
						tmp_int_A.set(i, intA.sup);
						tmp_int_B.set(j, intB.sup);

						Interval tmp = explorer(tmp_int_A, tmp_int_B);
						match = tmp.inf;
						weight = tmp.sup;

						if (weight > max_match) {

							// logger.info("---");
							// tmp.print();
							// logger.info("---");
							max_match = weight;
							findA.set(i, i + match - 1);
							findB.set(j, j + match - 1);

						}
					}
				}
			}
		}
	}
}
