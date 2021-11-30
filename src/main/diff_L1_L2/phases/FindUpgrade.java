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

import info.debatty.java.stringsimilarity.Jaccard;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindUpgrade extends Phase {

    Integer range;
    Integer minweight;

    List<Interval> listIntervalA = new ArrayList<>();
    List<Interval> listIntervalB = new ArrayList<>();

    /**
     * Constructor
     *
     * @param SearchField
     *            Research fields left in NxN
     * @param Rel
     *            Relationships detected between document nodes
     * @param Ta
     *            Dtree related to the original document
     * @param Tb
     *            Dtree related to the modified document
     * @param cfg
     *            Nconfig related to the Diff configuration
     */
    public FindUpgrade(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            Field processField;
            SF.StartFieldProcess(Field.NO);
            while ((processField = SF.nextField()) != null) {

                logger.info("FindUpgrade(Call)   \t X:" + processField.xRef.show()
                        + " \t Y:" + processField.yRef.show());

                findA.set(1, 0);
                findB.set(1, 0);

                FindMaxUpgrade(processField.xRef, processField.yRef, findA, findB);

                logger.info("FindUpgrade(Return)\t X:" + findA.show() + "\t Y:"
                        + findB.show());

                if (findA.size() > 0) {
                    // Remove nodes from the search space
                    SF.subField(findA, findB, Field.NO, Field.NO, Field.NO,
                            Field.NO);
                    // Insert nodes in the Relation structure
                    R.addFragment(findA, findB, A.getNode(findA.inf).weight,
                            Relation.UPGRADE);

                    // Change membership of nodes
                    for (int i = findA.inf; i <= findA.sup; i++)
                        A.getNode(i).inRel = Relation.UPGRADE;
                    for (int i = findB.inf; i <= findB.sup; i++)
                        B.getNode(i).inRel = Relation.UPGRADE;
                }
            }

            logger.info("END");
        } catch (Exception e) {
            throw new ComputePhaseException("FindUpgrade");
        }
    }

    /**
     * Cerca eventuali sottoalberi spostati nello spazio definito da intA e intB
     *
     * @param intA
     *            Intervallo di ricerca sul documento Originale
     * @param intB
     *            Intervallo di ricerca sul documento Modificato
     * @param findA
     *            Match trovato sul documento originale
     * @param findB
     *            Match trovato sul documento modificato
     */
    private void FindMaxUpgrade(Interval intA, Interval intB, Interval findA,
                             Interval findB) {

        for (int i = intA.inf; i <= intA.sup; i++) {

            Dnode fromNode = A.getNode(i);
            for (int j = intB.inf; j <= intB.sup; j++) {
                Dnode toNode = B.getNode(j);

                String content = toNode.getRefDomNode().getTextContent();
                String regExp = "^(\\d+\\.\\s)(.*)";
                Pattern p = Pattern.compile(regExp);
                Matcher m = p.matcher(content);
                content = m.replaceAll("$2");

                if (A.getNode(i).getNumChildSubtree() > findA.size()) {
                    String subSectionAContent = fromNode.refDomNode.getTextContent();
                    String regExpA = "^(\\d+\\.\\d+\\.\\s)(.*)";
                    Pattern pA = Pattern.compile(regExpA);
                    Matcher mA = pA.matcher(subSectionAContent);
                    subSectionAContent = mA.replaceAll("$2");

                    // Similarity 95%
                    Jaccard jc = new Jaccard();
                    if (content.equalsIgnoreCase(subSectionAContent) || jc.similarity(content, subSectionAContent) > 0.95) {
                        var toNodeDepth = 0;
                        var toNodeDepthChecker = B.getNode(j).getRefDomNode();;
                        while (toNodeDepthChecker.getParentNode() != null) {
                            toNodeDepth++;
                            toNodeDepthChecker = toNodeDepthChecker.getParentNode();
                        }
                        var fromNodeDepth = 0;
                        var fromNodeDepthChecker = A.getNode(i).getRefDomNode();
                        while (fromNodeDepthChecker.getParentNode() != null) {
                            fromNodeDepth++;
                            fromNodeDepthChecker = fromNodeDepthChecker.getParentNode();
                        }

                        if(toNodeDepth < fromNodeDepth) {
                            findA.set(i, i + A.getNode(i).getNumChildSubtree());
                            findB.set(j, j + B.getNode(j).getNumChildSubtree());
                            listIntervalA.add(findA);
                            listIntervalB.add(findB);
                            break;
                        }
                    }
                }
            }
        }
    }
}
