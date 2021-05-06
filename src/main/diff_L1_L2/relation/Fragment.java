/*****************************************************************************************
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

 *   You should have received a copy of the GNU LESSER GENERAL PUBLIC LICENSE
 *   along with JNdiff; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *   
 *****************************************************************************************/

package main.diff_L1_L2.relation;

/**
 * @author Mike Un frammento è composto dai due sottoalberi messi in relazione
 *         appartenenti ai due documenti esaminati
 * 
 */
public class Fragment {
	Interval intA; // Posizione root del sottoalbero in A
	Interval intB; // Posizione root del sottoalbero in B
	Integer weight; // Peso del frammento
	String ID; // Identificativo frammento

	/**
	 * Costruttore
	 * 
	 * @param intA
	 *            Intervallo che si riferisce al frammento nel documento
	 *            originale
	 * @param intB
	 *            Intervallo che si riferisce al frammento nel documento
	 *            modificato
	 * @param weight
	 *            Peso del frammento
	 */
	public Fragment(Interval intA, Interval intB, Integer weight) {
		this.intA = intA;
		this.intB = intB;
		this.weight = weight;
		ID = intA.inf + ":" + intB.inf;
	}

	/**
	 * Ritorna l'intervallo a cui si riferisce il frammento nel documento
	 * originale
	 * 
	 * @return intervallo a cui si riferisce il frammento nel documento
	 *         originale
	 */
	public Interval getA() {
		return intA;
	}

	/**
	 * Ritorna l'intervallo a cui si riferisce il frammento nel documento
	 * modificato
	 * 
	 * @return intervallo a cui si riferisce il frammento nel documento
	 *         modificato
	 */
	public Interval getB() {
		return intB;
	}

	/**
	 * Ritorna l'ID del frammento
	 * 
	 * @return ID del frammento
	 */
	public String getID() {
		return ID;
	}

	/**
	 * Ritorna l'indice dell'elemento radice a cui si riferisce il frammento nel
	 * documento originale
	 * 
	 * @return indice dell'elemento radice a cui si riferisce il frammento nel
	 *         documento originale
	 */
	public Integer getNnRootA() {
		return intA.inf;
	}

	/**
	 * Ritorna l'indice dell'elemento radice a cui si riferisce il frammento nel
	 * documento modificato
	 * 
	 * @return indice dell'elemento radice a cui si riferisce il frammento nel
	 *         documento modificato
	 */
	public Integer getNnRootB() {
		return intB.inf;
	}

	/**
	 * Ritorna il peso del frammento
	 * 
	 * @return peso del frammento
	 */
	public Integer getWeight() {
		return weight;
	}

}