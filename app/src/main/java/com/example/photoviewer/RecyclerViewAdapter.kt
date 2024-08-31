package com.example.photoviewer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.NonDisposableHandle.parent


class RecyclerViewAdapter(
    recyclerDataArrayList: ArrayList<RecyclerData>,
    private val mcontext: Context
) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    private val courseDataArrayList: ArrayList<RecyclerData> = recyclerDataArrayList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        // Inflate Layout
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_item_images, parent, false)
        if (itemModel == "album") {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.card_item_albums, parent, false)
        }
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        // Set the data to textview and imageview.
        val recyclerData: RecyclerData = courseDataArrayList[position]
        if (itemModel == "album") {
            holder.courseTV?.text = recyclerData.getTitle()
        }
        holder.courseIV.setImageBitmap(recyclerData.getImg())
    }

    override fun getItemCount(): Int {
        // this method returns the size of recyclerview
        return courseDataArrayList.size
    }

    // View Holder Class to handle Recycler View.
    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseTV: TextView? = if (itemModel == "album") itemView.findViewById(R.id.idCardText) else null
        val courseIV: ImageView = itemView.findViewById(R.id.idCardImage)
    }
}