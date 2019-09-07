package dev.shog.spotkey.tray

import dev.shog.spotkey.LOGGER
import dev.shog.spotkey.handle.HotKeyLoader
import dev.shog.spotkey.ui.Error
import dev.shog.spotkey.ui.Text
import java.awt.*
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

            // modifies config
            val configItem = MenuItem("Config")
            configItem.addActionListener {
                try {
                    Desktop.getDesktop().edit(HotKeyLoader.SPOTKEY_CFG)
                } catch (e: Exception) {
                    Error.make("You don't have a default program to open .json files!")
                }
            }

            // sets what device you want used when you first play or pause.
            val defaultDevice = MenuItem("Default Device")
            defaultDevice.addActionListener {
                Text.make("Default Device", "Default Device ID", { str ->
                    Error.make(str)
                }, { str ->
                    // Just saves it to the config file
                    HotKeyLoader.writeCfg(HotKeyLoader.getCfg().put("default-device", str))

                    LOGGER.debug("$str is now the default device ID.")

                    dispose()
                })
            }

            popup.add(configItem)
            popup.add(defaultDevice)
            popup.add(defaultItem)

            trayIcon = TrayIcon(image, "SpotKey", popup)

            tray.add(trayIcon)

        } else {
            Error.make("Your system doesn't support System Tray, avoiding.")
        }
    }
}