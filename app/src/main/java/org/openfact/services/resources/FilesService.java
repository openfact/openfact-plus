package org.openfact.services.resources;

import org.jboss.logging.Logger;
import org.openfact.files.FileModel;
import org.openfact.files.FileProvider;
import org.openfact.files.ModelFetchException;
import org.openfact.services.ErrorResponse;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
@Path("/files")
@Consumes(MediaType.APPLICATION_JSON)
public class FilesService {

    private static final Logger logger = Logger.getLogger(FilesService.class);

    @Context
    private UriInfo uriInfo;

    @Inject
    private FileProvider fileProvider;

    @GET
    @Path("{fileId}")
    public Response getFile(@PathParam("fileId") String fileId) {
        FileModel file = fileProvider.getFile(fileId);

        Response.ResponseBuilder response;
        try {
            response = Response.ok(file.getFile());
        } catch (ModelFetchException e) {
            logger.error("Could not fetch file from storage");
            return ErrorResponse.error("Could not read file", Response.Status.SERVICE_UNAVAILABLE);
        }

        response.type("application/xml");
        response.header("Content-Disposition", "attachment; filename=\"" + file.getFilename() + ".xml\"");

        return response.build();
    }

}