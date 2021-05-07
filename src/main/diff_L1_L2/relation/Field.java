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
 * @author schirinz Mantiene l'informazione relativa ad un'area di ricerca
 */
public class Field {

	// Possibili proprietà per un'area di ricerca
	public static final byte NO = 0;
	public static final byte LOCALITY = 1;

	// Proprità dell'area
	public int property;

	// Reference agli itnervalli a cui si riferisce l'area
	public Interval xRef;
	public Interval yRef;

	// Reference al precedente e al successivo della lista di appartenenza
	public Field next;
	public Field prev;

	// boolean cheindica l'appartenenza(true) o meno(false) ad una lista
	public boolean inList;

	/**
	 * Costruttore
	 * 
	 * @param type
	 *            Proprietà sull'area creata
	 * @param x
	 *            Riferimento all'intervallo del dominio
	 * @param y
	 *            Riferimento all'intervallo del codominio
	 */
	public Field(int type, Interval x, Interval y) {
		this.property = type;
		this.xRef = x;
		this.yRef = y;
		next = null;
		prev = null;
		inList = false;
	}

	/**
	 * Lega l'elemento dopo il Field ref
	 * 
	 * @param ref
	 *            Reference al Field di riferimento
	 */
	public void bindAfter(Field ref) {
		this.next = ref.next;
		this.prev = ref;
		ref.next.prev = this;
		ref.next = this;
		inList = true;
	}

	/**
	 * Lega l'elemento prima del Field ref
	 * 
	 * @param ref
	 *            Referenceal Field di riferimento
	 */
	public void bindFirst(Field ref) {
		this.next = ref;
		this.prev = ref.prev;
		ref.prev.next = this;
		ref.prev = this;
		inList = true;
	}

	/**
	 * Per fase di debug
	 * 
	 * @return Stringa che rappresenta il contenuto del Field
	 */
	public String show() {
		String ret = "";

		if (xRef != null)
			ret += " #xRef:" + xRef.show();
		else
			ret += " #xRef:Null";
		if (yRef != null)
			ret += " #yRef:" + yRef.show();
		else
			ret += " #yRef:Null";
		ret += " #property:" + property;

		return ret;
	}

	/**
	 * Rimuove se stesso dalla lista di appartenenza, collegando il suo
	 * precedente al suo successivo
	 */
	public void untie() {
		if (inList) {
			prev.next = next;
			next.prev = prev;
			inList = false;
		}
	}

}
