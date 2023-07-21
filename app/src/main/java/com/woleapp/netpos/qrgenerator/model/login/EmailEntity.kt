package com.woleapp.netpos.qrgenerator.model.login

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "email_table")
data class EmailEntity(
    @PrimaryKey val email: String
)

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val email: String,
    val pin: String
)

