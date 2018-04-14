package com.paralect.citiesquiz.data

import android.content.Context
import android.support.annotation.RawRes
import com.google.gson.Gson
import io.reactivex.Single
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by Oleg Tarashkevich on 14/04/2018.
 */
class DataUtil {

    private val gson = Gson()

    fun <T> getResponseFromResObservable(context: Context, clazz: Class<T>, @RawRes rawId: Int): Single<T> {
        return getRawStringObservable(context, rawId).map{ s -> deserialize(s, clazz) }
    }

    private fun <L> deserialize(json: String, tClass: Class<L>): L {
        return gson.fromJson(json, tClass)
    }

    private fun getRawStringObservable(context: Context, @RawRes rawId: Int): Single<String> {
        return Single.create {
            try {
                if (!it.isDisposed) {
                    val jsonString = readJsonFromRes(context, rawId)
                    it.onSuccess(jsonString)
                }
            } catch (e: IOException) {
                it.onError(e)
            }
        }
    }


    @Throws(IOException::class)
    private fun readJsonFromRes(context: Context, @RawRes rawId: Int): String {
        val inputStream = context.getResources().openRawResource(rawId)
        var content: String = inputStream.readBytes().toString(Charset.defaultCharset())
        return content
    }
}
