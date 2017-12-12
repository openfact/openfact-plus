package org.clarksnut.documents.parser.pe.summarydocuments;

import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.pe.voideddocuments.PEVoidedDocumentsParsedDocumentProvider;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PESummaryDocumentsParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void readRC_20170227_00001() throws Exception {
        InputStream is = getClass().getResourceAsStream("/peru/document/summarydocuments/RC-20170227-00001.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PESummaryDocumentsParsedDocumentProvider provider = new PESummaryDocumentsParsedDocumentProvider();
        ParsedDocument parsedDocument = provider.read(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof SummaryDocumentsType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        SkeletonDocument skeleton = parsedDocument.getSkeleton();

        assertThat(skeleton.getType()).isEqualTo("SummaryDocuments");
        assertThat(skeleton.getAssignedId()).isEqualTo("RC-20170227-00001");
        assertThat(skeleton.getAmount()).isNull();
        assertThat(skeleton.getTax()).isNull();
        assertThat(skeleton.getCurrency()).isNull();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        assertThat(skeleton.getIssueDate()).isEqualTo(calendar.getTime());

        assertThat(skeleton.getSupplierName()).isEqualTo("AHREN CONTRATISTAS GENERALES S.A.C");
        assertThat(skeleton.getSupplierAssignedId()).isEqualTo("20494637074");
        assertThat(skeleton.getSupplierStreetAddress()).isNull();
        assertThat(skeleton.getSupplierCity()).isNull();
        assertThat(skeleton.getSupplierCountry()).isNull();

        assertThat(skeleton.getCustomerName()).isNull();
        assertThat(skeleton.getCustomerAssignedId()).isNull();
        assertThat(skeleton.getCustomerStreetAddress()).isNull();
        assertThat(skeleton.getCustomerCity()).isNull();
        assertThat(skeleton.getCustomerCountry()).isNull();
    }

}