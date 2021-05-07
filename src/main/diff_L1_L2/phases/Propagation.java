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

package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Fragment;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * @author Mike Expands the match towards parents, starting from the equal fragments
 *         During the expansion, the following rules apply:
 * 
 *         The propagation is based on the following parameters: - node's name and
 *         node's attributes - fragments belonging to the subtree
 * 
 *         Logic: - if two nodes are equal - if they have the same fragments
 *         => EQUAL - if they don't have the same fragments
 *         => EQUAL + MOVE of non common fragments
 * 
 *         - if just the node name and a  %>level on the attributes are equal
 *         - if they have the same fragments => UPDATE on the attributes
 * 
 */
public class Propagation extends Phase {

	/** Class implementing the Quicksort algorithm as to modify fragments in weight order
	 * @author Mike 
	 *    
	 */
	public class Quicksort {

		public final Random RND = new Random();

		private int partition(Vector<Fragment> array, int begin, int end) {
			int index = begin + RND.nextInt(end - begin + 1);
			Fragment pivot = array.get(index);
			swap(array, index, end);
			for (int i = index = begin; i < end; ++i) {
				if (array.get(i).getWeight() > pivot.getWeight()) {
					swap(array, index++, i);
				}
			}
			swap(array, index, end);
			return (index);
		}

		private void qsort(Vector<Fragment> array, int begin, int end) {
			if (end > begin) {
				int index = partition(array, begin, end);
				qsort(array, begin, index - 1);
				qsort(array, index + 1, end);
			}
		}

		public void sort(Vector<Fragment> array) {
			qsort(array, 0, array.size() - 1);
		}

		/**
		 * Swap order of 2 elements
		 * 
		 * @param array
		 *            Vector on which to apply operation
		 * @param i
		 *            Index of the element to swap
		 * @param j
		 *            Index of the element to swap
		 */
		private void swap(Vector<Fragment> array, int i, int j) {
			Fragment tmp = array.get(i);
			array.setElementAt(array.get(j), i);
			array.setElementAt(tmp, j);
		}
	}

	Vector<Fragment> fragmentList = new Vector<Fragment>();
	Integer attsimilarity;

	Boolean forcematch;

	/**
	 * Constructor
	 * 
	 * @param SearchField
	 *            remaining search fields in NxN
	 * @param Rel
	 *            detected relations between document nodes
	 * @param Ta
	 *            Original document Dtree
	 * @param Tb
	 *            Modified document Dtree
	 * @param cfg
	 *            Diff configuration Nconfig
	 */
	public Propagation(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
                       Nconfig cfg) {
		super(SearchField, Rel, Ta, Tb, cfg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ndiff.phases.Phase#compute()
	 */
	@Override
	public void compute() throws ComputePhaseException {

		try {
			attsimilarity = cfg.getIntPhaseParam(Nconfig.Propagation,
					"attsimilarity");
			forcematch = cfg.getBoolPhaseParam(Nconfig.Propagation,
					"forcematch");
			logger.info("START with attsimilarity=" + attsimilarity
					+ " forcematch:" + forcematch);

			Fragment tmp;
			if (R.getFragments(Relation.EQUAL) != null)
				for (int i = 0; i < R.getFragments(Relation.EQUAL).size(); i++) {
					tmp = R.getFragments(Relation.EQUAL).get(i);
					fragmentList.add(tmp);
					A.propagationIdFragment(tmp.getNnRootA(), tmp);
					B.propagationIdFragment(tmp.getNnRootB(), tmp);
				}
			if (R.getFragments(Relation.UPDATE) != null)
				for (int i = 0; i < R.getFragments(Relation.UPDATE).size(); i++) {
					tmp = R.getFragments(Relation.UPDATE).get(i);
					fragmentList.add(tmp);
					A.propagationIdFragment(tmp.getNnRootA(), tmp);
					B.propagationIdFragment(tmp.getNnRootB(), tmp);
				}


			// Sorting the fragments according to their weight
			if (fragmentList.size() > 0)
				(new Quicksort()).qsort(fragmentList, 0,
						fragmentList.size() - 1);

			/*
			 * for(int i=0; i< fragmentList.size(); i++){
			 * logger.info(i+")"
			 * +fragmentList.get(i).getWeight()+" - "+fragmentList
			 * .get(i).getID()); }
			 */

			// Trying to equalize Father nodes in a recursive way
			for (int i = 0; i < fragmentList.size(); i++) {
				int x = A.getNode(fragmentList.get(i).getNnRootA()).posFather;
				int y = B.getNode(fragmentList.get(i).getNnRootB()).posFather;

				if ((x > 0) && (y > 0)) {
					logger.info("Start Propagation: " + "(" + x + ")"
							+ A.getNode(x).refDomNode.getNodeName() + " - ("
							+ y + ")" + B.getNode(y).refDomNode.getNodeName());
					propagationToParent(x, y);
				}
			}

			logger.info("END");
		} catch (Exception e) {
			logger.error("Errore: ", e);
			throw new ComputePhaseException("FindUpdate");
		}
	}

	/**
	 * Propagates the match towards parents
	 * 
	 * @param indexA
	 *            Index of the node from where to start in the original document
	 * @param indexB
	 *            Index of the node from where to start in the modified document
	 */
	private void propagationToParent(int indexA, int indexB) {
	
		String tmpDebug = "";

		// Se nn sono arrivato alla radice
		if ((indexA > 0) && (indexB > 0)) {

			// confronta i sue singoli nodi indexA e indexB
			Dnode nodeA = A.getNode(indexA);
			Dnode nodeB = B.getNode(indexB);

//			tmpDebug += "TEST  A:" + indexA + " - B:" + indexB;

			// Caso in cui i nodi sono identici
			if ((nodeA.inRel == Relation.NO) && (nodeB.inRel == Relation.NO)
					&& (nodeA.getHashNode().equals(nodeB.getHashNode()))) {

				tmpDebug += " identici:si";

				// Se i frammenti sono gli stessi
				if (nodeA.hashFragment.equals(nodeB.hashFragment)) {

					tmpDebug += " frammenti:si";

					nodeA.inRel = Relation.EQUAL;
					nodeB.inRel = Relation.EQUAL;
					SF.subPoint(indexA, indexB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					R.addFragment(indexA, indexB, 1, Relation.EQUAL);
					logger.info(tmpDebug);
					propagationToParent(nodeA.posFather, nodeB.posFather);

				} // If the fragments are not equals, I equalize the nodes
					// moving the correct fragments
				else if (forcematch) {

					tmpDebug += " frammenti:no move:";

					// Eguaglio il singolo nodo
					nodeA.inRel = Relation.EQUAL;
					nodeB.inRel = Relation.EQUAL;
					SF.subPoint(indexA, indexB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					R.addFragment(indexA, indexB, 1, Relation.EQUAL);

					// Sistemo i frammenti
					Vector<Fragment> diff = sub(nodeA.fragmentList,
							nodeB.fragmentList);

					for (int i = 0; i < diff.size(); i++) {
						tmpDebug += diff.get(i).getID() + "/";

						fragmentList.remove(diff.get(i));
						A.removeIdFragment(diff.get(i).getNnRootA(),
								diff.get(i));
						B.removeIdFragment(diff.get(i).getNnRootB(),
								diff.get(i));

						R.addFragment(diff.get(i).getA(), diff.get(i).getB(),
								1, Relation.MOVE);

						// Sistemo il campo interno sui vdom, (necessario solo
						// per visualizzazione debug)
						for (int k = diff.get(i).getA().inf; k <= diff.get(i)
								.getA().sup; k++)
							A.getNode(k).inRel = Relation.MOVE;
						for (int k = diff.get(i).getB().inf; k <= diff.get(i)
								.getB().sup; k++)
							B.getNode(k).inRel = Relation.MOVE;
					}
					logger.info(tmpDebug);
					propagationToParent(nodeA.posFather, nodeB.posFather);
				}
			}// nodes have the same name and attributes are similar 
			else if ((nodeA.inRel == Relation.NO)
					&& (nodeB.inRel == Relation.NO)
					&& (nodeA.refDomNode.getNodeName().equals(nodeB.refDomNode
							.getNodeName()))
					&& (nodeA.getSimilarity(nodeB) >= attsimilarity)) {

				tmpDebug += " identici:no attrib:si";

				// If the fragments are the same
				if (nodeA.hashFragment.equals(nodeB.hashFragment)) {
					tmpDebug += " frammenti:si";
					nodeA.inRel = Relation.UPDATE;
					nodeB.inRel = Relation.UPDATE;
					SF.subPoint(indexA, indexB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					R.addFragment(indexA, indexB, 1, Relation.UPDATE);
					logger.info(tmpDebug);
					propagationToParent(nodeA.posFather, nodeB.posFather);
				}
				// If the fragments are not identical, equal the nodes by moving the appropriate fragments
				else if (forcematch) {
					tmpDebug += " frammenti:no move:";

					// Eguaglio il singolo nodo
					nodeA.inRel = Relation.UPDATE;
					nodeB.inRel = Relation.UPDATE;
					SF.subPoint(indexA, indexB, Field.NO, Field.NO, Field.NO,
							Field.NO);
					R.addFragment(indexA, indexB, 1, Relation.UPDATE);

					// Sistemo i frammenti
					Vector<Fragment> diff = sub(nodeA.fragmentList,
							nodeB.fragmentList);

					for (int i = 0; i < diff.size(); i++) {
						tmpDebug += diff.get(i).getID() + "/";

						fragmentList.remove(diff.get(i));
						A.removeIdFragment(diff.get(i).getNnRootA(),
								diff.get(i));
						B.removeIdFragment(diff.get(i).getNnRootB(),
								diff.get(i));

						R.addFragment(diff.get(i).getA(), diff.get(i).getB(),
								1, Relation.MOVE);

						// Sistemo il campo interno sui vdom, (necessario solo
						// per visualizzazione debug)
						for (int k = diff.get(i).getA().inf; k <= diff.get(i)
								.getA().sup; k++)
							A.getNode(k).inRel = Relation.MOVE;
						for (int k = diff.get(i).getB().inf; k <= diff.get(i)
								.getB().sup; k++)
							B.getNode(k).inRel = Relation.MOVE;

					}
					logger.info(tmpDebug);
					propagationToParent(nodeA.posFather, nodeB.posFather);

				}
			}
		}
	}

	/**
	 * Execute subtraction between fragments lists
	 * 
	 * @param fragA
	 *            Original document fragments lists
	 * @param fragB
	 *            Modified document fragments lists
	 * @return Vector containing non common fragments
	 */
	private Vector<Fragment> sub(HashMap<String, Fragment> fragA,
			HashMap<String, Fragment> fragB) {

		Vector<Fragment> spare = new Vector<Fragment>();

		// Compute the difference and insert the result in spare
		Iterator<String> it;
		String key;

		it = fragA.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			if (!fragB.containsKey(key)) {
				spare.add(fragA.get(key));
			}
		}

		it = fragB.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();
			if (!fragA.containsKey(key)) {
				spare.add(fragB.get(key));
			}
		}
		return spare;
	}

}
