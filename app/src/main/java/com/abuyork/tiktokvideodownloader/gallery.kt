package com.abuyork.tiktokvideodownloader
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abuyork.tiktokvideodownloader.adapters.Adapter_VideoFolder
import com.abuyork.tiktokvideodownloader.models.Model_Video
import com.abuyork.tiktokvideodownloader.utils.Constants.DOWNLOAD_DIRECTORY
import kotlinx.android.synthetic.main.fragment_gallery.view.*


class gallery : Fragment()  {
     var obj_adapter: Adapter_VideoFolder? = null
    var al_video = ArrayList<Model_Video>()
    public  var recyclerView1: RecyclerView? = null
     var recyclerViewLayoutManager: RecyclerView.LayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater!!.inflate(R.layout.fragment_gallery, container, false)

        recyclerView1 = view.recyclerView

         recyclerViewLayoutManager = GridLayoutManager(context!!, 3) as RecyclerView.LayoutManager?
        recyclerView1!!.setLayoutManager(recyclerViewLayoutManager)
        fn_video(context!!,requireActivity(),true)
         return  view;
    }

    fun fn_video(cn: Context,activity: FragmentActivity,f:Boolean) {
        al_video= ArrayList<Model_Video>()
        val int_position = 0
        val uri: Uri
        val cursor: Cursor
        val column_index_data: Int
        val column_index_folder_name: Int
        val column_id: Int
        val thum: Int
        val duration: Int

        var absolutePathOfImage: String? = null
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val condition = MediaStore.Video.Media.DATA + " like?"
        val selectionArguments = arrayOf("%$DOWNLOAD_DIRECTORY%")
        val sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC"
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Video.Media.DURATION
        )
        cursor = cn!!.getContentResolver().query(uri, projection, condition, selectionArguments, "$sortOrder")

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        duration = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        var i : Int =0
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            Log.e("Column", absolutePathOfImage)
            Log.e("Folder", cursor.getString(column_index_folder_name))
            Log.e("column_id", cursor.getString(column_id))
            Log.e("thum", cursor.getString(thum))
            Log.e("duration", cursor.getString(duration))

            val obj_model = Model_Video()
            obj_model.isBoolean_selected = false
            obj_model.str_path = absolutePathOfImage
            obj_model.str_thumb = cursor.getString(thum)
            obj_model.duration = cursor.getInt(duration)
            obj_model.id=i

            al_video.add(obj_model)
            i=i+1
        }


        obj_adapter = Adapter_VideoFolder(cn!!, al_video, activity!!)

        recyclerView1!!.setAdapter(null);
        recyclerView1!!.setAdapter(obj_adapter)
        obj_adapter!!.notifyDataSetChanged();

//
//        //recyclerView1!!.setLayoutManager(null);
//        recyclerView1!!.getRecycledViewPool().clear();
//        recyclerView1!!.swapAdapter(obj_adapter, false);
//       // recyclerView1!!.setLayoutManager(layoutManager);
//        obj_adapter!!.notifyDataSetChanged();





    }


    override fun setMenuVisibility(visible: Boolean) {
        super.setMenuVisibility(visible)
        if(visible){
            fn_video(context!!,requireActivity(),true);
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("resume", "12412535")

    }

}

