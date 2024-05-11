package com.example.matchmentor.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val experience: String,
    @SerializedName("skills") val skills: List<String>
)
