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
package main.diff_L1_L2.relation;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author schirinz Struttura nella quale vengono salvate le relazioni identificate tra i nodi
 */
public class Relation {

	// Constants to identify the various types of Relationships
	public static final int NO = 0;
	public static final int EQUAL = 1;
	public static final int UPGRADE = 2;
	public static final int DOWNGRADE = 3;
	public static final int MERGE = 4;
	public static final int SPLIT = 5;
	public static final int MOVETEXT_FROM = 6;
	public static final int MOVETEXT_TO = 7;
	public static final int INSERT_STYLE = 8;
	public static final int DELETE_STYLE = 9;
	public static final int UPDATE_STYLE_FROM = 10;
	public static final int UPDATE_STYLE_TO = 11;
	public static final int INSERT_TEXT = 12;
	public static final int DELETE_TEXT = 13;
	public static final int UPDATE_TEXT_FROM = 14;
	public static final int UPDATE_TEXT_TO = 15;
	public static final int MOVE = 16;
	public static final int STYLE = 17;
	public static final int UPDATE = 18;

	HashMap<Integer, Vector<Fragment>> list;

	/**
	 * Costruttore
	 */
	public Relation() {
		list = new HashMap<Integer, Vector<Fragment>>();
	}

	/**
	 * Inserisce nella relazione il punto (intX,intY) come type
	 *
	 * @param intX Punto sul dominio
	 * @param intY Punto sul codominio
	 * @param weight Peso del frammento aggiunto
	 * @param type Tipo di relazione del punto
	 */
	public void addFragment(int intX, int intY, Integer weight, Integer type) {
		addFragment(new Interval(intX, intX), new Interval(intY, intY), weight,
			type);
	}

	/**
	 * Crea un nuovo frammento con gli intervalli passati e il tipo di relazione In ogni caso istanzia i frammenti e clona gli intervalli
	 * contenuti
	 *
	 * @param intX Intervallo sul dominio
	 * @param intY Intervallo sul codominio
	 * @param weight Peso del frammento aggiunto
	 * @param type Tipo di relazione degli intervalli
	 */
	public void addFragment(Interval intX, Interval intY, Integer weight,
		Integer type) {
		if (list.get(type) == null) {
			list.put(type, new Vector<Fragment>());
		}
		list.get(type).add(new Fragment(intX.clone(), intY.clone(), weight));
	}

	/**
	 * Restiruisce un vettore che contiene i frammenti del tipo di relazione richiesta
	 *
	 * @param type tipo di relazione
	 * @return vettore che contiene i frammenti del tipo di relazione richiesta
	 */
	public Vector<Fragment> getFragments(Integer type) {
		return list.get(type);
	}

	/**
	 * Rimuove il frammento f dalla relazione
	 *
	 * @param f Frammento da rimuovere
	 */
	public void subFragment(Fragment f) {
	}

}
