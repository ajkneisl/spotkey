package dev.shog.spotkey.handle

import com.tulskiy.keymaster.common.Provider
import dev.shog.spotkey.DATA
import dev.shog.spotkey.DataType
import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.obj.HotKey
import dev.shog.spotkey.ui.Error
import dev.shog.spotkey.util.OsUtil
import org.apache.commons.lang3.SystemUtils
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Toolkit
import java.io.File
import kotlin.system.exitProcess

/**
 * Manages the configuration file.
 */
object HotKeyLoader {
    /**
     * The current keymaster provider.
     */
    private val PROVIDER = Provider.getCurrentProvider(true)

    /**
     * Refreshes the currently loaded keybinds.
     */
    private fun refreshCurrentlyLoaded() {
        PROVIDER.reset()

        hotKeys.forEach { k ->
            PROVIDER.register(k.getKeyStroke()) { k.execute() }
        }
    }

    /**
     * Plays a beep noise, an indicator of an issue.
     */
    fun beep() = Toolkit.getDefaultToolkit().beep()
    /**
     * The loaded HotKeys
     */
    val hotKeys = ArrayList<HotKey>()

    /**
     * The SpotKey directory.
     */
    private val SPOTKEY_DIR = File(OsUtil.getDefaultLocation() + OsUtil.separator + "spotkey")

    /**
     * The configuration file.
     */
    val SPOTKEY_CFG = File(SPOTKEY_DIR.path + "${OsUtil.separator}conf.json")

    init {
        if (!SPOTKEY_DIR.exists() && !SPOTKEY_DIR.mkdirs()) {
            Error.make("There was an issue creating startup files.")
            exitProcess(-1)
        }

        if (!SPOTKEY_CFG.exists()) {
            if (!SPOTKEY_CFG.createNewFile()) {
                Error.make("There was an issue creating startup files.")
                exitProcess(-1)
            }

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

        if (!isCorrect(json)) throw IllegalArgumentException("There's something wrong with the current configuration files.")

        if (json.getBoolean("use-default")) {
            val ar = JSONArray(
                    String(this::class.java.classLoader.getResourceAsStream("default.json")?.readBytes() ?: ByteArray(0))
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

        refreshVariables(json.getJSONArray("vars"))

        refreshCurrentlyLoaded()
        LOGGER.debug("Completed hot-key refresh! Took ${System.currentTimeMillis()-start}ms")
    }

    /**
     * Writes a JSONObject to the configuration file
     */
    fun writeCfg(js: JSONObject) = SPOTKEY_CFG.outputStream().write(js.toString().toByteArray())

    /**
     * Refreshes the user variables.
     */
    private fun refreshVariables(data: JSONArray) {
        for (i in 0 until data.length()) {
            val obj = data.get(i) as? JSONObject ?: continue
            if (!isVariableCorrect(obj)) continue

            val name = obj.getString("name")
            val type = obj.getString("type")
            val value = obj.get("value") as? String ?: continue

            when (type) {
                "playlist" -> DATA[name] = Pair(DataType.PLAYLIST_URI, value)
            }
        }
    }

    /**
     * Initializes the config with default values
     */
    private fun initCfg(): Boolean {
        val obj = JSONObject()

        obj.put("use-default", true)
        obj.put("keys", JSONArray())
        obj.put("vars", JSONArray())

        writeCfg(obj)

        return true
    }

    /**
     * Gets a JSONObject from the configuration file
     */
    fun getCfg(): JSONObject = JSONObject(String(SPOTKEY_CFG.inputStream().readBytes()))

    /**
     * Makes sure the JSONObject contains the correct values.
     */
    private fun isCorrect(obj: JSONObject): Boolean =
            (obj.has("use-default") && obj.get("use-default") is Boolean) &&
                    (obj.has("keys") && obj.get("keys") is JSONArray) &&
                    (obj.has("vars") && obj.get("vars") is JSONArray)

    /**
     * Makes sure the variable JSONObject has all of the required parts.
     */
    private fun isVariableCorrect(obj: JSONObject): Boolean =
            (obj.has("name") && obj.get("name") is String) &&
                    (obj.has("type") && obj.get("type") is String) &&
                    (obj.has("value"))
}