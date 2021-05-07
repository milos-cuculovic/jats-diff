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

package main.diff_L1_L2.vdom.diffing;

import main.diff_L1_L2.vdom.Vtree;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.relation.Fragment;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Vector;

/**
 * Extends Vtree class, adding methods and properties for the diffing phase
 *
 * @author schirinz
 */
public class Dtree extends Vtree<Dnode> {

	Logger logger = Logger.getLogger(getClass());

	// HashMap to organize subtrees in classes
	public HashMap<String, Vector<Integer>> classes = new HashMap<String, Vector<Integer>>();

	/**
	 * Costruttore - Richiama il costruttore di Vtree che crea la struttura
	 * base, di seguito calcola nuovi attributi specifici per la fase di diff
	 *
	 * @param fileXML
	 *            Percorso del file XML su cui si vuole istanziare il Dtree
	 * @param ltrim
	 *            Se impostato a true elimina i whitespace a sinistra in tutti i
	 *            nodi di testo
	 * @param rtrim
	 *            Se impostato a true elimina i whitespace a destra in tutti i
	 *            nodi di testo
	 * @param collapse
	 *            Se impostato a true collassa i whitespace interni ai nodi di
	 *            testo
	 * @param emptynode
	 *            Se impostato a true non vengono considerati i nodi di testo
	 *            contenenti sono whitespace durante la costruzione del Dtree
	 * @param commentnode
	 *            Se impostato a true non vengono considerati i nodi commento
	 *            durante la costruzione del Dtree
	 */
	public Dtree(String fileXML, boolean ltrim, boolean rtrim,
			boolean collapse, boolean emptynode, boolean commentnode)
			throws InputFileException {
		super(fileXML, ltrim, rtrim, collapse, emptynode, commentnode);

		//System.out.println("BottomUp Visit Start");
		visitaBottomUp();
		getNode(0).hashNode = Hash.Hnode(getNode(0).refDomNode);
	}

	/**
	 * Accesso alle classi - Ritorna un vettore che contiene gli indici dei
	 * sottoalberi presenti in una classe
	 *
	 * @param key
	 *            kiave della classe di cui si vogliono gli indici
	 * @return vettore che contiene gli indici degli elementi appartenenti alla
	 *         classe key
	 */
	public Vector<Integer> getClass(String key) {
		return classes.get(key);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ndiff.vdom.Vtree#newNode(java.lang.Object, org.w3c.dom.Node,
	 * java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Dnode newNode(Object ownerVtree, Node refDomNode, Integer indexKey,
			Integer posFather, Integer posLikeChild) {
		return new Dnode(ownerVtree, refDomNode, indexKey, posFather,
				posLikeChild);
	}

	/**
	 * Espande le informazioni sui frammenti in modo bottom-up
	 *
	 * @param index
	 *            Indice del nodo che rappresenta la radice del frammento
	 * @param f
	 *            Frammento di cui espandere l'informazione
	 */
	public void propagationIdFragment(int index, Fragment f) {
		int indexNode = getNode(index).getPosFather();
		while (indexNode >= 0) {
			getNode(indexNode).fragmentList.put(f.getID(), f);
			getNode(indexNode).hashFragment = Hash
					.md5(getNode(indexNode).hashFragment + f.getID());
			indexNode = getNode(indexNode).posFather;
		}
	}

	/**
	 * Rimuove un frammento in modo bottom-up
	 *
	 * @param index
	 *            Indice del nodo che rappresenta la radice del frammento
	 * @param f
	 *            Frammento di cui espandere l'informazione
	 */
	public void removeIdFragment(int index, Fragment f) {
		int indexNode = getNode(index).getPosFather();
		while (indexNode >= 0) {
			getNode(indexNode).fragmentList.remove(f.getID());
			getNode(indexNode).hashFragment = Hash
					.md5(getNode(indexNode).hashFragment + f.getID());
			indexNode = getNode(indexNode).posFather;
		}
	}

	/**
	 * Effettua una visita Bottom-up del Dtree e valorizza un gruppo di
	 * attributi
	 */
	private void visitaBottomUp() {

		for (int i = nodeList.size() - 1; i > 0; i--) {

			Dnode tmp = getNode(i);
			Dnode father = getNode(tmp.getPosFather());

			// Inizializzo il valroe hashTree per il padre
			if (father.hashTree == null)
				father.hashTree = Hash.Hnode(father.getRefDomNode());

			// Inserisco il valore hashNode per il nodo visitato
			tmp.hashNode = Hash.Hnode(tmp.getRefDomNode());

			// Caso in cui sono in un nodo foglia
			if (!tmp.refDomNode.hasChildNodes()) {
				tmp.numChildSubtree = 0;
				tmp.hashTree = tmp.hashNode;
				tmp.weight = tmp.refDomNode.getTextContent().length() + 1;
			}

			// Aggiornamento dei campi
			father.hashTree = Hash.md5(father.hashTree + tmp.hashTree);
			father.weight += tmp.weight;
			father.numChildSubtree += 1 + tmp.numChildSubtree;

			if (tmp.refDomNode.getNodeType() == Node.TEXT_NODE) {
				tmp.Sequence = new Sequence(tmp.refDomNode);
			}

			if (classes.get(tmp.hashTree) == null)
				classes.put(tmp.hashTree, new Vector<Integer>());
			// classes.get(tmp.hashTree).add(i);
			classes.get(tmp.hashTree).insertElementAt(i, 0);

		}
	}

}
