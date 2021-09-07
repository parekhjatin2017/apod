package com.nasa.astronomypicture.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "fav_apod_table")
data class ApodDataModel (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long = 0,

    @ColumnInfo(name = "copyright")
    @SerializedName("copyright")
    var copyright: String?,

    @ColumnInfo(name = "date")
    @SerializedName("date")
    var date: String?,

    @ColumnInfo(name = "explanation")
    @SerializedName("explanation")
    var explanation: String?,

    @ColumnInfo(name = "hdurl")
    @SerializedName("hdurl")
    var hdurl: String?,

    @ColumnInfo(name = "media_type")
    @SerializedName("media_type")
    var media_type: String?,

    @ColumnInfo(name = "service_version")
    @SerializedName("service_version")
    var service_version: String?,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String?,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    var url: String?
)
