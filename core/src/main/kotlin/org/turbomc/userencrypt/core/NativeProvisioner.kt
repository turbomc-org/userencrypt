package org.turbomc.userencrypt.core

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.*
import java.security.MessageDigest
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger

class NativeProvisioner(
    private val version: String,
    private val logger: Logger = Logger.getLogger(NativeProvisioner::class.java.name)
) {
    data class Platform(
        val version: String,
        val target: String,
        val libName: String,
        val isWindows: Boolean
    ) {
        val cacheKey = "$version-$target-$libName"
    }

    fun provision(cacheDir: Path): CompletableFuture<Path> = CompletableFuture.supplyAsync {
        val platform = detectPlatform()
        val libFile = cacheDir.resolve(platform.cacheKey)

        if (Files.exists(libFile) && verifyChecksum(libFile, platform)) {
            logger.info("Using cached native library: $libFile")
            return@supplyAsync libFile.toAbsolutePath()
        }

        Files.createDirectories(cacheDir)
        val downloadUrl = buildDownloadUrl(platform)

        logger.info("Downloading native library for ${platform.target}...")
        downloadWithProgress(downloadUrl, libFile)

        if (!verifyChecksum(libFile, platform)) {
            Files.deleteIfExists(libFile)
            throw SecurityException("Checksum verification failed for ${platform.libName}")
        }

        if (!platform.isWindows) {
            libFile.toFile().setExecutable(true)
        }

        logger.info("Native library ready: $libFile")
        libFile.toAbsolutePath()
    }

    private fun detectPlatform(): Platform {
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        return when {
            os.contains("win") -> Platform(version, "x86_64-pc-windows-gnu", "userencrypt.dll", true)
            os.contains("mac") && arch.contains("aarch64") ->
                Platform(version, "aarch64-apple-darwin", "libuserencrypt.dylib", false)
            os.contains("mac") ->
                Platform(version, "x86_64-apple-darwin", "libuserencrypt.dylib", false)
            arch.contains("aarch64") ->
                Platform(version, "aarch64-unknown-linux-gnu", "libuserencrypt.so", false)
            else ->
                Platform(version, "x86_64-unknown-linux-gnu", "libuserencrypt.so", false)
        }
    }

    private fun buildDownloadUrl(platform: Platform): String {
        return "https://github.com/turbomc-org/userencrypt/releases/download/v$version/" +
                "${platform.target}/${platform.libName}"
    }

    private fun downloadWithProgress(url: String, dest: Path) {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 30000
        conn.readTimeout = 120000
        conn.instanceFollowRedirects = true

        if (conn.responseCode != 200) {
            throw IOException("Failed to download $url: HTTP ${conn.responseCode}")
        }

        conn.inputStream.use { input ->
            Files.copy(input, dest, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private fun verifyChecksum(file: Path, platform: Platform): Boolean {
        return try {
            val checksumUrl = buildDownloadUrl(platform) + ".sha256"
            val expected = URL(checksumUrl).readText().trim().split("\\s+".toRegex())[0]
            val actual = sha256(file)
            expected.equals(actual, ignoreCase = true)
        } catch (e: Exception) {
            logger.warning("Could not verify checksum: ${e.message}")
            true
        }
    }

    private fun sha256(file: Path): String {
        val digest = MessageDigest.getInstance("SHA-256")
        Files.newInputStream(file).use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun configureUniFFI(libraryPath: Path) {
        System.setProperty("uniffi.component.userencrypt.libraryOverride", libraryPath.toString())
        logger.fine("Set uniffi.component.userencrypt.libraryOverride=$libraryPath")
    }
}