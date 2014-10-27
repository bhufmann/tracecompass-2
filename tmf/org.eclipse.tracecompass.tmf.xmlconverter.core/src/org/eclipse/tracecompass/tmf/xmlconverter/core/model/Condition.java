//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.07.16 at 02:05:35 PM EDT 
//


package org.eclipse.tracecompass.tmf.xmlconverter.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * Define a conditional statement. Conditions may use values of the state system or from the event being handled. This element defines a statement in the form of "if (some_path == value)".
 * 
 * <p>Java class for condition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="condition">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="stateAttribute" type="{}stateAttribute" maxOccurs="unbounded"/>
 *           &lt;element name="field" type="{}eventField"/>
 *         &lt;/choice>
 *         &lt;element name="stateValue" type="{}stateValue"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "condition", propOrder = {
    "stateAttribute",
    "field",
    "stateValue"
})
public class Condition {

    protected List<StateAttribute> stateAttribute;
    protected EventField field;
    @XmlElement(required = true)
    protected StateValue stateValue;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the stateAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stateAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStateAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StateAttribute }
     * 
     * 
     */
    public List<StateAttribute> getStateAttribute() {
        if (stateAttribute == null) {
            stateAttribute = new ArrayList<StateAttribute>();
        }
        return this.stateAttribute;
    }

    /**
     * Gets the value of the field property.
     * 
     * @return
     *     possible object is
     *     {@link EventField }
     *     
     */
    public EventField getField() {
        return field;
    }

    /**
     * Sets the value of the field property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventField }
     *     
     */
    public void setField(EventField value) {
        this.field = value;
    }

    /**
     * Gets the value of the stateValue property.
     * 
     * @return
     *     possible object is
     *     {@link StateValue }
     *     
     */
    public StateValue getStateValue() {
        return stateValue;
    }

    /**
     * Sets the value of the stateValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateValue }
     *     
     */
    public void setStateValue(StateValue value) {
        this.stateValue = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
