package com.example.astroguessr.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "stars")
@Parcelize
data class Star(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "proper")
    val name: String?,
    @ColumnInfo(name = "bayer")
    val bayer: String?,
    @ColumnInfo(name = "ra")
    val ra: Double,
    @ColumnInfo(name = "dec")
    val dec: Double,
    @ColumnInfo(name = "mag")
    val mag: Float,
    @ColumnInfo(name = "con")
    val constellation: String
) : Parcelable