package com.example.photoviewer


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// on below line we are creating an
// adapter class for our grid view.
class GridRVAdapterAlbums(
    // on below line we are creating two
    // variables for course list and context
    private val recyclerDataArrayList: MutableList<RecyclerDataAlbums>,
    private val context: Context
) :
    RecyclerView.Adapter<GridRVAdapterAlbums.RecyclerViewHolder>() {



    // below function is use to return item id of grid view.
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // in below function we are getting individual item of grid view.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GridRVAdapterAlbums.RecyclerViewHolder {
        val albumView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_item_albums, parent, false)
        return RecyclerViewHolder(albumView)
    }

    override fun onBindViewHolder(holder: GridRVAdapterAlbums.RecyclerViewHolder, position: Int) {
        val item = recyclerDataArrayList[position]
        holder.album_name.text = item.getTitle()
        holder.image_album.setImageBitmap(item.getImgeAlbum())
    }

    override fun getItemCount(): Int {
        return recyclerDataArrayList.size
    }


    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val album_name = itemView.findViewById<TextView>(R.id.idCardText)
        internal val image_album = itemView.findViewById<ImageView>(R.id.idCardImage)



    }
}

