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

package main.diff_L1_L2.vdom.reconstruction;

import main.diff_L1_L2.vdom.Vnode;
import org.w3c.dom.Node;

/**
 * @author Mike Estensione del nodo DOM per aggiungere delle proprietà per la
 *         ricostruzione del documento
 * 
 */
public class Rnode extends Vnode {

	public int isNew = 0;
	public boolean isEditing = false;
	public int countDel = 0;

	/**
	 * Costruttore
	 */
	public Rnode() {
	};

	/**
	 * Costruttore
	 * 
	 * @param ownerVtree
	 *            Dtree di appartenenza del nodo
	 * @param refDomNode
	 *            Nodo DOM di riferimento
	 * @param indexKey
	 *            Indice del nodo
	 * @param posFather
	 *            Indice del padre del nodo
	 * @param posLikeChild
	 *            Posizione come figlio del nodo
	 */
	public Rnode(Object ownerVtree, Node refDomNode, int indexKey,
			int posFather, int posLikeChild) {
		super(indexKey, refDomNode, posFather, posLikeChild);
		setOwnerVtree(ownerVtree);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see vdom.Vnode#getOwnerVtree()
	 */
	@Override
	public Rtree getOwnerVtree() {
		return (Rtree) ownerVtree;
	}

}
