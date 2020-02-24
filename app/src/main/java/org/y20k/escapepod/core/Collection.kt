/*
 * Collection.kt
 * Implements the Collection class
 * A Collection object holds a list of Podcasts
 *
 * This file is part of
 * ESCAPEPOD - Free and Open Podcast App
 *
 * Copyright (c) 2018-20 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.y20k.escapepod.core

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize
import org.y20k.escapepod.Keys
import java.util.*


/*
 * Collection class
 */
@Keep
@Parcelize
data class Collection(@Expose val version: Int = Keys.CURRENT_COLLECTION_CLASS_VERSION_NUMBER,
                      @Expose var podcasts: MutableList<Podcast> = mutableListOf<Podcast>(),
                      @Expose var modificationDate: Date = Date()) : Parcelable {

    /* overrides toString method */
    override fun toString(): String {
        val stringBuilder: StringBuilder = StringBuilder()
        stringBuilder.append("Format version: $version\n")
        stringBuilder.append("Number of podcasts in collection: ${podcasts.size}\n\n")
        podcasts.forEach {
            stringBuilder.append("${it.toString()}\n")
        }
        return stringBuilder.toString()
    }


    /* Creates a deep copy of a Collection */
    fun deepCopy(): Collection {
        val podcastsCopy: MutableList<Podcast> = mutableListOf<Podcast>()
        podcasts.forEach { podcastsCopy.add(it.deepCopy()) }
        return Collection(version = version,
                          podcasts = podcastsCopy,
                          modificationDate = modificationDate)
    }

}

