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

package main.diff_L1_L2.core;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Likeness;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author schirinz Classe che si occupa di calcolare il diff sugli attributi
 *         tra due nodi
 */
public class ATTdiff {
	static METAdelta MINIdelta;
	static Double Common;

	/**
	 * Crea un METAdelta relativo agli attributi dei nodi passati in input
	 * Ritorna la percentuale di match tra i due nodi Attacca a VnodeB il
	 * MetaDelta Calcolato nel vettore con posizione A
	 * 
	 * @param nodeA
	 *            Nodo su cui calcolare il diff relativo al documento originale
	 * @param nodeB
	 *            Nodo su cui calcolare il diff relativo al documento modificato
	 * @return Ritorna la somiglianza dei due nodi
	 */
	static public Likeness compute(Dnode nodeA, Dnode nodeB) {
		Common = 0.0;
		
		//its the same node, otherwise similarity would be 0
		if((nodeA.inRel == Relation.NO)
					&& (nodeB.inRel == Relation.NO)
					&& (nodeA.refDomNode.getNodeName().equals(nodeB.refDomNode
							.getNodeName())))	
			Common+=0.5;	

		
		NamedNodeMap attA = nodeA.refDomNode.getAttributes();
		NamedNodeMap attB = nodeB.refDomNode.getAttributes();

		MINIdelta = new METAdelta();

		Node tmp;
		for (int i = 0; i < attA.getLength(); i++) {
			if ((tmp = attB.getNamedItem(attA.item(i).getNodeName())) != null) {
				if (!tmp.getNodeValue().endsWith(attA.item(i).getNodeValue())) {
					MINIdelta.addChangeValueAttOperation(nodeA, nodeB, attA
							.item(i).getNodeName(), tmp.getNodeValue(), attA
							.item(i).getNodeValue());
				}
				Common+=0.3;
		
			} else {// attributo rimosso
				MINIdelta.addDeleteAttOperation(nodeA, nodeB, attA.item(i)
						.getNodeName(), attA.item(i).getNodeValue());
			}
		}

		for (int i = 0; i < attB.getLength(); i++) {
			if ((tmp = attA.getNamedItem(attB.item(i).getNodeName())) == null) {
				MINIdelta.addInsertAttOperation(nodeA, nodeB, attB.item(i)
						.getNodeName(), attB.item(i).getNodeValue());
			}
		}
		// Percentualizzo Common
		if( attA.getLength() == 0 || attB.getLength() == 0 )
			Common = (Common * 100);
		else
			Common = (Common / (Math.min(attA.getLength(), attB.getLength()) )) * 100;
		return new Likeness(Common.intValue(), MINIdelta);
	}

}
