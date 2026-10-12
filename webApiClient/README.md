# AlpsLib Web API Client

This is the generated Java client for the BuildTheEarth.net API. The OpenAPI document in [`swagger.json`](./swagger.json) is the source of truth; generated Java sources are not committed.

## Use the published client

Add the Alps BTE Maven repository and the client dependency to your Maven project:

```xml
<repositories>
    <repository>
        <id>alpsbte-repo</id>
        <url>https://mvn.alps-bte.com/repository/alps-bte/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.alpsbte.alpslib</groupId>
        <artifactId>web-api-client</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

The client uses OpenAPI Generator's `okhttp-gson` Java library. Generated API classes are under `com.alpsbte.alpslib.web.api`, models under `com.alpsbte.alpslib.web.model`, and client support classes under `com.alpsbte.alpslib.web.invoker`.

Public routes require the build team's API key as a bearer token in the `Authorization` header (`Authorization: Bearer <api-key>`). Configure the generated client's `ApiClient` with the token before calling public operations:

```java
ApiClient apiClient = new ApiClient();
apiClient.setBearerToken(apiKey);
```

The OpenAPI security requirement applies to every operation under `/public/`, including claim creation. The `X-API-Key` header is not used by these routes.

## Generate and release

Pull requests that change the spec run `.github/workflows/web-api-client.yml`. The workflow validates `swagger.json`, generates a standalone Maven project with OpenAPI Generator CLI 7.26.0, and packages it to ensure the generated client compiles. Generated files live only in the runner's temporary directory.

To publish a new client version, push a tag of the form `web-api-client-vX.Y.Z` (for example, `web-api-client-v1.0.0`). The workflow uses the tag version as the Maven artifact version and publishes `com.alpsbte.alpslib:web-api-client` to the Alps BTE Maven repository. The API contract's `info.version` in `swagger.json` is independent; increment the client tag when you intend to release the generated client.

The release workflow requires the repository secrets `MAVEN_USERNAME` and `MAVEN_PASSWORD`.

For a local generation and package check, download the pinned CLI and run the following from the repository root (substitute the desired version):

```sh
curl --fail --location --output openapi-generator-cli-7.26.0.jar \
  https://repo.maven.apache.org/maven2/org/openapitools/openapi-generator-cli/7.26.0/openapi-generator-cli-7.26.0.jar
java -jar openapi-generator-cli-7.26.0.jar generate \
  --input-spec webApiClient/swagger.json \
  --generator-name java \
  --output build/web-api-client \
  --library okhttp-gson \
  --api-package com.alpsbte.alpslib.web.api \
  --model-package com.alpsbte.alpslib.web.model \
  --invoker-package com.alpsbte.alpslib.web.invoker \
  --additional-properties=groupId=com.alpsbte.alpslib,artifactId=web-api-client,artifactVersion=1.0.0,buildTool=maven,dateLibrary=java8,useJakartaEe=false,projectName=AlpsLib-Web-API-Client,projectDescription=BuildTheEarth.net-API-client-for-AlpsLib,licenseName=MIT,licenseUrl=https://opensource.org/licenses/MIT,scmConnection=scm:git:https://github.com/AlpsBTE/Alps-Lib.git,scmDeveloperConnection=scm:git:ssh://github.com/AlpsBTE/Alps-Lib.git,scmUrl=https://github.com/AlpsBTE/Alps-Lib \
  --global-property=apiTests=false,modelTests=false,apiDocs=false,modelDocs=false
mvn -f build/web-api-client/pom.xml -Dmaven.test.skip=true -Djavax.net.ssl.trustStoreType=Windows-ROOT package
```

On non-Windows systems, omit `-Djavax.net.ssl.trustStoreType=Windows-ROOT`.
