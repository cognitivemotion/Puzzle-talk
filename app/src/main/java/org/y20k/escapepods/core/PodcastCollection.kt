/*
 * PodcastCollection.kt
 * Implements the PodcastCollection class
 * A PodcastCollection object holds a list of Podcasts
 *
 * This file is part of
 * ESCAPEPODS - Free and Open Podcast App
 *
 * Copyright (c) 2018 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */



package org.y20k.escapepods.core

import com.google.gson.annotations.Expose
import org.y20k.escapepods.helpers.Keys
import java.util.*


/*
 * PodcastCollection class
 */
class PodcastCollection(@Expose var podcasts: MutableList<Podcast> = mutableListOf<Podcast>(),
                        @Expose val version: Int = Keys.CURRENT_COLLECTION_CLASS_VERSION_NUMBER,
                        @Expose var lastUpdate: Date = Date(0)) {


    /* Checks if feed is already in collection */
    fun isInCollection(remotePodcastFeedLocation: String): Boolean {
        for (podcast in podcasts) {
            if (podcast.remotePodcastFeedLocation == remotePodcastFeedLocation) return true
        }
        return false
    }

}


