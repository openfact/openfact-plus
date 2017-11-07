package org.openfact.services.resources;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;
import org.openfact.documents.DocumentModel;
import org.openfact.documents.DocumentProvider;
import org.openfact.files.exceptions.FileFetchException;
import org.openfact.models.ModelType;
import org.openfact.report.ReportTemplateProvider;
import org.openfact.report.ReportTheme;
import org.openfact.representations.idm.GenericDataRepresentation;
import org.openfact.representations.idm.UserThemeRepresentation;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Stateless
@Path("/themes")
public class ThemesService {

    private static final Logger logger = Logger.getLogger(ThemesService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private DocumentProvider documentProvider;

    @Inject
    private ReportTemplateProvider reportTemplateProvider;

    private DocumentModel getDocumentById(String documentId) {
        DocumentModel ublDocument = documentProvider.getDocument(documentId);
        if (ublDocument == null) {
            throw new NotFoundException();
        }
        return ublDocument;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericDataRepresentation getThemes(@QueryParam("documentId") String documentId) {
        Set<ReportTheme> themes = null;
        if (documentId != null) {
            DocumentModel document = getDocumentById(documentId);
            try {
                themes = reportTemplateProvider.getSupportedThemes(document);
            } catch (FileFetchException e) {
                logger.error("Error fetching file", e);
            }

        }

        if (themes == null) {
            return null;
        }

        Function<ReportTheme, UserThemeRepresentation.Data> mapper = reportTheme -> {
            UserThemeRepresentation.Data data = new UserThemeRepresentation.Data();
            String id = reportTheme.getType() + "_" + reportTheme.getName();

            data.setId(id);
            data.setType(ModelType.THEMES.getAlias());

            // Attributes
            UserThemeRepresentation.Attributes attributes = new UserThemeRepresentation.Attributes();
            attributes.setId(id);
            attributes.setName(reportTheme.getName());
            attributes.setType(reportTheme.getType());
            data.setAttributes(attributes);

            // Links
            UserThemeRepresentation.ThemeLink links = new UserThemeRepresentation.ThemeLink();
            String previewImgLink = uriInfo.getBaseUriBuilder()
                    .path(ThemesService.class)
                    .path(ThemesService.class, "getThemePreview")
                    .build(id).toString();
            links.setPreviewImg(previewImgLink);
            data.setLinks(links);

            return data;
        };

        return new GenericDataRepresentation(themes.stream().map(mapper).collect(Collectors.toList()));
    }

    @GET
    @Path("/{themeId}/preview")
    @Produces("image/png")
    public Response getThemePreview(@PathParam("themeId") String themeId) {
        String[] s = themeId.split("_");
        ReportTheme theme = reportTemplateProvider.getTheme(s[0], s[1]);

        InputStream is;
        try {
            is = theme.getImagePreviewAsStream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(new ByteArrayInputStream(bytes)).build();
    }

}
