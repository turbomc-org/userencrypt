package org.turbomc.userencrypt

import org.bukkit.plugin.java.JavaPlugin
import uniffi.userencrypt.greet

class Userencrypt : JavaPlugin() {

    companion object {
        lateinit var instance: Userencrypt
            private set

    }

    override fun onEnable() {
        instance = this

        val greeting = greet("Paper Plugin")
        logger.info(greeting)

        logger.info("Userencrypt has been enabled.")
    }

    override fun onDisable() {
        logger.info("SurrealDB connection closed.")
        logger.info("Userencrypt has been disabled.")
    }
}
