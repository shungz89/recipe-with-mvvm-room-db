package com.ftasia.recipeapp.utils

import android.content.Context
import android.content.res.XmlResourceParser
import com.ftasia.recipeapp.R
import com.ftasia.recipeapp.entity.RecipeType
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class XMLUtils {
    companion object {
        fun loadRecipeTypes(context: Context): List<RecipeType> {
            val recipeTypes = mutableListOf<RecipeType>()
            val parser: XmlResourceParser = context.resources.getXml(R.xml.recipetypes)
            try {
                var eventType = parser.eventType
                var currentName: String? = null
                var recipeTypeName: String? = null
                var recipeTypeId: Int? = null

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            currentName = parser.name
                            if (currentName == "recipe") {
                                recipeTypeName = null
                                recipeTypeId = null
                            }
                        }

                        XmlPullParser.TEXT -> {
                            when (currentName) {
                                "recipeTypeName" -> recipeTypeName = parser.text
                                "recipeTypeId" -> recipeTypeId = parser.text.toIntOrNull()
                            }
                        }

                        XmlPullParser.END_TAG -> {
                            if (parser.name == "recipe" && recipeTypeName != null && recipeTypeId != null) {
                                recipeTypes.add(RecipeType(recipeTypeName, recipeTypeId))
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                parser.close()
            }
            return recipeTypes
        }

        fun getTypesBasedOnId(context: Context, id: Int): RecipeType? {
            val recipeTypes = loadRecipeTypes(context)
            return recipeTypes.find { it.recipeTypeId == id }
        }

    }
}