import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.databinding.RowImagesPickedBinding
import com.example.campusolx.models.ModelImagePicked

class AdapterImagePicked(
    private val context: Context,
    private val imagesPickedArrayList: ArrayList<ModelImagePicked>
) : RecyclerView.Adapter<AdapterImagePicked.HolderImagePicked>() {

    private var onImageLoadedListener: OnImageLoadedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagePicked {
        val binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagePicked(binding)
    }

    override fun onBindViewHolder(holder: HolderImagePicked, position: Int) {
        val model = imagesPickedArrayList[position]
        val imageUri: Uri = model.imageUri ?: Uri.EMPTY
        try {
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.baseline_image_24)
                .into(holder.binding.imageTv)
        } catch (e: Exception) {
            Log.e("IMAGES_TAG", "onBindViewHolder", e)
        }

        holder.binding.closeBtn.setOnClickListener {
            imagesPickedArrayList.remove(model)
            notifyDataSetChanged()
        }

        // Notify the listener when the image is loaded
        onImageLoadedListener?.onImageLoaded(imageUri, holder.binding.imageTv)
    }

    override fun getItemCount(): Int {
        return imagesPickedArrayList.size
    }

    inner class HolderImagePicked(val binding: RowImagesPickedBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Perform any action when an item is clicked
                }
            }
        }
    }

    interface OnImageLoadedListener {
        fun onImageLoaded(imageUri: Uri, imageView: ImageView)
    }

    fun setOnImageLoadedListener(listener: OnImageLoadedListener) {
        this.onImageLoadedListener = listener
    }
}
