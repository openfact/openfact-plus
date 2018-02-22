package org.clarksnut.models;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum DocumentType {

    APPLICATION_RESPONSE("ApplicationResponse"),
    ATTACHED_DOCUMENT("AttachedDocument"),
    AWARDED_NOTIFICATION("AwardedNotification"),
    BILL_OF_LADING("BillOfLading"),
    CALL_FOR_TENDERS("CallForTenders"),
    CATALOGUE("Catalogue"),
    CATALOGUE_DELETION("CatalogueDeletion"),
    CATALOGUE_ITEM_SPECIFICATION_UPDATE("CatalogueItemSpecificationUpdate"),
    CATALOGUE_PRICING_UPDATE("CataloguePricingUpdate"),
    CATALOGUE_REQUEST("CatalogueRequest"),
    CERTIFICATE_OF_ORIGIN("CertificateOfOrigin"),
    CONTRACT_AWARD_NOTICE("ContractAwardNotice"),
    CONTRACT_NOTICE("ContractNotice"),
    CREDIT_NOTE("CreditNote"),
    DEBIT_NOTE("DebitNote"),
    DESPATCH_ADVICE("DespatchAdvice"),
    DOCUMENTS_STATUS("DocumentStatus"),
    DOCUMENTS_STATUS_REQUEST("DocumentStatusRequest"),
    EXCEPTION_CRITERIA("ExceptionCriteria"),
    EXCEPTION_NOTIFICATION("ExceptionNotification"),
    FORECAST("Forecast"),
    FORECAST_REVISION("ForecastRevision"),
    FORWARDING_INSTRUCTIONS("ForwardingInstructions"),
    FREIGHT_INVOICE("FreightInvoice"),
    FULFILMENT_CANCELLATION("FulfilmentCancellation"),
    GOODS_ITEM_ITINERARY("GoodsItemItinerary"),
    GUARANTEE_CERTIFICATE("GuaranteeCertificate"),
    INSTRUCTION_FOR_RETURNS("InstructionForReturns"),
    INVENTORY_REPORT("InventoryReport"),
    INVOICE("Invoice"),
    ITEM_INFORMATION_REQUEST("ItemInformationRequest"),
    ORDER("Order"),
    ORDER_CANCELLATION("OrderCancellation"),
    ORDER_CHANGE("OrderChange"),
    ORDER_RESPONSE("OrderResponse"),
    ORDER_RESPONSE_SIMPLE("OrderResponseSimple"),
    PACKING_LIST("PackingList"),
    PRIOR_INFORMATION_NOTICE("PriorInformationNotice"),
    PRODUCT_ACTIVITY("ProductActivity"),
    QUOTATION("Quotation"),
    RECEIPT_ADVICE("ReceiptAdvice"),
    REMINDER("Reminder"),
    REMITTANCE_ADVICE("RemittanceAdvice"),
    REQUEST_FOR_QUOTATION("RequestForQuotation"),
    RETAIL_EVENT("RetailEvent"),
    SELF_BILLED_CREDIT_NOTE("SelfBilledCreditNote"),
    SELF_BILLED_CREDIT_INVOICE("SelfBilledInvoice"),
    STATEMENT("Statement"),
    STOCK_AVAILABILITY_REPORT("StockAvailabilityReport"),
    TENDER("Tender"),
    TENDERER_QUALIFICATION("TendererQualification"),
    TENDERER_QUALIFICATION_RESPONSE("TendererQualificationResponse"),
    TENDER_RECEIPT("TenderReceipt"),
    TRADE_ITEM_LOCATION_PROFILE("TradeItemLocationProfile"),
    TRANSPORTATION_STATUS("TransportationStatus"),
    TRANSPORTATION_STATUS_REQUEST("TransportationStatusRequest"),
    TRANSPORT_EXECUTION_PLAN("TransportExecutionPlan"),
    TRANSPORT_EXECUTION_PLAN_REQUEST("TransportExecutionPlanRequest"),
    TRANSPORT_PROGRESS_STATUS("TransportProgressStatus"),
    TRANSPORT_PROGRESS_STATUS_REQUEST("TransportProgressStatusRequest"),
    TRANSPORT_SERVICE_DESCRIPTION("TransportServiceDescription"),
    TRANSPORT_SERVICE_DESCRIPTION_REQUEST("TransportServiceDescriptionRequest"),
    UNAWARDED_NOTIFICATION("UnawardedNotification"),
    UTILITY_STATEMENT("UtilityStatement"),
    WAYBILL("Waybill");

    private String type;

    DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static List<String> typeValues() {
        return Arrays.stream(DocumentType.values())
                .map(DocumentType::getType)
                .collect(Collectors.toList());
    }

    public static Optional<DocumentType> getByType(String type) {
        return Arrays.stream(DocumentType.values())
                .filter(f -> f.getType().equals(type))
                .findFirst();
    }

}
