package com.awickham.thycoticss

import com.morpheusdata.core.util.HttpApiClient
import com.morpheusdata.cypher.util.RestApiUtil
import com.morpheusdata.cypher.util.ServiceResponse
import com.morpheusdata.model.AccountIntegration
import com.morpheusdata.response.ServiceResponse
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class SecretServerHelper {
    static String getAuthToken(String thycoticUrl, String thycoticUsername, String thycoticPassword) {
        RestApiUtil.RestOptions restOptions = new RestApiUtil.RestOptions()
        restOptions.body = [
            username: thycoticUsername,
            password: thycoticPassword,
            grant_type: "password"
        ]
        restOptions.contentType = 'form'
        log.info("Getting token with request: ${restOptions.body}")
        com.morpheusdata.cypher.util.ServiceResponse resp = RestApiUtil.callApi(thycoticUrl, "SecretServer/oauth2/token", null, null, restOptions, "POST")
        if(resp.getSuccess()) {
            JsonSlurper slurper = new JsonSlurper()
            Object authResp = slurper.parseText(resp.getContent())
            log.info("Retrieved ${authResp.token_type} token from Secret Server")

            return authResp.access_token
        } else {
            log.error("Could not obtain token from Secret Server")
            return null
        }
    }

    static String getAuthToken(HttpApiClient client, AccountIntegration integration) {
        def authResults = this.getAuthToken(integration.serviceUrl, integration.serviceUsername, integration.servicePassword)
        if(authResults != null) {
            return ServiceResponse.success(authResults)
        } else {
            return ServiceResponse.error("Authentication failed with Thycotic Secret Server")
        }
    }
}