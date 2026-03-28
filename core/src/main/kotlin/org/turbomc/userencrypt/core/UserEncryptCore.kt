package org.turbomc.userencrypt.core

import uniffi.userencrypt.*
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

/**
 * Core UserEncrypt API that works on any platform.
 * Initializes lazily on first use.
 */
class UserEncryptCore private constructor() {
    companion object {
        @Volatile
        private var initialized = false
        private val lock = Any()

        /**
         * Initialize with lazy loading. Call this once at plugin startup.
         */
        fun initialize(provisioner: NativeProvisioner, cacheDir: Path, logger: Logger): CompletableFuture<Void?> {
            return provisioner.provision(cacheDir).thenAccept { libPath ->
                synchronized(lock) {
                    if (!initialized) {
                        provisioner.configureUniFFI(libPath)
                        uniffiEnsureInitialized()
                        initialized = true
                        logger.info("UserEncryptCore initialized with native library: $libPath")
                    }
                }
            }
        }

        fun isInitialized(): Boolean = initialized
    }

    fun greet(name: String): String = greet(name)

    fun addNumbers(a: Int, b: Int): Int = addNumbers(a, b)
}
