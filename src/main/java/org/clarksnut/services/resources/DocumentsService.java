package org.clarksnut.services.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.clarksnut.files.FileModel;
import org.clarksnut.files.uncompress.exceptions.NotReadableCompressFileException;
import org.clarksnut.managers.ImportedDocumentManager;
import org.clarksnut.models.DocumentModel;
import org.clarksnut.models.DocumentProviderType;
import org.clarksnut.models.exceptions.IsNotXmlOrCompressedFileDocumentException;
import org.clarksnut.report.ExportFormat;
import org.clarksnut.report.ReportTemplateConfiguration;
import org.clarksnut.report.ReportTemplateProvider;
import org.clarksnut.report.exceptions.ReportException;
import org.clarksnut.representations.idm.DocumentRepresentation;
import org.clarksnut.services.ErrorResponse;
import org.clarksnut.services.ErrorResponseException;
import org.clarksnut.utils.ModelToRepresentation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Stateless
@Path("/api/documents")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Documents", description = "Document REST API", consumes = "application/json")
public class DocumentsService extends AbstractResource {

    private static final Logger logger = Logger.getLogger(DocumentsService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private ImportedDocumentManager importedDocumentManager;

    @Inject
    private ModelToRepresentation modelToRepresentation;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Import Document")
    public Response importDocument(final MultipartFormDataInput multipartFormDataInput) throws ErrorResponseException {
        Map<String, List<InputPart>> formParts = multipartFormDataInput.getFormDataMap();
        List<InputPart> inputParts = formParts.get("file");

        for (InputPart inputPart : inputParts) {
            // Extract file
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String fileName = getFileName(headers);

            if (fileName == null) {
                throw new ErrorResponseException("Could not read filename", Response.Status.BAD_REQUEST);
            }

            InputStream inputStream;
            try {
                inputStream = inputPart.getBody(InputStream.class, null);
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file " + fileName, Response.Status.BAD_REQUEST);
            }

            // Save document
            try {
                importedDocumentManager.importDocument(inputStream, fileName, DocumentProviderType.USER_COLLECTOR);
                logger.debug("Document has been imported");
            } catch (IOException e) {
                throw new ErrorResponseException("Could not read file", Response.Status.INTERNAL_SERVER_ERROR);
            } catch (IsNotXmlOrCompressedFileDocumentException e) {
                throw new ErrorResponseException("File should be .xml or compressed e.x. .zip, .tag.gz, .rar", Response.Status.BAD_REQUEST);
            } catch (NotReadableCompressFileException e) {
                throw new ErrorResponseException("Could not uncompress file, corrupted file", Response.Status.BAD_REQUEST);
            }

            // Return result
            return Response.ok().build();
        }

        throw new ErrorResponseException("Could not find any file to process");
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                return name[1].trim().replaceAll("\"", "");
            }
        }
        return null;
    }

    @GET
    @Path("{documentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Document")
    public DocumentRepresentation getDocument(
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId
    ) {
        DocumentModel document = getDocumentById(documentId);
        return modelToRepresentation.toRepresentation(document, uriInfo).toSpaceRepresentation();
    }

    @GET
    @Path("/{documentId}/download")
    @Produces("application/xml")
    @ApiOperation(value = "Download Document")
    public Response downloadDocumentXml(
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @Context final HttpServletRequest httpServletRequest
    ) {
        DocumentModel document = getDocumentById(documentId);

        FileModel file = document.getCurrentVersion().getImportedDocument().getFile();
        byte[] reportBytes = file.getFile();

        Response.ResponseBuilder response = Response.ok(reportBytes);
        response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".xml\"");
        return response.build();
    }

    @GET
    @Path("/{documentId}/print")
    @ApiOperation(value = "Print Document")
    public Response downloadDocumentPdf(
            @Context final HttpServletRequest httpServletRequest,
            @ApiParam(value = "Document Id") @PathParam("documentId") String documentId,
            @ApiParam(value = "Theme") @QueryParam("theme") String theme,
            @ApiParam(value = "Locale") @QueryParam("locale") String locale,
            @ApiParam(value = "format", allowableValues = "pdf, html") @QueryParam("format") @DefaultValue("pdf") String format
    ) {
        DocumentModel document = getDocumentById(documentId);

        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
                .themeName(theme)
                .locale(locale != null ? new Locale(locale) : Locale.ENGLISH)
                .build();

        byte[] reportBytes;
        try {
            reportBytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
        } catch (ReportException e) {
            return ErrorResponse.error("Could not generate report, please try again", Response.Status.INTERNAL_SERVER_ERROR);
        }

        Response.ResponseBuilder response = Response.ok(reportBytes);
        switch (exportFormat) {
            case PDF:
                response.type("application/pdf");
                response.header("Content-Disposition", "attachment; filename=\"" + document.getAssignedId() + ".pdf\"");
                break;
            case HTML:
                response.type("application/html");
                break;
        }

        return response.build();
    }

}
