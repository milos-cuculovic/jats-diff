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
package main.diff_L1_L2.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.relation.Fragment;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;
import java.util.Vector;

/**
 * @author schirinz Deriva dalle informazioni rilevate nell fasi precedenti, un
 * set di operazioni che rappresentano i cambiamenti da
 * effetuare sul documento originale per ottenere il documento modificato
 */
public class DeltaDerive extends Phase {

    METAdelta Ndelta = new METAdelta();

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public DeltaDerive(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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

            logger.info("START");

            // Calculate delete operations
            Vector<Interval> dom = SF.getIntervalsOnX();
            Interval toProcess;
            for (int i = 0; i < dom.size(); i++) {
                toProcess = dom.get(i);

                for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                    if (k + A.getNode(k).getNumChildSubtree() <= toProcess.sup) {
                        if (A.getNode(k).inRel != Relation.MERGE
                                && A.getNode(k).inRel != Relation.SPLIT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MERGE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.SPLIT
                                && A.getNode(k).inRel != Relation.UPGRADE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPGRADE
                                && A.getNode(k).inRel != Relation.DOWNGRADE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DOWNGRADE
                                && A.getNode(k).inRel != Relation.MOVETEXT_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MOVETEXT_TO
                                && A.getNode(k).inRel != Relation.MOVETEXT_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MOVETEXT_FROM
                                && A.getNode(k).inRel != Relation.INSERT_STYLE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.INSERT_STYLE
                                && A.getNode(k).inRel != Relation.DELETE_STYLE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DELETE_STYLE
                                && A.getNode(k).inRel != Relation.UPDATE_STYLE_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_TO
                                && A.getNode(k).inRel != Relation.UPDATE_STYLE_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_FROM
                                && A.getNode(k).inRel != Relation.INSERT_TEXT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.INSERT_TEXT
                                && A.getNode(k).inRel != Relation.DELETE_TEXT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DELETE_TEXT
                                && A.getNode(k).inRel != Relation.UPDATE_TEXT_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_TO
                                && A.getNode(k).inRel != Relation.UPDATE_TEXT_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_FROM) {
                            Ndelta.addDeleteTreeOperation(A.getNode(k));
                        }

                        k += A.getNode(k).getNumChildSubtree();
                    } else {
                        if (A.getNode(k).inRel != Relation.MERGE
                                && A.getNode(k).inRel != Relation.SPLIT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MERGE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.SPLIT
                                && A.getNode(k).inRel != Relation.UPGRADE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPGRADE
                                && A.getNode(k).inRel != Relation.DOWNGRADE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DOWNGRADE
                                && A.getNode(k).inRel != Relation.MOVETEXT_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MOVETEXT_TO
                                && A.getNode(k).inRel != Relation.MOVETEXT_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.MOVETEXT_FROM
                                && A.getNode(k).inRel != Relation.INSERT_STYLE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.INSERT_STYLE
                                && A.getNode(k).inRel != Relation.DELETE_STYLE
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DELETE_STYLE
                                && A.getNode(k).inRel != Relation.UPDATE_STYLE_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_TO
                                && A.getNode(k).inRel != Relation.UPDATE_STYLE_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_FROM
                                && A.getNode(k).inRel != Relation.INSERT_TEXT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.INSERT_TEXT
                                && A.getNode(k).inRel != Relation.DELETE_TEXT
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.DELETE_TEXT
                                && A.getNode(k).inRel != Relation.UPDATE_TEXT_TO
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_TO
                                && A.getNode(k).inRel != Relation.UPDATE_TEXT_FROM
                                && A.getNode(A.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_FROM) {

                            Ndelta.addDeleteNodeOperation(A.getNode(k));
                        }
                    }
                }
            }

            // Calculate insert operations
            Vector<Interval> cod = SF.getIntervalsOnY();
            for (int i = 0; i < cod.size(); i++) {
                toProcess = cod.get(i);

                for (int k = toProcess.inf; k <= toProcess.sup; k++) {
                    if (k + B.getNode(k).getNumChildSubtree() <= toProcess.sup) {
                        if (B.getNode(k).inRel != Relation.MERGE
                                && B.getNode(k).inRel != Relation.SPLIT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.MERGE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.SPLIT
                                && B.getNode(k).inRel != Relation.UPGRADE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPGRADE
                                && B.getNode(k).inRel != Relation.DOWNGRADE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DOWNGRADE
                                && B.getNode(k).inRel != Relation.MOVETEXT_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.MOVETEXT_TO
                                && B.getNode(k).inRel != Relation.INSERT_STYLE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.INSERT_STYLE
                                && B.getNode(k).inRel != Relation.DELETE_STYLE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DELETE_STYLE
                                && B.getNode(k).inRel != Relation.UPDATE_STYLE_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_TO
                                && B.getNode(k).inRel != Relation.UPDATE_STYLE_FROM
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_FROM
                                && B.getNode(k).inRel != Relation.INSERT_TEXT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.INSERT_TEXT
                                && B.getNode(k).inRel != Relation.DELETE_TEXT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DELETE_TEXT
                                && B.getNode(k).inRel != Relation.UPDATE_TEXT_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_TO
                                && B.getNode(k).inRel != Relation.UPDATE_TEXT_FROM
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_FROM) {
                            Ndelta.addInsertTreeOperation(B.getNode(k));
                        }

                        k += B.getNode(k).getNumChildSubtree();
                    } else {
                        if (B.getNode(k).inRel != Relation.MERGE
                                && B.getNode(k).inRel != Relation.SPLIT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.MERGE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.SPLIT
                                && B.getNode(k).inRel != Relation.UPGRADE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPGRADE
                                && B.getNode(k).inRel != Relation.DOWNGRADE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DOWNGRADE
                                && B.getNode(k).inRel != Relation.MOVETEXT_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.MOVETEXT_TO
                                && B.getNode(k).inRel != Relation.MOVETEXT_FROM
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.MOVETEXT_FROM
                                && B.getNode(k).inRel != Relation.INSERT_STYLE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.INSERT_STYLE
                                && B.getNode(k).inRel != Relation.DELETE_STYLE
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DELETE_STYLE
                                && B.getNode(k).inRel != Relation.UPDATE_STYLE_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_TO
                                && B.getNode(k).inRel != Relation.UPDATE_STYLE_FROM
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_STYLE_FROM
                                && B.getNode(k).inRel != Relation.INSERT_TEXT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.INSERT_TEXT
                                && B.getNode(k).inRel != Relation.DELETE_TEXT
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.DELETE_TEXT
                                && B.getNode(k).inRel != Relation.UPDATE_TEXT_TO
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_TO
                                && B.getNode(k).inRel != Relation.UPDATE_TEXT_FROM
                                && B.getNode(B.getNode(k).posFather).inRel != Relation.UPDATE_TEXT_FROM) {
                            Ndelta.addInsertNodeOperation(B.getNode(k)
                            );
                        }
                    }
                }
            }

            // CALCULATE MERGE
            Vector<Fragment> tmpFragMerge;

            tmpFragMerge = R.getFragments(Relation.MERGE);

            if (tmpFragMerge != null) {
                //merge to
                for (int i = 0; i < tmpFragMerge.size(); i++) {

                    for (int k = tmpFragMerge.get(i).getB().inf; k <= tmpFragMerge.get(i).getB().inf; k++) {
//                        if (B.getNode(B.getNode(k).posFather).inRel == Relation.MERGE) {
                        Ndelta.addMergeToOperation(B.getNode(k), tmpFragMerge.get(i).getID());
//                        }
                    }

                }

                //merge from
                for (int i = 0; i < tmpFragMerge.size(); i++) {

                    for (int k = tmpFragMerge.get(i).getA().inf; k <= tmpFragMerge.get(i).getA().sup; k++) {
//                        if (A.getNode(A.getNode(k).posFather).inRel == Relation.MERGE) {
                        String hash = tmpFragMerge.get(i).getID();
                        logger.info(hash);
                        Ndelta.addMergeFromOperation(A.getNode(k), tmpFragMerge.get(i).getID());
//                        }
                    }
                }
            }

            // CALCULATE SPLIT
            Vector<Fragment> tmpFragSplit;

            tmpFragSplit = R.getFragments(Relation.SPLIT);

            if (tmpFragSplit != null) {
                //split to
                for (int i = 0; i < tmpFragSplit.size(); i++) {

                    for (int k = tmpFragSplit.get(i).getB().inf; k <= tmpFragSplit.get(i).getB().inf; k++) {
//                        if (B.getNode(B.getNode(k).posFather).inRel == Relation.SPLIT) {
                        Ndelta.addSplitToOperation(B.getNode(k), tmpFragSplit.get(i).getID());
//                        }
                    }

                }

                //split from
                for (int i = 0; i < tmpFragSplit.size(); i++) {

                    for (int k = tmpFragSplit.get(i).getA().inf; k <= tmpFragSplit.get(i).getA().sup; k++) {
//                        if (A.getNode(A.getNode(k).posFather).inRel == Relation.SPLIT) {
                        Ndelta.addSplitFromOperation(A.getNode(k), tmpFragSplit.get(i).getID());//
//                        }
                    }
                }
//

            }

            // CALCULATE Upgrade
            Vector<Fragment> tmpFragUpgrade;

            tmpFragUpgrade = R.getFragments(Relation.UPGRADE);

            if (tmpFragUpgrade != null) {
                //upgrade to
                for (int i = 0; i < tmpFragUpgrade.size(); i++) {

                    for (int k = tmpFragUpgrade.get(i).getB().inf; k <= tmpFragUpgrade.get(i).getB().inf; k++) {
//                        if (B.getNode(B.getNode(k).posFather).inRel == Relation.MERGE) {
                        Ndelta.addUpgradeToOperation(B.getNode(k));
//                        }
                    }

                }

                //upgrade from
                for (int i = 0; i < tmpFragUpgrade.size(); i++) {

                    for (int k = tmpFragUpgrade.get(i).getA().inf; k <= tmpFragUpgrade.get(i).getA().sup; k++) {
//                        if (A.getNode(A.getNode(k).posFather).inRel == Relation.MERGE) {
                        Ndelta.addUpgradeFromOperation(A.getNode(k));
//                        }
                    }
                }
            }

            // CALCULATE Downgrade
            Vector<Fragment> tmpFragDowngrade;

            tmpFragDowngrade = R.getFragments(Relation.DOWNGRADE);

            if (tmpFragDowngrade != null) {
                //downgrade to
                for (int i = 0; i < tmpFragDowngrade.size(); i++) {

                    for (int k = tmpFragDowngrade.get(i).getB().inf; k <= tmpFragDowngrade.get(i).getB().inf; k++) {
//                        if (B.getNode(B.getNode(k).posFather).inRel == Relation.MERGE) {
                        Ndelta.addDowngradeToOperation(B.getNode(k));
//                        }
                    }

                }

                //downgrade from
                for (int i = 0; i < tmpFragDowngrade.size(); i++) {

                    for (int k = tmpFragDowngrade.get(i).getA().inf; k <= tmpFragDowngrade.get(i).getA().sup; k++) {
//                        if (A.getNode(A.getNode(k).posFather).inRel == Relation.MERGE) {
                        Ndelta.addDowngradeFromOperation(A.getNode(k));
//                        }
                    }
                }
            }

            //STYLE PART
            //STYLE TAG INSERT
            Vector<Fragment> tmpFragStyleInsert;
            tmpFragStyleInsert = R.getFragments(Relation.INSERT_STYLE);
            if (tmpFragStyleInsert != null) {
                //style to
                for (int i = 0; i < tmpFragStyleInsert.size(); i++) {
                    for (int k = tmpFragStyleInsert.get(i).getB().inf; k <= tmpFragStyleInsert.get(i).getB().inf; k++) {
                        Ndelta.addStyleInsertOperation(B.getNode(k));
                    }
                }
            }

            //STYLE TAG DELETE
            Vector<Fragment> tmpFragStyleDelete;
            tmpFragStyleDelete = R.getFragments(Relation.DELETE_STYLE);
            if (tmpFragStyleDelete != null) {
                for (int i = 0; i < tmpFragStyleDelete.size(); i++) {
                    for (int k = tmpFragStyleDelete.get(i).getA().inf; k <= tmpFragStyleDelete.get(i).getA().inf; k++) {
                        Ndelta.addStyleDeleteOperation(A.getNode(k));
                    }
                }
            }

            //STYLE TAG UPDATE
            Vector<Fragment> tmpFragStyleFromUpdate;
            tmpFragStyleFromUpdate = R.getFragments(Relation.UPDATE_STYLE_FROM);

            if (tmpFragStyleFromUpdate != null) {
                //style from
                for (int i = 0; i < tmpFragStyleFromUpdate.size(); i++) {
                    for (int k = tmpFragStyleFromUpdate.get(i).getA().inf; k <= tmpFragStyleFromUpdate.get(i).getA().inf; k++) {
                        Ndelta.addStyleUpdateFromOperation(A.getNode(k));
                    }
                }

            }

            Vector<Fragment> tmpFragStyleFromToUpdate;
            tmpFragStyleFromToUpdate = R.getFragments(Relation.UPDATE_STYLE_TO);

            if (tmpFragStyleFromToUpdate != null) {
                //style from
                for (int i = 0; i < tmpFragStyleFromToUpdate.size(); i++) {
                    for (int k = tmpFragStyleFromToUpdate.get(i).getB().inf; k <= tmpFragStyleFromToUpdate.get(i).getB().inf; k++) {
                        Ndelta.addStyleUpdateToOperation(B.getNode(k));
                    }
                }

            }

            //INSERT STYLE RAW TEXT
            Vector<Fragment> tmpFragInsertStyleText;
            tmpFragInsertStyleText = R.getFragments(Relation.INSERT_TEXT);
            if (tmpFragInsertStyleText != null) {
                for (int i = 0; i < tmpFragInsertStyleText.size(); i++) {
                    for (int k = tmpFragInsertStyleText.get(i).getB().inf; k <= tmpFragInsertStyleText.get(i).getB().inf; k++) {
                        Ndelta.addInsertStyleTextOperation(B.getNode(k));
                    }
                }
            }

            //DELETE STYLE RAW TEXT
            Vector<Fragment> tmpFragDeleteStyleText;
            tmpFragDeleteStyleText = R.getFragments(Relation.DELETE_TEXT);

            if (tmpFragDeleteStyleText != null) {
                for (int i = 0; i < tmpFragDeleteStyleText.size(); i++) {
                    for (int k = tmpFragDeleteStyleText.get(i).getA().inf; k <= tmpFragDeleteStyleText.get(i).getA().inf; k++) {
                        Ndelta.addDeleteStyleTextOperation(A.getNode(k));
                    }
                }
            }

            //UPDATE RAW TEXT IF HAVE STYLE OR MOVE
            Vector<Fragment> tmpFragUpdateStyleFromText;
            tmpFragUpdateStyleFromText = R.getFragments(Relation.UPDATE_TEXT_FROM);
            if (tmpFragUpdateStyleFromText != null) {
                for (int i = 0; i < tmpFragUpdateStyleFromText.size(); i++) {
                    for (int k = tmpFragUpdateStyleFromText.get(i).getA().inf; k <= tmpFragUpdateStyleFromText.get(i).getA().inf; k++) {
                        Ndelta.addUpdateStyleFromTextOperation(A.getNode(k));
                    }
                }

            }

            Vector<Fragment> tmpFragUpdateStyleToText;
            tmpFragUpdateStyleToText = R.getFragments(Relation.UPDATE_TEXT_TO);
            if (tmpFragUpdateStyleToText != null) {
                for (int i = 0; i < tmpFragUpdateStyleToText.size(); i++) {
                    for (int k = tmpFragUpdateStyleToText.get(i).getB().inf; k <= tmpFragUpdateStyleToText.get(i).getB().inf; k++) {
                        Ndelta.addUpdateStyleToTextOperation(B.getNode(k));
                    }
                }

            }

            // CALCULATE MOVETEXT
            Vector<Fragment> tmpFragMoveToText;
            tmpFragMoveToText = R.getFragments(Relation.MOVETEXT_TO);
            if (tmpFragMoveToText != null) {
                //MOVE TEXT TO
                for (int i = 0; i < tmpFragMoveToText.size(); i++) {
                    for (int k = tmpFragMoveToText.get(i).getB().inf; k <= tmpFragMoveToText.get(i).getB().inf; k++) {
                        Ndelta.addMoveTextToOperation(A.getNode(k));
                    }
                }
            }

            Vector<Fragment> tmpFragMoveFromText;
            tmpFragMoveFromText = R.getFragments(Relation.MOVETEXT_FROM);
            if (tmpFragMoveFromText != null) {

                //MOVE TEXT FROM
                for (int i = 0; i < tmpFragMoveFromText.size(); i++) {
                    for (int k = tmpFragMoveFromText.get(i).getA().inf; k <= tmpFragMoveFromText.get(i).getA().inf; k++) {
                        Ndelta.addMoveTextFromOperation(A.getNode(k));
                    }
                }
            }

            Vector<Fragment> tmpFrag;
            // Calculate move operations
            tmpFrag = R.getFragments(Relation.MOVE);
            if (tmpFrag != null) {
                for (int i = 0; i < tmpFrag.size(); i++) {
                    int indexA = tmpFrag.get(i).getNnRootA();
                    int indexB = tmpFrag.get(i).getNnRootB();
                    if ((A.getNode(indexA).inRel != Relation.MERGE
                            && A.getNode(indexA).inRel != Relation.SPLIT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.MERGE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.SPLIT
                            && A.getNode(indexA).inRel != Relation.UPGRADE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPGRADE
                            && A.getNode(indexA).inRel != Relation.DOWNGRADE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DOWNGRADE
                            && A.getNode(indexA).inRel != Relation.MOVETEXT_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.MOVETEXT_TO
                            && A.getNode(indexA).inRel != Relation.MOVETEXT_FROM
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.MOVETEXT_FROM
                            && A.getNode(indexA).inRel != Relation.INSERT_STYLE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.INSERT_STYLE
                            && A.getNode(indexA).inRel != Relation.DELETE_STYLE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DELETE_STYLE
                            && A.getNode(indexA).inRel != Relation.UPDATE_STYLE_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_STYLE_TO
                            && A.getNode(indexA).inRel != Relation.UPDATE_STYLE_FROM
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_STYLE_FROM
                            && A.getNode(indexA).inRel != Relation.INSERT_TEXT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.INSERT_TEXT
                            && A.getNode(indexA).inRel != Relation.DELETE_TEXT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DELETE_TEXT
                            && A.getNode(indexA).inRel != Relation.UPDATE_TEXT_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_TEXT_TO
                            && A.getNode(indexA).inRel != Relation.UPDATE_TEXT_FROM
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_TEXT_FROM)
                            || (B.getNode(indexB).inRel != Relation.MERGE
                            && B.getNode(indexB).inRel != Relation.SPLIT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.MERGE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.SPLIT
                            && B.getNode(indexB).inRel != Relation.UPGRADE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPGRADE)
                            && B.getNode(indexB).inRel != Relation.DOWNGRADE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DOWNGRADE
                            && B.getNode(indexB).inRel != Relation.MOVETEXT_FROM
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.MOVETEXT_FROM
                            && B.getNode(indexB).inRel != Relation.MOVETEXT_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.MOVETEXT_TO
                            && B.getNode(indexB).inRel != Relation.INSERT_STYLE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.INSERT_STYLE
                            && B.getNode(indexB).inRel != Relation.DELETE_STYLE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DELETE_STYLE
                            && B.getNode(indexB).inRel != Relation.UPDATE_STYLE_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_STYLE_TO
                            && B.getNode(indexB).inRel != Relation.UPDATE_STYLE_FROM
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_STYLE_FROM
                            && B.getNode(indexB).inRel != Relation.INSERT_TEXT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.INSERT_TEXT
                            && B.getNode(indexB).inRel != Relation.DELETE_TEXT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DELETE_TEXT
                            && B.getNode(indexB).inRel != Relation.UPDATE_TEXT_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_TEXT_TO
                            && B.getNode(indexB).inRel != Relation.UPDATE_TEXT_FROM
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_TEXT_FROM) {

                        Ndelta.addMoveOperation(
                                A.getNode(tmpFrag.get(i).getNnRootA()),
                                B.getNode(tmpFrag.get(i).getNnRootB()));
                    }
                }

            }
            // Calculate update operations
            tmpFrag = R.getFragments(Relation.UPDATE);
            if (tmpFrag != null) {
                for (int i = 0; i < tmpFrag.size(); i++) {
                    int indexA = tmpFrag.get(i).getNnRootA();
                    int indexB = tmpFrag.get(i).getNnRootB();

                    if ((A.getNode(indexA).inRel != Relation.MERGE
                            && A.getNode(indexA).inRel != Relation.SPLIT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.MERGE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.SPLIT
                            && A.getNode(indexA).inRel != Relation.UPGRADE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPGRADE
                            && A.getNode(indexA).inRel != Relation.DOWNGRADE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DOWNGRADE
                            && A.getNode(indexA).inRel != Relation.MOVETEXT_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.MOVETEXT_TO
                            && A.getNode(indexA).inRel != Relation.INSERT_STYLE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.INSERT_STYLE
                            && A.getNode(indexA).inRel != Relation.DELETE_STYLE
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DELETE_STYLE
                            && A.getNode(indexA).inRel != Relation.UPDATE_STYLE_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_STYLE_TO
                            && A.getNode(indexA).inRel != Relation.INSERT_TEXT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.INSERT_TEXT
                            && A.getNode(indexA).inRel != Relation.DELETE_TEXT
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.DELETE_TEXT
                            && A.getNode(indexA).inRel != Relation.UPDATE_TEXT_TO
                            && A.getNode(A.getNode(indexA).posFather).inRel != Relation.UPDATE_TEXT_TO)
                            || (B.getNode(indexB).inRel != Relation.MERGE
                            && B.getNode(indexB).inRel != Relation.SPLIT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.MERGE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.SPLIT
                            && B.getNode(indexB).inRel != Relation.UPGRADE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPGRADE
                            && B.getNode(indexB).inRel != Relation.DOWNGRADE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DOWNGRADE
                            && B.getNode(indexB).inRel != Relation.MOVETEXT_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.MOVETEXT_TO
                            && B.getNode(indexB).inRel != Relation.INSERT_STYLE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.INSERT_STYLE
                            && B.getNode(indexB).inRel != Relation.DELETE_STYLE
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DELETE_STYLE
                            && B.getNode(indexB).inRel != Relation.UPDATE_STYLE_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_STYLE_TO
                            && B.getNode(indexB).inRel != Relation.INSERT_TEXT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.INSERT_TEXT
                            && B.getNode(indexB).inRel != Relation.DELETE_TEXT
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.DELETE_TEXT
                            && B.getNode(indexB).inRel != Relation.UPDATE_TEXT_TO
                            && B.getNode(B.getNode(indexB).posFather).inRel != Relation.UPDATE_TEXT_TO)) {

                        Ndelta.merge((A.getNode(tmpFrag.get(i).getNnRootA()))
                                .getDeltaLikeness(tmpFrag.get(i).getNnRootB()));
                    }
                }
            }

            logger.info("END");
        } catch (Exception e) {
            throw new ComputePhaseException("DeltaDerive");
        }
    }

    /**
     * Calcola il METAdelta e lo restituisce
     *
     * @return METAdelta calcolato
     * @throws ComputePhaseException Solleva l'eccezione nel caso in cui si
     * hanno problemi durante la trasformazione
     */
    public METAdelta derive() throws ComputePhaseException {
        compute();
        return Ndelta;
    }
}
