package com.awickham.thycoticss

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.morpheusdata.cypher.Cypher
import com.morpheusdata.cypher.CypherMeta
import com.morpheusdata.cypher.CypherModule
import com.morpheusdata.cypher.CypherObject
import com.morpheusdata.cypher.util.RestApiUtil
import com.morpheusdata.cypher.util.ServiceResponse
import groovy.util.logging.Slf4j

@Slf4j
class SecretServerCypherModule implements CypherModule {
    Cypher cypher
    @Override
    public void setCypher(Cypher cypher) {
        this.cypher = cypher
    }

    @Override
    public CypherObject read(String relativeKey, String path, Long leaseTimeout, String leaseObjectRef, String createdBy) {
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

            String thycoticPath = "/v1/secrets/0/?secretPath=${relativeKey}"
            RestApiUtil.RestOptions restOptions = new RestApiUtil.RestOptions()
            restOptions.headers = new LinkedHashMap<>()
            restOptions.headers.put("Authorization", "Bearer ${thycoticToken}")
            try {
                ServiceResponse resp = RestApiUtil.callApi(thycoticUrl, thycoticPath, null, null, restOptions, 'GET')
                if(resp.getSuccess()) {
                    ObjectMapper mapper = new ObjectMapper()

                    CypherObject thycoticResult = new CypherObject(key, resp.getContent(), leaseTimeout, leaseObjectRef, createdBy)
                    thycoticResult.shouldPersist = false
                    return thycoticResult
                } else {
                    log.error("Error Fetching cypher key [${thycoticPath}] ${resp}")
                    return null //throw exception?
                }
            } catch(Exception ex) {
                ex.printStackTrace()
                return null
            }
        }
    }

    @Override
    public CypherObject write(String relativeKey, String path, String value, Long leaseTimeout, String leaseObjectRef, String createdBy) {
        // Nothing to do. Secrets will be managed in Secret Server, not through Morpheus.
        return true
    }

    @Override
    public boolean delete(String relativeKey, String path, CypherObject object) {
        // Nothing to do. Secrets will be managed in Secret Server, not through Morpheus.
        return true
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder()

        usage.append("This allows secret data to be fetched from a Thycotic Secret Server integration. This can be configured in the thycoticss/config key setup")

        return usage.toString()
    }

    @Override
    public String getHTMLUsage() {
        return null
    }
}
