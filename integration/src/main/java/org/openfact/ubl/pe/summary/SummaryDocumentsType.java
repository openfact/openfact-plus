package org.openfact.ubl.pe.summary;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SignatureType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.SupplierPartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_21.UBLExtensionsType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryDocumentsType", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SummaryDocuments-1", propOrder = {
        "ublExtensions", "ublVersionID", "customizationID", "id", "documentCurrencyCode",
        "referenceDate", "issueDate", "signature", "accountingSupplierParty", "summaryDocumentsLine"

})
@CodingStyleguideUnaware
public class SummaryDocumentsType implements Serializable, Cloneable {

    @XmlElement(name = "UBLExtensions", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2")
    protected UBLExtensionsType ublExtensions;

    @XmlElement(name = "UBLVersionID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected UBLVersionIDType ublVersionID;

    @XmlElement(name = "CustomizationID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected CustomizationIDType customizationID;

    @XmlElement(name = "ID", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected IDType id;

    @XmlElement(name = "DocumentCurrencyCode", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    private DocumentCurrencyCodeType documentCurrencyCode;

    @XmlElement(name = "ReferenceDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2")
    protected ReferenceDateType referenceDate;

    @XmlElement(name = "IssueDate", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2", required = true)
    protected IssueDateType issueDate;

    @XmlElement(name = "Signature", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected List<SignatureType> signature;

    @XmlElement(name = "AccountingSupplierParty", namespace = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2")
    protected SupplierPartyType accountingSupplierParty;

    @XmlElement(name = "SummaryDocumentsLine", namespace = "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1" )
    protected List<SummaryDocumentsLineType> summaryDocumentsLine;

    public UBLExtensionsType getUblExtensions() {
        return ublExtensions;
    }

    public void setUblExtensions(UBLExtensionsType ublExtensions) {
        this.ublExtensions = ublExtensions;
    }

    public UBLVersionIDType getUblVersionID() {
        return ublVersionID;
    }

    public void setUblVersionID(UBLVersionIDType ublVersionID) {
        this.ublVersionID = ublVersionID;
    }

    public CustomizationIDType getCustomizationID() {
        return customizationID;
    }

    public void setCustomizationID(CustomizationIDType customizationID) {
        this.customizationID = customizationID;
    }

    public IDType getId() {
        return id;
    }

    public void setId(IDType id) {
        this.id = id;
    }

    public IssueDateType getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(IssueDateType issueDate) {
        this.issueDate = issueDate;
    }

    public DocumentCurrencyCodeType getDocumentCurrencyCode() {
        return documentCurrencyCode;
    }

    public void setDocumentCurrencyCode(DocumentCurrencyCodeType documentCurrencyCode) {
        this.documentCurrencyCode = documentCurrencyCode;
    }

    public ReferenceDateType getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(ReferenceDateType referenceDate) {
        this.referenceDate = referenceDate;
    }

    public List<SignatureType> getSignature() {
        if (signature == null) {
            signature = new ArrayList<>();
        }
        return signature;
    }

    public SupplierPartyType getAccountingSupplierParty() {
        return accountingSupplierParty;
    }

    public void setAccountingSupplierParty(SupplierPartyType accountingSupplierParty) {
        this.accountingSupplierParty = accountingSupplierParty;
    }

    public List<SummaryDocumentsLineType> getSummaryDocumentsLine() {
        if (summaryDocumentsLine == null) {
            summaryDocumentsLine = new ArrayList<>();
        }
        return summaryDocumentsLine;
    }

    @Nonnull
    public UBLVersionIDType setUblVersionID(@Nullable final String valueParam) {
        UBLVersionIDType aObj = getUblVersionID();
        if (aObj == null) {
            aObj = new UBLVersionIDType(valueParam);
            setUblVersionID(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    @Nonnull
    public CustomizationIDType setCustomizationID(@Nullable final String valueParam) {
        CustomizationIDType aObj = getCustomizationID();
        if (aObj == null) {
            aObj = new CustomizationIDType(valueParam);
            setCustomizationID(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    public void addSignature(@Nonnull final SignatureType elem) {
        getSignature().add(elem);
    }

    public void addSummaryDocumentsLine(@Nonnull final SummaryDocumentsLineType elem) {
        getSummaryDocumentsLine().add(elem);
    }

    @Nonnull
    public IDType setId(@Nullable final String valueParam) {
        IDType aObj = getId();
        if (aObj == null) {
            aObj = new IDType(valueParam);
            setId(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    @Nonnull
    public IssueDateType setIssueDate(@Nullable final XMLGregorianCalendar valueParam) {
        IssueDateType aObj = getIssueDate();
        if (aObj == null) {
            aObj = new IssueDateType(valueParam);
            setIssueDate(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    @Nonnull
    public ReferenceDateType setReferenceDateTime(@Nullable final XMLGregorianCalendar valueParam) {
        ReferenceDateType aObj = getReferenceDate();
        if (aObj == null) {
            aObj = new ReferenceDateType(valueParam);
            setReferenceDate(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }

    @Nonnull
    public DocumentCurrencyCodeType setDocumentCurrencyCode(@Nullable final String valueParam) {
        DocumentCurrencyCodeType aObj = getDocumentCurrencyCode();
        if (aObj == null) {
            aObj = new DocumentCurrencyCodeType(valueParam);
            setDocumentCurrencyCode(aObj);
        } else {
            aObj.setValue(valueParam);
        }
        return aObj;
    }
}
