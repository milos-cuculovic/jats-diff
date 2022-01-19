package main.diff_L1_L2.phases;

import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import main.diff_L1_L2.vdom.diffing.Dtree;

public class PhaseBuilder {

    protected String depth_rel_AtoB, node_rel;

    public static void build(NxN SearchField, Relation Rel, Dtree Ta, Dtree Tb, Nconfig config,
                     String depth_relation_AtoB, String node_relation) throws ComputePhaseException {
        try {
            System.out.println("Start phase build");
            new PhaseCalculator(SearchField, Rel, Ta, Tb, config, depth_relation_AtoB, node_relation).compute();
            //new Propagation(SearchField, Rel, Ta, Tb, config).compute();

        } catch (Exception e) {
            throw new ComputePhaseException("PhaseBuilder");
        }
    }
}
