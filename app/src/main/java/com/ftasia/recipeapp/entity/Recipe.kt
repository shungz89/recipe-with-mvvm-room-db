package com.ftasia.recipeapp.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipeTable")
class Recipe(
    @ColumnInfo(name = "title") val recipleTitle: String,
    @ColumnInfo(name = "ingredients") val recipeIngredients: String,
    @ColumnInfo(name = "steps") val recipeSteps: String,
    @ColumnInfo(name = "types") val recipeTypes: String,
    @ColumnInfo(name = "imagePath") val recipeImagePath: String,
    @ColumnInfo(name = "timestamp") val timeStamp: String
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}