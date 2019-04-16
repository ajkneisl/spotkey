package dev.shog.spotkey.handle

import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.obj.HotKey
import org.apache.commons.lang3.SystemUtils
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Manages the configuration file.
 */
object HotKeyLoader {
    /**
     * The loaded HotKeys
     */
    val hotKeys = ArrayList<HotKey>()

    /**
     * If the client is using Linux, if false assume Windows 10
     */
    private val LINUX = SystemUtils.IS_OS_LINUX

    /**
     * The SpotKey directory.
     */
    private val SPOTKEY_DIR = if (LINUX) File("/etc/spotkey") else File(System.getenv("appdata") + "\\spotkey\\")

    /**
     * The configuration file.
     */
    private val SPOTKEY_CFG = if (LINUX) File(SPOTKEY_DIR.path + "/conf.json") else File(SPOTKEY_DIR.path + "\\conf.json")

    init {
        if (!SPOTKEY_DIR.exists() && !SPOTKEY_DIR.mkdirs()) throw Exception("There was an issue creating startup files.")
        if (!SPOTKEY_CFG.exists()) {
            if (!SPOTKEY_CFG.createNewFile()) throw Exception("There was an issue creating startup files.")

            initCfg()
        }

        reloadHotKeys()
    }

    /**
     * Initializes all of the hotkeys within the main configuration folder.
     */
    fun reloadHotKeys() {
        val start = System.currentTimeMillis()
        LOGGER.debug("Refreshing hotkeys from config...")

        hotKeys.clear()
        val json = getCfg()

        if (!json.has("use-default") || !json.has("keys")) throw IllegalArgumentException("There's something wrong with the current configuration files.")

        if (json.getBoolean("use-default")) {
            val ar = JSONArray(
                    String(
                            File(
                                    this::class.java.classLoader.getResource("default.json").toURI()
                            ).inputStream().readBytes()
                    )
            )

            for (i in 0 until ar.length()) {
                val obj = ar.getJSONObject(i)

                if (!obj.has("keystroke") || !obj.has("actions")) {
                    LOGGER.warn("There was an issue loading a default hotkey!")
                    continue
                }

                val keystroke = obj.get("keystroke")
                val actions = obj.get("actions")

                if (keystroke !is String || actions !is JSONArray) {
                    LOGGER.warn("There was an issue loading a default hotkey!")
                    continue
                }

                if (actions.isEmpty) {
                    LOGGER.warn("There was an issue loading a default hotkey!")
                    continue
                }

                LOGGER.debug("Loaded default hot-key with keystroke ${keystroke.toUpperCase()} with actions $actions")
                hotKeys.add(HotKey(keystroke, actions))
            }
        }

        val hkAr = json.getJSONArray("keys")

        for (i in 0 until hkAr.length()) {
            val obj = hkAr.getJSONObject(i)

            if (!obj.has("keystroke") || !obj.has("actions")) {
                LOGGER.warn("There was an issue loading a hotkey!")
                continue
            }

            val keystroke = obj.get("keystroke")
            val actions = obj.get("actions")

            if (keystroke !is String || actions !is JSONArray) {
                LOGGER.warn("There was an issue loading a hotkey!")
                continue
            }

            if (actions.isEmpty) {
                LOGGER.warn("There was an issue loading a hotkey!")
                continue
            }

            LOGGER.debug("Loaded hot-key with keystroke ${keystroke.toUpperCase()} with actions $actions")
            hotKeys.add(HotKey(keystroke, actions))
        }

        HotKeyHandler.refreshCurrentlyLoaded()
        LOGGER.debug("Completed hot-key refresh! Took ${System.currentTimeMillis()-start}ms")
    }

    /**
     * Writes a JSONObject to the configuration file
     */
    private fun writeCfg(js: JSONObject) = SPOTKEY_CFG.outputStream().write(js.toString().toByteArray())

    /**
     * Initializes the config with default values
     */
    private fun initCfg(): Boolean {
        val obj = JSONObject()

        obj.put("use-default", true)
        obj.put("keys", JSONArray())

        writeCfg(obj)

        return true
    }

    /**
     * Gets a JSONObject from the configuration file
     */
    private fun getCfg(): JSONObject = JSONObject(String(SPOTKEY_CFG.inputStream().readBytes()))
}