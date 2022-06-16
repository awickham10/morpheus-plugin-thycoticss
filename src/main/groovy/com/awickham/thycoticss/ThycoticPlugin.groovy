package com.awickham.thycoticss

import com.morpheusdata.core.Plugin

class ThycoticPlugin extends Plugin {
    @Override
    String getCode() {
        return 'awickham-thycoticss-plugin'
    }

    @Override
    void initialize() {
        this.pluginProviders.put("thycoticss-cypher", new SecretServerCypherProvider(this, morpheus))
        this.setName("Thycotic Secret Server")
    }

    @Override
    void onDestroy() {
        //nothing to do for now
    }
}
