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

// Adapter for displaying picked images in a RecyclerView
class AdapterImagePicked(
    private val context: Context,
    private val imagesPickedArrayList: ArrayList<ModelImagePicked>
) : RecyclerView.Adapter<AdapterImagePicked.HolderImagePicked>() {

    // Listener for image loaded events
    private var onImageLoadedListener: OnImageLoadedListener? = null

    // Create a new ViewHolder for the image item view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImagePicked {
        // Inflate the row_images_picked.xml layout to create a view for an image item
        val binding = RowImagesPickedBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderImagePicked(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: HolderImagePicked, position: Int) {
        val model = imagesPickedArrayList[position]
        val imageUri: Uri = model.imageUri ?: Uri.EMPTY

        try {
            // Load the image using Glide library, and set a placeholder and error image
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.baseline_image_24)
                .error(com.google.android.material.R.drawable.mtrl_ic_error) // Add an error image if needed
                .centerCrop()
                .into(holder.binding.imageTv)
        } catch (e: Exception) {
            // Handle any exceptions that occur while loading images
            Log.e("IMAGES_TAG", "onBindViewHolder", e)
        }

        // Handle click event to remove the image from the list
        holder.binding.closeBtn.setOnClickListener {
            imagesPickedArrayList.remove(model)
            notifyDataSetChanged()
        }

        // Notify the listener when the image is loaded
        onImageLoadedListener?.onImageLoaded(imageUri, holder.binding.imageTv)
    }

    // Return the total number of picked images in the list
    override fun getItemCount(): Int {
        return imagesPickedArrayList.size
    }

    // ViewHolder class to hold and manage the image item views
    inner class HolderImagePicked(val binding: RowImagesPickedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            // Initialize click listener for the image item view
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Perform any action when an item is clicked
                }
            }
        }
    }

    // Interface to listen for image loaded events
    interface OnImageLoadedListener {
        fun onImageLoaded(imageUri: Uri, imageView: ImageView)
    }

    // Set the listener for image loaded events
    fun setOnImageLoadedListener(listener: OnImageLoadedListener) {
        this.onImageLoadedListener = listener
    }
}
