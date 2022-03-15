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
package main.diff_L1_L2.core;

import main.diff_L1_L2.vdom.DOMDocument;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.core.alternatives.ThreadedBuildDtree;
import main.diff_L1_L2.debug.Debug;
import main.diff_L1_L2.exceptions.ComputePhaseException;
import main.diff_L1_L2.exceptions.InputFileException;
import main.diff_L1_L2.exceptions.OutputFileException;
import main.diff_L1_L2.metadelta.METAdelta;
import main.diff_L1_L2.phases.*;
import main.diff_L1_L2.relation.Field;
import main.diff_L1_L2.relation.NxN;
import main.diff_L1_L2.relation.Relation;
import main.diff_L1_L2.ui.ParametersHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe principale per il calcolo del Diff
 *
 * Per la procedura di diffing è necessario richiamare il metodo factory come
 * segue
 *
 * DOMDocument document = Ndiff.getDOMDocument("File originale.xml","File
 * modificato.xml",(Nconfig)
 * <file di configurazione>);
 *
 * Così facendo viene generato un documento (document) con le differenze tra i
 * file dati.
 *
 * Successivamente si può scrivere il documento generato su stream o su file.
 *
 * Scrittura su standard output: document.writeToStream(System.out);
 *
 * Scrittura su file sia come stringa:
 *
 * document.writeToFile("output.xml");
 *
 * Che come oggetto File:
 *
 * document.writeToFile(new File("output.xml"));
 *
 * @author schirinz
 * @author Cesare Jacopo Corzani
 */
public class Ndiff {

    public static final String[] EXCLUDE_STYLE_TAG = new String[]{
        "bold",
        "italic",
        "underline",
        "overline",
        "sup",
        "sub",
        "monospace",
        "preformat",
        "named-content",
        "sc",
        "b",
        "i",
        "u"
    };

    public static DOMDocument getDOMDocument(String URIdocA, String URIdocB)
            throws InputFileException, ComputePhaseException {
        Nconfig config = new Nconfig();
        return getDOMDocument(URIdocA, URIdocB, config);
    }

    /**
     * Returns the version of jats-diff written in the manifest
     *
     * @return String version of jats-diff
     */
    public static String getNdiffVersion() {

        String version = ParametersHandler.class.getPackage().getImplementationVersion();
        return version != null ? version : "N/A";

    }

    /**
     * Calcola il diff tra i due documenti e restituisce il DOMDocument
     * corrispondente al delta. N.B. La costruzione degli alberi DTree non sono
     * in parallelo
     *
     * @param URIdocA Percorso del documento originale da confrontare
     * @param URIdocB Percorso del documento modificato da confrontare
     * @param config Configurazione di ndiff
     * @return DOMdocument relativo al delta ottenuto
     */
    public static DOMDocument getDOMDocument(String URIdocA, String URIdocB,
            Nconfig config) throws InputFileException, ComputePhaseException {

        return getDOMDocument(URIdocA, URIdocB, config, false);
    }

    /**
     * Calcola il diff tra i due documenti e restituisce il DOMDocument
     * corrispondente al delta. E' possibile richiamare il metodo senza il
     * parametro config in
     * modo che venga usata la configurazione di default
     *
     * @param URIdocA Percorso del documento originale da confrontare
     * @param URIdocB Percorso del documento modificato da confrontare
     * @param config Configurazione di ndiff
     * @param enableThread Costruisce i due alberi di DTree in parallelo
     *
     * @return DOMdocument relativo al delta ottenuto
     */
    public static DOMDocument getDOMDocument(String URIdocA, String URIdocB,
            Nconfig config, boolean enableThread) throws InputFileException,
            ComputePhaseException {

        Dtree a;
        Dtree b;
        Dtree aTmp;
        Dtree bTmp;
        NxN SearchField;
        NxN SearchFieldTmp;
        Relation R;
        METAdelta Delta;

        // Nconfig parameters
        boolean ltrim = config.getBoolPhaseParam(Nconfig.Normalize, "ltrim");
        boolean rtrim = config.getBoolPhaseParam(Nconfig.Normalize, "rtrim");
        boolean collapse = config.getBoolPhaseParam(Nconfig.Normalize,
                "collapse");
        boolean emptynode = config.getBoolPhaseParam(Nconfig.Normalize,
                "emptynode");
        boolean commentNode = config.getBoolPhaseParam(Nconfig.Normalize,
                "commentnode");

        String fileContentA = getFileContents(URIdocA);
        String fileContentB = getFileContents(URIdocB);

        String URIdocATmp = putFileContents(fileContentA, URIdocA, Boolean.TRUE, Boolean.TRUE);
        String URIdocBTmp = putFileContents(fileContentB, URIdocB, Boolean.TRUE, Boolean.TRUE);

        // Construction of the data structure for comparison, with or without
        // using threads
        if (enableThread) {
            Dtree[] tempTree = (new ThreadedBuildDtree()).getDTree(URIdocA,
                    URIdocB, ltrim, rtrim, collapse, emptynode, commentNode);

            Dtree[] tempTreeTmp = (new ThreadedBuildDtree()).getDTree(URIdocATmp,
                    URIdocBTmp, ltrim, rtrim, collapse, emptynode, commentNode);

            a = tempTreeTmp[0];
            b = tempTreeTmp[1];

        } else {
            a = new Dtree(URIdocATmp, ltrim, rtrim, collapse, emptynode,
                    commentNode);

            b = new Dtree(URIdocBTmp, ltrim, rtrim, collapse, emptynode,
                    commentNode);
        }

        SearchField = new NxN(a.count() - 1, b.count() - 1);
        R = new Relation();

        Debug.diffing_normalize(a, b, R, SearchField);

        // The root must be the same
        SearchField.subPoint(0, 0, Field.LOCALITY, Field.LOCALITY,
                Field.LOCALITY, Field.LOCALITY);
        R.addFragment(0, 0, 1, Relation.EQUAL);
        a.getNode(0).inRel = Relation.EQUAL;
        b.getNode(0).inRel = Relation.EQUAL;

        // Calculation in different phases
        new Partition(SearchField, R, a, b, config).compute();

        Debug.diffing_partition(a, b, R, SearchField);

        PhaseBuilder.build(SearchField, R, a, b, config,
                "<", "=");

        //new Propagation(SearchField, R, a, b, config).compute();

        /**
        for (int i = 0; i < config.phasesOrder.size(); i++) {
            switch (config.phasesOrder.get(i)) {

                case Nconfig.FindTextChangeStyle:
                    new FindTextChangeStyle(SearchField, R, a, b, config).compute();
                    Debug.diffing_findtextchangestyle(a, b, R, SearchField);
                    try {
                        a.writeToFile(URIdocATmp);
                        b.writeToFile(URIdocBTmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (enableThread) {
                        Dtree[] tempTreeTmp = (new ThreadedBuildDtree()).getDTree(URIdocATmp,
                                URIdocBTmp, ltrim, rtrim, collapse, emptynode, commentNode);

                        a = tempTreeTmp[0];
                        b = tempTreeTmp[1];

                    } else {
                        a = new Dtree(URIdocATmp, ltrim, rtrim, collapse, emptynode,
                                commentNode);

                        b = new Dtree(URIdocBTmp, ltrim, rtrim, collapse, emptynode,
                                commentNode);
                    }

                    new Partition(SearchField, R, a, b, config).compute();
                    break;

                case Nconfig.FindTextMove:
                    new FindTextMove(SearchField, R, a, b, config).compute();
                    Debug.diffing_findtextmove(a, b, R, SearchField);
                    try {
                        a.writeToFile(URIdocATmp);
                        b.writeToFile(URIdocBTmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (enableThread) {
                        Dtree[] tempTreeTmp = (new ThreadedBuildDtree()).getDTree(URIdocATmp,
                                URIdocBTmp, ltrim, rtrim, collapse, emptynode, commentNode);

                        a = tempTreeTmp[0];
                        b = tempTreeTmp[1];

                    } else {
                        a = new Dtree(URIdocATmp, ltrim, rtrim, collapse, emptynode,
                                commentNode);

                        b = new Dtree(URIdocBTmp, ltrim, rtrim, collapse, emptynode,
                                commentNode);
                    }

                    new Partition(SearchField, R, a, b, config).compute();
                    break;

                case Nconfig.FindUpgrade:
                    new FindUpgrade(SearchField, R, a, b, config).compute();
                    Debug.diffing_findupgrade(a, b, R, SearchField);
                    break;

                case Nconfig.FindDowngrade:
                    new FindDowngrade(SearchField, R, a, b, config).compute();
                    Debug.diffing_finddowngrade(a, b, R, SearchField);
                    break;

                case Nconfig.FindMerge:
                    new FindMerge(SearchField, R, a, b, config).compute();
                    Debug.diffing_findmerge(a, b, R, SearchField);
                    break;

                case Nconfig.FindSplit:
                    new FindSplit(SearchField, R, a, b, config).compute();
                    Debug.diffing_findsplit(a, b, R, SearchField);
                    break;

                case Nconfig.FindMove:
                    new FindMove(SearchField, R, a, b, config).compute();
                    Debug.diffing_findmove(a, b, R, SearchField);
                    break;

                case Nconfig.FindUpdate:
                    new FindUpdate(SearchField, R, a, b, config).compute();
                    Debug.diffing_findupdate(a, b, R, SearchField);
                    break;

                case Nconfig.Propagation:
                    new Propagation(SearchField, R, a, b, config).compute();
                    Debug.diffing_propagation(a, b, R, SearchField);
                    break;
            }
        }
         */

        // Derivazione del delta
        Delta = new DeltaDerive(SearchField, R, a, b, config).derive();

        /*
		 * Debug.status(SearchField, "../debug/SearchField.html");
		 * Debug.status(R, "../debug/Relation.html"); Debug.status(Delta,
		 * "../debug/Mdelta.html");
         */
        return Delta.transformToXML(config);
    }

    Nconfig config = null;

    protected DOMDocument document = null;

    /**
     * Costruttore
     */
    public Ndiff() {
        this.config = new Nconfig();
    }

    /**
     * Costruttore
     *
     * @param configPath File XML relativo alla configurazione dell'algoritmo
     */
    public Ndiff(String configPath) {
        this.config = new Nconfig(configPath);

    }

    /**
     * Calcola il diff tra docA e docB e salva il delta in URIdelta Non mantiene
     * in memoria l'albero DOM generato Mantenuto per retrocompatibilita
     *
     * @param URIdocA Percorso del documento originale da confrontare
     * @param URIdocB Percorso del documento modificato da confrontare
     * @param URIdelta Percorso in cui salvare il file XML relativo al delta
     *
     * @deprecated
     */
    @Deprecated
    public void diff(String URIdocA, String URIdocB, String URIdelta)
            throws InputFileException, OutputFileException,
            ComputePhaseException {
        getDOMDocument(URIdocA, URIdocB, config).writeToFile(URIdelta);
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static String getFileContents(String filePath) {
        String text = "";
        try {
            text = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

    /**
     *
     * @param contents
     * @param filePath file name
     *
     * @return
     *
     */
    public static String putFileContents(String contents, String filePath, Boolean excludeStyleTag, Boolean excludeXrefTag) {

        try {
            contents = contents.replaceAll("[\n\r]", "");

            Path p = Paths.get(filePath);
            if (excludeStyleTag == Boolean.TRUE) {
                for (String var : EXCLUDE_STYLE_TAG) {
                    contents = contents.replaceAll("\\<" + var + "\\>", "_|" + var + "|_");
                    contents = contents.replaceAll("\\<\\/" + var + "\\>", "_|/" + var + "|_");
                }
            } else {
                for (String var : EXCLUDE_STYLE_TAG) {
                    contents = contents.replaceAll("_\\|" + var + "\\|_", "<" + var + ">");
                    contents = contents.replaceAll("_\\|\\/" + var + "\\|_", "</" + var + ">");
                }
            }

            //"(<xref).*(>).*(<\\/xref>)"
            if (excludeXrefTag == Boolean.TRUE) {
                List<String> allMatches = new ArrayList<String>();
                Matcher m = Pattern.compile("<xref .*?>.*?<\\/xref>")
                        .matcher(contents);
                while (m.find()) {
                    String mOrig = m.group();
                    String mNew = m.group();
                    Matcher n = Pattern.compile("(<xref).*(>).*(</xref)(>)")
                            .matcher(m.group());
                    while (n.find()) {
                        mNew = mNew.replace(n.group(1), "-|xref");
                        mNew = mNew.replace(n.group(2), "|-");
                        mNew = mNew.replace(n.group(3), "-|/xref");
                        mNew = mNew.replace(n.group(4), "|-"); ///NOTE: additional last entry >
                    }
                    contents = contents.replace(mOrig, mNew);
                }
            }

            //String uniqueID = UUID.randomUUID().toString();
            String fileName = p.getFileName().toString();
            String filePathNew = filePath.replace(fileName, "tmp_" + fileName);
            if (!Files.isDirectory(Paths.get("tmp"))) {
                Files.createDirectories(Paths.get("tmp"));
            }

            Files.writeString(Paths.get(filePathNew), contents);
            return filePathNew;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param contents
     *
     * @return
     *
     */
    public static String encodeTags(String contents) {

        try {

            for (String var : EXCLUDE_STYLE_TAG) {
                contents = contents.replaceAll("(_\\|" + var + "\\|_)", "<" + var + ">");
                contents = contents.replaceAll("(_\\|\\/" + var + "\\|_)", "</" + var + ">");
            }

            return contents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param contents
     *
     * @return
     *
     */
    public static String encodeXrefTags(String contents) {

        try {
            //NOTE: additional encode xref
            contents = contents.replaceAll("(-\\|xref)(.*?)(\\|-)", "<xref$2>");
            contents = contents.replaceAll("(-\\|xref\\|-)", "<xref>");
            contents = contents.replaceAll("-\\|\\/xref\\|-", "</xref>");

            return contents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String clearEncodedTags(String contents) {

        try {

            for (String var : EXCLUDE_STYLE_TAG) {
                contents = contents.replaceAll("(_\\|" + var + "\\|_)", "");
                contents = contents.replaceAll("(_\\|\\/" + var + "\\|_)", "");
            }

            return contents;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDtreeAsString(Dtree dtree) {
        try {
            Charset charset = Charset.defaultCharset();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(os, true, charset.name());
            dtree.writeToStream(printStream);
            String resContent = new String(os.toByteArray(), charset);
            return resContent;
        } catch (Exception e) {
            return "";
        }
    }

    public static String prepareRexExpPaternAsString(boolean closeTag) {
        String regExp = "";
        for (String tag : Ndiff.EXCLUDE_STYLE_TAG) {
            if (closeTag) {
                regExp = regExp.concat("(_\\|/" + tag + "\\|_)" + "|");
            } else {
                regExp = regExp.concat("(_\\|" + tag + "\\|_)" + "|");
            }

        }
        return org.apache.commons.lang.StringUtils.stripEnd(regExp, "|");
    }
}
