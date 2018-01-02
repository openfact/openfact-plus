package org.clarksnut.documents.parser.pe.retention;

import org.clarksnut.documents.parser.ParsedDocument;
import org.clarksnut.documents.parser.SkeletonDocument;
import org.clarksnut.documents.parser.pe.perception.PEPerceptionParsedDocumentProvider;
import org.clarksnut.files.XmlUBLFileModel;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openfact.perception.PerceptionType;
import org.openfact.retention.RetentionType;

import java.io.InputStream;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PERetentionParsedDocumentProviderTest {

    @Mock
    private XmlUBLFileModel file;

    @Test
    public void read() throws Exception {
        InputStream is = getClass().getResourceAsStream("/peru/document/retention/R001-123.xml");

        Mockito.when(this.file.getDocument()).thenReturn(ClarksnutModelUtils.toDocument(is));

        PERetentionParsedDocumentProvider provider = new PERetentionParsedDocumentProvider();
        ParsedDocument parsedDocument = provider.read(file);

        Mockito.verify(this.file, Mockito.atLeastOnce()).getDocument();


        assertThat(parsedDocument).isNotNull();
        assertThat(parsedDocument.getType() instanceof RetentionType).isEqualTo(true);
        assertThat(parsedDocument.getType()).isNotNull();


        SkeletonDocument skeleton = parsedDocument.getSkeleton();

        assertThat(skeleton.getType()).isEqualTo("Retention");
        assertThat(skeleton.getAssignedId()).isEqualTo("R001-123");
        assertThat(skeleton.getAmount()).isEqualTo(1_144.60F);
        assertThat(skeleton.getTax()).isNull();
        assertThat(skeleton.getCurrency()).isEqualTo("PEN");

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        assertThat(skeleton.getIssueDate()).isEqualTo(calendar.getTime());

        assertThat(skeleton.getSupplierName()).isEqualTo("ENTERPRISE SOLUTIONS S.A.");
        assertThat(skeleton.getSupplierAssignedId()).isEqualTo("20382567855");
        assertThat(skeleton.getSupplierStreetAddress()).isEqualTo("CALLE TIAHUANACO 146");
        assertThat(skeleton.getSupplierCity()).isEqualTo("SAN MIGUEL, LIMA, LIMA");
        assertThat(skeleton.getSupplierCountry()).isEqualTo("PE");

        assertThat(skeleton.getCustomerName()).isEqualTo("FABRICACIONES ONUR SAC");
        assertThat(skeleton.getCustomerAssignedId()).isEqualTo("20101295673");
        assertThat(skeleton.getCustomerStreetAddress()).isEqualTo("AV. ABANCAY 1023");
        assertThat(skeleton.getCustomerCity()).isEqualTo("LIMA, LIMA, LIMA");
        assertThat(skeleton.getCustomerCountry()).isEqualTo("PE");
    }

}