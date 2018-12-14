/*
 * DownloadWorker.kt
 * Implements the DownloadWorker class
 * A DownloadWorker is a worker that triggers download actions when the app is not in use
 *
 * This file is part of
 * ESCAPEPODS - Free and Open Podcast App
 *
 * Copyright (c) 2018 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.y20k.escapepods.helpers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.y20k.escapepods.Keys


/*
 * DownloadWorker class
 */
class DownloadWorker(context : Context, params : WorkerParameters): Worker(context, params) {

    /* Define log tag */
    private val TAG: String = LogHelper.makeLogTag(DownloadWorker::class.java)


    /* Overrides doWork */
    override fun doWork(): Result {
        // determine what type of download is requested
        when(inputData.getInt(Keys.KEY_DOWNLOAD_WORK_REQUEST,0)) {
            // CASE: update collection
            Keys.REQUEST_UPDATE_COLLECTION -> { updateCollection() }
            // CASE: add podcast to collection
            Keys.REQUEST_ADD_PODCASTS -> { addPodcasts() }
            // CASE: download episode
            Keys.REQUEST_DOWNLOAD_EPISODE -> { downloadEpisode() }
        }
        return Result.success()
        // (Returning Result.retry() tells WorkManager to try this task again later; Result.failure() says not to try again.)
    }


    /* Updates podcast collection */
    private fun updateCollection() {
        DownloadHelper.updateCollection(applicationContext)
    }


    /* Add podcasts */
    private fun addPodcasts() {
        val podcastUrls: Array<String> = inputData.getStringArray(Keys.KEY_PODCAST_URLS) ?: emptyArray()
        DownloadHelper.downloadPodcasts(applicationContext, podcastUrls)
    }


    /* Downloads an episode */
    private fun downloadEpisode() {
        val mediaId: String = inputData.getString(Keys.KEY_EPISODE_MEDIA_ID) ?: ""
        val ignoreWifiRestriction: Boolean = inputData.getBoolean(Keys.KEY_IGNORE_WIFI_RESTRICTION, false)
        DownloadHelper.downloadEpisode(applicationContext, mediaId, ignoreWifiRestriction)
    }

}