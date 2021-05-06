/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.delta;

//import java.math.int;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "delete")
public class Delete {

    @XmlAttribute(name = "nodecount", required = true)
    protected int nodecount;
    @XmlAttribute(name = "nodenumber", required = true)
    protected int nodenumber;
    @XmlAnyElement
    protected Element[] elements;
    //    @XmlValue
//    protected String value;

    /**
     * Gets the value of the nodecount property.
     *
     * @return possible object is {@link int }
     *
     */
    public int getNodecount() {
        return nodecount;
    }

    /**
     * Sets the value of the nodecount property.
     *
     * @param value allowed object is {@link int }
     *
     */
    public void setNodecount(int value) {
        this.nodecount = value;
    }

    /**
     * Gets the value of the nodenumber property.
     *
     * @return possible object is {@link int }
     *
     */
    public int getNodenumber() {
        return nodenumber;
    }

    /**
     * Sets the value of the nodenumber property.
     *
     * @param value allowed object is {@link int }
     *
     */
    public void setNodenumber(int value) {
        this.nodenumber = value;
    }

    public Element[] getElements() {
        return elements;
    }

    public void setElements(Element[] elements) {
        this.elements = elements;
    }

    /**
     *
     * @return String
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("\nnumber ", nodenumber).
                append("\nnodecount ", nodecount).
                toString();
    }

}
