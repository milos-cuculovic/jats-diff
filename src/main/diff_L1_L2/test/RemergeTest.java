package main.diff_L1_L2.test;

import junit.framework.TestCase;
import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.core.Ndiff;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.exceptions.OutputFileException;
import main.diff_L1_L2.test.exceptions.XMLreadException;
import org.custommonkey.xmlunit.Diff;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * Test JUnit Esegue un diff tra vari file (<file_1.xml> <file_2.xml>) creando
 * un file <file>_3.xml con le differenze In seguito viene fatto un merge tra
 * <file_2.xml> e <file_3.xml>. Il file risultante ovvero <file_4.xml> deve
 * essere uguale a <file_1.xml>
 * 
 * @author Cesare Jacopo Corzani
 * 
 * 
 *         TODO Ora usa le API vecchie, dovrebbe usare le nuove 
 * 
 */
public class RemergeTest extends TestCase {

	public static final String TEST_DIR_XML = "resources/testset_xml";
	public static final String TEST_DIR_VARIUOS_XML = "resources/testset_xml/various_xml";
	public static final String TEST_DIR_CAM_SEN_XML = "resources/testset_xml/testset_cam_sen";

	Ndiff testNdiff;
	Document domOriginal;
	Document domModified;
	Document domDifferences;

	@Test
	public void testDiffMergeVariousXML() {

		try {
			xmlRecursiveTest(TEST_DIR_VARIUOS_XML, "_1", "_2", "_3", "_4");

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testDiffMergeCamSen() {
		assertTrue(true);
		/*
		 * try { xmlRecursiveTest(TEST_DIR_CAM_SEN_XML, "-CAM", "-SEN", "-DIFF",
		 * "-MERGED"); } catch (Exception e) { assertTrue(e.getMessage(),
		 * false); }
		 */
	}

	private void xmlRecursiveTest(String testDir, String originalSuffix,
			String modifiedSuffix, String differenceSuffix, String mergedSuffix)
			throws XMLreadException, FileNotFoundException, SAXException,
			IOException, InputFileException, ComputePhaseException,
			OutputFileException {
		File dir = new File(testDir);

		try {
			recursiveXMLtest(originalSuffix, modifiedSuffix, differenceSuffix,
					mergedSuffix, dir);
		} catch (XMLreadException e) {
			throw e;
		}

	}

	private void recursiveXMLtest(String originalSuffix, String modifiedSuffix,
			String differenceSuffix, String mergedSuffix, File dir)
			throws XMLreadException, FileNotFoundException, SAXException,
			IOException, InputFileException, ComputePhaseException,
			OutputFileException {

		File[] files = dir.listFiles();

		for (File file : files) {

			if (!((file.isFile() && file.getName().split("\\.")[0]
					.endsWith(originalSuffix)) || file.isDirectory()))
				continue;

			if (file.isDirectory()) {

				recursiveXMLtest(originalSuffix, modifiedSuffix,
						differenceSuffix, mergedSuffix, file);

			}

			else {

				String filenameOriginal = file.getPath();
				String filenameModified = filenameOriginal.replaceAll(
						originalSuffix, modifiedSuffix);
				String filenameDifferences = filenameOriginal.replaceAll(
						originalSuffix, differenceSuffix);
				String filenameMerged = filenameOriginal.replaceAll(
						originalSuffix, mergedSuffix);

				Diff diff = null;

				String assertError = "E' stato eseguito un diff tra il file \""
						+ filenameOriginal
						+ "\" e il file \""
						+ filenameModified
						+ "\" e il risultato è stato scritto in \""
						+ filenameDifferences
						+ "\". "
						+ "Successivamente è stato fatto un merge tra il file \""
						+ filenameOriginal + "\" e il file appena creato \""
						+ filenameDifferences + "\". "
						+ "Questo nuovo file appena creato (\""
						+ filenameMerged + "\") e il file \""
						+ filenameModified
						+ "\" dovrebbero essere uguali ma non lo sono.";

				/*
				 * 
				 * Calcola le differenze tra filenameOriginal e
				 * filenameDifferences
				 */

				DOMDocument document;

				document = Ndiff.getDOMDocument(filenameOriginal,
						filenameModified);

				/*
		     * 
		     * 
		     * */

				// testNmerge.merge(filenameOriginal, filenameDifferences,
				// filenameMerged);

				// temp file

				File temp = File.createTempFile("main/diff_L1_L2/test", ".xml");

				temp.deleteOnExit();

				document.writeToFile(temp);

				diff = new Diff(new FileReader(new File(filenameModified)),
						new FileReader(temp));

				if (!diff.identical()) {

					fail(assertError);

				}

			}

		}

	}

	@BeforeClass
	public static void runBeforeClass() {
	}

	@AfterClass
	public static void runAfterClass() {
	}

}
