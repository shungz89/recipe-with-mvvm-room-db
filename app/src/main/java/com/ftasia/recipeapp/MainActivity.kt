package com.ftasia.recipeapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.ftasia.recipeapp.utils.XMLUtils
import com.ftasia.recipeapp.viewmodel.RecipeViewModal
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), RecipeClickInterface, RecipeClickDeleteInterface {

    //on below line we are creating a variable for our recycler view, exit text, button and viewmodal.
    lateinit var viewModal: RecipeViewModal
    lateinit var recipesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton
    lateinit var spinnerRecipeTypes: Spinner
    lateinit var progressDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //on below line we are initializing all our variables.
        recipesRV = findViewById(R.id.recipesRV)
        addFAB = findViewById(R.id.idFAB)
        spinnerRecipeTypes = findViewById(R.id.recipeTypesSp)


        // Load and parse the XML data
        val recipeTypes = XMLUtils.loadRecipeTypes(application)

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
                            (recipesRV.adapter as RecipeRVAdapter).updateList(it)
                        }
                    })
                } else {
                    // Specific recipe type selected
                    viewModal.allRecipes.observe(this@MainActivity, Observer { list ->
                        list?.let {
                            (recipesRV.adapter as RecipeRVAdapter).updateList(it.filter { it.recipeTypes == "${selectedItem.recipeTypeId}" })
                        }
                    })
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        //on below line we are setting layout manager to our recycler view.
        recipesRV.layoutManager = LinearLayoutManager(this)
        //on below line we are initializing our adapter class.
        val recipeRVAdapter = RecipeRVAdapter(this, this, this)
        //on below line we are setting adapter to our recycler view.
        recipesRV.adapter = recipeRVAdapter


        // Set up the progress dialog
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog.show()

        //on below line we are initializing our view modal.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RecipeViewModal::class.java)

        // Observe the loading status
        viewModal.isLoading.observe(this, Observer { isLoading ->
            if (isLoading == false) {
                progressDialog.dismiss()
            }
        })

        //on below line we are calling all recipes method from our view modal class to observer the changes on list.
        viewModal.allRecipes.observe(this, Observer { list ->
            list?.let {
                //on below line we are updating our list.
                recipeRVAdapter.updateList(it)
                for (i in it) {
                    Log.d("Recipe ImgPath", i.recipeImagePath)
                }
            }
        })
        addFAB.setOnClickListener {
            //adding a click listner for fab button and opening a new intent to add a new recipe.
            val intent = Intent(this@MainActivity, AddEditRecipeActivity::class.java)
            intent.putExtra(IntentConstant.RECIPE_DETAILS_PAGE_ACTION, IntentConstant.ACTION_ADD)
            startActivity(intent)
        }
    }

    override fun onRecipeClick(recipe: Recipe) {
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

    override fun onDeleteIconClick(recipe: Recipe) {
        //in on recipe click method we are calling delete method from our viw modal to delete our not.
        viewModal.deleteRecipe(recipe)
        //displaying a toast message
        Toast.makeText(this, "${recipe.recipleTitle} Deleted", Toast.LENGTH_LONG).show()
    }


}