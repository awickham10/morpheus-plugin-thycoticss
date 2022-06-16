## Thycotic Secret Server Plugin

This is a Morpheus plugin for interacting with `Thycotic Secret Server`. This plugin enables the ability to store secure credentials for various tasks remotely in a secure store external to Morpheus and Cypher. This utilizes Secret Server's API.

### Using With Cypher
Create the below Cypher keys to configure the integration. Once these keys have been created, secrets can be read in tasks using the cypher.read function. Any field can be read by using the field slug. Common field slugs include: domain, username, password.

| Key | Description | Example |
| --- | ----------- | ------- |
| thycoticss/config/url | Base URL of the Thycotic Secret Server | https://myserver.mydomain.com/ |
| thycoticss/config/username | Username to perform OAuth 2.0 authentication with | MyUsername |
| thycoticss/config/password | Password to perform OAuth 2.0 authentication with | MySup3rP@55w0rd! |

#### Example 
Read the "username" field from a secret named "My Secert Name" in the folder "My Folder."

```bash
from_vault="<%= cypher.read('thycoticss/My Folder/My Secret Name/username') %>"
echo $from_vault
```

### Building
This is a Morpheus plugin that leverages the `morpheus-plugin-core` which can be referenced by visiting [https://developer.morpheusdata.com](https://developer.morpheusdata.com). It is a groovy plugin designed to be uploaded into a Morpheus environment via the `Administration -> Integrations -> Plugins` section. To build this product from scratch simply run the shadowJar gradle task on java 11:

```bash
./gradlew shadowJar
```

A jar will be produced in the `build/lib` folder that can be uploaded into a Morpheus environment.