<%@page import="org.keycloak.AuthorizationContext" %>
<%@ page import="org.keycloak.KeycloakSecurityContext" %>
<%@ page import="org.keycloak.common.util.KeycloakUriBuilder" %>
<%@ page import="org.keycloak.constants.ServiceUrlConstants" %>
<%@ page import="org.keycloak.representations.idm.authorization.Permission" %>

<%
    KeycloakSecurityContext keycloakSecurityContext = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
    AuthorizationContext authzContext = keycloakSecurityContext.getAuthorizationContext();
    authzContext.
%>
<html>
<body>
<h2>Welcome !</h2>
<h2><a href="<%= KeycloakUriBuilder.fromUri("/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
            .queryParam("redirect_uri", "http://localhost:8080/hello-world-authz-service").build("hello-world-authz").toString()%>">Logout</a></h2>

<h3>Your permissions are:</h3>

<ul>
    <%
        for (Permission permission : authzContext.getPermissions()) {
    %>
    <li>
        <p>Resource: <%= permission.getResourceSetName() %></p>
        <p>ID: <%= permission.getResourceSetId() %></p>
    </li>
    <%
        }
    %>
</ul>
</body>
</html>