/*
 * NotificationHelper.kt
 * Implements the NotificationHelper class
 * A NotificationHelper creates and configures a notification
 *
 * This file is part of
 * ESCAPEPOD - Free and Open Podcast App
 *
 * Copyright (c) 2018-21 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.y20k.escapepod.helpers

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.y20k.escapepod.Keys
import org.y20k.escapepod.R


/*
 * NotificationHelper class
 * Credit: https://github.com/android/uamp/blob/5bae9316b60ba298b6080de1fcad53f6f74eb0bf/common/src/main/java/com/example/android/uamp/media/UampNotificationManager.kt
 */
class NotificationHelper(private val context: Context, sessionToken: MediaSessionCompat.Token, notificationListener: PlayerNotificationManager.NotificationListener) {

    /* Define log tag */
    private val TAG: String = LogHelper.makeLogTag(NotificationHelper::class.java)


    /* Main class variables */
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Main + serviceJob)
    private val notificationManager: PlayerNotificationManager


    /* Constructor */
    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                Keys.NOW_PLAYING_NOTIFICATION_CHANNEL_ID,
                R.string.notification_now_playing_channel_name,
                R.string.notification_now_playing_channel_description,
                Keys.NOW_PLAYING_NOTIFICATION_ID,
                DescriptionAdapter(mediaController),
                notificationListener
        ).apply {
            // note: notification icons are customized in values.xml
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_notification_app_icon_white_24dp)
            setUsePlayPauseActions(true)
            setControlDispatcher(DefaultControlDispatcher(Keys.SKIP_FORWARD_TIME_SPAN, Keys.SKIP_BACK_TIME_SPAN))
            setUseStopAction(true) // set true to display the dismiss button
            setUsePreviousAction(false)
            setUsePreviousActionInCompactView(false)
            setUseNextAction(false)
            setUseNextActionInCompactView(false)
        }
    }


    /* Hides notification via notification manager */
    fun hideNotification() {
        notificationManager.setPlayer(null)
    }


    /* Displays notification via notification manager */
    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }


    /*
     * Inner class: Create content of notification from metaddata
     */
    private inner class DescriptionAdapter(private val controller: MediaControllerCompat):  PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? = controller.sessionActivity

        override fun getCurrentContentText(player: Player) = controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player) = controller.metadata.description.title.toString()

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
            val iconUri: Uri? = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {
                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(currentIconUri: Uri): Bitmap {
            return withContext(IO) {
                // Block on downloading artwork.
                ImageHelper.getPodcastCover(context, currentIconUri.toString())
            }
        }
    }
    /*
    * End of inner class
    */
}
