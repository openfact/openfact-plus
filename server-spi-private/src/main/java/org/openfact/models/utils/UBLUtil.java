package org.openfact.models.utils;

import org.openfact.models.ubl.UBLCustomizator;

public interface UBLUtil {

    UBLCustomizator getCustomizationProvider(String documentType);
    UBLCustomizator getCustomizationProvider(String providerType, String documentType);

}
