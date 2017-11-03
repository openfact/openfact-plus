package org.openfact.datasource.peru;


import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import org.openfact.files.*;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.utils.OpenfactModelUtils;
import org.openfact.datasource.DatasourceProvider;
import org.openfact.datasource.DatasourceType;
import org.openfact.datasource.peru.beans.DebitNoteBean;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

@Stateless
@DatasourceType(datasource = "PeruDebitNoteDS")
public class PeruDebitNoteBeanProvider implements DatasourceProvider {

    @Override
    public Object getDatasource(XmlFileModel file) throws FileFetchException {
        DebitNoteType debitNoteType;
        try {
            debitNoteType = OpenfactModelUtils.unmarshall(file.getDocument(), DebitNoteType.class);
        } catch (JAXBException e) {
            return null;
        }

        DebitNoteBean bean = new DebitNoteBean();
        return bean;
    }

}
