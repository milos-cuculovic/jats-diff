package main.diff_L1_L2.test.utils;

import net.sf.saxon.s9api.SaxonApiException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;

/**
 * 
 * 
 * Dato un set di file xml genera dei cambiamenti random basati su percentuali
 * 
 * @author Cesare Jacopo Corzani
 * 
 */
public class XMLChangeGenerator {

	public static final String SOURCE_SUFFIX = "_1.xml";
	public static final String DEST_SUFFIX = "_2.xml";

	/**
	 *
	 */
	public static void main(String[] args) {

		Logger logger = Logger.getLogger(XMLChangeGenerator.class);

		int pStart = 0;
		int pStop = 100;
		int pStep = 10;

		if (args.length != 6) {

			System.out
					.println("Utilizzo: xmlchange <source_dir> <output_dir> <xsl_file>  <percentage_start> <percentage_stop> <percentage_step>");
			System.exit(1);
		}

		logger.debug("Directory corrente: " + System.getProperty("user.dir"));

		// Test dei parametri in ingresso
		String source = args[0];
		File sourcePath = new File(source);
		if (!(sourcePath.exists() && sourcePath.isDirectory())) {

			logger.info(source + " è una cartella valida");
			System.exit(1);
		}

		String output = args[1];
		File outputPath = new File(output);

		if (!outputPath.exists()) {
			outputPath.mkdirs();
		}

		if (outputPath.isFile()) {

			logger.info(outputPath + " è un file e non una cartella");
			System.exit(1);
		}

		String xsl = args[2];
		File xslPath = new File(xsl);
		if (!(xslPath.exists() && xslPath.isFile())) {

			logger.info(xsl + " non esiste");
			System.exit(1);
		}

		try {
			pStart = Integer.parseInt(args[3]);
			pStop = Integer.parseInt(args[4]);
			pStep = Integer.parseInt(args[5]);
		} catch (NumberFormatException e) {

			System.out
					.println("I parametri <percentage_*> devono essere numeri interi");
			System.exit(1);

		}

		/*
		 * 
		 * Inizio trasformazione con i parametri dati
		 */
		for (int i = pStart; i <= pStop; i += pStep) {

			HashMap<String, String> parameters = new HashMap<String, String>();

			parameters.put("percentageTOTAL", i + "");

			try {
				JNdiffXmlUtils.recursiveXMLtransform(SOURCE_SUFFIX,
						DEST_SUFFIX, i + "", xslPath, sourcePath, outputPath,
						parameters);
			} catch (SaxonApiException e) {
				// Per ora gestione blanda degli errori...
				e.printStackTrace();
			}
		}
	}

}
