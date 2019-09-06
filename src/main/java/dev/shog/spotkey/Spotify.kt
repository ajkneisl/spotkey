package dev.shog.spotkey

import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.SpotifyHttpManager
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext
import dev.shog.spotkey.ui.Error
import dev.shog.spotkey.ui.InitialFrame
import java.awt.Desktop
import kotlin.system.exitProcess
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import java.io.InputStreamReader
import java.io.BufferedReader




/**
 * Manages the Spotify API
 */
object Spotify {
    /**
     * If the instance is ready to take requests
     */
    private var ready = false

    /**
     * If the client's available
     */
    var clientAvailable = false

    val SPOTIFY_API = SpotifyApi.Builder()
            .setClientId("256caa4198404370a060496d31d73f9e")
            .setClientSecret("e5ee6d7ed77e43cf9be3b04a66a58cbe")
            .setRedirectUri(SpotifyHttpManager.makeUri("https://shog.dev/spotkey"))
            .build()!!

    private val AUTHORIZATION_URI_REQUEST = SPOTIFY_API.authorizationCodeUri()
            .scope("user-read-private,user-read-email,app-remote-control,user-read-playback-state,user-modify-playback-state,user-read-currently-playing")
            .show_dialog(true)
            .build()

    /**
     * Opens the client browser to allow for the user to login/
     */
    private fun openBrowser() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(AUTHORIZATION_URI_REQUEST.execute())
        } else exitProcess(-1)
    }

    /**
     * Opens the browser and allows the user to login.
     */
    fun login() {
        if (HEADLESS) {
            val br = BufferedReader(InputStreamReader(System.`in`))
            print("Please paste your authorization code:")
            val s = br.readLine()

            val auth = try {
                SPOTIFY_API.authorizationCode(s ?: "").build().execute()
            } catch (e: Exception) {
                Error.make("Invalid authorization code!")
                exitProcess(-1)
            }

            SPOTIFY_API.accessToken = auth.accessToken
            SPOTIFY_API.refreshToken = auth.refreshToken
            ready = true

            LOGGER.info("Successfully signed into Spotify with user ${getUserData().email}.")

            clientAvailable = true
        }
        openBrowser()

        val frame = InitialFrame.create()

        frame.second.cont.addActionListener {
            frame.first.isVisible = false

            val auth = try {
                SPOTIFY_API.authorizationCode(frame.second.codeField.text ?: "").build().execute()
            } catch (e: Exception) {
                Error.make("Invalid authorization code!")
                exitProcess(-1)
            }

            SPOTIFY_API.accessToken = auth.accessToken
            SPOTIFY_API.refreshToken = auth.refreshToken
            ready = true

            LOGGER.info("Successfully signed into Spotify with user ${getUserData().email}.")

            clientAvailable = true
        }
    }

    /**
     * If it's currently playing anything.
     */
    fun isCurrentlyPlaying(): Boolean {
        val x = try {
            Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute()
        } catch (ex: java.lang.Exception) {
            return false
        } ?: return false

        return x.is_playing ?: false
    }

    /**
     * Information about what the user is currently playing
     */
    fun getCurrentPlayingData(): CurrentlyPlayingContext = Spotify.SPOTIFY_API.informationAboutUsersCurrentPlayback.build().execute()

    /**
     * If the user currently logged in is premium
     */
    fun isPremium() = if (ready) Spotify.SPOTIFY_API.currentUsersProfile.build().execute().product.name.equals("premium", true) else false
}