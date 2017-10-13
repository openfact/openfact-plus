package org.openfact.documents.reader.pe.invoice;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "pe_invoice_line")
public class PEInvoiceLineEntity {

    @Id
    @Access(AccessType.PROPERTY)// Relationships often fetch id, but not entity.  This avoids an extra SQL
    @Column(name = "id", length = 36)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey, name = "invoice_id")
    private PEInvoiceEntity invoice;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "extension_amount")
    private BigDecimal extensionAmount;

    @Column(name = "extension_amount_currency_id")
    private String extensionAmountCurrencyId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PEInvoiceEntity  getInvoice() {
        return invoice;
    }

    public void setInvoice(PEInvoiceEntity  invoice) {
        this.invoice = invoice;
    }

    /**
     * Quantity
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    /**
     * Extension Amount
     */
    public BigDecimal getExtensionAmount() {
        return extensionAmount;
    }

    public void setExtensionAmount(BigDecimal extensionAmount) {
        this.extensionAmount = extensionAmount;
    }

    public String getExtensionAmountCurrencyId() {
        return extensionAmountCurrencyId;
    }

    public void setExtensionAmountCurrencyId(String extensionAmountCurrencyId) {
        this.extensionAmountCurrencyId = extensionAmountCurrencyId;
    }
}
