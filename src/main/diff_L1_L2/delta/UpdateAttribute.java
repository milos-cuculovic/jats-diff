/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.delta;

//import java.math.int;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="newvalue" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="nodenumber" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="oldvalue" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="op" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "update-attribute")
public class UpdateAttribute {

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "newvalue", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    protected String newvalue;
    @XmlAttribute(name = "nodenumber", required = true)
    protected int nodenumber;
    @XmlAttribute(name = "oldvalue")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String oldvalue;
    @XmlAttribute(name = "op", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String op;

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the newvalue property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNewvalue() {
        return newvalue;
    }

    /**
     * Sets the value of the newvalue property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setNewvalue(String value) {
        this.newvalue = value;
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

    /**
     * Gets the value of the oldvalue property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOldvalue() {
        return oldvalue;
    }

    /**
     * Sets the value of the oldvalue property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOldvalue(String value) {
        this.oldvalue = value;
    }

    /**
     * Gets the value of the op property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getOp() {
        return op;
    }

    /**
     * Sets the value of the op property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setOp(String value) {
        this.op = value;
    }

    /**
     *
     * @return String
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("\nnodenumber", nodenumber).
                toString();
    }

}
