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
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Vector;
import main.diff_L1_L2.phases.FindTextChangeStyle;

/**
 *
 * Classe che mantiene i parametri di configurazione del Diff
 *
 * @author Mike
 */
public class Nconfig {

    Logger logger = Logger.getLogger(getClass().getName());

    // different phases activation
    public static final int Normalize = 0;
    public static final int Partition = 1;
    public static final int FindTextChangeStyle = 2;
    public static final int FindTextMove = 3;
    public static final int FindMove = 4;
    public static final int FindUpgrade = 5;
    public static final int FindDowngrade = 6;
    public static final int FindMerge = 7;
    public static final int FindSplit = 8;
    public static final int FindStyle = 9;
    public static final int FindUpdate = 10;
    public static final int Propagation = 11;

    // Order of the phases and phases activated
    public Vector<Integer> phasesOrder = new Vector<Integer>();
    public HashMap<Integer, HashMap<String, String>> phaseParam = new HashMap<Integer, HashMap<String, String>>();

    /**
     * If no specifications, use the default config
     */
    public Nconfig() {
        setDefault();
    }

    /**
     * Set the object with the configuration parameters from the config file
     *
     * @param XMLconfig Path to the diff configuration file in XML format
     */
    public Nconfig(String XMLconfig) {

        try {
            if (XMLconfig != null) {
                DOMDocument cfg = new DOMDocument(XMLconfig);
                cfg.strongNormalize(true, true, true, true, true);

                // Parsing the configuration file
                Node slice;
                for (int i = 0; i < cfg.root.getChildNodes().getLength(); i++) {

                    slice = cfg.root.getChildNodes().item(i);

                    // Parsing of the normalization part
                    if (slice.getNodeName().equals("normalize")) {
                        for (int k = 0; k < slice.getAttributes().getLength(); k++) {
                            addPhaseParam(Normalize, slice.getAttributes()
                                    .item(k).getNodeName(), slice
                                    .getAttributes().item(k).getNodeValue());
                        }
                    }

                    if (slice.getNodeName().equals("phases")) {
                        parseParamPhases(slice);
                    }

                }

            } else {
                setDefault();
            }
        } catch (Exception e) {
            logger.error("Error to load config file :" + e.getMessage());
            setDefault();
        }
    }

    /**
     * Aggiunge il parametro per una fase
     *
     * @param phase Fase per cui aggiungere il parametro
     * @param param Nome del parametro da aggiungere
     * @param value Valore del parametro da aggiungere
     */
    public void addPhaseParam(Integer phase, String param, String value) {
        if (phaseParam.get(phase) == null) {
            phaseParam.put(phase, new HashMap<String, String>());
        }
        phaseParam.get(phase).put(param, value);
    }

    /**
     * Ritorna il valore di un parametro relativo ad una fase
     *
     * @param phase Fase per cui si vuole ottenere il parametro
     * @param param Parametro di cui si vuole conoscere il valore
     * @return Valore del parametro richiesto
     */
    public Boolean getBoolPhaseParam(Integer phase, String param) {
        return Boolean.valueOf(phaseParam.get(phase).get(param));
    }

    /**
     * Ritorna il valore di un parametro relativo ad una fase
     *
     * @param phase Fase per cui si vuole ottenere il parametro
     * @param param Parametro di cui si vuole conoscere il valore
     * @return Valore del parametro richiesto
     */
    public Integer getIntPhaseParam(Integer phase, String param) {
        return Integer.valueOf(phaseParam.get(phase).get(param));
    }

    /**
     * Ritorna il valore di un parametro relativo ad una fase
     *
     * @param phase Fase per cui si vuole ottenere il parametro
     * @param param Parametro di cui si vuole conoscere il valore
     * @return Valore del parametro richiesto
     */
    public String getPhaseParam(Integer phase, String param) {
        return phaseParam.get(phase).get(param);
    }

    /**
     * Fa il parsing della parte di file relativa alle fasi e alla loro
     * configurazione
     *
     * @param slice Fetta di file di configurazione da parsare per prelevare i
     * parametri
     */
    private void parseParamPhases(Node slice) {

        Node phase;

        for (int i = 0; i < slice.getChildNodes().getLength(); i++) {

            phase = slice.getChildNodes().item(i);
            if (phase.getNodeName().equals("FindUpgrade")) {
                phasesOrder.add(FindUpgrade);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindUpgrade, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                            .getNodeValue());
                }
            } else if (phase.getNodeName().equals("FindDowngrade")) {
                phasesOrder.add(FindDowngrade);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindDowngrade, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }

            } else if (phase.getNodeName().equals("FindMerge")) {
                phasesOrder.add(FindMerge);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindMerge, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }

            } else if (phase.getNodeName().equals("FindSplit")) {
                phasesOrder.add(FindSplit);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindSplit, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }

            }// Parsing part related to FindMove
            else if (phase.getNodeName().equals("FindMove")) {
                phasesOrder.add(FindMove);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindMove, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }
            } else if (phase.getNodeName().equals("FindTextChangeStyle")) {
                phasesOrder.add(FindTextChangeStyle);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindTextChangeStyle, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }
            } else if (phase.getNodeName().equals("FindTextMove")) {
                phasesOrder.add(FindTextMove);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindTextMove, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }
            } else if (phase.getNodeName().equals("FindUpdate")) {
                phasesOrder.add(FindUpdate);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(FindUpdate, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }
            } // Parsing part related to Pagination
            else if (phase.getNodeName().equals("Propagation")) {
                phasesOrder.add(Propagation);
                for (int k = 0; k < phase.getAttributes().getLength(); k++) {
                    addPhaseParam(Propagation, phase.getAttributes().item(k)
                            .getNodeName(), phase.getAttributes().item(k)
                                    .getNodeValue());
                }
            }
        }
    }

    /**
     * Imposta la configurazione di default
     */
    private void setDefault() {
        logger.info("Loading Default Config");

        addPhaseParam(Normalize, "ltrim", "true");
        addPhaseParam(Normalize, "rtrim", "true");
        addPhaseParam(Normalize, "collapse", "true");
        addPhaseParam(Normalize, "emptynode", "true");
        addPhaseParam(Normalize, "commentnode", "true");

        phasesOrder.add(FindTextChangeStyle);
        addPhaseParam(FindTextChangeStyle, "level", "40");

        phasesOrder.add(FindTextMove);
        addPhaseParam(FindTextMove, "level", "40");

        phasesOrder.add(FindUpgrade);
        addPhaseParam(FindUpgrade, "level", "40");
        addPhaseParam(FindUpgrade, "range", "40");
        addPhaseParam(FindUpgrade, "minweight", "10");

        phasesOrder.add(FindDowngrade);
        addPhaseParam(FindDowngrade, "level", "40");

        phasesOrder.add(FindMerge);
        addPhaseParam(FindMerge, "level", "40");

        phasesOrder.add(FindSplit);
        addPhaseParam(FindSplit, "level", "40");

        phasesOrder.add(FindMove);
        addPhaseParam(FindMove, "range", "40");
        addPhaseParam(FindMove, "minweight", "10");

        phasesOrder.add(FindStyle);
        addPhaseParam(FindStyle, "level", "40");

        phasesOrder.add(FindUpdate);
        addPhaseParam(FindUpdate, "level", "10");

        phasesOrder.add(Propagation);
        addPhaseParam(Propagation, "attsimilarity", "20");
        addPhaseParam(Propagation, "forcematch", "false");
    }
}
