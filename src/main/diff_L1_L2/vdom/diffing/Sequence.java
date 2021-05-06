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

package main.diff_L1_L2.vdom.diffing;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author schirinz Struttura costruita su un testo, che tokenizza e indicizza i
 *         vari elementi secondo regole prestabilite
 */
public class Sequence {

	Logger logger = Logger.getLogger(getClass());

	// Vettori che contengono gli elemnti e gli offset di partenza per ogni
	// elemento(legati dall'indice)
	public Vector<String> elements = new Vector<String>();
	public Vector<Integer> offset = new Vector<Integer>();

	// HashMap per la suddivisione in classi degli elementi
	public HashMap<String, Vector<Integer>> classes = new HashMap<String, Vector<Integer>>();

	/**
	 * Costruttore
	 * 
	 * @param DOMtextNode
	 *            Nodo dom sul quale costruire la sequenza
	 */
	public Sequence(Node DOMtextNode) {
		// System.out.println("Creazione nuova sequenza");
		cutTextOnWord(DOMtextNode.getTextContent());
		fillClasses();
	}

	/**
	 * Costruttore
	 * 
	 * @param Text
	 *            Testo sul quale costruire la sequenza
	 */
	public Sequence(String Text) {
		cutTextOnWord(Text);
		fillClasses();

	}

	/**
	 * Tokenizzatore, divide il testo in input su determinati token
	 * 
	 * @param text
	 *            Testo da dividere
	 */
	public void cutTextOnWord(String text) {

		// Caratteri su cui splittare
		char split[] = { ',', '.', ' ', '\n', '\t', '\'' };

		Arrays.sort(split);
		// System.out.println(split);

		String word = "";

		int startOffsetWord = 0;

		for (int i = 0; i < text.length(); i++) {

			// System.out.println(text.charAt(i));

			int charSplit = Arrays.binarySearch(split, text.charAt(i));

			// System.out.println("-->"+charSplit);

			if (charSplit >= 0) {
				if (word.length() != 0) {
					elements.add(word);
					offset.add(startOffsetWord);
					word = "";
				}

				elements.add(new String(split, charSplit, 1));
				offset.add(i);
			} else {
				if (word.length() == 0) {
					startOffsetWord = i;
				}
				word += text.charAt(i);
			}

		}

		/* per eventuale parola finale senza nessun testo */
		if (word.length() != 0) {
			elements.add(word);
			offset.add(startOffsetWord);
		}
	}

	/**
	 * Popola le classi della sequenza
	 */
	public void fillClasses() {
		for (int i = 0; i < elements.size(); i++) {
			if (classes.get(elements.get(i)) == null)
				classes.put(elements.get(i), new Vector<Integer>());
			classes.get(elements.get(i)).add(i);
		}
	}

	/**
	 * Ritorna un vettore contenente gli indici degli elementi appartenenti alla
	 * classe key
	 * 
	 * @param key
	 *            Chiave della classe
	 * @return Vector di indici di elementi
	 */
	public Vector<Integer> getClass(String key) {
		return classes.get(key);

	}

	/**
	 * Ritorna la Stringa corrispondente all'elemento index
	 * 
	 * @param index
	 *            indice dell'elemento
	 * @return Contenuto dell'elemento
	 */
	public String getElement(int index) {
		return elements.get(index);
	}

	/**
	 * Ritorna l'offset dell'elemento index
	 * 
	 * @param index
	 *            indice dell'elemento di cui si vuole l'offset
	 * @return offset iniziale dell'elemento index
	 */
	public int getOffset(int index) {
		return offset.get(index);
	}

	/**
	 * Ritorna il numero di elementi presenti nella sequenza
	 * 
	 * @return Numero di elementi presenti nella sequenza
	 */
	public int length() {
		return elements.size();
	}

	/**
	 * Ritorna una stringa che rappresenta il contenuto della sequenza
	 * 
	 * @param Voffset
	 *            True se voglio visualizzare anche gli offset
	 * @return Stringa che rappresenta il contenuto della sequenza
	 */
	public String show(boolean Voffset) {
		String ret = "";

		for (int i = 0; i < elements.size(); i++) {
			if (Voffset)
				ret += i + ":[" + offset.get(i) + "(" + elements.get(i) + ")] ";
			else
				ret += i + ":[" + elements.get(i) + "] ";
		}

		return ret;
	}

}
