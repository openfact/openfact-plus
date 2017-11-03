package org.openfact.datasource;

import com.helger.ubl21.UBL21Reader;
import oasis.names.specification.ubl.schema.xsd.debitnote_21.DebitNoteType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.datasource.beans.DebitNoteBean;

import javax.ejb.Stateless;

@Stateless
@DatasourceType(datasource = "BasicDebitNoteDS")
public class BasicDebitNoteDatasourceProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        DebitNoteType debitNoteType = UBL21Reader.debitNote().read(file.getFile());
        if (debitNoteType == null) {
            return null;
        }

        DebitNoteBean bean = new DebitNoteBean();
        return bean;
    }

}
