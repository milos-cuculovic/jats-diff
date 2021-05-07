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

package main.diff_L1_L2.relation;

/**
 * @author schirinz Classe che mantiene le informazioni relative ad un
 *         Intervallo
 */
public class Interval {
	public int inf;
	public int sup;

	/**
	 * Costruttore
	 * 
	 * @param inf
	 *            Estremo inferiore
	 * @param sup
	 *            Estremo superiore
	 */
	public Interval(int inf, int sup) {
		this.inf = inf;
		this.sup = sup;
	}

	@Override
	public Interval clone() {
		return new Interval(this.inf, this.sup);
	}

	/**
	 * Controlla se un numero appartiene all'intervallo, estremi inclusi
	 * 
	 * @param num
	 *            numero da confrontare
	 * @return true se il numero appartiene all'intervallo
	 */
	public boolean isIntro(int num) {
		if ((num >= this.inf) && (num <= this.sup))
			return true;
		return false;
	}

	/**
	 * Imposta gli estremi dell'intervallo
	 * 
	 * @param inf
	 *            Estremo inferiore
	 * @param sup
	 *            Estremo superiore
	 */
	public void set(int inf, int sup) {
		this.inf = inf;
		this.sup = sup;
	}

	/**
	 * Imposta gli estremi dell'intervallo uguali a quelli dell'intervallo
	 * passato
	 * 
	 * @param intRef
	 *            Intervallo da cui copiare gli estremi
	 */
	public void set(Interval intRef) {
		this.inf = intRef.inf;
		this.sup = intRef.sup;
	}

	/**
	 * Per fase di Debug
	 * 
	 * @return Stringa che esplicita il contenuto dell'intervallo
	 */
	public String show() {
		return "[" + inf + "," + sup + "]";
	}

	/**
	 * Ritorna la dimensione dell'intervallo
	 * 
	 * @return Dimensione dell'intervallo
	 */
	public int size() {
		return sup - inf + 1;
	}

}
