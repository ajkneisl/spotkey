package dev.shog.spotkey.tray

import dev.shog.spotkey.handle.HotKeyHandler
import dev.shog.spotkey.handle.HotKeyLoader
import dev.shog.spotkey.ui.Error
import java.awt.*
import java.util.logging.FileHandler
import javax.swing.ImageIcon
import kotlin.system.exitProcess

/**
 * Manages system tray.
 */
object Tray {
    fun initTray() {
        val trayIcon: TrayIcon
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()
            val image = ImageIcon(this.javaClass.getResource("/img.png")).image

            val popup = PopupMenu()

            // exits spotkey
            val defaultItem = MenuItem("Quit")
            defaultItem.addActionListener {
                exitProcess(0)
            }
            popup.add(defaultItem)

            // modifies config
            val configItem = MenuItem("Config")
            configItem.addActionListener {
                try {
                    Desktop.getDesktop().edit(HotKeyLoader.SPOTKEY_CFG)
                } catch (e: Exception) {
                    Error.make("You don't have a default program to open .json files!")
                }
            }
            popup.add(configItem)

            trayIcon = TrayIcon(image, "SpotKey", popup)

            tray.add(trayIcon)

        } else {
            Error.make("Your system doesn't support SystemTray.")
            exitProcess(-1)
        }
    }
}