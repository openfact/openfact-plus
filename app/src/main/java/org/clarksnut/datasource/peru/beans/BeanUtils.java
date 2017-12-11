package org.clarksnut.datasource.peru.beans;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.AdditionalAccountIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.CustomerAssignedAccountIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueDateType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.ExtensionContentType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;
import org.clarksnut.datasource.peru.types.ConceptosTributarios;
import org.clarksnut.datasource.peru.types.TipoDocumentoEntidad;
import org.clarksnut.datasource.peru.types.TipoPrecioVentaUnitario;
import org.clarksnut.datasource.peru.types.TipoTributo;
import org.clarksnut.models.utils.ClarksnutModelUtils;
import org.w3c.dom.Document;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.AdditionalInformationType;
import sunat.names.specification.ubl.peru.schema.xsd.sunataggregatecomponents_1.AdditionalMonetaryTotalType;

import javax.xml.bind.JAXBException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BeanUtils {

    public static ProveedorBean toSupplier(SupplierPartyType supplierPartyType) {
        ProveedorBean bean = new ProveedorBean();

        bean.setIdAssignado(supplierPartyType.getCustomerAssignedAccountID().getValue());
        bean.setNombre(supplierPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());
        TipoDocumentoEntidad.getByCode(supplierPartyType.getAdditionalAccountID().get(0).getValue()).ifPresent(c -> {
            bean.setTipoDocumento(c.getDenominacion());
        });
        bean.setDireccion(toAddress(supplierPartyType.getParty().getPostalAddress()));

        return bean;
    }

    public static ClienteBean toCustomer(CustomerPartyType customerPartyType) {
        ClienteBean bean = new ClienteBean();

        // Assigned ID is not always present
        CustomerAssignedAccountIDType customerAssignedAccountID = customerPartyType.getCustomerAssignedAccountID();
        if (customerAssignedAccountID != null) {
            bean.setIdAssignado(customerAssignedAccountID.getValue());
        }

        bean.setNombre(customerPartyType.getParty().getPartyLegalEntity().get(0).getRegistrationName().getValue());

        // Document Type
        List<AdditionalAccountIDType> additionalAccountIDTypes = customerPartyType.getAdditionalAccountID();
        if (additionalAccountIDTypes != null && !additionalAccountIDTypes.isEmpty()) {
            TipoDocumentoEntidad.getByCode(additionalAccountIDTypes.get(0).getValue()).ifPresent(c -> {
                bean.setTipoDocumento(c.getDenominacion());
            });
        }

        // Address is optional for customers
        AddressType addressType = customerPartyType.getParty().getPostalAddress();
        if (addressType != null) {
            bean.setDireccion(toAddress(addressType));
        }

        return bean;
    }

    public static PostalAddressBean toAddress(AddressType addressType) {
        PostalAddressBean bean = new PostalAddressBean();

        bean.setId(addressType.getID().getValue());
        bean.setStreetName(addressType.getStreetName().getValue());
        bean.setCitySubdivisionName(addressType.getCitySubdivisionName().getValue());
        bean.setCityName(addressType.getCityName().getValue());
        bean.setCountrySubentity(addressType.getCountrySubentity().getValue());

        // Country might not being present
        CountryType countryType = addressType.getCountry();
        if (countryType != null) {
            bean.setCountryIdentificationCode(countryType.getIdentificationCode().getValue());
        }

        return bean;
    }

    public static TributosBean toTributos(List<TaxTotalType> taxTotal) {
        TributosBean bean = new TributosBean();
        for (TaxTotalType taxTotalType : taxTotal) {
            TipoTributo.getFromCode(taxTotalType.getTaxSubtotal().get(0).getTaxCategory().getTaxScheme().getID().getValue()).ifPresent(c -> {
                switch (c) {
                    case IGV:
                        bean.setTotalIgv(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                    case ISC:
                        bean.setTotalIsc(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                    case OTROS:
                        bean.setTotalOtrosTributos(taxTotalType.getTaxAmount().getValue().floatValue());
                        break;
                }
            });
        }
        return bean;
    }

    public static InformacionAdicionalBean toInformacionAdicional(UBLExtensionsType ublExtensionsType) {
        InformacionAdicionalBean bean = new InformacionAdicionalBean();
        for (UBLExtensionType ublExtensionType : ublExtensionsType.getUBLExtension()) {
            ExtensionContentType extensionContent = ublExtensionType.getExtensionContent();
            Document extensionContentDocument = extensionContent.getAny().getOwnerDocument();
            try {
                AdditionalInformationType additionalInformationType = ClarksnutModelUtils.unmarshall(extensionContentDocument, AdditionalInformationType.class);
                for (AdditionalMonetaryTotalType additionalMonetaryTotalType : additionalInformationType.getAdditionalMonetaryTotal()) {
                    Optional<ConceptosTributarios> optional = ConceptosTributarios.getByCode(additionalMonetaryTotalType.getID().getValue());
                    optional.ifPresent(c -> {
                        switch (c) {
                            case TOTAL_VALOR_VENTA_OPERACIONES_GRAVADAS:
                                bean.setTotalGravada(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_INAFECTAS:
                                bean.setTotalInafecta(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_EXONERADAS:
                                bean.setTotalExonerada(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_VENTA_OPERACIONES_GRATUITAS:
                                bean.setTotalGratuita(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                            case TOTAL_VALOR_DESCUENTOS:
                                bean.setTotalDescuentos(additionalMonetaryTotalType.getPayableAmount().getValue().floatValue());
                                break;
                        }
                    });
                }
                break;
            } catch (JAXBException e) {
                // No problem
            }
        }

        return bean;
    }

    public static Date toDate(IssueDateType issueDate, Optional<IssueTimeType> issueTimeType) {
        Date date = issueDate.getValue().toGregorianCalendar().getTime();
        if (issueTimeType.isPresent()) {
            Date time = issueTimeType.get().getValue().toGregorianCalendar().getTime();

            Calendar issueDateCalendar = Calendar.getInstance();
            issueDateCalendar.setTime(date);

            Calendar issueTimeCalendar = Calendar.getInstance();
            issueTimeCalendar.setTime(time);

            issueDateCalendar.set(Calendar.HOUR_OF_DAY, issueTimeCalendar.get(Calendar.HOUR_OF_DAY));
            issueDateCalendar.set(Calendar.MINUTE, issueTimeCalendar.get(Calendar.MINUTE));
            issueDateCalendar.set(Calendar.SECOND, issueTimeCalendar.get(Calendar.SECOND));

            date = issueDateCalendar.getTime();
        }
        return date;
    }

    // Line utils

    public static void agregarPrecioUnitario(LineBean lineBean, Optional<List<PriceType>> priceTypes) {
        if (priceTypes.isPresent()) {
            for (PriceType priceType : priceTypes.get()) {
                TipoPrecioVentaUnitario.getByCode(priceType.getPriceTypeCode().getValue()).ifPresent(c -> {
                    switch (c) {
                        case PRECIO_UNITARIO:
                            lineBean.setPrecioVentaUnitario(priceType.getPriceAmount().getValue().floatValue());
                            break;
                        case VALOR_REFERENCIAL_UNITARIO_EN_OPERACIONES_NO_ONEROSAS:
                            lineBean.setValorReferencialUnitarioEnOperacionesNoOnerosas(priceType.getPriceAmount().getValue().floatValue());
                            break;
                    }
                });
            }
        }
    }

    public static void agregarDescripcionYCodigo(LineBean lineBean, Optional<ItemType> itemType) {
        if (itemType.isPresent()) {
            lineBean.setDescripcion(itemType.get().getDescription().get(0).getValue());

            ItemIdentificationType itemIdentificationType = itemType.get().getSellersItemIdentification();
            if (itemIdentificationType != null) {
                lineBean.setCodidoProducto(itemIdentificationType.getID().getValue());
            }
        }
    }

    public static void agregarImpuestosEnLine(LineBean lineBean, Optional<List<TaxTotalType>> taxTotalTypes) {
        if (taxTotalTypes.isPresent()) {
            for (TaxTotalType taxTotalType : taxTotalTypes.get()) {
                TipoTributo.getFromCode(taxTotalType.getTaxSubtotal().get(0).getTaxCategory().getTaxScheme().getID().getValue()).ifPresent(c -> {
                    switch (c) {
                        case IGV:
                            lineBean.setTotalIgv(taxTotalType.getTaxAmount().getValue().floatValue());
                            break;
                        case ISC:
                            lineBean.setTotalIsc(taxTotalType.getTaxAmount().getValue().floatValue());
                            break;
                    }
                });
            }
        }
    }

}
