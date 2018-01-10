package org.clarksnut.report.jasper;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelTest {

//    @Test
//    public void test() throws IOException, JRException {
//        JasperReport jr = getReport();
//
//        ClassLoader classLoader = getClass().getClassLoader();
//        URL logoUrl = classLoader.getResource("report/base/model/resources/logo.png");
//        URL headerTopLeftUrl = classLoader.getResource("report/clarksnut/model/resources/header_top_left.png");
//        URL backgroundUrl = classLoader.getResource("report/clarksnut/model/resources/background.png");
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("LOGO_URL", ImageIO.read(logoUrl));
//        parameters.put("HEADER_TOP_LEFT_URL", ImageIO.read(headerTopLeftUrl));
//        parameters.put("BACKGROUND_URL", ImageIO.read(backgroundUrl));
//
//        //
//        Locale locale = new Locale("es");
//        URL resourceBundleURL = classLoader.getResource("report/clarksnut/model/messages/messages_es.properties");
//        ResourceBundle messages = new PropertyResourceBundle(resourceBundleURL.openStream());
//
//        parameters.put(JRParameter.REPORT_LOCALE, locale);
//        parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, messages);
//
//        JRDataSource dataSource = new JRBeanCollectionDataSource(getDatasource(), false);
//        JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
//
//        assertThat(jp).isNotNull();
//
//        OutputStream output = new FileOutputStream(new File("/home/admin/Documents/JasperReport.pdf"));
//        JasperExportManager.exportReportToPdfStream(jp, output);
//    }
//
//    public JasperReport getReport() throws IOException, JRException {
//        ClassLoader classLoader = getClass().getClassLoader();
//        URL url = classLoader.getResource("report/clarksnut/model/model.jrxml");
//        return JasperCompileManager.compileReport(url.openStream());
//    }

//    public List<ModelDatasource> getDatasource() {
//        List<ModelDatasource> models = new ArrayList<>();
//        ModelDatasource model1 = new ModelDatasource();
//        model1.setId(UUID.randomUUID().toString());
//        model1.setType("Invoice");
//        model1.setAssignedId("BB12-3");
//        model1.setTax(10F);
//        model1.setAmount(100F);
//        model1.setCurrency("PEN");
//        model1.setIssueDate(Calendar.getInstance().getTime());
//        model1.setSupplierName("Sistcoop S.A.C.");
//        model1.setSupplierAssignedId("10467793549");
//        model1.setSupplierStreetAddress("Jr. Mariano Melgar 997");
//        model1.setSupplierCity("0051 Ayacucho, Huamanga, Ayacucho");
//        model1.setSupplierCountry("PE");
//        model1.setCustomerName("Feria Carlos E.");
//        model1.setCustomerAssignedId("46779354");
//        model1.setCustomerStreetAddress("Jr. Los alamos 158");
//        model1.setCustomerCity("0051 Jesus Nazareno, Huamanga, Ayacucho");
//        model1.setCustomerCountry("CO");
//        model1.setLocation("www.clarksnut.com/documents/1");
//
//        ModelDatasource model2 = new ModelDatasource();
//        model2.setId(UUID.randomUUID().toString());
//        model2.setType("Credit Note");
//        model2.setAssignedId("CN12-3");
//        model2.setAmount(1000F);
//        model2.setCurrency("PEN");
//        model2.setIssueDate(Calendar.getInstance().getTime());
//        model2.setSupplierName("Sistcoop S.A.C.");
//        model2.setSupplierAssignedId("10467793549");
//        model2.setCustomerName("Feria Carlos E.");
//        model2.setCustomerAssignedId("46779354");
//        model2.setLocation("www.clarksnut.com/documents/2");
//
//        models.add(model1);
//        models.add(model2);
//
//        return models;
//    }

}
