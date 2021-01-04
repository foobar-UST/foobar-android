package com.foobarust.data.remoteapi

import com.foobarust.data.common.Constants.GM_DIR_DEST
import com.foobarust.data.common.Constants.GM_DIR_KEY
import com.foobarust.data.common.Constants.GM_DIR_ORIGIN
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by kevin on 1/4/21
 */

interface MapService {

    @GET
    suspend fun getDirections(
        @Query(GM_DIR_KEY) key: String,
        @Query(GM_DIR_ORIGIN) origin: String,
        @Query(GM_DIR_DEST) destination: String
    )
}