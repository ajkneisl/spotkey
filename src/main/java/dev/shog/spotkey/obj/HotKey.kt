package dev.shog.spotkey.obj

import dev.shog.spotkey.Action
import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.getAction
import dev.shog.spotkey.getDetailedAction
import org.json.JSONArray
import javax.swing.KeyStroke

/**
 * Holds a loaded Hot Key
 */
class HotKey(private val keystrokeString: String, private val actions: JSONArray) {
    init { validate(); generate() }

    /**
     * The keystroke that executes the HotKey
     */
    private var keystroke: KeyStroke? = null

    /**
     * If the keystroke is valid
     */
    var valid: Boolean = false

    /**
     * If the variable generatedValues has been created.
     */
    private var generated: Boolean = false

    /**
     * The different actions the HotKey executes.
     */
    private var generatedValues: ArrayList<Action>? = null

    /**
     * Generates a executable Thread for when the HotKey is executed
     */
    private fun generate() {
        val ia = ArrayList<Action>()

        for (i in 0 until actions.length()) {
            val obj = actions.get(i)

            // Assume this is a detailed action
            if (obj.toString().contains(":")) {
                val argsSplit = obj.toString().split(":")
                val args = HashMap<String, Any>()

                if (argsSplit.size != 2) throw IllegalArgumentException("Illegal detailed action.")

                val id = try {
                    argsSplit[0].toInt()
                } catch (ex: NumberFormatException) {
                    throw IllegalArgumentException("Illegal ID for detailed action.")
                }

                for (pvc in argsSplit[1].split("/")) {
                    if (pvc.contains("|")) {
                        val pvcSplit = pvc.split("|")

                        if (pvcSplit.size != 2) throw IllegalArgumentException("Illegal attribute on detailed action.")

                        val attr = pvcSplit[0]
                        val attrVal = pvcSplit[1]

                        args[attr] = attrVal
                    } else continue
                }

                val ob = getDetailedAction(id, args) ?: continue

                ia.add(ob)
                continue
            }

            val ob = actions.get(i) as? Int ?: continue

            ia.add(getAction(ob))
        }

        generatedValues = ia
        generated = true
    }

    /**
     * Parses a keystroke from a string. If it's valid, valid will equal true.
     */
    private fun validate() {
        if (keystrokeString.isBlank()) {
            valid = false
            return
        }

        keystroke = KeyStroke.getKeyStroke(keystrokeString)

        if (keystroke == null) {
            valid = false
            LOGGER.error("$keystrokeString is invalid!")
        }
    }

    /**
     * If the HotKey is valid, get the keystroke.
     */
    fun getKeyStroke(): KeyStroke? = if (valid) keystroke else { validate(); keystroke }

    /**
     * Executes the HotKey
     */
    fun execute() {
        if (!valid) validate()

        for (a in generatedValues!!) {
            if (a.run()) a.action.run() else LOGGER.warn("Waiting for cool-down...")
        }
    }
}