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
package main.diff_L1_L2.ui;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.core.Nconfig;
import main.diff_L1_L2.core.Ndiff;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.exceptions.OutputFileException;
import org.apache.log4j.Logger;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.commons.lang.StringEscapeUtils;

public class OperationsHandler {

    /**
     * @param params
     * @throws InputFileException
     * @throws ComputePhaseException
     * @throws OutputFileException
     */
    private static void doDiff(Parameters params) throws InputFileException,
            ComputePhaseException, OutputFileException {
        DOMDocument document;
        document = Ndiff.getDOMDocument(params.getOriginalPath(),
                params.getModifiedPath(), new Nconfig(params.getConfigPath()));

        if (params.getDeltaPath() == null || params.isStdout()) {
//			document.writeToStream(System.out); //original output in jats-diff uncommend this if need)
            String results = documentToString(document); ////added
            results = Ndiff.encodeTags(results);
//			logger.info(StringEscapeUtils.unescapeHtml(results)); //added becasue need decode tags vs without tringEscapeUtils.unescapeHtml
//			logger.info(results);

        } else {
//			document.writeToFile(params.getDeltaPath()); //original
            String results = documentToString(document); ////added
            results = Ndiff.encodeTags(results);
            try {
//				Files.writeString(Paths.get(params.getDeltaPath()), StringEscapeUtils.unescapeHtml(results)); //added becasue need decode tags
                Files.writeString(Paths.get(params.getDeltaPath()), results);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void doOperation(Parameters params)
            throws InputFileException, OutputFileException,
            ComputePhaseException, FileNotFoundException {

        Logger logger = Logger.getLogger(OperationsHandler.class);
        try {
            switch (ParametersHandler.getOperation(params.isDiff(),
                    params.isMerge())) {

                case ParametersHandler.DIFF:

                    doDiff(params);

                    break;
            }
        } catch (InputFileException e) {
            logger.fatal("Error in the input file", e);
            throw e;

        } catch (OutputFileException e) {
            logger.fatal("Error in the output file", e);
            throw e;

        } catch (ComputePhaseException e) {
            logger.fatal("Error processing a phase", e);
            throw e;
        }
    }

    public static String documentToString(DOMDocument document) {
        try {

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(document.DOM), new StreamResult(sw));
            return sw.toString();
        } catch (TransformerException tEx) {
            tEx.printStackTrace();
        }
        return null;
    }
}
