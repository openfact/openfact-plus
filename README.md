# Openfact Sync
Openfact Sync allows you to centralize all your XML-UBL Documents on on site.

# Project Configuration

## Clone the repository:
```
git clone https://github.com/openfact/openfact-sync.git
```
## Configure database:

The database connection can be configured using environment variables:
```
export DB_DRIVER_NAME=[h2|mysql|postgresql]
export DB_CONNECTION_URL=[database_url]
export DB_USER_NAME=[database_username]
export DB_PASSWORD=[database_password]
```

Default values:
```
DB_DRIVER_NAME=h2
DB_CONNECTION_URL=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_USER_NAME=sa
DB_PASSWORD=sa
```


## Configure database creation strategy (Optional):

Database strategy can be configured using environment variables:
```
export HIBERNATE_STRATEGY=[validate|update|create|create-drop]
export HIBERNATE_SHOW_SQL=[true|false]
export HIBERNATE_FORMAT_SQL=[true|false]
export HIBERNATE_SECOND_LEVEL_CACHE=[true|false]
```

Default values:
```
HIBERNATE_STRATEGY=update
HIBERNATE_SHOW_SQL=false
HIBERNATE_FORMAT_SQL=false
HIBERNATE_SECOND_LEVEL_CACHE=true
```

## Configure database search strategy (Optional):

Database search strategy can be configured using environment variables:
```
export HIBERNATE_INDEX_MANAGER=[directory-based|near-real-time|elasticsearch]
```
Default values:
```
HIBERNATE_INDEX_MANAGER=directory-based
```

### Elasticsearch (Production)
This configuration should be considered in production environments.

In case elasticsearch was selected as index manager, then you need to configure additional environment variables:
```
export ES_HOST=[my_elasticsearch_host]
export ES_USER=[my_elasticsearch_username]
export ES_PASSWORD=[my_elasticsearch_password]
export ES_INDEX_SCHEMA_MANAGEMENT_STRATEGY=[none|validate|update|create|drop-and-create|drop-and-create-and-drop]
```
Default values:
```
ES_INDEX_SCHEMA_MANAGEMENT_STRATEGY=update
```

### Elasticsearch AWS (Production)
In case your elasticsearch cluster is provided by AWS you need to follow previous step and additionally:

```
export HIBERNATE_ES_AWS_ENABLED=[true|false]
export HIBERNATE_ES_AWS_ACCESS_KEY=[my_aws_access_key]
export HIBERNATE_ES_AWS_SECRET_KEY=[my_aws_secret_key]
export HIBERNATE_ES_AWS_REGION=[my_aws_region]
```

Default values:
```
HIBERNATE_ES_AWS_ENABLED=false
```

# Start project
After configure the basic environment variables, then execute:

```
mvn wildfly-swarm:run -pl app
```

Wait until the server starts, and then go to:

<http://localhost:8080>

# Start project on Openshift
After configure the basic environment variables, then execute:

```
mvn fabric8:deploy -Popenshift
```