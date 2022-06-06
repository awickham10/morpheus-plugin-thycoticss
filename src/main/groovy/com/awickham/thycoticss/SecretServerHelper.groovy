package com.awickham.thycoticss

import com.morpheusdata.cypher.util.RestApiUtil
import com.morpheusdata.cypher.util.ServiceResponse
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
        ServiceResponse resp = RestApiUtil.callApi(thycoticUrl, "SecretServer/oauth2/token", null, null, restOptions, "POST")
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
}