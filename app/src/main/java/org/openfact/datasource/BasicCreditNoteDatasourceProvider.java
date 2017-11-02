package org.openfact.datasource;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.datasource.beans.CreditNoteBean;

import javax.ejb.Stateless;

@Stateless
@DatasourceType(region = "basic", documentType = "CreditNote")
public class BasicCreditNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        CreditNoteType creditNoteType = UBL21Reader.creditNote().read(file.getFile());
        if (creditNoteType == null) {
            return null;
        }

        CreditNoteBean bean = new CreditNoteBean();
        return bean;
    }

}
