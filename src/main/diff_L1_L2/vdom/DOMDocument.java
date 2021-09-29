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

package main.diff_L1_L2.vdom;

import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.exceptions.OutputFileException;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * Class incapsulating the standar DOM and providing methods to create/save DOM
 * @author Mike
 */
public class DOMDocument {

	/** Class used as handler during the parsing of documents
	 * @author Mike 
	 */
	static class DOMErrorHandlerImpl implements DOMErrorHandler {

		public String errorSeverity(DOMError error) {
			String ret = "";
			switch (error.getSeverity()) {
			case DOMError.SEVERITY_ERROR:
				ret = "SEVERITY_ERROR";
			case DOMError.SEVERITY_FATAL_ERROR:
				ret = "SEVERITY_FATAL_ERROR";
			case DOMError.SEVERITY_WARNING:
				ret = "SEVERITY_WARNING";
			}
			return ret;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.w3c.dom.DOMErrorHandler#handleError(org.w3c.dom.DOMError)
		 */
		public boolean handleError(DOMError error) {
			/*
			 * System.out.println("<Error occured>");
			 * System.out.println("Severity: "+errorSeverity(error));
			 * System.out.println("Message: "+error.getMessage());
			 * System.out.println("Type: "+error.getType());
			 */
			return true;
		}

	}

	Logger logger = Logger.getLogger(getClass().getName());
	DOMImplementation domImpl;
	DOMImplementationLS domImplLS;
	public Document DOM;

	public Element root;
	// Variables to recursively normalize the DOM document
	boolean ltrim;
	boolean rtrim;
	boolean collapse;
	boolean emptynode;

	boolean commentnode;

	/**
	 * Create a DOM document from the provided URI
	 * 
	 * @param URIfile
	 *            path of the file
	 * @throws InputFileException an Exception will be raised if parsing problems occur
	 */
	public DOMDocument(String URIfile) throws InputFileException {

		setDomImplementationDOML3();

		// Create parser and set parser features
		LSParser parser = domImplLS.createLSParser(
				DOMImplementationLS.MODE_SYNCHRONOUS,
				"http://www.w3.org/2001/XMLSchema");
		parser.getDomConfig().setParameter("error-handler",
				new DOMErrorHandlerImpl());

		parser.getDomConfig().setParameter("element-content-whitespace",
				Boolean.TRUE);
		parser.getDomConfig().setParameter("well-formed", Boolean.TRUE);

		// parser.getDomConfig().setParameter("namespaces",Boolean.TRUE);
		// parser.getDomConfig().setParameter("validate",Boolean.TRUE);

		// showDOMConfig(parser.getDomConfig());

		// Set the input
		LSInput input = domImplLS.createLSInput();
		// input.setEncoding("UTF-16");

		input.setSystemId(URIfile);

		// Parse file
		try {
			DOM = parser.parse(input);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new InputFileException(URIfile);
		}

		root = DOM.getDocumentElement();
	}

	/**
	 * Create a new DOM document
	 * 
	 * @param namespace
	 *            document's namespace
	 * @param rootname
	 *            name of the root node of the document
	 */
	public DOMDocument(String namespace, String rootname) {
		setDomImplementationJAXP();

		//System.out.println("Creating new DOMdocument, namespace:" + namespace
		//		+ " root:" + rootname);

		// Create DOM
		DOM = domImpl.createDocument(namespace, rootname, null);
		root = DOM.getDocumentElement();
		// showDOMConfig(DOM.getDomConfig());

	}

	/**
	 * apply xslt and write into the provided file
	 * 
	 * @param xsltPath
	 * @param outputPath
	 */
	public void applyXSLT(String xsltPath, File outputPath) {

		logger.debug("Apply XSLT");
		logger.debug("XSLT:   " + xsltPath);
		logger.debug("OUTPUT: " + outputPath);

		Transformer transformer;
		TransformerFactory factory = TransformerFactory.newInstance();

		try {
			transformer = factory.newTransformer(new StreamSource(xsltPath));
			transformer.transform(new DOMSource(DOM), new StreamResult(
					outputPath));
		} catch (Exception e) {
			logger.error("Error during XSL tranformation", e);
		}

	}

	/** apply xslt and write into the provided PrintStream
	 * 
	 * 
	 * @param xsltPath
	 * @param outputPath
	 */
	public void applyXSLT(String xsltPath, PrintStream outputPath) {

		logger.debug("Apply XSLT");
		logger.debug("XSLT:   " + xsltPath);
		logger.debug("OUTPUT: " + outputPath);

		Transformer transformer;
		TransformerFactory factory = TransformerFactory.newInstance();

		try {
			transformer = factory.newTransformer(new StreamSource(xsltPath));
			transformer.transform(new DOMSource(DOM), new StreamResult(
					outputPath));
		} catch (Exception e) {
			logger.error("Error during XSL tranformation", e);
		}

	}

	/**
	 * Returns the string s with all the whitespaces, returns, tabs, collapsed in
	 * a single whitespace
	 * 
	 * @param s
	 *            string to be normalized
	 * @return string with whitespaces, returns, tabs collapsed
	 */
	public String collapse(String s) {
		String collapseString = "";
		StringTokenizer st = new StringTokenizer(s);
		while (st.hasMoreTokens()) {
			collapseString += st.nextToken();
			if (st.hasMoreTokens())
				collapseString += " ";
		}
		return collapseString;
	}

	/**
	 * Set the DOM implementation 
	 */
	public void setDomImplementationDOML3() {

		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			domImpl = registry.getDOMImplementation("XML 3.0 LS 3.0");
			domImplLS = (DOMImplementationLS) domImpl;
		} catch (Exception e) {
			logger.error("Not DOML3 imlementation set", e);
		}

	}

	/**
	 * Set the DOM implementation 
	 */
	public void setDomImplementationJAXP() {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			domImpl = builder.getDOMImplementation();
			domImplLS = (DOMImplementationLS) domImpl;
		} catch (Exception e) {
			logger.error("Not DOML3 imlementation set", e);
		}

	}

	/**
	 * View the content of a DOMconfiguration
	 * 
	 * @param domcfg
	 *            DOM configuration parameters
	 */
	public void showDOMConfig(DOMConfiguration domcfg) {
		System.out.println(" ### DOM configuration ###");
		DOMStringList cfg = domcfg.getParameterNames();

		String svalue;
		String par;
		Object value;
		for (int i = 0; i < cfg.getLength(); i++) {
			par = cfg.item(i);
			value = domcfg.getParameter(par);

			if (value == null)
				svalue = "null";
			else
				svalue = value.toString();

			System.out.println(" #" + par + " : " + svalue);
		}
		System.out.println(" #########################");

	}

	/**
	 * Normalize every node
	 * 
	 * @param node
	 *            node to be normalized
	 */
	public void strongNodeNormalize(Node node) {

		// right recursive call
		if (node.hasChildNodes())
			for (int i = node.getChildNodes().getLength() - 1; i >= 0; i--) {
				strongNodeNormalize(node.getChildNodes().item(i));
			}

		switch (node.getNodeType()) {

		case Node.TEXT_NODE:

			String toNorm = node.getTextContent();

			// remove empty text nodes
			if (!emptynode && (collapse(toNorm).length() <= 0)) {
				node.getParentNode().removeChild(node);
			} else {

				// collapse internal whitespaces
				if (collapse)
					toNorm = collapse(toNorm);

				// removing trailing whitespaces
				else if (ltrim || rtrim)
					toNorm = toNorm.trim();

				node.setTextContent(toNorm);
			}

			break;

		case Node.COMMENT_NODE:

			// Elimino i nodi commento
			if (!commentnode)
				node.getParentNode().removeChild(node);

			break;

		/*
		 * case Node.PROCESSING_INSTRUCTION_NODE: break;
		 * 
		 * case Node.NOTATION_NODE: break;
		 * 
		 * case Node.ELEMENT_NODE: break;
		 */

		}
	}

	/**
	 * Apply a strong normalization on the document
	 * 
	 * @param ltrim
	 *            (true) remove left trailing whitespaces
	 * @param rtrim
	 *            (true) remove right trailing whitespaces
	 * @param collapse
	 *            (true) collapses internal whitespaces
	 * @param emptynode
	 *            (true) if set doesn't consider textnodes only containing
	 *            whitespaces
	 * @param commentnode
	 *            (true) if set doesn't consider comment nodes
	 */
	public void strongNormalize(boolean ltrim, boolean rtrim, boolean collapse,
			boolean emptynode, boolean commentnode) {
		//System.out.println("Normalizing document ltrim:" + ltrim + " rtrim:" + rtrim
		//		+ " collapse:" + collapse + " emptynode:" + emptynode
		//		+ " commentNode:" + commentnode);
		this.ltrim = ltrim;
		this.rtrim = rtrim;
		this.collapse = collapse;
		this.emptynode = emptynode;
		this.commentnode = commentnode;
		strongNodeNormalize(root);
		DOM.normalizeDocument();
	}

	/**
	 * Save the DOM to a file
	 * 
	 * @param pathFile
	 *            Path of the file
	 */

	public void writeToFile(File pathFile) throws FileNotFoundException {

		try {
			writeToStream(new PrintStream(pathFile));
		} catch (FileNotFoundException e) {
			logger.fatal("No Document save" + pathFile, e);
			throw e;
		}

	}

	/**
	 * Save the DOM to a file
	 * 
	 * @param pathFile
	 *            Path of the file
	 */

	public void writeToFile(String pathFile) throws OutputFileException {

		try {
			writeToStream(new PrintStream(pathFile));
		} catch (FileNotFoundException e) {
			logger.fatal("No Document save" + pathFile, e);
			throw new OutputFileException(pathFile);
		}

	}

	/**
	 * Output DOM on a PrintStream object
	 * 
	 * @param writer
	 *            target object
	 */
	public void writeToStream(PrintStream writer) {

		// Create LSserializer
		LSSerializer serializer = domImplLS.createLSSerializer();
		LSOutput output = domImplLS.createLSOutput();

		// Set features for serializer
		// serializer.getDomConfig().setParameter("ignore-unknown-character-denormalizations",
		// true);
		// serializer.getDomConfig().setParameter("format-pretty-print",
		// true);
		serializer.getDomConfig().setParameter("error-handler",
				new DOMErrorHandlerImpl());
		serializer.getDomConfig().setParameter("well-formed", true);

		serializer.getDomConfig().setParameter("namespaces", false);
		// serializer.getDomConfig().setParameter("infoset",true);

		serializer.getDomConfig().setParameter("element-content-whitespace",
				true);

		// showDOMConfig(serializer.getDomConfig());

		// Set output
		// output.setEncoding("UTF-16");
		// output.setByteStream(new FileOutputStream(new File(pathFile)));
		output.setByteStream(writer);

		// DOM serialize
		serializer.write(DOM, output);
	}
}
