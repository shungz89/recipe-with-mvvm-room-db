package com.ftasia.rcp.com.ftasia.recipeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ftasia.recipeapp.R
import com.ftasia.recipeapp.entity.Recipe

class RecipeRVAdapter(
    val context: Context,
    val recipeDeleteInterface: RecipeClickDeleteInterface,
    val recipeClickInterface: RecipeClickInterface
) :
    RecyclerView.Adapter<RecipeRVAdapter.ViewHolder>() {

    //on below line we are creating a variable for our all recipe list.
    private val allRecipes = ArrayList<Recipe>()

    //on below line we are creating a view holder class.
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //on below line we are creating an initializing all our variables which we have added in layout file.
        val recipeTV = itemView.findViewById<TextView>(R.id.idTVRecipe)
        val dateTV = itemView.findViewById<TextView>(R.id.idTVDate)
        val deleteIV = itemView.findViewById<ImageView>(R.id.idIVDelete)
        val recipeIV = itemView.findViewById<ImageView>(R.id.idIVRecipe)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflating our layout file for each item of recycler view.
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recipe_rv_item,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //on below line we are setting data to item of recycler view.
        holder.recipeTV.setText(allRecipes.get(position).recipleTitle)
        Glide.with(context)
            .load(allRecipes.get(position).recipeImagePath)
            .placeholder(R.drawable.no_image)
            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
            .into(holder.recipeIV)
        holder.dateTV.setText("Last Updated : " + allRecipes.get(position).timeStamp)
        //on below line we are adding click listner to our delete image view icon.
        holder.deleteIV.setOnClickListener {
            //on below line we are calling a note click interface and we are passing a position to it.
            recipeDeleteInterface.onDeleteIconClick(allRecipes.get(position))
        }

        //on below line we are adding click listner to our recycler view item.
        holder.itemView.setOnClickListener {
            //on below line we are calling a note click interface and we are passing a position to it.
            recipeClickInterface.onNoteClick(allRecipes.get(position))
        }
    }

    override fun getItemCount(): Int {
        //on below line we are returning our list size.
        return allRecipes.size
    }

    //below method is use to update our list of notes.
    fun updateList(newList: List<Recipe>) {
        //on below line we are clearing our notes array list/
        allRecipes.clear()
        //on below line we are adding a new list to our all notes list.
        allRecipes.addAll(newList)
        //on below line we are calling notify data change method to notify our adapter.
        notifyDataSetChanged()
    }

}

interface RecipeClickDeleteInterface {
    //creating a method for click action on delete image view.
    fun onDeleteIconClick(note: Recipe)
}

interface RecipeClickInterface {
    //creating a method for click action on recycler view item for updating it.
    fun onNoteClick(note: Recipe)
}