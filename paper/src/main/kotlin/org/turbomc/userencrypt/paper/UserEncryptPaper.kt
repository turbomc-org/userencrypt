package org.turbomc.userencrypt.paper

import org.bukkit.plugin.java.JavaPlugin
import org.turbomc.userencrypt.core.NativeProvisioner
import org.turbomc.userencrypt.core.UserEncryptCore
import java.nio.file.Path

class UserEncryptPaper : JavaPlugin() {

    companion object {
        lateinit var instance: UserEncryptPaper
            private set
    }

    override fun onEnable() {
        instance = this

        val cacheDir = dataFolder.toPath().resolve("natives")
        val version = pluginMeta.version
        val provisioner = NativeProvisioner(
            version = version,
            githubRepo = "turbomc/userencrypt",
            logger = logger
        )

        UserEncryptCore.initialize(provisioner, cacheDir, logger)
            .thenRun {
                val core = UserEncryptCore()
                val greeting = core.greet("Paper Server")
                logger.info(greeting)
                logger.info("UserEncrypt v$version enabled!")
            }
            .exceptionally { e ->
                logger.severe("Failed to initialize UserEncrypt: ${e.message}")
                e.printStackTrace()
                server.pluginManager.disablePlugin(this)
                null
            }
    }

    override fun onDisable() {
        logger.info("UserEncrypt disabled.")
    }
}
