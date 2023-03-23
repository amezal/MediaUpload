package ni.edu.uca.mediaupload

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageAdapter(private val files: Array<ImgFile>) : BaseAdapter() {

    override fun getCount(): Int {
        return files.size
    }

    override fun getItem(position: Int): Any {
        return files[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = ImageView(parent?.context)
        imageView.layoutParams = AbsListView.LayoutParams(350, 350) // Set the size of the image
        Picasso.get().load(files[position].name).into(imageView) // Load the image using Picasso
        return imageView
    }
}