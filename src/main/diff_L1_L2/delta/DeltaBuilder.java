/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.delta;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import javax.xml.stream.*;
import javax.xml.transform.stream.StreamSource;
import org.apache.log4j.Logger;
import java.util.List;
import org.w3c.dom.Element;

/**
 * build delta objects
 *
 * @author sasha
 */
public class DeltaBuilder {

    Logger logger = Logger.getLogger(DeltaBuilder.class);
    Delta delta;

    /**
     *
     * @param deltaPath
     *
     * @return delta | null
     */
    public Delta parse(String deltaPath) {
        try {
            logger.info("start delta builder");
            File xmlFile = new File(deltaPath);
            JAXBContext jaxbContext = JAXBContext.newInstance(Delta.class);
            XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
            xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
            StreamSource source = new StreamSource(deltaPath);
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(source);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            delta = (Delta) jaxbUnmarshaller.unmarshal(xmlStreamReader);
            logger.info("end delta builder");
            findUpdate();
            return delta;
        } catch (JAXBException | XMLStreamException e) {
            logger.error(e.getMessage());
            e.printStackTrace(System.out);

        }
        return null;
    }

    public void findUpdate() {
//        insert.getElements().forEach(System.out::println);
//        Arrays.toString(insert.getElements());
//        logger.info(insert.getElements());
//        for (Element e : insert.getElements()) {
//            logger.info(e.getTextContent());
//        }
//        logger.info(insert.toString());
        logger.info("statrt update");
        List<Insert> inserts = this.delta.getInsert();
        List<Delete> deletes = this.delta.getDelete();
        for (Delete delete : deletes) {
            for (Insert insert : inserts) {
//                logger.info(insert.getNodenumber());
                Element[] elements = insert.getElements();
                logger.info(elements[0].getTextContent());
                int nodeCount = insert.getNodecount();

                if (delete.getNodecount() - 1 == insert.getNodecount()) {
                    logger.info(insert.getAt());
                    logger.info(insert.getNodenumber());
                }
            }
        }
    }

    public void findMerge() {

    }

    public void findMove() {

    }

    public void findSplit() {

    }

}
