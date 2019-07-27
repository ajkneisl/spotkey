package dev.shog.spotkey.ui

import java.awt.Dimension
import javax.swing.*
import javax.swing.UIManager

/**
 * Where the Spotify access key is pasted.
 */
class InitialFrame: JPanel() {
    var codeField: JTextField = JTextField(5)
    private var retrievedLabel: JLabel = JLabel("Paste in Retrieved Code")
    var cont: JButton = JButton("Continue")

    init {
        preferredSize = Dimension(325, 121)
        layout = null

        add(codeField)
        add(retrievedLabel)
        add(cont)

        codeField.setBounds(10, 35, 300, 40)
        retrievedLabel.setBounds(10, 5, 150, 25)
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
            frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            frame.isVisible = true

            return Pair(frame, initPanel)
        }
    }
}