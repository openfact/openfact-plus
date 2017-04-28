package org.openfact.ubl.pe.factories;

import org.openfact.models.ModelRuntimeException;
import org.openfact.ubl.pe.perception.PerceptionFactory;
import org.openfact.ubl.pe.perception.PerceptionType;
import org.openfact.ubl.pe.retention.RetentionFactory;
import org.openfact.ubl.pe.retention.RetentionType;
import org.openfact.ubl.pe.summary.SummaryDocumentFactory;
import org.openfact.ubl.pe.summary.SummaryDocumentsType;
import org.openfact.ubl.pe.voided.VoidedDocumentFactory;
import org.openfact.ubl.pe.voided.VoidedDocumentsType;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class SunatDocumentToType {

    public static VoidedDocumentsType toVoidedDocumentsType(Document document) {
        try {
            JAXBContext factory = JAXBContext.newInstance(VoidedDocumentFactory.class);
            Unmarshaller unmarshal = factory.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<VoidedDocumentsType> jaxbVoidedDocumentsType = (JAXBElement<VoidedDocumentsType>) unmarshal.unmarshal(document);
            VoidedDocumentsType voidedDocumentsType = jaxbVoidedDocumentsType.getValue();
            return voidedDocumentsType;
        } catch (JAXBException e) {
            throw new ModelRuntimeException(e);
        }
    }

    public static RetentionType toRetentionType(Document document) {
        try {
            JAXBContext factory = JAXBContext.newInstance(RetentionFactory.class);
            Unmarshaller unmarshal = factory.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<RetentionType> jaxbRetentionType = (JAXBElement<RetentionType>) unmarshal.unmarshal(document);
            RetentionType retentionType = jaxbRetentionType.getValue();
            return retentionType;
        } catch (JAXBException e) {
            throw new ModelRuntimeException(e);
        }
    }

    public static SummaryDocumentsType toSummaryDocumentsType(Document document) {
        try {
            JAXBContext factory = JAXBContext.newInstance(SummaryDocumentFactory.class);
            Unmarshaller unmarshal = factory.createUnmarshaller();

            @SuppressWarnings("unchecked")
            JAXBElement<SummaryDocumentsType> jaxbSummaryDocumentsType = (JAXBElement<SummaryDocumentsType>) unmarshal.unmarshal(document);
            SummaryDocumentsType summaryDocumentsType = jaxbSummaryDocumentsType.getValue();
            return summaryDocumentsType;
        } catch (JAXBException e) {
            throw new ModelRuntimeException(e);
        }
    }

    public static PerceptionType toPerceptionType(Document document) {
        try {
            JAXBContext factory = JAXBContext.newInstance(PerceptionFactory.class);
            Unmarshaller unmarshal = factory.createUnmarshaller();


            @SuppressWarnings("unchecked")
            JAXBElement<PerceptionType> jaxbPerceptionType = (JAXBElement<PerceptionType>) unmarshal.unmarshal(document);
            PerceptionType perceptionType = jaxbPerceptionType.getValue();
            return perceptionType;
        } catch (JAXBException e) {
            throw new ModelRuntimeException(e);
        }
    }
}
