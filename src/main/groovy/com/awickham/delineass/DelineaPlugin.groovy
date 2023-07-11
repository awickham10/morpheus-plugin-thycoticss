package com.awickham.delineass

import com.morpheusdata.core.Plugin

class DelineaPlugin extends Plugin {
    @Override
    String getCode() {
        return 'awickham-delineass-plugin'
    }

    @Override
    void initialize() {
        this.pluginProviders.put("delineass-cypher", new SecretServerCypherProvider(this, morpheus))
        this.setName("Delinea Secret Server")
    }

    @Override
    void onDestroy() {
        //nothing to do for now
    }
}
