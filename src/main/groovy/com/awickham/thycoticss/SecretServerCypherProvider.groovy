package com.awickham.thycoticss

import com.morpheusdata.core.CypherModuleProvider
import com.morpheusdata.core.MorpheusContext
import com.morpheusdata.core.Plugin
import com.morpheusdata.cypher.CypherModule

class SecretServerCypherProvider implements CypherModuleProvider{
    MorpheusContext morpheusContext
    Plugin plugin

    SecretServerCypherProvider(Plugin plugin, MorpheusContext morpheusContext) {
        this.plugin = plugin
        this.morpheusContext = morpheusContext
    }

    @Override
    CypherModule getCypherModule() {
        new SecretServerCypherModule()
    }

    @Override
    String getCypherMountPoint() {
        return 'thycoticss'
    }

    @Override
    MorpheusContext getMorpheus() {
        return morpheusContext
    }

    @Override
    String getCode() {
        return 'thycoticss-cypher'
    }

    @Override
    String getName() {
        return 'Thycotic Secret Server'
    }
}
