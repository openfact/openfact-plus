[![Build Status](https://travis-ci.org/clarksnut/clarksnut.svg?branch=master)](https://travis-ci.org/clarksnut/clarksnut)

# Clarksnut
Clarksnut allows you to centralize all your XML-UBL Documents on one site.

# Quick Start
Clone the repository:
```
git clone https://github.com/clarksnut/clarksnut.git
```

Inside project root folder execute:
```
mvn clean wildfly-swarm:run
```

Wait until the server starts, and then go to: <http://localhost:8080>

# Default Keycloak Configuration
Clarksnut is configured for working with Keycloak and by default has this configuration:

```
{
  "realm": "${sso.realm:clarksnut}",
  "bearer-only": true,
  "auth-server-url": "${sso.auth.server.url:http://localhost:8180/auth}",
  "ssl-required": "external",
  "resource": "${sso.clientId:clarksnut-restful-api}",
  "enable-cors": true
}
```
