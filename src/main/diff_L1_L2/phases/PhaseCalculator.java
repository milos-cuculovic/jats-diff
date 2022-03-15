package main.diff_L1_L2.phases;

import info.debatty.java.stringsimilarity.Jaccard;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhaseCalculator extends Phase {

    protected String depth_rel_AtoB;
    protected String content_rel_AtoB;
    protected Boolean depth_validated, content_sim_validated = false;

    /**
     * SVariables used by the super class
     *
     * @param SearchField
     * @param Rel
     * @param Ta          - Dtree A
     * @param Tb          - Dtree B
     * @param config
     * @param depth_relation_AtoB - Relation between A and B: = same depth; > higher depth; < lower depth
     * @param content_relation_AtoB  - Relation between node contents: = for same content, %xx for similarity,
     *                            ⊂ for is included in
     */
    public PhaseCalculator(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb, Nconfig config,
                           String depth_relation_AtoB, String content_relation_AtoB) {
        super(SearchField, Rel, Ta, Tb, config);
        depth_rel_AtoB = depth_relation_AtoB;
        content_rel_AtoB = content_relation_AtoB;

    }

    /*
     * @see ndiff.phases.Phase#compute()
     */
    @Override
    public void compute() throws ComputePhaseException {
        try {
            System.out.println("Start phase calculate");

            Interval findA = new Interval(1, 0);
            Interval findB = new Interval(1, 0);

            Field processField;
            SF.StartFieldProcess(Field.NO);
            while ((processField = SF.nextField()) != null) {
                findA.set(1, 0);
                findB.set(1, 0);

                Interval intA = processField.xRef;
                Interval intB = processField.yRef;

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

                            if (content_sim == 100) {
                                if (content.equalsIgnoreCase(subSectionAContent)) {
                                    content_sim_validated = true;
                                }
                            }
                            else {
                                Jaccard jc = new Jaccard();
                                if (jc.similarity(content, subSectionAContent) > 0.95) {
                                    content_sim_validated = true;
                                }
                            }

                            if(toNodeDepth < fromNodeDepth) {
                                findA.set(i, i + A.getNode(i).getNumChildSubtree());
                                findB.set(j, j + B.getNode(j).getNumChildSubtree());
                                break;
                            }
                        }
                    }
                }





                switch (depth_rel_AtoB) {
                    case "<":
                        FindMaxUpgrade(processField.xRef, processField.yRef, findA, findB);

                        if (findA.size() > 0) {
                            // Remove nodes from the search space
                            SF.subField(findA, findB, Field.NO, Field.NO, Field.NO, Field.NO);
                            // Insert nodes in the Relation structure
                            R.addFragment(findA, findB, A.getNode(findA.inf).weight, Relation.UPGRADE);

                            // Change membership of nodes
                            for (int i = findA.inf; i <= findA.sup; i++)
                                A.getNode(i).inRel = Relation.UPGRADE;
                            for (int i = findB.inf; i <= findB.sup; i++)
                                B.getNode(i).inRel = Relation.UPGRADE;
                        }
                break;

                case ">":
                    System.out.println(depth_rel_AtoB);
                    break;

                case "=":
                    System.out.println(depth_rel_AtoB);
                    break;

                case "na":
                    System.out.println(depth_rel_AtoB);
                    break;
                }
            }

        } catch (Exception e) {
            throw new ComputePhaseException("PhaseBuilder");
        }
    }


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
                            break;
                        }
                    }
                }
            }
        }
    }
}
