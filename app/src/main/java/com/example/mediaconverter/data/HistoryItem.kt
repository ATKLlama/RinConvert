package com.example.mediaconverter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "input_url")
    val inputUrl: String,

    @ColumnInfo(name = "output_file_path")
    val outputFilePath: String,

    @ColumnInfo(name = "output_format")
    val outputFormat: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),

    @ColumnInfo(name = "status")
    val status: String = "COMPLETED" // COMPLETED, FAILED, CANCELLED
)