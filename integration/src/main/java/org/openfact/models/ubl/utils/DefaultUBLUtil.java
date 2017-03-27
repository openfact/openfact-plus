package org.openfact.models.ubl.utils;

import org.jboss.logging.Logger;
import org.openfact.Config;
import org.openfact.models.ubl.UBLCustomizator;
import org.openfact.models.ubl.UBLReaderWriter;
import org.openfact.models.ubl.ubl21.qualifiers.UBLDocumentType;
import org.openfact.models.utils.UBLUtil;
import org.openfact.provider.ProviderType;

import javax.ejb.Stateless;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.lang.annotation.Annotation;

@Stateless
public class DefaultUBLUtil implements UBLUtil {

    private static final Logger logger = Logger.getLogger(DefaultUBLUtil.class);

    public static final String READER_WRITER = "readerWriter";
    public static final String ID_GENERATOR = "idGenerator";
    public static final String MODEL_CUSTOMIZATION = "modelCustomization";
    public static final String THIRD_PARTY_SENDER = "thirdPartySender";

    @Inject
    @Any
    private Instance<UBLReaderWriter> readerWriterProviders;

    @Inject
    @Any
    private Instance<UBLCustomizator> customizationProviders;


    @Override
    public UBLReaderWriter getReaderWriter(String documentType) {
        return getReaderWriter(Config.scope(documentType.toLowerCase()).get(READER_WRITER, "default"), documentType);
    }

    @Override
    public UBLReaderWriter getReaderWriter(String providerType, String documentType) {
        Annotation providerTypeLiteral = new ProviderTypeLiteral(providerType);
        Annotation documentTypeLiteral = new DocumentTypeLiteral(documentType);

        Instance<UBLReaderWriter> instance = readerWriterProviders.select(providerTypeLiteral, documentTypeLiteral);
        if (!instance.isAmbiguous() && !instance.isUnsatisfied()) {
            return readerWriterProviders.select(providerTypeLiteral, documentTypeLiteral).get();
        }

        logger.warn("Insufficient information to get an instance of " + UBLReaderWriter.class.getSimpleName());
        return null;
    }

    @Override
    public UBLCustomizator getCustomizationProvider(String documentType) {
        return getCustomizationProvider(Config.scope(documentType.toLowerCase()).get(MODEL_CUSTOMIZATION, "default"), documentType);
    }

    @Override
    public UBLCustomizator getCustomizationProvider(String providerType, String documentType) {
        Annotation providerTypeLiteral = new ProviderTypeLiteral(providerType);
        Annotation documentTypeLiteral = new DocumentTypeLiteral(documentType);

        Instance<UBLCustomizator> instance = customizationProviders.select(providerTypeLiteral, documentTypeLiteral);
        if (!instance.isAmbiguous() && !instance.isUnsatisfied()) {
            return customizationProviders.select(providerTypeLiteral, documentTypeLiteral).get();
        }

        logger.warn("Insufficient information to get an instance of " + UBLCustomizator.class.getSimpleName());
        return null;
    }

    /**
     * Literals
     */
    public class ProviderTypeLiteral extends AnnotationLiteral<ProviderType> implements ProviderType {
        private final String value;

        public ProviderTypeLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    public class DocumentTypeLiteral extends AnnotationLiteral<UBLDocumentType> implements UBLDocumentType {
        private final String value;

        public DocumentTypeLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
