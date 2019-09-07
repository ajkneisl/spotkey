package dev.shog.spotkey.ui

import dev.shog.spotkey.HEADLESS
import dev.shog.spotkey.ICON
import dev.shog.spotkey.LOGGER
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

/**
 * Debug information
 */
object Debug {
    /**
     * A button that can be added to the debug page.
     */
    data class InjectableButton(val text: String, val action: () -> Unit)

    /**
     * Create a debug UI.
     */
    fun make(debug: String, title: String, buttons: ArrayList<InjectableButton> = arrayListOf()) {
        if (HEADLESS) {
            LOGGER.info(debug)
            return
        }

        val debugStr = "<html>" + debug.replace("\n", "<br />") + "</html>"

        val panel = JFrame("SpotKey - $title")

        panel.contentPane.add(DebugUI(debugStr, title, buttons))
        panel.pack()
        panel.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        panel.isResizable = false

        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (ex: java.lang.Exception) { }

        panel.iconImage = ICON

        panel.isVisible = true
    }

    /**
     * The debug UI
     */
    internal class DebugUI(dbgStr: String, title: String, buttons: ArrayList<InjectableButton>): JPanel() {
        private val debugLabel: JLabel = JLabel(title)
        private val debugText: JLabel = JLabel(dbgStr)

        init {
            preferredSize = Dimension(if (debugText.preferredSize.width < debugLabel.preferredSize.width)
                debugLabel.preferredSize.width + 48
                    else
                debugText.preferredSize.width + 48, 180)
            layout = null

            add(debugLabel)
            add(debugText)

            debugLabel.font = Font("Sans Serif", 0, 24)

            debugLabel.setBounds(15, 10, debugLabel.preferredSize.width, debugLabel.preferredSize.height)
            debugText.setBounds(45, 45, debugText.preferredSize.width, debugText.preferredSize.height)

            preferredSize = Dimension(preferredSize.width, preferredSize.height + 36)

            var curLoc = Pair(preferredSize.width - 24, preferredSize.height)
            for (button in buttons) {
                val but = JButton(button.text)

                but.addActionListener {
                    button.action.invoke()
                }

                add(but)
                but.isVisible = true
                but.setBounds(curLoc.first - but.preferredSize.width, curLoc.second - 24, but.preferredSize.width, but.preferredSize.height)
                curLoc = Pair(curLoc.first - but.preferredSize.width - 24, curLoc.second)
            }
        }
    }
}