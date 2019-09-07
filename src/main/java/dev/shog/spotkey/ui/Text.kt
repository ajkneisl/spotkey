package dev.shog.spotkey.ui

import dev.shog.spotkey.HEADLESS
import dev.shog.spotkey.ICON
import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.handle.HotKeyLoader
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

/**
 * Where things can be inserted.
 */
object Text {
    /**
     * Create an text-insertable page.
     */
    fun make(title: String, obj: String, onError: (String) -> Unit, onSuccess: JFrame.(String) -> Unit) {
        if (HEADLESS) {
            onError.invoke("JVM is currently headless!")
            return
        }

        val panel = JFrame("SpotKey - $title")
        val ui = TextUI(obj, onError)
        panel.contentPane.add(ui)
        panel.pack()
        panel.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (ex: java.lang.Exception) { }

        ui.cont.addActionListener {
            onSuccess.invoke(panel, ui.textField.text)
        }

        panel.iconImage = ICON

        panel.isVisible = true
    }

    /**
     * The Text UI
     */
    internal class TextUI(obj: String, onError: (String) -> Unit): JPanel() {
        /**
         * Where the code is inserted.
         */
        internal var textField: JTextField = JTextField(5)

        /**
         * Label for [textField].
         */
        private var retrievedLabel: JLabel = JLabel(obj)

        /**
         * Inserts.
         */
        internal var cont: JButton = JButton("Continue")

        init {
            preferredSize = Dimension(325, 121)
            layout = null

            add(textField)
            add(retrievedLabel)
            add(cont)

            retrievedLabel.font = Font("Sans Serif", 0, 24)

            textField.setBounds(10, 45, 300, textField.preferredSize.height)
            retrievedLabel.setBounds(10, 5, retrievedLabel.preferredSize.width, 25)
            cont.setBounds(10, 80, 100, 25)

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: Exception) {
                onError.invoke(e.message.toString())
            }
        }
    }
}