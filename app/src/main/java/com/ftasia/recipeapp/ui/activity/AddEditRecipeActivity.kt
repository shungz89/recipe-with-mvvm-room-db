package com.ftasia.recipeapp.ui.activity

import android.app.Activity
import android.content.res.XmlResourceParser
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ftasia.rcp.com.ftasia.recipeapp.RecipeRVAdapter
import com.ftasia.recipeapp.R
import com.ftasia.recipeapp.constant.IntentConstant
import com.ftasia.recipeapp.entity.Recipe
import com.ftasia.recipeapp.entity.RecipeType
import com.ftasia.recipeapp.utils.XMLUtils
import com.ftasia.recipeapp.viewmodel.RecipeViewModal
import com.github.dhaval2404.imagepicker.ImagePicker
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class AddEditRecipeActivity : AppCompatActivity() {
    //on below line we are creating variables for our UI components.
    lateinit var recipeTitleEdt: EditText
    lateinit var recipeIngredientsEdt: EditText
    lateinit var recipeStepsEdt: EditText
    lateinit var spinnerRecipeTypes: Spinner
    var recipeTypeSelectedIndex = -1


    lateinit var recipeIV: ImageView
    lateinit var saveBtn: Button
    lateinit var uploadBtn: Button


    var bitmapString = "";

    //on below line we are creating variable for viewmodal and and integer for our recipe id.
    lateinit var viewModal: RecipeViewModal
    var recipe = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_recipe)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //on below line we are initlaiing our view modal.
        viewModal = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RecipeViewModal::class.java)
        //on below line we are initializing all our variables.
        recipeTitleEdt = findViewById(R.id.idEdtTitle)
        recipeIV = findViewById(R.id.idRecipeIV)
        recipeIngredientsEdt = findViewById(R.id.idEdtIngredients)
        recipeStepsEdt = findViewById(R.id.idEdtSteps)
        saveBtn = findViewById(R.id.idBtn)
        uploadBtn = findViewById(R.id.idUploadImgBtn)
//        uploadBtn = findViewById(R.id.idUploadImgBtn)

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
                recipeTypeSelectedIndex = selectedItem.recipeTypeId
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }

        //on below line we are getting data passsed via an intent.
        val recipeDetailsPageAction =
            intent.getStringExtra(IntentConstant.RECIPE_DETAILS_PAGE_ACTION)
        if (recipeDetailsPageAction.equals(IntentConstant.ACTION_EDIT)) {
            supportActionBar?.setTitle("Edit Recipe")

            //on below line we are setting data to edit text.
            val recipeTitle = intent.getStringExtra(IntentConstant.RECIPE_TITLE)
            val recipeType = intent.getStringExtra(IntentConstant.RECIPE_TYPE)
            recipeTypeSelectedIndex = try {
                recipeType?.toInt() ?: -1
            } catch (e: Exception) {
                -1
            }
            spinnerRecipeTypes.setSelection(recipeTypeSelectedIndex)
            val recipeIngredients = intent.getStringExtra(IntentConstant.RECIPE_INGREDIENTS)
            val recipeSteps = intent.getStringExtra(IntentConstant.RECIPE_STEPS)
            bitmapString = intent.getStringExtra(IntentConstant.RECIPE_IMAGE_PATH) ?: ""
            recipe = intent.getIntExtra(IntentConstant.RECIPE_ID, -1)
            saveBtn.setText("Update Recipe")
            recipeTitleEdt.setText(recipeTitle)
            Log.d("1BitMap", bitmapString);

            if (!bitmapString.equals("")) {
                loadImageIntoIV(bitmapString)
            }

            // Load the Bitmap into the ImageView using Glide

            recipeIngredientsEdt.setText(recipeIngredients)
            recipeStepsEdt.setText(recipeSteps)
        } else {
            supportActionBar?.setTitle("Add Recipe")
            saveBtn.setText("Save Recipe")
        }


        //on below line we are adding click listner to our save button.
        saveBtn.setOnClickListener {
            //on below line we are getting title and desc from edit text.
            val recipeTitle = recipeTitleEdt.text.toString()
            val recipeIngredients = recipeIngredientsEdt.text.toString()
            val recipeSteps = recipeStepsEdt.text.toString()

            //on below line we are checking the type and then saving or updating the data.
            if (recipeDetailsPageAction.equals("Edit")) {
                if (recipeTitle.isNotEmpty() && recipeIngredients.isNotEmpty() && recipeSteps.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())

                    //TODO: Image Update here
                    val image = bitmapString;

                    val updatedRecipe =
                        Recipe(
                            recipeTitle,
                            recipeIngredients,
                            recipeSteps,
                            "$recipeTypeSelectedIndex",
                            image,
                            currentDateAndTime
                        )
                    updatedRecipe.id = recipe
                    viewModal.updateRecipe(updatedRecipe)
                    Toast.makeText(this, "Recipe Updated..", Toast.LENGTH_LONG).show()
                    this.finish()

                } else {
                    Toast.makeText(this, "Please fill in Title, Ingredients & Steps", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                if (recipeTitle.isNotEmpty() && recipeIngredients.isNotEmpty()) {
                    val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
                    val currentDateAndTime: String = sdf.format(Date())

                    //TODO: Image Update here
                    val image = bitmapString

                    //if the string is not empty we are calling a add recipe method to add data to our room database.
                    viewModal.addRecipe(
                        Recipe(
                            recipeTitle,
                            recipeIngredients,
                            recipeSteps,
                            "$recipeTypeSelectedIndex",
                            image,
                            currentDateAndTime
                        )
                    )
                    Toast.makeText(this, "$recipeTitle Added", Toast.LENGTH_LONG).show()
                    this.finish()

                } else {
                    Toast.makeText(this, "Please fill in Title and Description", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        uploadBtn.setOnClickListener {

            ImagePicker.with(this)
                .crop() //Crop image(Optional), Check Customization for more option
                .compress(1024)//Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                val fileUriPath = fileUri.path
                // Assuming you have a URI named 'imageUri' and 'context' is the Context object
//                val bitmap = getBitmapFromUri(this, fileUri)
                if (fileUriPath != null) {
                    // Use the bitmap here
                    bitmapString = fileUriPath;

                    loadImageIntoIV(bitmapString)
                    Toast.makeText(this, "Successful Added Image", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle failure to load bitmap
                    Log.d("Bitmap", "No Bitmap");
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish();
//                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun loadImageIntoIV(image: String) {
        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.no_image)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(recipeIV)
    }


}