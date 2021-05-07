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
package main.diff_L1_L2.metadelta;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.core.Nconfig;
import java.util.List;
import java.util.ArrayList;

import java.util.Vector;
import main.diff_L1_L2.vo.TextChange;
import main.diff_L1_L2.vo.TextChangeData;

/**
 * @author schirinz Contiene le informazioni che verranno trasformate nel delta
 */
public class METAdelta {

	public final static String NAMESPACE = "https://github.com/milos-cuculovic/jats-diff";

	// Vettori che rappresentano i tre blocchi di operazioni
	public Vector<Operation> deleteOps = new Vector<Operation>(); // Operazioni
	// di
	// cancellazione
	public Vector<Operation> insertOps = new Vector<Operation>(); // Operazioni
	// di
	// inseirmento
	public Vector<Operation> updateOps = new Vector<Operation>(); // Operazioni

	// merge
	public Vector<Operation> mergeFromOps = new Vector<Operation>();

	// merge
	public Vector<Operation> mergeToOps = new Vector<Operation>();

	// split
	public Vector<Operation> splitFromOps = new Vector<Operation>();

	// split
	public Vector<Operation> splitToOps = new Vector<Operation>();

	// upgrade
	public Vector<Operation> upgradeToOps = new Vector<Operation>();

	public Vector<Operation> upgradeFromOps = new Vector<Operation>();

	// downgrade
	public Vector<Operation> downgradeToOps = new Vector<Operation>();

	public Vector<Operation> downgradeFromOps = new Vector<Operation>();

	public Vector<Operation> moveTextToOps = new Vector<Operation>();

	public Vector<Operation> moveTextFromOps = new Vector<Operation>();

	public Vector<Operation> insertStyleOps = new Vector<Operation>();

	public Vector<Operation> deleteStyleOps = new Vector<Operation>();

	public Vector<Operation> updateStyleOps = new Vector<Operation>();

	public Vector<Operation> updateStyleToOps = new Vector<Operation>();

	public Vector<Operation> updateStyleFromOps = new Vector<Operation>();

	public Vector<Operation> insertStyleTextOps = new Vector<Operation>();

	public Vector<Operation> deleteStyleTextOps = new Vector<Operation>();

	public Vector<Operation> updateStyleTextOps = new Vector<Operation>();

	public Vector<Operation> updateStyleTextToOps = new Vector<Operation>();

	public Vector<Operation> updateStyleTextFromOps = new Vector<Operation>();

	public List<Dnode> tmpListA = new ArrayList<>();
	public List<Dnode> tmpListB = new ArrayList<>();

	// di
	// aggiornamento
	/**
	 * Op. di cambio del valore di un attributo
	 *
	 * @param nodeA Nodo nel documento originale su cui devono essere modificati gli attributi
	 * @param nodeB Nodo nel documento modificato con gli attributi modificati
	 * @param attName Nome dell'attributo da modificare
	 * @param newValue Nuono valore dell'attributo modificato
	 * @param oldValue Vecchio valore dell'attributo modificato
	 */
	public void addChangeValueAttOperation(Dnode nodeA, Dnode nodeB,
		String attName, String newValue, String oldValue) {
		updateOps.add(new AOperation(Operation.CHANGE_VALUE_ATT, nodeA, nodeB,
			attName, newValue, oldValue));
	}

	/**
	 * Op. di cancellazione di un attributo
	 *
	 * @param nodeA Nodo nel documento originale su cui devono essere modificati gli attributi
	 * @param nodeB Nodo nel documento modificato con gli attributi modificati
	 * @param attName Nome dell'attributo da rimuovere
	 * @param attValue Valore dell'attributo che viene rimosso
	 */
	public void addDeleteAttOperation(Dnode nodeA, Dnode nodeB, String attName,
		String attValue) {
		updateOps.add(new AOperation(Operation.DELETE_ATT, nodeA, nodeB,
			attName, null, attValue));
	}

	/**
	 * Op. di cancellazione di un singolo nodo
	 *
	 * @param nodeA node che deve essere rimosso dal documento originale
	 */
	public void addDeleteNodeOperation(Dnode nodeA) {
		deleteOps.add(new SOperation(Operation.DELETE_NODE, nodeA, null));
	}

	/**
	 * Op. di cancellazine di testo
	 *
	 * @param nodeA Nodo di testo relativo al documento originale su cui deve essere effettuata la modifica al contenuto
	 * @param nodeB Nodo di testo relativo al documento modificato su cui deve essere effettuata la modifica al contenuto
	 * @param pStart Offset di partenza per la cancellazione del testo
	 * @param length Lunghezza del testo che deve essere rimosso
	 */
	public void addDeleteTextOperation(Dnode nodeA, Dnode nodeB, int pStart,
		int length) {
		updateOps.add(new TOperation(Operation.DELETE_TEXT, nodeA, nodeB,
			pStart, length));
	}

	/**
	 * Op. di cancellazione di un sottoalbero
	 *
	 * @param nodeA Nodo relativo alla radice del sottoalbero che deve essere rimosso
	 */
	public void addDeleteTreeOperation(Dnode nodeA) {
		deleteOps.add(new SOperation(Operation.DELETE_TREE, nodeA, null));
	}

	/**
	 * Op. di spostamento di contesto di un nodo
	 *
	 * @param indexNodeA Indice in VtreeA della radice del sottoalbero spostato
	 * @param indexNodeB Indice in VtreeB della radice del sottoalbero spostato
	 * @param indexFatherB Indice in VtreeB del nodo padre in VtreeB del sottoalbero spostato *
	 */
	/*
	 * public void addContextMoveOperation(Dnode nodeA, Dnode nodeB){
	 * deleteOps.add( new SOperation(Operation.CONTEXT_MOVE_TO,nodeA,nodeB) );
	 * insertOps.add( new SOperation(Operation.CONTEXT_MOVE_FROM,nodeA,nodeB) );
	 * }
	 */
	/**
	 * Op. di inserimento di un attributo
	 *
	 * @param nodeA Nodo nel documento originale su cui devono essere modificati gli attributi
	 * @param nodeB Nodo nel documento modificato con gli attributi modificati
	 * @param attName Nome dell'attributo da inserire
	 * @param attValue Valore dell'attributo da inserire
	 */
	public void addInsertAttOperation(Dnode nodeA, Dnode nodeB, String attName,
		String attValue) {
		updateOps.add(new AOperation(Operation.INSERT_ATT, nodeA, nodeB,
			attName, attValue, null));
	}

	/**
	 * Op. di inserimento singolo nodo
	 *
	 * @param nodeB Nodo relativo al documento B che deve essere inserito
	 */
	public void addInsertNodeOperation(Dnode nodeB) {
		insertOps.add(new SOperation(Operation.INSERT_NODE, null, nodeB));
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	/**
	 * Op. di inserimento di testo all'interno di un nodo testuale
	 *
	 * @param nodeA Nodo di testo relativo al documento originale su cui deve essere effettuata la modifica al contenuto
	 * @param nodeB Nodo di testo relativo al documento modificato su cui deve essere effettuata la modifica al contenuto
	 * @param pStart Offset di partenza per l'inserimento del testo
	 * @param length Lunghezza del testo che deve essere inserito
	 */
	public void addInsertTextOperation(Dnode nodeA, Dnode nodeB, int pStart,
		int length) {
		updateOps.add(new TOperation(Operation.INSERT_TEXT, nodeA, nodeB,
			pStart, length));
	}

	/**
	 * Op. di inserimento sottoalbero
	 *
	 * @param nodeB Nodo relativo alla radice del sottoalbero da inserire nel documento B
	 */
	public void addInsertTreeOperation(Dnode nodeB) {
		insertOps.add(new SOperation(Operation.INSERT_TREE, null, nodeB));
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	/**
	 * Op. di spostamento di un sottoalbero
	 *
	 * @param nodeA nodo che rappresenta la radice del sottoalbero da spostare nel documento originale
	 * @param nodeB nodo che rappresenta la radice del sottoalbero da spostare nel documento modificato
	 */
	public void addMoveOperation(Dnode nodeA, Dnode nodeB) {

		boolean find = false;

		// Create an operation and insert it in the right position between the deletes
		SOperation tmpOp = new SOperation(Operation.MOVE_TO, nodeA, nodeB);
		tmpOp.IDmove = nodeA.indexKey + "::" + nodeB.indexKey;
		for (int i = 0; i < deleteOps.size() && !find; i++) {
			if (deleteOps.get(i).nodeA.indexKey > nodeA.indexKey) {
				deleteOps.insertElementAt(tmpOp, i);
				find = true;
			}
		}
		if (!find) {
			deleteOps.add(tmpOp);
		}

		// Create an operation and insert it in the right position between the insertions
		find = false;
		tmpOp = new SOperation(Operation.MOVE_FROM, nodeA, nodeB);
		tmpOp.IDmove = nodeA.indexKey + "::" + nodeB.indexKey;
		for (int i = 0; i < insertOps.size() && !find; i++) {

			if (insertOps.get(i).nodeB.indexKey > nodeB.indexKey) {
				insertOps.insertElementAt(tmpOp, i);
				find = true;
			}
		}
		if (!find) {
			insertOps.add(tmpOp);
		}
		(nodeB.getOwnerVtree().getNode(nodeB.posFather)).insOnMe++;
	}

	public void addMergeFromOperation(Dnode nodeA, String id) {
		SOperation tmpOp = new SOperation(Operation.MERGE_FROM, nodeA, null);
		tmpOp.IDmerge = id;
		mergeFromOps.add(tmpOp);
	}

	/**
	 * Merge of a subtree
	 *
	 * @param nodeB node representing the root of the subtree to be merged to the modified document
	 */
	public void addMergeToOperation(Dnode nodeB, String id) {

		SOperation tmpOp = new SOperation(Operation.MERGE_TO, null, nodeB);
		tmpOp.IDmerge = id;
		mergeToOps.add(tmpOp);

	}

	public void addSplitFromOperation(Dnode nodeA, String id) {
		SOperation tmpOp = new SOperation(Operation.SPLIT_FROM, nodeA, null);
		tmpOp.IDsplit = id;
		splitFromOps.add(tmpOp);
	}

	public void addSplitToOperation(Dnode nodeB, String id) {

		SOperation tmpOp = new SOperation(Operation.SPLIT_TO, null, nodeB);
		tmpOp.IDsplit = id;
		splitToOps.add(tmpOp);

	}

	//upgrade
	public void addUpgradeFromOperation(Dnode nodeA) {
		SOperation tmpOp = new SOperation(Operation.UPGRADE_FROM, nodeA, null);
		tmpOp.IDupgrade = nodeA.indexKey + "";
		upgradeFromOps.add(tmpOp);
	}

	/**
	 * Upgrade of a subtree
	 *
	 *
	 * @param nodeB node representing the root of the subtree to be merged to the modified document
	 */
	public void addUpgradeToOperation(Dnode nodeB) {

		SOperation tmpOp = new SOperation(Operation.UPGRADE_TO, null, nodeB);
		tmpOp.IDupgrade = nodeB.indexKey + "";
		upgradeToOps.add(tmpOp);

	}

	//downgrade
	public void addDowngradeFromOperation(Dnode nodeA) {
		SOperation tmpOp = new SOperation(Operation.DOWNGRADE_FROM, nodeA, null);
		tmpOp.IDdowngrade = nodeA.indexKey + "";
		downgradeFromOps.add(tmpOp);
	}

	/**
	 * Upgrade of a subtree
	 *
	 *
	 * @param nodeB node representing the root of the subtree to be merged to the modified document
	 */
	public void addDowngradeToOperation(Dnode nodeB) {

		SOperation tmpOp = new SOperation(Operation.DOWNGRADE_TO, null, nodeB);
		tmpOp.IDdowngrade = nodeB.indexKey + "";
		downgradeToOps.add(tmpOp);

	}

	//movetext from
	public void addMoveTextFromOperation(Dnode nodeA) {
		SOperation tmpOp = new SOperation(Operation.MOVETEXT_FROM, nodeA, null);
		tmpOp.IDmovetext = nodeA.indexKey + "";
		moveTextFromOps.add(tmpOp);
	}

	/**
	 * movetext to
	 */
	public void addMoveTextToOperation(Dnode nodeB) {
		SOperation tmpOp = new SOperation(Operation.MOVETEXT_TO, null, nodeB);
		tmpOp.IDmovetext = nodeB.indexKey + "";
		moveTextToOps.add(tmpOp);

	}

	/**
	 * style from
	 *
	 * @param nodeA
	 */
	public void addStyleDeleteOperation(Dnode nodeA) {
		SOperation tmpOp = new SOperation(Operation.DELETE_STYLE, nodeA, null);
		tmpOp.IDdeletestyle = nodeA.indexKey + "";
		deleteStyleOps.add(tmpOp);
	}

	/**
	 * style to
	 *
	 * @param nodeB
	 */
	public void addStyleInsertOperation(Dnode nodeB) {

		SOperation tmpOp = new SOperation(Operation.INSERT_STYLE, null, nodeB);
		tmpOp.IDinsertstyle = nodeB.indexKey + "";
		insertStyleOps.add(tmpOp);

	}

	/**
	 * style to
	 *
	 * @param nodeB
	 */
	public void addStyleUpdateToOperation(Dnode nodeB) {

		SOperation tmpOp = new SOperation(Operation.UPDATE_STYLE_TO, null, nodeB);
		tmpOp.IDupdatestyleTo = nodeB.indexKey + "";
		updateStyleToOps.add(tmpOp);

	}

	/**
	 * style to
	 *
	 * @param nodeB
	 */
	public void addStyleUpdateFromOperation(Dnode nodeA) {

		SOperation tmpOp = new SOperation(Operation.UPDATE_STYLE_FROM, nodeA, null);
		tmpOp.IDupdatestyleFrom = nodeA.indexKey + "";
		updateStyleFromOps.add(tmpOp);

	}
///////////////////////////////////

	/**
	 * Insert style text operation
	 *
	 * @param nodeB
	 */
	public void addInsertStyleTextOperation(Dnode nodeB) {

		SOperation tmpOp = new SOperation(Operation.INSERT_STYLE_TEXT, null, nodeB);
		tmpOp.IDinsertstyle = nodeB.indexKey + "";
		insertStyleTextOps.add(tmpOp);

	}

	/**
	 * Delete style text operation
	 *
	 * @param nodeA
	 */
	public void addDeleteStyleTextOperation(Dnode nodeA) {

		SOperation tmpOp = new SOperation(Operation.DELETE_STYLE_TEXT, nodeA, null);
		tmpOp.IDdeletestyletext = nodeA.indexKey + "";
		deleteStyleTextOps.add(tmpOp);

	}

	/**
	 * Update Style text operation
	 *
	 * @param nodeB
	 */
	public void addUpdateStyleToTextOperation(Dnode nodeB) {
		SOperation tmpOp = new SOperation(Operation.UPDATE_STYLE_TEXT_TO, null, nodeB);
		tmpOp.IDupdatestyletextto = nodeB.indexKey + "";
		updateStyleTextToOps.add(tmpOp);

	}

	/**
	 * Update Style text operation
	 *
	 * @param nodeB
	 */
	public void addUpdateStyleFromTextOperation(Dnode nodeA) {

		SOperation tmpOp = new SOperation(Operation.UPDATE_STYLE_TEXT_FROM, nodeA, null);
		tmpOp.IDupdatestyletextfrom = nodeA.indexKey + "";
		updateStyleTextFromOps.add(tmpOp);

	}

	/**
	 * Aggiunge le operazioni presenti nel METAdelta passato
	 *
	 * @param M METAdelta di cui si vogliono aggiungere le operazioni
	 */
	public void merge(METAdelta M) {
		for (int k = 0; k < M.insertOps.size(); k++) {
			insertOps.add(M.insertOps.get(k));
		}

		for (int k = 0; k < M.deleteOps.size(); k++) {
			deleteOps.add(M.deleteOps.get(k));
		}

		for (int k = 0; k < M.updateOps.size(); k++) {
			updateOps.add(M.updateOps.get(k));
		}
	}

	/**
	 * Trasforma le informazioni contenute nel META delta in un DOMDocument
	 *
	 * @return DOMDocument corrispondente alle operazioni presenti nel METAdelta
	 */
	public DOMDocument transformToXML(Nconfig cfg) {

		DOMDocument Ndelta = new DOMDocument(NAMESPACE, "ndiff");

		Ndelta.root.setAttribute("ltrim",
			cfg.getPhaseParam(Nconfig.Normalize, "ltrim"));
		Ndelta.root.setAttribute("rtrim",
			cfg.getPhaseParam(Nconfig.Normalize, "rtrim"));
		Ndelta.root.setAttribute("collapse",
			cfg.getPhaseParam(Nconfig.Normalize, "collapse"));
		Ndelta.root.setAttribute("emptynode",
			cfg.getPhaseParam(Nconfig.Normalize, "emptynode"));
		Ndelta.root.setAttribute("commentnode",
			cfg.getPhaseParam(Nconfig.Normalize, "commentnode"));

		//try reomve style change in text changes before create xml part for text change
//        this.removeStyleOpertaionToTextOperation();
		///// MOVE TEXT
		for (int i = 0; i < moveTextToOps.size(); i++) {
			if (!tmpListB.contains(moveTextToOps.get(i).nodeB)) {
				tmpListB.add(moveTextToOps.get(i).nodeB);
			}

			moveTextToOps.get(i).dump(Ndelta);
		}

		for (int i = 0; i < moveTextFromOps.size(); i++) {
			if (!tmpListA.contains(moveTextFromOps.get(i).nodeA)) {
				tmpListA.add(moveTextFromOps.get(i).nodeA);
			}
			moveTextFromOps.get(i).dump(Ndelta);
		}

		//INSERT STYLE
		for (int i = 0; i < insertStyleOps.size(); i++) {
//			logger.info("test");
//			System.exit(0);

			insertStyleOps.get(i).dump(Ndelta);

		}
		//DELETE STYLE
		for (int i = 0; i < deleteStyleOps.size(); i++) {

			if (!tmpListA.contains(deleteStyleOps.get(i).nodeA)) {
				tmpListA.add(deleteStyleOps.get(i).nodeA);
			}
			deleteStyleOps.get(i).dump(Ndelta);

		}
		//UPDATE STYLE TO
		for (int i = 0; i < updateStyleToOps.size(); i++) {

			if (!tmpListB.contains(updateStyleToOps.get(i).nodeB)) {
				tmpListB.add(updateStyleToOps.get(i).nodeB);
			}
			updateStyleToOps.get(i).dump(Ndelta);

		}
		//UPDATE STYLE FROM
		for (int i = 0; i < updateStyleFromOps.size(); i++) {

			if (!tmpListA.contains(updateStyleFromOps.get(i).nodeA)) {
				tmpListA.add(updateStyleFromOps.get(i).nodeA);
			}
			updateStyleFromOps.get(i).dump(Ndelta);

		}
		//INSERT TEXT IF STYLE CASE
		for (int i = 0; i < insertStyleTextOps.size(); i++) {

			if (!tmpListB.contains(insertStyleTextOps.get(i).nodeB)) {
				tmpListB.add(insertStyleTextOps.get(i).nodeB);
			}
			insertStyleTextOps.get(i).dump(Ndelta);
		}
		//DELETE TEXT IF STYLE CASE
		for (int i = 0; i < deleteStyleTextOps.size(); i++) {

			if (!tmpListA.contains(deleteStyleTextOps.get(i).nodeA)) {
				tmpListA.add(deleteStyleTextOps.get(i).nodeA);
			}
			deleteStyleTextOps.get(i).dump(Ndelta);

		}
		//UPDATE TEXT TO IF STYLE CASE
		for (int i = 0; i < updateStyleTextToOps.size(); i++) {

			if (!tmpListB.contains(updateStyleTextToOps.get(i).nodeB)) {
				tmpListB.add(updateStyleTextToOps.get(i).nodeB);
			}
			updateStyleTextToOps.get(i).dump(Ndelta);

		}

		//UPDATE TEXT TO IF STYLE CASE
		for (int i = 0; i < updateStyleTextFromOps.size(); i++) {

			if (!tmpListA.contains(updateStyleTextFromOps.get(i).nodeA)) {
				tmpListA.add(updateStyleTextFromOps.get(i).nodeA);
			}
			updateStyleTextFromOps.get(i).dump(Ndelta);

		}

		for (int i = 0; i < deleteOps.size(); i++) {
			deleteOps.get(i).dump(Ndelta);
		}

		for (int i = 0; i < insertOps.size(); i++) {
			insertOps.get(i).dump(Ndelta);
		}

		for (int i = 0; i < updateOps.size(); i++) {
			updateOps.get(i).dump(Ndelta);
		}

		///// Upgrade
		List<Dnode> tmpUpgradeTo = new ArrayList<>();
		for (int i = 0; i < upgradeToOps.size(); i++) {

			if (tmpUpgradeTo.contains(upgradeToOps.get(i).nodeB)) {
				continue;
			}
			upgradeToOps.get(i).dump(Ndelta);
			tmpUpgradeTo.add(upgradeToOps.get(i).nodeB);
		}

		List<Dnode> tmpUpgradeFrom = new ArrayList<>();
		for (int i = 0; i < upgradeFromOps.size(); i++) {

			if (tmpUpgradeFrom.contains(upgradeFromOps.get(i).nodeA)) {
				continue;
			}
			upgradeFromOps.get(i).dump(Ndelta);
			tmpUpgradeFrom.add(upgradeFromOps.get(i).nodeA);
		}

		//downgrade
		List<Dnode> tmpDowngradeTo = new ArrayList<>();
		for (int i = 0; i < downgradeToOps.size(); i++) {

			if (tmpDowngradeTo.contains(downgradeToOps.get(i).nodeB)) {
				continue;
			}
			downgradeToOps.get(i).dump(Ndelta);
			tmpDowngradeTo.add(downgradeToOps.get(i).nodeB);
		}

		List<Dnode> tmpDowngradeFrom = new ArrayList<>();
		for (int i = 0; i < downgradeFromOps.size(); i++) {

			if (tmpDowngradeFrom.contains(downgradeFromOps.get(i).nodeA)) {
				continue;
			}
			downgradeFromOps.get(i).dump(Ndelta);
			tmpDowngradeFrom.add(downgradeFromOps.get(i).nodeA);
		}

		//// MERGE
		List<Dnode> tmpMergeTo = new ArrayList<>();
		for (int i = 0; i < mergeToOps.size(); i++) {

			if (tmpMergeTo.contains(mergeToOps.get(i).nodeB)) {
				continue;
			}
			mergeToOps.get(i).dump(Ndelta);
			tmpMergeTo.add(mergeToOps.get(i).nodeB);
		}

		List<Dnode> tmpMergeFrom = new ArrayList<>();
		for (int i = 0; i < mergeFromOps.size(); i++) {

			if (tmpMergeFrom.contains(mergeFromOps.get(i).nodeA)) {
				continue;
			}
			mergeFromOps.get(i).dump(Ndelta);
			tmpMergeFrom.add(mergeFromOps.get(i).nodeA);
		}

		///// SPLIT
		List<Dnode> tmpSplitTo = new ArrayList<>();
		for (int i = 0; i < splitToOps.size(); i++) {

			if (tmpSplitTo.contains(splitToOps.get(i).nodeB)) {
				continue;
			}
			splitToOps.get(i).dump(Ndelta);
			tmpSplitTo.add(splitToOps.get(i).nodeB);
		}

		List<Dnode> tmpSplitFrom = new ArrayList<>();
		for (int i = 0; i < splitFromOps.size(); i++) {

			if (tmpSplitFrom.contains(splitFromOps.get(i).nodeA)) {
				continue;
			}
			splitFromOps.get(i).dump(Ndelta);
			tmpSplitFrom.add(splitFromOps.get(i).nodeA);
		}

		Ndelta.DOM.normalizeDocument();

		return Ndelta;
	}

	/**
	 * additional function removeStyleOpertaionToTextOperation
	 */
	public void removeStyleOpertaionToTextOperation() {
		TextChangeData textChangeData = TextChangeData.getInstance();
		List<TextChange> textChangeDataAll = textChangeData.getAllTextChanges();
//        logger.info(textChangeData.getAllTextChanges());
//        System.exit(0);
		//remove style changes in jats-diff insert/delete
		Vector<Operation> tmpDel = new Vector<Operation>();
		for (int i = 0; i < insertOps.size(); i++) {
			Dnode tmpB = insertOps.get(i).nodeB;
			TextChange resultB = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeB().getIndexKey().intValue() == tmpB.indexKey.intValue())
				.findAny()
				.orElse(null);
			TextChange resultPrentB = textChangeDataAll.stream()
				.filter(textChange -> textChange.getParentNodeB().getIndexKey().intValue() == tmpB.indexKey.intValue())
				.findAny()
				.orElse(null);
			if (resultB != null || resultPrentB != null) {

				insertOps.remove(insertOps.get(i));
			}

		}

		for (int i = 0; i < deleteOps.size(); i++) {
			Dnode tmpA = deleteOps.get(i).nodeA;
			TextChange resultA = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeA().getIndexKey().intValue() == tmpA.indexKey.intValue())
				.findAny()
				.orElse(null);
			TextChange resultPrentA = textChangeDataAll.stream()
				.filter(textChange -> textChange.getParentNodeA().getIndexKey().intValue() == tmpA.indexKey.intValue())
				.findAny()
				.orElse(null);
			if (resultA != null || resultPrentA != null) {

				deleteOps.remove(deleteOps.get(i));
			}

		}

		for (int i = 0; i < updateOps.size(); i++) {
			Dnode tmpA = updateOps.get(i).nodeA;
			Dnode tmpB = updateOps.get(i).nodeB;

//            TextChange resultPrentA = textChangeDataAll.stream()
//                    .filter(textChange -> textChange.getParentNodeA().getIndexKey().intValue() == tmpA.indexKey.intValue())
//                    .findAny()
//                    .orElse(null);
//            TextChange resultPrentB = textChangeDataAll.stream()
//                    .filter(textChange -> textChange.getParentNodeB().getIndexKey().intValue() == tmpB.indexKey.intValue())
//                    .findAny()
//                    .orElse(null);
			TextChange resultAo = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeA().getIndexKey().intValue() == tmpB.indexKey.intValue())
				.findAny()
				.orElse(null);
			TextChange resultBo = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeB().getIndexKey().intValue() == tmpA.indexKey.intValue())
				.findAny()
				.orElse(null);

			TextChange resultA = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeA().getIndexKey().intValue() == tmpA.indexKey.intValue())
				.findAny()
				.orElse(null);
			TextChange resultB = textChangeDataAll.stream()
				.filter(textChange -> textChange.getNodeB().getIndexKey().intValue() == tmpB.indexKey.intValue())
				.findAny()
				.orElse(null);
			if (resultA != null || resultB != null
				|| resultAo != null || resultBo != null) {

				updateOps.remove(updateOps.get(i));
			}

		}

	}

}
