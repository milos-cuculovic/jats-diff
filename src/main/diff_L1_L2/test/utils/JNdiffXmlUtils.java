package main.diff_L1_L2.test.utils;

import main.diff_L1_L2.test.exceptions.XPathOperationException;
import net.sf.saxon.s9api.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 
 * JNDiff main.diff_L1_L2.test utility - Trasformazioni XSLT di file singoli o ricorsivamente su
 * una cartella data (supporta XSLT 2.0 con saxon9)
 * 
 * 
 * @author Cesare Jacopo Corzani
 * 
 */

public class JNdiffXmlUtils {

	/**
	 * 
	 * Applica ad un file una trasformazione XSL con parametri in input, se i
	 * parametri non esistono parameters deve essere impostato a null
	 * 
	 * @param sourcePath
	 *            Percorso del file a cui applicare la trasformazione xsl
	 * @param xslPath
	 *            Percorso del file xsl
	 * @param outputPath
	 *            Percorso del file di output
	 * @param parameters
	 *            Tabella Hash che contiene i parametri in input <nome,valore>
	 * @throws SaxonApiException
	 */

	public static void XSLtransform(File sourcePath, File xslPath,
			File outputPath, HashMap<String, String> parameters)
			throws SaxonApiException {

		Processor proc = new Processor(false);
		XsltCompiler comp = proc.newXsltCompiler();
		XsltExecutable exp = comp.compile(new StreamSource(xslPath));
		XdmNode source = proc.newDocumentBuilder().build(
				new StreamSource(sourcePath));
		Serializer out = new Serializer();
		out.setOutputProperty(Serializer.Property.METHOD, "xml");
		out.setOutputProperty(Serializer.Property.INDENT, "yes");
		out.setOutputFile(outputPath);
		XsltTransformer trans = exp.load();
		trans.setInitialContextNode(source);
		trans.setDestination(out);
		if (parameters != null) {

			Iterator<Entry<String, String>> it = parameters.entrySet()
					.iterator();

			while (it.hasNext()) {

				Entry<String, String> parameter = it.next();
				QName paramName = new QName(parameter.getKey());
				trans.setParameter(paramName,
						new XdmAtomicValue(parameter.getValue()));

			}

		}
		trans.transform();

	}

	/**
	 * 
	 * Richiama la versione recursiveXML con il parametro index uguale a null
	 * 
	 * @see recursiveXMLtransform
	 * 
	 * @param originalSuffix
	 * @param transformSuffix
	 * @param xslFile
	 * @param sourcePath
	 * @param outputPath
	 * @param parameters
	 * @throws SaxonApiException
	 */

	public static void recursiveXMLtransform(String originalSuffix,
			String transformSuffix, File xslFile, File sourcePath,
			File outputPath, HashMap<String, String> parameters)
			throws SaxonApiException {

		recursiveXMLtransform(originalSuffix, transformSuffix, null, xslFile,
				sourcePath, outputPath, parameters);

	}

	/**
	 * Visita il filesystem ricorsivamente in cerca dei vari file che contengono
	 * il suffisso dato e applica la trasformazione xsl ad ogni file che termina
	 * con originalSuffix
	 * 
	 * Ogni per ogni file trovato viene creata una nuova cartella (dentro
	 * outputPath) che si chiama come il file trovato senza originalSuffix
	 * Dentro questa cartella appena creata vengono posizionati il file
	 * originale e la sua trasformazione (che finisce con transformSuffix e
	 * prima di questo suffisso viene inserito index)
	 * 
	 * Es. di output File di output con index diverso da null:
	 * <nomefile>_<index>_<transformSuffix> File di output con index uguale
	 * null: <nomefile>_<transformSuffix>
	 * 
	 * @param originalSuffix
	 *            Suffisso dei file di input
	 * @param transformSuffix
	 *            Suffisso del file di output
	 * @param index
	 *            Ulteriore identificativo del file, se è null non viene
	 *            inserito
	 * @param xslFile
	 *            XSL da applicare ai file
	 * @param sourcePath
	 *            Directory da scansionare ricorsivamente
	 * @param outputPath
	 *            Directory di output
	 * 
	 * 
	 * 
	 * @param parameters
	 *            Parametri da applicare all'xsl
	 * @throws SaxonApiException
	 */

	public static void recursiveXMLtransform(String originalSuffix,
			String transformSuffix, String index, File xslFile,
			File sourcePath, File outputPath, HashMap<String, String> parameters)
			throws SaxonApiException {

		Logger logger = Logger.getLogger(JNdiffXmlUtils.class);

		File[] files = sourcePath.listFiles();

		for (File file : files) {

			if (!((file.isFile() && file.getName().endsWith(originalSuffix)) || file
					.isDirectory()))
				continue;

			if (file.isDirectory()) {

				recursiveXMLtransform(originalSuffix, transformSuffix, index,
						xslFile, file, outputPath, parameters);

			}

			else {

				/*
				 * 
				 * Se il file si chiama "a_1.xml", originalSuffix = "_1.xml" e
				 * transformSuffix = "_5.xml" allora Creo una cartella (se non
				 * esiste) dentro outputPath che si chiama come il nome del file
				 * senza "_1.xml". Copio il file "_1.xml" dentro questa cartella
				 * rinominato con aggiunta del parametro ovvero "a_1.xml"
				 * diventa "a_50_1.xml" se il parametro dovesse essere 50
				 * Applico la trasformazione xsl e chiamo il file risultante
				 * a_50_5.xml mettendolo nella cartella appena creata.
				 */

				String tempPath = outputPath.getAbsolutePath();

				String filenameXsltOutput = tempPath
						+ (tempPath.endsWith(File.separator) ? ""
								: File.separator);

				File newDir = new File(filenameXsltOutput
						+ file.getName().replaceAll(originalSuffix, ""));

				if (!newDir.exists())
					newDir.mkdir();

				tempPath = newDir.getAbsolutePath();

				filenameXsltOutput = tempPath
						+ (tempPath.endsWith(File.separator) ? ""
								: File.separator) + file.getName();
				filenameXsltOutput = filenameXsltOutput.replaceAll(
						originalSuffix, ((index != null) ? ("_" + index) : "")
								+ transformSuffix);

				try {
					copyFile(
							file,
							new File(filenameXsltOutput.replaceAll(
									transformSuffix, originalSuffix)));
				} catch (IOException e) {
					// TODO gestione errore
					e.printStackTrace();
				}

				logger.debug("Creazione file " + filenameXsltOutput);
				XSLtransform(file, xslFile, new File(filenameXsltOutput),
						parameters);

			}
		}

	}

	/**
	 * Esegue la copia di un file
	 * 
	 * 
	 */

	private static void copyFile(File source, File target) throws IOException {

		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);

		byte[] buf = new byte[1024];
		int len;

		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}

	/**
	 * 
	 * Calcola il numero dei nodi di un dato xml
	 * 
	 * @param source
	 *            File sorgente
	 * @return Numero dei nodi di un xml
	 * @throws XPathOperationException
	 */
	public static long nodeCount(String source) throws XPathOperationException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		//domFactory.setNamespaceAware(false); // never forget this!
		DocumentBuilder builder;
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {

			throw new XPathOperationException(e);
		}

		Document doc;
		try {
			doc = builder.parse(source);
		} catch (Exception e) {
			throw new XPathOperationException(e);
		}

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr;
		try {
			expr = xpath.compile("count(//*)");
		} catch (XPathExpressionException e) {
			throw new XPathOperationException(e);
		}

		Double result;
		try {
			result = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
		} catch (XPathExpressionException e) {
			throw new XPathOperationException(e);
		}

		return result.longValue();

	}

}
