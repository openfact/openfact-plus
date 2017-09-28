package org.openfact.models.db.es.reader.pe;

import com.helger.ubl21.UBL21NamespaceContext;
import com.helger.xml.namespace.MapBasedNamespaceContext;

public class MarshallerUtils {

    public static MapBasedNamespaceContext getBasedNamespaceContext(String defaultNamespace) {
        UBL21NamespaceContext namespace = UBL21NamespaceContext.getInstance();
        namespace.setMapping("ccts", "urn:un:unece:uncefact:documentation:2");
        namespace.setMapping("ds", "http://www.w3.org/2000/09/xmldsig#");
        namespace.setMapping("ext", "urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2");
        namespace.setMapping("qdt", "urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2");
        namespace.setMapping("sac", "urn:sunat:names:specification:ubl:peru:schema:xsd:SunatAggregateComponents-1");
        namespace.setMapping("udt", "urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2");
        MapBasedNamespaceContext mapBasedNamespace = new MapBasedNamespaceContext();
        mapBasedNamespace.addMappings(namespace);
        mapBasedNamespace.setDefaultNamespaceURI(defaultNamespace);
        return mapBasedNamespace;
    }

}
