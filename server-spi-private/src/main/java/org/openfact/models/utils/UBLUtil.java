package org.openfact.models.utils;

import org.openfact.models.ubl.UBLCustomizator;
import org.openfact.models.ubl.UBLReaderWriter;

public interface UBLUtil {

    UBLReaderWriter getReaderWriter(String documentType);
    UBLReaderWriter getReaderWriter(String providerType, String documentType);

    UBLCustomizator getCustomizationProvider(String documentType);
    UBLCustomizator getCustomizationProvider(String providerType, String documentType);

}
