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

package main.diff_L1_L2.vdom.alternatives;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.exceptions.InputFileException;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * @author schirinz Classe astratta Vtree
 * 
 *         Crea la base di un Vtree, inserendo i valori base per ogni nodo. E'
 *         costruita su ArrayList invece che Vector in modo da velocizzare
 *         alcune operazioni a scapito di altre
 */
public abstract class Vtree<T> extends DOMDocument {

	Logger logger = Logger.getLogger(getClass());

	public ArrayList<T> nodeList = new ArrayList<T>(); // Vettore che contiene i
	// Vnode
	public int numNode = 0; // Contatore dei nodi presenti

	/**
	 * Costruttore
	 * 
	 * @param fileXML
	 *            Percorso del file XML su cui costruire il Vtree
	 * @param ltrim
	 *            true se si vogliono eliminare gli spazi a destra nei nodi di
	 *            testo
	 * @param rtrim
	 *            true se si vogliono eliminare gli spazi a sinistra dei nodi di
	 *            testo
	 * @param collapse
	 *            true se si vogliono collassare gli spazi interni in un nodi di
	 *            testo
	 * @param emptynode
	 *            true se si vogliono considerare i nodi vuoti o formati da soli
	 *            spazi di testo
	 * @param commentnode
	 *            true se si vogliono considerare i nodi commento
	 * @throws InputFileException
	 *             Solleva l'eccezione nel caso in cui non riesce a creare il
	 *             Vtree
	 */
	public Vtree(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML);

		//System.out.println("Creating Vtree on document:" + fileXML);

		// Applica normalizzazione forte prima di creare la struttura del Vtree
		strongNormalize(ltrim, rtrim, collapse, emptynode, commentnode);

		//System.out.println("TopDown Visit Start");
		// Calcolo attributi base del Vtree
		visitaTopDown(root, 0, -1, 0);
	}

	/**
	 * Ritorna il numero di nodi presenti
	 * 
	 * @return numero di nodi presenti
	 */
	public int count() {
		return nodeList.size();
	}

	/**
	 * Ritorna il nodo indicizzato da index
	 * 
	 * @param index
	 *            indice del nodo
	 * @return nodo in posizione index
	 */
	public T getNode(int index) {
		return nodeList.get(index);
	}

	/**
	 * Metodo astratto che viene implementato dalle classi che estendono Vtree,
	 * serve per istanziare corretamente il tipo di nodi del Vtree(Dnode,Rnode)
	 * 
	 * @param ownerVtree
	 *            Tree a cui appartiene il nodo
	 * @param DOMnode
	 *            Nodo Dom a cui si riferisce il nodo
	 * @param indexKey
	 *            kiave di accesso per il nodo
	 * @param posFather
	 *            kiave d accesso per il nodo padre
	 * @param posLikeChild
	 *            posizione come figlio
	 * @return nuovo nodo del tipo opportuno
	 */
	public abstract T newNode(Object ownerVtree, Node DOMnode,
			Integer indexKey, Integer posFather, Integer posLikeChild);

	/**
	 * Calcolo degli attributi base del nodo. Attributi ereditati.
	 * 
	 * @param node
	 *            Nodo DOM sul quale sto effettuando la ricorsione
	 * @param index
	 *            indice/kiave del nodo
	 * @param posFather
	 *            indice/kiave del nodo padre
	 * @param posLikeChild
	 *            posizione del nodo come figlio
	 */
	private void visitaTopDown(Node node, int index, int posFather,
			int posLikeChild) {
		if (node != null) {
			index = numNode;
			nodeList.add(index,
					newNode(this, node, numNode, posFather, posLikeChild));
			numNode++;

			// Chiamata ricorsiva a tutti i figli
			if (node.hasChildNodes())
				for (int i = 0; i < node.getChildNodes().getLength(); i++)
					visitaTopDown(node.getChildNodes().item(i), numNode, index,
							i);

		}
	}

}