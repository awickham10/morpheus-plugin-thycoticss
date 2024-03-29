package com.awickham.delineass

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
            String delineaUrl = cypher.read("delineass/config/url").value
            String delineaUsername = cypher.read("delineass/config/username").value
            String delineaPassword = cypher.read("delineass/config/password").value
            String delineaToken = SecretServerHelper.getAuthToken(delineaUrl, delineaUsername, delineaPassword)

            // search for the secret by the path
            String encodedKey = java.net.URLEncoder.encode(("/" + relativeKey), 'UTF-8')
            String searchPath = "SecretServer/api/v1/secrets/0/?secretPath=" + encodedKey;
            log.debug("Searching for secret from ${searchPath}")

            RestApiUtil.RestOptions restOptions = new RestApiUtil.RestOptions()
            restOptions.apiToken = delineaToken

            JsonSlurper slurper = new JsonSlurper()
            try {
                ServiceResponse resp = RestApiUtil.callApi(delineaUrl, searchPath, null, null, restOptions, 'GET')
                if(resp.getSuccess()) {
                    Object searchResponse = slurper.parseText(resp.getContent())
                    Object field = searchResponse.items.find{ it -> it.slug == slug }
                    if (field == null) {
                        log.error("Could not find secret field ${slug}")
                        return null
                    } else {
                        CypherObject delineaResult = new CypherObject(key, field.itemValue, leaseTimeout, leaseObjectRef, createdBy)
                        delineaResult.shouldPersist = false
                        return delineaResult
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
        return "This allows secret data to be fetched from a Delinea Secret Server integration. This can be configured in the delineass/config key setup"
    }

    @Override
    public String getHTMLUsage() {
        return null
    }

    /**
     * The readFromDatastore method is used to determine if Cypher should read from the value stored within the {@link Datastore} on read requests
     * @return if this returns false then Cypher read requests are always executed through the module and do not read from a value that exists within the {@link Datastore}.
     */
    @Override
    Boolean readFromDatastore() {
        return false //important to ensure reads are always obtained from conjur
    }
}