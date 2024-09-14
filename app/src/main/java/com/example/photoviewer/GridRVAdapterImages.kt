package com.example.photoviewer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


// on below line we are creating an
// adapter class for our grid view.
class GridRVAdapterImages(
    // on below line we are creating two
    // variables for course list and context
    private val recyclerDataArrayList: MutableList<RecyclerDataImages>,
    private val context: Context
) :
    RecyclerView.Adapter<GridRVAdapterImages.RecyclerViewHolder>() {


    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private var noMore = false


    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // in below function we are getting individual item of grid view.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridRVAdapterImages.RecyclerViewHolder {
        val imageView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item_images, parent, false)
        return RecyclerViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: GridRVAdapterImages.RecyclerViewHolder, position: Int) {
        val item = recyclerDataArrayList[position]
        holder.image_image.setImageBitmap(item.getImage())
        print(position)
        if(onLoadMoreListener != null && !isLoading && !noMore && holder.getAdapterPosition() == getItemCount() - 1) {
            isLoading = true
            onLoadMoreListener!!.onLoadMore()
        }
    }

    override fun getItemCount(): Int {
        return recyclerDataArrayList.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val image_image = itemView.findViewById<ImageView>(R.id.idCardImage)

    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
        this.onLoadMoreListener = listener
    }
    fun endLoading() {
        this.isLoading = false
    }

    fun setNoMore(noMore: Boolean) {
        this.noMore = noMore
    }
}

interface OnLoadMoreListener {
    fun onLoadMore()
}

