package edu.tcu.tanmaykejriwal.paint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import androidx.core.view.get
import androidx.core.view.iterator
import com.bumptech.glide.Glide
import edu.tcu.tanmaykejriwal.paint.DrawingView
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var pallet = findViewById<LinearLayout>(R.id.pallete_ll)
        var board = findViewById<DrawingView>(R.id.board)
        var undo = findViewById<ImageView>(R.id.undo)
        var gallery = findViewById<ImageView>(R.id.gallery)
        var selected=pallet[0]
        var location=""
        var save = findViewById<ImageView>(R.id.save)
        for(iv in pallet){
            iv.setOnClickListener {
                board.SetPathColor((iv.background as ColorDrawable).color)
                (iv as ImageView).setImageResource(R.drawable.path_color_selected)
                (selected as ImageView).setImageResource(R.drawable.path_color_normal)
                selected=iv
            }
        }

        undo.setOnClickListener{
            board.undoPath()
        }



        findViewById<ImageView>(R.id.brush).setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.path_width_selector)
            dialog.show()
            dialog.findViewById<ImageView>(R.id.small_width_iv).setOnClickListener {
                board.SetPathWidth(5*resources.displayMetrics.density)
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.medium_width_iv).setOnClickListener {
                board.SetPathWidth(10*resources.displayMetrics.density)
                dialog.dismiss()
            }

            dialog.findViewById<ImageView>(R.id.large_width_iv).setOnClickListener {
                board.SetPathWidth(15*resources.displayMetrics.density)
                dialog.dismiss()
            }


        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            var background = findViewById<ImageView>(R.id.background_img)
            if (uri != null) {
                location= uri.toString()
                //background.setImageURI(Uri.parse(uri.toString()));
                Glide.with(this)
                    .load(location)
                    .into(background)
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        gallery.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //will take time
        var background = findViewById<ImageView>(R.id.background_img)
        if(background.drawable==null){
            background.setBackgroundColor(Color.WHITE)
        }

        val bitmap = findViewById<FrameLayout>(R.id.frame).drawToBitmap()
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString().substring(2,11)+".jpeg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        //external means outsideof yor app
        var uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        uri?.let {
            contentResolver.openOutputStream(it).use {it_->
                //can take time
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,it_)
            }
        }



    }


}


//Notes
/*
* Paint : Store Style(including width) and color
* Path: Store the footprint
* Custom Path(color,width) : Path
* pathList -> Mutable List of <CustomPath>()
* color change or width change -> CustomPath(path.color,path.width)
*
* Hw for Tuesday 25 OCT
* Create Method SetPathColor(Color:Int)
* Create Method SetPathWidth(width:Int) dp -> gives float
* Create Undo - > remove last form pathList and the Invalidate -> undoPath()
* 3 buttons for undo,width and one color
*
* */