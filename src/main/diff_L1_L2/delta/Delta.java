/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.diff_L1_L2.delta;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "delete",
    "insert",
    "updateAttribute"
})
@XmlRootElement(name = "jats-diff")
public class Delta {

    @XmlElement(name = "delete")
    protected List<Delete> delete;
    @XmlElement(name = "insert")
    protected List<Insert> insert;
    @XmlElement(name = "update-attribute")
    protected List<UpdateAttribute> updateAttribute;
    @XmlAttribute(name = "collapse", required = true)
    protected boolean collapse;
    @XmlAttribute(name = "commentnode", required = true)
    protected boolean commentnode;
    @XmlAttribute(name = "emptynode", required = true)
    protected boolean emptynode;
    @XmlAttribute(name = "ltrim", required = true)
    protected boolean ltrim;
    @XmlAttribute(name = "rtrim", required = true)
    protected boolean rtrim;

    /**
     * Gets the value of the delete property.
     *
     * @return
     *
     *
     */
    public List<Delete> getDelete() {
        if (delete == null) {
            delete = new ArrayList<>();
        }
        return this.delete;
    }

    /**
     * Gets the value of the insert property.
     *
     * @return
     *
     */
    public List<Insert> getInsert() {
        if (insert == null) {
            insert = new ArrayList<>();
        }
        return this.insert;
    }

    /**
     * Gets the value of the updateAttribute property.
     *
     * @return
     */
    public List<UpdateAttribute> getUpdateAttribute() {
        if (updateAttribute == null) {
            updateAttribute = new ArrayList<>();
        }
        return this.updateAttribute;
    }

    /**
     * Gets the value of the collapse property.
     *
     * @return
     */
    public boolean isCollapse() {
        return collapse;
    }

    /**
     * Sets the value of the collapse property.
     *
     * @param value
     */
    public void setCollapse(boolean value) {
        this.collapse = value;
    }

    /**
     * Gets the value of the commentnode property.
     *
     * @return
     */
    public boolean isCommentnode() {
        return commentnode;
    }

    /**
     * Sets the value of the commentnode property.
     *
     * @param value
     */
    public void setCommentnode(boolean value) {
        this.commentnode = value;
    }

    /**
     * Gets the value of the emptynode property.
     *
     * @return
     */
    public boolean isEmptynode() {
        return emptynode;
    }

    /**
     * Sets the value of the emptynode property.
     *
     * @param value
     */
    public void setEmptynode(boolean value) {
        this.emptynode = value;
    }

    /**
     * Gets the value of the ltrim property.
     *
     * @return
     */
    public boolean isLtrim() {
        return ltrim;
    }

    /**
     * Sets the value of the ltrim property.
     *
     * @param value
     */
    public void setLtrim(boolean value) {
        this.ltrim = value;
    }

    /**
     * Gets the value of the rtrim property.
     *
     * @return
     */
    public boolean isRtrim() {
        return rtrim;
    }

    /**
     * Sets the value of the rtrim property.
     *
     * @param value
     */
    public void setRtrim(boolean value) {
        this.rtrim = value;
    }

    /**
     * Get Insert object by index
     *
     * @param index
     *
     * @return
     */
    public Insert getInsertByIndex(int index) {
        try {
            return this.insert.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

    }

    /**
     * Get Delete object by index
     *
     * @param index
     *
     * @return
     */
    public Delete getDeletetByIndex(int index) {

        try {
            return this.delete.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get Update attribute object by index
     *
     * @param index
     *
     * @return
     */
    public UpdateAttribute getUpdateAttributeByIndex(int index) {

        try {
            return this.updateAttribute.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
