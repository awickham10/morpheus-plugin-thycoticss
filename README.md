## Thycotic Secret Server Plugin

This is a Morpheus plugin for interacting with Thycotic Secret Server. This plugin enables the ability to store secure credentials for various clouds, tasks, and integrations remotely in a secure store external to Morpheus and Cypher. This utilizes Secret Server's API. 

### Building

This is a Morpheus plugin that leverages the `morpheus-plugin-core` which can be referenced by visiting [https://developer.morpheusdata.com](https://developer.morpheusdata.com). It is a groovy plugin designed to be uploaded into a Morpheus environment via the `Administration -> Integrations -> Plugins` section. To build this product from scratch simply run the shadowJar gradle task on java 11:

```bash
./gradlew shadowJar
```

A jar will be produced in the `build/lib` folder that can be uploaded into a Morpheus environment.


### Configuring

Once the plugin is loaded in the environment. Secret Server Becomes available in `Infrastructure -> Trust -> Services`.

When adding the integration simply enter the URL of the Thycotic Secret Server (no path is needed just the root url) and the token with sufficient enough privileges to talk to the API.