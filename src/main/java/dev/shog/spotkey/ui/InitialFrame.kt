package dev.shog.spotkey.ui

import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.UIManager

/**
 * Where the Spotify access key is pasted.
 */
class InitialFrame: JPanel() {
    /**
     * The field where the code should be pasted.
     */
    var codeField: JTextField = JTextField(5)

    /**
     * Label for [codeField].
     */
    private var retrievedLabel: JLabel = JLabel("Paste in Retrieved Code")

    /**
     * Inserts.
     */
    var cont: JButton = JButton("Continue")

    init {
        preferredSize = Dimension(325, 121)
        layout = null

        add(codeField)
        add(retrievedLabel)
        add(cont)

        retrievedLabel.font = Font("Sans Serif", 0, 24)

        codeField.setBounds(10, 45, 300, codeField.preferredSize.height)
        retrievedLabel.setBounds(10, 5, retrievedLabel.preferredSize.width, 25)
        cont.setBounds(10, 80, 100, 25)

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Creates a JFrame with the [InitialFrame].
         */
        fun create(): Pair<JFrame, InitialFrame> {
            val frame = JFrame("SpotKey")
            val initPanel = InitialFrame()

            frame.contentPane.add(initPanel)
            frame.pack()
            frame.isAlwaysOnTop = true
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.isVisible = true

            return Pair(frame, initPanel)
        }
    }
}