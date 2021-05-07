package main.diff_L1_L2.test.phases;

import de.mcs.jmeasurement.InvalidMeasureDataTypeException;
import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.MeasurePoint;
import de.mcs.jmeasurement.Monitor;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.core.Ndiff;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.phases.*;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import main.diff_L1_L2.test.exceptions.XMLreadException;
import main.diff_L1_L2.test.utils.JNdiffXmlUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.PrintStream;

/**
 * 
 * Genera un report sulle fasi di jndiff Per ora è solo csv ma implementando la
 * classe MeasureDataRenderer si può implementare qualsiasi tipo di output (ved.
 * documentazione di Jmeasurement)
 * 
 * @author Cesare Jacopo Corzani
 * 
 */
public class NdiffTest extends Ndiff {

	// private static final String OUTPUT_FILE = "outputPhases.xml";

	public static void main(String[] args) {

		if (args.length == 1 || args.length == 2) {

			String originalFile = args[0];

			if ((new File(originalFile)).exists()) {

				Nconfig config = new Nconfig((args.length == 2 && new File(
						args[1]).exists()) ? (args[1]) : null);

				try {
					recursiveGetInfo("_1.xml", "_2.xml", new File(args[0]),
							System.out, config, true);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					System.exit(1);
				}

			}

		} else {
			System.err
					.println("I parametri devono essere al massimo 2: <XML_DIR>  [File di configurazione]");
			System.exit(1);

		}

	}

	/**
	 * 
	 * Genera un output sui tempi delle fasi
	 * 
	 * @param URIdocA
	 * @param URIdocB
	 * @param config
	 * @param output
	 * @param printHeader
	 * @throws XMLreadException
	 */
	public static void getInfo(String URIdocA, String URIdocB, Nconfig config,
			PrintStream output, boolean printHeader) throws XMLreadException {

		Logger logger = Logger.getLogger(NdiffTest.class);
		MeasureFactory.setApplicationName("testPhases");
		MeasurePoint[] measures;
		long size = 0, totalExecTime = 0;

		long nodeCount;

		try {
			logger.debug("Calcolo delle fasi");

			NdiffTest ndiff = new NdiffTest();

			ndiff.mainPhases(URIdocA, URIdocB, config);
			measures = MeasureFactory.getMeasurePoints(null);

			logger.debug("Calcolo del numero dei nodi");
			nodeCount = JNdiffXmlUtils.nodeCount(URIdocA);
			size = new File(URIdocA).length();

		} catch (Exception e) {
			throw new XMLreadException(e);
		}

		if (printHeader) {

			printHeader(output, measures);
		}

		output.print(new File(URIdocA.replaceAll("_1.xml", "")).getName() + ","); // nome
																					// del
																					// file
																					// senza
																					// la
																					// parte
																					// finale
		output.print(size + ",");
		output.print(nodeCount + ",");

		try {
			for (int i = 0; i < measures.length; i++) {

				totalExecTime += measures[i].getData("totalMSec").getAsLong();

			}

			output.print(totalExecTime);

			for (int i = 0; i < measures.length; i++) {

				output.print("," + measures[i].getData("totalMSec").getAsLong());

			}

		} catch (InvalidMeasureDataTypeException e) {
			output.print("," + "ERR");
		}

		output.println();

	}

	/**
	 * @param output
	 * @param measures
	 */
	private static void printHeader(PrintStream output, MeasurePoint[] measures) {
		output.print("file_name,size,num_nodes(input),total_exec_time");
		for (int i = 0; i < measures.length; i++) {

			output.print("," + measures[i].getName());

		}

		output.println();
	}

	public static void recursiveGetInfo(String originalSuffix,
			String modifiedSuffix, File sourcePath, PrintStream output,
			Nconfig config, boolean printHeader) {
		File[] files = sourcePath.listFiles();

		boolean tempHeader = printHeader;

		for (File file : files) {

			if (!((file.isFile() && file.getName().endsWith(originalSuffix)) || file
					.isDirectory()))
				continue;

			if (file.isDirectory()) {

				recursiveGetInfo(originalSuffix, modifiedSuffix, file, output,
						config, tempHeader);

			}

			else {

				String modifiedFile = file.getAbsolutePath().replaceAll(
						"originalSuffix", modifiedSuffix);

				if (new File(modifiedFile).exists()) {

					try {

						getInfo(file.getAbsolutePath(), modifiedFile, config,
								output, tempHeader);

						if (tempHeader)
							tempHeader = false;

					} catch (Exception e) {
						System.err
								.println("Errore durante l'elaborazione del file "
										+ file + ", il suddetto verrà ignorato");
						e.printStackTrace(System.err);
					}

				} else {

					System.err
							.println("Il file "
									+ modifiedFile
									+ " non esiste, il calcolo delle fasi verrà ignorato");

				}

			}

		}

	}

	public void mainPhases(String URIdocA, String URIdocB, Nconfig config)
			throws InputFileException, ComputePhaseException {
		Monitor monitor;
		MeasurePoint point;
		Dtree a;
		Dtree b;
		NxN SearchField;
		Relation R;
		// METAdelta Delta;

		if (config == null)
			config = new Nconfig();

		// Parametri di Nconfig
		boolean ltrim = config.getBoolPhaseParam(Nconfig.Normalize, "ltrim");
		boolean rtrim = config.getBoolPhaseParam(Nconfig.Normalize, "rtrim");
		boolean collapse = config.getBoolPhaseParam(Nconfig.Normalize,
				"collapse");
		boolean emptynode = config.getBoolPhaseParam(Nconfig.Normalize,
				"emptynode");
		boolean commentNode = config.getBoolPhaseParam(Nconfig.Normalize,
				"commentnode");

		point = MeasureFactory.getMeasurePoint("Create_Dtree");
		monitor = MeasureFactory.start(point.getName());
		try {
			a = new Dtree(URIdocA, ltrim, rtrim, collapse, emptynode,
					commentNode);
			b = new Dtree(URIdocB, ltrim, rtrim, collapse, emptynode,
					commentNode);
		} catch (InputFileException e) {
			throw e;

		} finally {

			monitor.stop();

		}
		SearchField = new NxN(a.count() - 1, b.count() - 1);
		R = new Relation();

		// La radice deve essere uguale per forza
		SearchField.subPoint(0, 0, Field.LOCALITY, Field.LOCALITY,
				Field.LOCALITY, Field.LOCALITY);
		R.addFragment(0, 0, 1, Relation.EQUAL);
		a.getNode(0).inRel = Relation.EQUAL;
		b.getNode(0).inRel = Relation.EQUAL;

		// Calcolo delle varie fasi
		point = MeasureFactory.getMeasurePoint("Partition_Phase");
		monitor = MeasureFactory.start(point.getName());
		new Partition(SearchField, R, a, b, config).compute();
		monitor.stop();

		for (int i = 0; i < config.phasesOrder.size(); i++) {

			try {
				switch (config.phasesOrder.get(i)) {

				case Nconfig.FindMove:
					point = MeasureFactory.getMeasurePoint("FindMove_Phase");
					monitor = MeasureFactory.start(point.getName());
					new FindMove(SearchField, R, a, b, config).compute();
					break;
				case Nconfig.FindUpdate:
					point = MeasureFactory.getMeasurePoint("FindUpdate_Phase");
					monitor = MeasureFactory.start(point.getName());
					new FindUpdate(SearchField, R, a, b, config).compute();
					break;
				case Nconfig.Propagation:
					point = MeasureFactory.getMeasurePoint("Propagation_Phase");
					monitor = MeasureFactory.start(point.getName());
					new Propagation(SearchField, R, a, b, config).compute();
					break;
				}
			} catch (ComputePhaseException e) {
				throw e;
			}

			finally {
				monitor.stop();
			}

		}

		try {
			point = MeasureFactory.getMeasurePoint("DeltaDerive_Phase");
			monitor = MeasureFactory.start(point.getName());
			new DeltaDerive(SearchField, R, a, b, config).derive();
		} catch (ComputePhaseException e) {
			throw e;
		} finally {

			monitor.stop();

		}

	}

}
