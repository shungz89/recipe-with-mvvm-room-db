package com.ftasia.recipeapp

import android.content.Intent
import android.content.res.XmlResourceParser
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ftasia.rcp.com.ftasia.recipeapp.RecipeClickDeleteInterface
import com.ftasia.rcp.com.ftasia.recipeapp.RecipeClickInterface
import com.ftasia.rcp.com.ftasia.recipeapp.RecipeRVAdapter
import com.ftasia.recipeapp.constant.IntentConstant
import com.ftasia.recipeapp.entity.Recipe
import com.ftasia.recipeapp.entity.RecipeType
import com.ftasia.recipeapp.ui.activity.AddEditRecipeActivity
import com.ftasia.recipeapp.viewmodel.RecipeViewModal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException


class MainActivity : AppCompatActivity(), RecipeClickInterface, RecipeClickDeleteInterface {

    //on below line we are creating a variable for our recycler view, exit text, button and viewmodal.
    lateinit var viewModal: RecipeViewModal
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    lateinit var spinnerRecipeTypes: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //on below line we are initializing all our variables.
        notesRV = findViewById(R.id.notesRV)
        addFAB = findViewById(R.id.idFAB)
        spinnerRecipeTypes = findViewById(R.id.recipeTypesSp)


        // Load and parse the XML data
        val recipeTypes = loadRecipeTypes()

        // Create a new list with "All" as the first item
        val extendedRecipeTypes = mutableListOf(RecipeType("All", -1))
        extendedRecipeTypes.addAll(recipeTypes)

        // Create an ArrayAdapter using the new list
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            extendedRecipeTypes.map { it.recipeTypeName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter to the spinner
        spinnerRecipeTypes.adapter = adapter

        // Set the OnItemSelectedListener
        spinnerRecipeTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = extendedRecipeTypes[position]
//                viewModal.filterRecipesByType(selectedItem.recipeTypeId)
                if (selectedItem.recipeTypeId == -1) {
                    // "All" selected
                    viewModal.allRecipes.observe(this@MainActivity, Observer { list ->
                        list?.let {
                            (notesRV.adapter as RecipeRVAdapter).updateList(it)
                        }
                    })
                } else {
                    // Specific recipe type selected
                    viewModal.allRecipes.observe(this@MainActivity, Observer { list ->
                        list?.let {
                            (notesRV.adapter as RecipeRVAdapter).updateList(it.filter { it.recipeTypes == "${selectedItem.recipeTypeId}" })
                        }
                    })
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        //on below line we are setting layout manager to our recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)
        //on below line we are initializing our adapter class.
        val noteRVAdapter = RecipeRVAdapter(this, this, this)
        //on below line we are setting adapter to our recycler view.
        notesRV.adapter = noteRVAdapter
        //on below line we are initializing our view modal.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RecipeViewModal::class.java)
        //on below line we are calling all notes methof from our view modal class to observer the changes on list.
        viewModal.allRecipes.observe(this, Observer { list ->
            list?.let {
                //on below line we are updating our list.
                noteRVAdapter.updateList(it)
                for(i in it){
                    Log.d("Recipe ImgPath", i.recipeImagePath)
                }
            }
        })
        addFAB.setOnClickListener {
            //adding a click listner for fab button and opening a new intent to add a new note.
            val intent = Intent(this@MainActivity, AddEditRecipeActivity::class.java)
            intent.putExtra(IntentConstant.RECIPE_DETAILS_PAGE_ACTION, IntentConstant.ACTION_ADD)
            startActivity(intent)
        }
    }

    override fun onNoteClick(recipe: Recipe) {
        //opening a new intent and passing a data to it.
        val intent = Intent(this@MainActivity, AddEditRecipeActivity::class.java)
        intent.putExtra(IntentConstant.RECIPE_DETAILS_PAGE_ACTION, IntentConstant.ACTION_EDIT)
        intent.putExtra(IntentConstant.RECIPE_TITLE, recipe.recipleTitle)
        intent.putExtra(IntentConstant.RECIPE_TYPE, recipe.recipeTypes)
        intent.putExtra(IntentConstant.RECIPE_INGREDIENTS, recipe.recipeIngredients)
        intent.putExtra(IntentConstant.RECIPE_STEPS, recipe.recipeSteps)
        intent.putExtra(IntentConstant.RECIPE_IMAGE_PATH, recipe.recipeImagePath)
        intent.putExtra(IntentConstant.RECIPE_ID, recipe.id)
        startActivity(intent)
    }

    override fun onDeleteIconClick(note: Recipe) {
        //in on note click method we are calling delete method from our viw modal to delete our not.
        viewModal.deleteRecipe(note)
        //displaying a toast message
        Toast.makeText(this, "${note.recipleTitle} Deleted", Toast.LENGTH_LONG).show()
    }

    private fun loadRecipeTypes(): List<RecipeType> {
        val recipeTypes = mutableListOf<RecipeType>()
        val parser: XmlResourceParser = resources.getXml(R.xml.recipetypes)
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

}