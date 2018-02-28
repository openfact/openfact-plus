package org.clarksnut.services.resources;

public class ReportsService {

    //    @PATCH
//    @Path("massive")
//    @Produces(MediaType.APPLICATION_JSON)
//    public void updateDocuments(@Context final HttpServletRequest httpServletRequest,
//                                GenericDataRepresentation<List<DocumentRepresentation.DocumentData>> representation) {
//        UserModel user = getUser(httpServletRequest);
//
//        representation.getData().forEach(documentRepresentation -> {
//            DocumentModel document = null;
//            try {
//                document = getDocumentById(user, documentRepresentation.getId());
//            } catch (ModelForbiddenException forbiddenExceptionModel) {
//                throw new ForbiddenException("User not allowed to access this document");
//            }
//            updateDocument(documentRepresentation.getAttributes(), user, document);
//        });
//    }
//
//    @GET
//    @Path("/massive/download")
//    @Produces("application/zip")
//    public Response downloadDocuments(@Context final HttpServletRequest httpServletRequest,
//                                      @QueryParam("documents") List<String> documentsId) {
//        UserModel user = getUser(httpServletRequest);
//
//        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
//        documentsId.stream()
//                .map(documentId -> {
//                    try {
//                        return getDocumentById(user, documentId);
//                    } catch (ModelForbiddenException forbiddenExceptionModel) {
//                        throw new ForbiddenException("User not allowed to access this document");
//                    }
//                })
//                .forEach(document -> {
//                    FileModel file = document.getCurrentVersion().getImportedDocument().getFile();
//                    try {
//                        zipInMemory.add(file.getFile()).path(document.getAssignedId()).save();
//                    } catch (IOException e) {
//                        logger.error("Could not add file to zip", e);
//                    }
//                });
//
//        byte[] zip = zipInMemory.toBytes();
//
//        Response.ResponseBuilder response = Response.ok(zip);
//        response.header("Content-Disposition", "attachment; filename=\"" + "files" + ".zip\"");
//        return response.build();
//    }
//
//    @GET
//    @Path("/massive/print")
//    @Produces("application/zip")
//    public Response printDocuments(
//            @Context final HttpServletRequest httpServletRequest,
//            @QueryParam("documents") List<String> documentsId,
//            @QueryParam("theme") String theme,
//            @QueryParam("format") @DefaultValue("PDF") String format
//    ) {
//        UserModel user = getUser(httpServletRequest);
//
//        //
//        ExportFormat exportFormat = ExportFormat.valueOf(format.toUpperCase());
//
//        Set<DocumentModel> documents = documentsId.stream()
//                .map(documentId -> {
//                    try {
//                        return getDocumentById(user, documentId);
//                    } catch (ModelForbiddenException forbiddenExceptionModel) {
//                        throw new ForbiddenException("User not allowed to access this document");
//                    }
//                })
//                .collect(Collectors.toSet());
//
//        ReportTemplateConfiguration reportConfig = ReportTemplateConfiguration.builder()
//                .themeName(theme)
//                .locale(user.getDefaultLanguage() != null ? new Locale(user.getDefaultLanguage()) : Locale.ENGLISH)
//                .build();
//
//        ZipBuilder zipInMemory = ZipBuilder.createZipInMemory();
//        for (DocumentModel document : documents) {
//            try {
//                byte[] bytes = reportTemplateProvider.getReport(reportConfig, document, exportFormat);
//                zipInMemory.add(bytes).path(document.getAssignedId() + "." + exportFormat.getExtension()).save();
//            } catch (ReportException e) {
//                logger.error("Could not generate a report", e);
//            } catch (IOException e) {
//                logger.error("Could not add file to zip", e);
//            }
//        }
//        byte[] zip = zipInMemory.toBytes();
//
//        Response.ResponseBuilder response = Response.ok(zip);
//        response.header("Content-Disposition", "attachment; filename=\"" + "file" + ".zip\"");
//        return response.build();
//    }

}
