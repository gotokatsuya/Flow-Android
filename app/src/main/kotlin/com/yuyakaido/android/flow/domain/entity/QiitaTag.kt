package com.yuyakaido.android.flow.domain.entity

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import java.io.Serializable

/**
 * Created by yuyakaido on 8/15/16.
 */
@Table(value = "QiitaTag")
data class QiitaTag constructor(
        @Column(value = "name", unique = true, uniqueOnConflict = com.github.gfx.android.orma.annotation.OnConflict.IGNORE)
        @Setter("name")
        val name: String,

        @Column(value = "subscribed")
        @Setter("subscribed")
        var subscribed: Boolean = false) : Serializable