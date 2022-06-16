package com.awickham.thycoticss

import com.morpheusdata.core.Plugin

class ThycoticPlugin extends Plugin {
    @Override
    String getCode() {
        return 'awickham-thycoticss-plugin'
    }

    @Override
    void initialize() {
        SecretServerCredentialProvider secretServerCredentialProvider = new SecretServerCredentialProvider(this, morpheus)
        this.pluginProviders.put("thycoticss", secretServerCredentialProvider)
        this.pluginProviders.put("thycoticss-cypher", new SecretServerCypherProvider(this, morpheus))
        this.setName("Thycotic Secret Server")
    }

    @Override
    void onDestroy() {
        //nothing to do for now
    }
}
