package main.diff_L1_L2.test.phases;

import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.core.alternatives.ThreadedBuildDtree;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.exceptions.InputFileException;

public class TestThreadedPerformance {
	private final static String fileA = "resources/testset_xml/various_xml/docj_1.xml";
	private final static String fileB = "resources/testset_xml/various_xml/docj_2.xml";

	public static void main(String[] args) throws InputFileException,
			ComputePhaseException {

		Nconfig config = new Nconfig();
		boolean ltrim = config.getBoolPhaseParam(Nconfig.Normalize, "ltrim");
		boolean rtrim = config.getBoolPhaseParam(Nconfig.Normalize, "rtrim");
		boolean collapse = config.getBoolPhaseParam(Nconfig.Normalize,
				"collapse");
		boolean emptynode = config.getBoolPhaseParam(Nconfig.Normalize,
				"emptynode");
		boolean commentNode = config.getBoolPhaseParam(Nconfig.Normalize,
				"commentnode");

		{

			new Dtree(fileA, ltrim, rtrim, collapse, emptynode, commentNode);

			new Dtree(fileB, ltrim, rtrim, collapse, emptynode, commentNode);

		}

		{
			(new ThreadedBuildDtree()).getDTree(fileA, fileB, ltrim, rtrim,
					collapse, emptynode, commentNode);

		}

	}

}
