package org.openfact.datasource.peru;

import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.CreditNoteBean;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(region = "peru", documentType = "CreditNote")
public class PeruCreditNoteBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        CreditNoteType creditNoteType;
        try {
            creditNoteType = OpenfactModelUtils.unmarshall(file.getDocument(), CreditNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        CreditNoteBean bean = new CreditNoteBean();
        return bean;
    }

}
