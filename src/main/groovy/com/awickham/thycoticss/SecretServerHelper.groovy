package com.awickham.thycoticss

import com.morpheusdata.cypher.util.RestApiUtil
import com.morpheusdata.cypher.util.ServiceResponse
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class SecretServerHelper {
    static String getAuthToken(String thycoticUrl, String thycoticUsername, String thycoticPassword) {
        RestApiUtil.RestOptions restOptions = new RestApiUtil.RestOptions()
        restOptions.body = JsonOutput.toJson([
            username: thycoticUsername,
            password: thycoticPassword,
            grant_type: 'password'
        ])
        ServiceResponse resp = RestApiUtil.callApi(thycoticUrl, 'SecretServer/oauth2/token', null, null, restOptions, 'POST')
        if(resp.getSuccess()) {
            def slurper = new JsonSlurper()
            def authResp = slurper.parseText(resp.getContent())

            return authResp.access_token
        } else {
            return null
        }
    }
}