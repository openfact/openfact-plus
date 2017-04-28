package org.openfact.ubl.pe.summary;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.AmountType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DocumentTypeCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.LineIDType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.IdentifierType;
import org.openfact.ubl.pe.perception.SUNATPerceptionDocumentReferenceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryDocumentsLineType",
        propOrder = {
                "lineID", "documentTypeCode", "id",
                "documentSerialID", "startDocumentNumberID", "endDocumentNumberID", "accountingCustomerParty",
                "billingReference", "sunatPerceptionDocumentReference", "status", "totalAmount", "billingPayment",
                "allowanceCharge", "taxTotal"
        }
)
public class SummaryDocumentsLineType {
    @XmlElement(name = "LineID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected LineIDType lineID;

    @XmlElement(name = "DocumentTypeCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected DocumentTypeCodeType documentTypeCode;

    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected IDType id;

    @XmlElement(name = "DocumentSerialID", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1")
    protected IdentifierType documentSerialID;

    @XmlElement(name = "StartDocumentNumberID", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1")
    protected IdentifierType startDocumentNumberID;

    @XmlElement(name = "EndDocumentNumberID", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1")
    protected IdentifierType endDocumentNumberID;

    @XmlElement(name = "AccountingCustomerParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected CustomerPartyType accountingCustomerParty;

    @XmlElement(name = "BillingReference", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected BillingReferenceType billingReference;

    @XmlElement(name = "SUNATPerceptionDocumentReference")
    protected SUNATPerceptionDocumentReferenceType sunatPerceptionDocumentReference;

    @XmlElement(name = "Status", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected StatusType status;

    @XmlElement(name = "TotalAmount", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1", required = true)
    protected AmountType totalAmount;

    @XmlElement(name = "BillingPayment", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1")
    protected List<PaymentType> billingPayment;

    @XmlElement(name = "AllowanceCharge", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<AllowanceChargeType> allowanceCharge;

    @XmlElement(name = "TaxTotal", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<TaxTotalType> taxTotal;

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line. Identifier
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Identifies the Consolidated Invoice Line.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Identifier&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Identifier&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Identifier. Type&lt;/ccts:DataType&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link LineIDType }
     */
    public LineIDType getLineID() {
        return lineID;
    }

    /**
     * Sets the value of the lineID property.
     *
     * @param value allowed object is {@link LineIDType }
     */
    public void setLineID(LineIDType value) {
        this.lineID = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Type Code.
     * 								Code
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Code specifying the type of the Invoice.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Consolidated Invoice Line Type Code
     * 							&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Code&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Code. Type&lt;/ccts:DataType&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link DocumentTypeCodeType }
     */
    public DocumentTypeCodeType getDocumentTypeCode() {
        return documentTypeCode;
    }

    /**
     * Sets the value of the documentTypeCode property.
     *
     * @param value allowed object is {@link DocumentTypeCodeType }
     */
    public void setDocumentTypeCode(DocumentTypeCodeType value) {
        this.documentTypeCode = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Type Code.
     * 								Code
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Billing Payment.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Billing Payment&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Consolidated Invoice Line
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link IDType }
     */
    public IDType getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link IDType }
     */
    public void setID(IDType value) {
        this.id = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Billing
     * 								Payment
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Billing Payment.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Billing Payment&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Consolidated Invoice Line
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link IdentifierType }
     */
    public IdentifierType getDocumentSerialID() {
        return documentSerialID;
    }

    /**
     * Sets the value of the documentSerialID property.
     *
     * @param value allowed object is {@link IdentifierType }
     */
    public void setDocumentSerialID(IdentifierType value) {
        this.documentSerialID = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Billing
     * 								Payment
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Billing Payment.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Billing Payment&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Consolidated Invoice Line
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link IdentifierType }
     */
    public IdentifierType getStartDocumentNumberID() {
        return startDocumentNumberID;
    }

    /**
     * Sets the value of the startDocumentNumberID property.
     *
     * @param value allowed object is {@link IdentifierType }
     */
    public void setStartDocumentNumberID(IdentifierType value) {
        this.startDocumentNumberID = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line End Invoice
     * 								Number
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to End Invoice Number.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;End Invoice Number&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Consolidated Invoice Line
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link IdentifierType }
     */
    public IdentifierType getEndDocumentNumberID() {
        return endDocumentNumberID;
    }

    /**
     * Sets the value of the endDocumentNumberID property.
     *
     * @param value allowed object is {@link IdentifierType }
     */
    public void setEndDocumentNumberID(IdentifierType value) {
        this.endDocumentNumberID = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Invoice. Accounting_ Customer Party. Customer Party&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to the Accounting Customer Party.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Invoice&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTermQualifier&gt;Accounting&lt;/ccts:PropertyTermQualifier&gt;&lt;ccts:PropertyTerm&gt;Customer Party&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Customer Party&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link CustomerPartyType }
     */
    public CustomerPartyType getAccountingCustomerParty() {
        return accountingCustomerParty;
    }

    /**
     * Sets the value of the accountingCustomerParty property.
     *
     * @param value allowed object is {@link CustomerPartyType }
     */
    public void setAccountingCustomerParty(CustomerPartyType value) {
        this.accountingCustomerParty = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Credit or Debit Note. Billing Reference&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Billing Reference.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Credit Note&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Billing Reference&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Billing Reference&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link BillingReferenceType }
     */
    public BillingReferenceType getBillingReference() {
        return billingReference;
    }

    /**
     * Sets the value of the billingReference property.
     *
     * @param value allowed object is {@link BillingReferenceType }
     */
    public void setBillingReference(BillingReferenceType value) {
        this.billingReference = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ABIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Perception Details
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Information about a Consolidated Perception Line.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:ObjectClass&gt;Consolidated Perception Line&lt;/ccts:ObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link SUNATPerceptionDocumentReferenceType }
     */
    public SUNATPerceptionDocumentReferenceType getSUNATPerceptionDocumentReference() {
        return sunatPerceptionDocumentReference;
    }

    /**
     * Sets the value of the sunatPerceptionDocumentReference property.
     *
     * @param value allowed object is {@link SUNATPerceptionDocumentReferenceType
     *              }
     */
    public void setSUNATPerceptionDocumentReference(SUNATPerceptionDocumentReferenceType value) {
        this.sunatPerceptionDocumentReference = value;
    }

    /**
     * Gets the value of the status property.
     *
     * @return possible object is {@link StatusType }
     */
    public StatusType getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     *
     * @param value allowed object is {@link StatusType }
     */
    public void setStatus(StatusType value) {
        this.status = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;BBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Type Code.
     * 								Code
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;Code specifying the type of the Invoice.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..1&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Consolidated Invoice Line Type Code
     * 							&lt;/ccts:PropertyTerm&gt;&lt;ccts:RepresentationTerm&gt;Code&lt;/ccts:RepresentationTerm&gt;&lt;ccts:DataType&gt;Code. Type&lt;/ccts:DataType&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     *
     * @return possible object is {@link AmountType }
     */
    public AmountType getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the value of the totalAmount property.
     *
     * @param value allowed object is {@link AmountType }
     */
    public void setTotalAmount(AmountType value) {
        this.totalAmount = value;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line Billing
     * 								Payment
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Billing Payment.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Billing Payment&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Consolidated Invoice Line
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     * <p>
     * Gets the value of the billingPayment property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the billingPayment property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getBillingPayment().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentType }
     */
    public List<PaymentType> getBillingPayment() {
        if (billingPayment == null) {
            billingPayment = new ArrayList<PaymentType>();
        }
        return this.billingPayment;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line. Allowance
     * 								Charge
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Allowance Charge.
     * 							&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Allowance Charge&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Allowance Charge
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     * <p>
     * Gets the value of the allowanceCharge property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the allowanceCharge property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getAllowanceCharge().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllowanceChargeType }
     */
    public List<AllowanceChargeType> getAllowanceCharge() {
        if (allowanceCharge == null) {
            allowanceCharge = new ArrayList<AllowanceChargeType>();
        }
        return this.allowanceCharge;
    }

    /**
     * <pre>
     * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;ccts:Component xmlns:ccts="urn:un:unece:uncefact:documentation:2" xmlns="urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:qdt="urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2" xmlns:udt="urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;&lt;ccts:ComponentType&gt;ASBIE&lt;/ccts:ComponentType&gt;&lt;ccts:DictionaryEntryName&gt;Consolidated Invoice Line. Tax Total
     * 							&lt;/ccts:DictionaryEntryName&gt;&lt;ccts:Definition&gt;An association to Tax Total.&lt;/ccts:Definition&gt;&lt;ccts:Cardinality&gt;0..n&lt;/ccts:Cardinality&gt;&lt;ccts:ObjectClass&gt;Consolidated Invoice Line&lt;/ccts:ObjectClass&gt;&lt;ccts:PropertyTerm&gt;Tax Total&lt;/ccts:PropertyTerm&gt;&lt;ccts:AssociatedObjectClass&gt;Tax Total
     * 							&lt;/ccts:AssociatedObjectClass&gt;
     * 						&lt;/ccts:Component&gt;
     * </pre>
     * <p>
     * Gets the value of the taxTotal property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the taxTotal property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <p>
     * <pre>
     * getTaxTotal().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaxTotalType }
     */
    public List<TaxTotalType> getTaxTotal() {
        if (taxTotal == null) {
            taxTotal = new ArrayList<TaxTotalType>();
        }
        return this.taxTotal;
    }

    @Nonnull
    public IDType setID(@Nullable final String valueParam) {
        IDType aObj = getID();
        if (aObj == null) {
            aObj = new IDType(valueParam);
            setID(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    public LineIDType setLineID(@Nullable final String valueParam) {
        LineIDType type = getLineID();
        if (type == null) {
            type = new LineIDType(valueParam);
            setLineID(type);
        } else {
            type.setValue(valueParam);
        }
        return type;
    }

    public DocumentTypeCodeType setDocumentTypeCode(@Nullable final String valueParam) {
        DocumentTypeCodeType type = getDocumentTypeCode();
        if (type == null) {
            type = new DocumentTypeCodeType(valueParam);
            setDocumentTypeCode(type);
        } else {
            type.setValue(valueParam);
        }
        return type;
    }

    public IdentifierType setDocumentSerialID(@Nullable final String valueParam) {
        IdentifierType type = getDocumentSerialID();
        if (type == null) {
            type = new IdentifierType(valueParam);
            setDocumentSerialID(type);
        } else {
            type.setValue(valueParam);
        }
        return type;
    }

    public IdentifierType setStartDocumentNumberID(@Nullable final String valueParam) {
        IdentifierType type = getStartDocumentNumberID();
        if (type == null) {
            type = new IdentifierType(valueParam);
            setStartDocumentNumberID(type);
        } else {
            type.setValue(valueParam);
        }
        return type;
    }

    public IdentifierType setEndDocumentNumberID(@Nullable final String valueParam) {
        IdentifierType type = getEndDocumentNumberID();
        if (type == null) {
            type = new IdentifierType(valueParam);
            setEndDocumentNumberID(type);
        } else {
            type.setValue(valueParam);
        }
        return type;
    }

    public AmountType setTotalAmount(@Nullable final BigDecimal valueParam,
                                     @Nullable final String currencyID) {
        AmountType aObj = getTotalAmount();
        if (aObj == null) {
            aObj = new AmountType(valueParam);
            aObj.setCurrencyID(currencyID);
            setTotalAmount(aObj);
        } else {
            aObj.setValue(valueParam);
            aObj.setCurrencyID(currencyID);
        }
        return aObj;
    }

    public void addBillingPayment(PaymentType type) {
        getBillingPayment().add(type);
    }

    public void addAllowanceCharge(AllowanceChargeType type) {
        getAllowanceCharge().add(type);
    }

    public void addTaxTotal(TaxTotalType type) {
        getTaxTotal().add(type);
    }
}
