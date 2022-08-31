package com.awickham.thycoticss

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.morpheusdata.cypher.Cypher
import com.morpheusdata.cypher.CypherMeta
import com.morpheusdata.cypher.CypherModule
import com.morpheusdata.cypher.CypherObject
import com.morpheusdata.cypher.util.RestApiUtil
import com.morpheusdata.cypher.util.ServiceResponse
import java.net.URLEncoder
import groovy.util.logging.Slf4j
import groovy.json.JsonSlurper

@Slf4j
class SecretServerCypherModule implements CypherModule {
    Cypher cypher
    @Override
    public void setCypher(Cypher cypher) {
        this.cypher = cypher
    }

    @Override
    public CypherObject read(String relativeKey, String path, Long leaseTimeout, String leaseObjectRef, String createdBy) {
        String slug = relativeKey.split(":", 2)[1]
        relativeKey = relativeKey.split(":", 2)[0]
        String key = relativeKey
        if(path != null) {
            key = path + "/" + key
        }
        if(relativeKey.startsWith("config/")) {
            return null
        } else {
            String thycoticUrl = cypher.read("thycoticss/config/url").value
            String thycoticUsername = cypher.read("thycoticss/config/username").value
            String thycoticPassword = cypher.read("thycoticss/config/password").value
            String thycoticToken = SecretServerHelper.getAuthToken(thycoticUrl, thycoticUsername, thycoticPassword)

            // search for the secret by the path
            String encodedKey = java.net.URLEncoder.encode(("/" + relativeKey), 'UTF-8')
            String searchPath = "SecretServer/api/v1/secrets/0/?secretPath=" + encodedKey;
            log.debug("Searching for secret from ${searchPath}")

            RestApiUtil.RestOptions restOptions = new RestApiUtil.RestOptions()
            restOptions.apiToken = thycoticToken

            JsonSlurper slurper = new JsonSlurper()
            try {
                ServiceResponse resp = RestApiUtil.callApi(thycoticUrl, searchPath, null, null, restOptions, 'GET')
                if(resp.getSuccess()) {
                    Object searchResponse = slurper.parseText(resp.getContent())
                    Object field = searchResponse.items.find{ it -> it.slug == slug }
                    if (field == null) {
                        log.error("Could not find secret field ${slug}")
                        return null
                    } else {
                        CypherObject thycoticResult = new CypherObject(key, field.itemValue, leaseTimeout, leaseObjectRef, createdBy)
                        thycoticResult.shouldPersist = false
                        return thycoticResult
                    }
                } else {
                    log.error("Error searching for secret: ${resp}")
                    return null //throw exception?
                }
            } catch(Exception ex) {
                log.error("Error: " + ex)
                return null
            }
        }
    }

    @Override
    public CypherObject write(String relativeKey, String path, String value, Long leaseTimeout, String leaseObjectRef, String createdBy) {
        String key = relativeKey;
        if(path != null) {
            key = path + "/" + key;
        }
        
        // Nothing to do. Secrets will be managed in Secret Server, not through Morpheus.
        return new CypherObject(key, value, leaseTimeout, leaseObjectRef, createdBy)
    }

    @Override
    public boolean delete(String relativeKey, String path, CypherObject object) {
        // Nothing to do. Secrets will be managed in Secret Server, not through Morpheus.
        return true
    }

    @Override
    public String getUsage() {
        return "This allows secret data to be fetched from a Thycotic Secret Server integration. This can be configured in the thycoticss/config key setup"
    }

    @Override
    public String getHTMLUsage() {
        return null
    }
}