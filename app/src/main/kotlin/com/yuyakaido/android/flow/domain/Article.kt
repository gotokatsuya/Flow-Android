package com.yuyakaido.android.flow.domain

/**
 * Created by yuyakaido on 7/23/16.
 */
interface Article {
    fun title(): String
    fun url(): String
    fun thumbnail(): String?
}