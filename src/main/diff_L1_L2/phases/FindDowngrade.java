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
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.Interval;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import java.util.Vector;
import java.util.HashMap;
import main.diff_L1_L2.phases.common.Match;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import java.util.Collections;
import main.diff_L1_L2.vdom.Vnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.relation.Fragment;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.Cosine;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static main.diff_L1_L2.phases.FindUpgrade.TAGS;
import org.apache.commons.lang.time.StopWatch;
//import org.apache.commons.text.similarity.CosineDistance;
//import org.apache.commons.text.similarity.JaccardDistance;
//import org.apache.commons.text.similarity.JaroWinklerDistance;
//import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * @author Mike Fase che implementa la ricerca degli spostamenti all'interno dei
 * documenti
 */
public class FindDowngrade extends Phase {

    protected static final String[] TAGS = {"sec"};

    /**
     * Costruttore
     *
     * @param SearchField Campi di ricerca rimasti in NxN
     * @param Rel Relazioni che sono state rilevate tra i nodi dei documenti
     * @param Ta Dtree relativo al documento originale
     * @param Tb Dtree relativo al documento modificato
     * @param cfg Nconfig relativo alla configurazione del Diff
     */
    public FindDowngrade(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb,
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
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("START FIND DOWNGRADE");

            downgrade();
            logger.info("END");
            stopWatch.stop();
            logger.error("Downgrade : " + stopWatch.toString());
//            System.exit(0);
        } catch (Exception e) {
            logger.error("ERROR LINE: " + e.getStackTrace()[0].getLineNumber());
            e.printStackTrace();
        }
    }

    protected void downgrade() {

        Field processField;
        List<Interval> listIntervalA = new ArrayList<>();
        List<Interval> listIntervalB = new ArrayList<>();
        SF.StartFieldProcess(Field.NO);
        while ((processField = SF.nextField()) != null) {
            for (int i = processField.yRef.inf; i <= processField.yRef.sup; i++) {

                Dnode toNode = B.getNode(i);
                if (ArrayUtils.indexOf(TAGS, toNode.getRefDomNode().getNodeName()) > -1
                        && ArrayUtils.indexOf(TAGS, B.getNode(B.getNode(i).getPosFather()).getRefDomNode().getNodeName()) > -1) {

//                    for (Dnode fromNode : A.nodeList) {
                    for (int j = processField.xRef.inf; j <= processField.xRef.sup; j++) {
                        Dnode fromNode = A.getNode(j);
                        if (ArrayUtils.indexOf(TAGS, toNode.getRefDomNode().getNodeName()) > -1
                                && ArrayUtils.indexOf(TAGS, A.getNode(A.getNode(j).getPosFather()).getRefDomNode().getNodeName()) == -1
                                && fromNode.getNumChildSubtree() != A.getNode(toNode.getPosFather()).getNumChildSubtree()) {

                            String content = toNode.getRefDomNode().getTextContent();
                            String regExp = "^(\\d+\\.\\d+\\.\\s)(.*)";
                            Pattern p = Pattern.compile(regExp);
                            Matcher m = p.matcher(content);
                            boolean titleSub = m.find();
                            content = m.replaceAll("$2");

                            if (titleSub && toNode.getRefDomNode().getNodeName().equals(fromNode.getRefDomNode().getNodeName())
                                    && toNode.posFather.intValue() > fromNode.posFather.intValue()) {
                                String subSectionAContent = fromNode.refDomNode.getTextContent();
                                String regExpA = "^(\\d+\\.\\s)(.*)";
                                Pattern pA = Pattern.compile(regExpA);
                                Matcher mA = pA.matcher(subSectionAContent);
                                boolean titleBeginWith = mA.find();
                                subSectionAContent = mA.replaceAll("$2");
                                Jaccard jc = new Jaccard();
                                if (titleBeginWith && (content.equalsIgnoreCase(subSectionAContent) || jc.similarity(content, subSectionAContent) > 0.95)) {
                                    Interval fromNodeInterval = new Interval(fromNode.getIndexKey(), fromNode.getIndexKey());
                                    Interval toNodeInterval = new Interval(toNode.getIndexKey(), toNode.getIndexKey());
                                    R.addFragment(fromNodeInterval, toNodeInterval, toNode.weight, Relation.DOWNGRADE);
                                    listIntervalA.add(fromNodeInterval);
                                    listIntervalB.add(toNodeInterval);
                                    for (int l = fromNodeInterval.inf; l <= fromNodeInterval.inf + fromNode.numChildSubtree; l++) {
                                        A.getNode(l).inRel = Relation.DOWNGRADE;

                                    }
                                    for (int l = toNodeInterval.inf; l <= toNodeInterval.inf + toNode.numChildSubtree; l++) {
                                        B.getNode(l).inRel = Relation.DOWNGRADE;
                                    }
//                                SF.subField(fromNodeInterval, toNodeInterval, Field.NO, Field.NO, Field.NO, Field.NO);
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        }
        this.clearIntervals(listIntervalA, listIntervalB);
//        System.exit(0);
    }

    /**
     *
     * @param listIntervalA
     * @param listIntervalB
     */
    protected void clearIntervals(List<Interval> listIntervalA, List<Interval> listIntervalB) {

        Vector<Interval> cod = SF.getIntervalsOnY();
        for (int c = 0; c < cod.size(); c++) {
            Interval interval = cod.get(c);
            if (listIntervalB.contains(interval)) {
                SF.subField(interval, interval, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
            }
        }
        Vector<Interval> dom = SF.getIntervalsOnX();
        for (int d = 0; d < dom.size(); d++) {
            Interval interval = dom.get(d);
            if (listIntervalA.contains(interval)) {
                SF.subField(interval, interval, Field.NO, Field.LOCALITY, Field.LOCALITY, Field.NO);
            }
        }
    }
}
