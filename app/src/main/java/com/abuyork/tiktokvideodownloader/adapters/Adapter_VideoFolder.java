package com.abuyork.tiktokvideodownloader.adapters;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.abuyork.tiktokvideodownloader.R;
import com.abuyork.tiktokvideodownloader.models.Model_Video;
import com.abuyork.tiktokvideodownloader.utils.iUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.abuyork.tiktokvideodownloader.utils.Constants.DEL_CONFIRM;


public  class Adapter_VideoFolder extends RecyclerView.Adapter<Adapter_VideoFolder.ViewHolder> {

    private ArrayList<Model_Video> al_video;

    private Context context;
    private ActionMode mActiveActionMode;
    private boolean multiSelect = false;
    private ArrayList<Integer> selectedItems = new ArrayList<Integer>();
     private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActiveActionMode=mode;

            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.delete, menu);

             return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

            new AlertDialog.Builder(context)
                    .setTitle("Delete "+selectedItems.size()+" video?")
                    .setMessage(DEL_CONFIRM)
                    .setCancelable(false)
                     .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Collections.sort(selectedItems,Collections.reverseOrder());
                            for (Integer intItem : selectedItems) {
                                //  Log.e("Deleted",intItem.toString());
                                // al_video.remove(intItem);
                                deleteItem(Integer.valueOf(intItem.toString()));
                             }
                            mode.finish();
                        }})
                    .setNegativeButton("CANCEL", null).show();

            return true;
        }
         @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    public Adapter_VideoFolder(Context context, ArrayList<Model_Video> al_video, Activity activity) {
         this.al_video = al_video;
        this.context = context;

        this.notifyDataSetChanged();
        Log.e("updated","yesupdate"+al_video.toString());

    }

       class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_image;
        FrameLayout rl_select;
        public TextView tvDuration;
        public View vCheckBackColor;
        public CheckBox chkVideoSelected;

        public ViewHolder(View v) {

            super(v);

            iv_image = (ImageView) v.findViewById(R.id.media_img_bck);
            rl_select = (FrameLayout) v.findViewById(R.id.frameLayout);
            tvDuration = (TextView)v.findViewById(R.id.tvDuration);
            vCheckBackColor = (View)v.findViewById(R.id.vCheckBackColor);
            chkVideoSelected = (CheckBox)v.findViewById(R.id.chkVideoSelected);

        }
        void selectItem(int item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(Integer.valueOf(item));
                    rl_select.setBackgroundColor(Color.WHITE);
                    chkVideoSelected.setVisibility(View.GONE);
                    vCheckBackColor.setVisibility(View.GONE);

                   Log.e("selctedItems",selectedItems.toString()+"---"+item);


                   if(selectedItems.isEmpty()){
                       multiSelect=false;
                       mActiveActionMode.finish();
                   }
                } else {

                     selectedItems.add(item);
                    rl_select.setBackgroundColor(Color.LTGRAY);
                    chkVideoSelected.setVisibility(View.VISIBLE);
                    vCheckBackColor.setVisibility(View.VISIBLE);

                        Log.e("UnselctedItems",selectedItems.toString()+"---"+item);

                }
                mActiveActionMode.setTitle(Integer.toString(selectedItems.size())+" Selected");
            }
        }
        void update(final Model_Video video, final int id) {

            Glide.with(context).load(video.getStr_thumb()).skipMemoryCache(false).into(iv_image);
            tvDuration.setText(secToTime(video.getDuration()));
            rl_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  iUtils.ShowToast(context,"clicked :*");

                    if(multiSelect){

                        selectItem(id);

                    }else{


                        final String[] options = {"Watch", "Delete", "Share"};

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Choose");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(options[which].contains("Watch")){

                                    try {
                                        String Path =video.getStr_path();
                                        Intent mVideoWatch = new Intent(Intent.ACTION_VIEW);
                                        mVideoWatch.setDataAndType(Uri.parse(Path),"video/mp4");
                                        context.startActivity(mVideoWatch);
                                    }
                                    catch(ActivityNotFoundException e)
                                    {
                                        iUtils.ShowToast(context, "Something went wrong while playing video! Please try again ");
                                        Log.e("Error",e.getMessage());
                                    }
                                }else if (options[which].contains("Delete")){
                                    new AlertDialog.Builder(context)
                                            .setTitle("Delete")
                                            .setMessage(DEL_CONFIRM)
                                            .setCancelable(false)
                                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                         //  Log.e("Deleted",intItem.toString());
                                                        // al_video.remove(intItem);
                                                        deleteItem(id);


                                                }})
                                            .setNegativeButton("CANCEL", null).show();

                                }else{
                                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                                    File fileWithinMyDir = new File(video.getStr_path());

                                    if(fileWithinMyDir.exists()) {

                                        try {
                                            intentShareFile.setType("video/mp4");
                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(video.getStr_path()));

                                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                    context.getString(R.string.SharingVideoSubject));
                                            intentShareFile.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.SharingVideoBody));

                                            context.startActivity(Intent.createChooser(intentShareFile, context.getString(R.string.SharingVideoTitle)));
                                        }catch (ActivityNotFoundException e){
                                            iUtils.ShowToast(context, "Something went wrong while sharing video! Please try again ");

                                        }

                                    }
                                }
                            }
                        });
                        builder.show();






                }
                }
            });

            rl_select.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                     selectItem(id);
                    return true;
                }
            });
              //textView.setText(value + "");
            if (selectedItems.contains(id)) {
                chkVideoSelected.setVisibility(View.VISIBLE);
                vCheckBackColor.setVisibility(View.VISIBLE);
                rl_select.setBackgroundColor(Color.LTGRAY);
            } else {
                rl_select.setBackgroundColor(Color.WHITE);
                chkVideoSelected.setVisibility(View.GONE);
                vCheckBackColor.setVisibility(View.GONE);
            }

        }
    }

    @NotNull
    @Override
    public Adapter_VideoFolder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }
     @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

        viewHolder.update(al_video.get(i),i);
    }

    private void deleteItem(int position) {


       String video=al_video.get(position).getStr_path();
       // context.getContentResolver().delete(Uri.parse(video), null, null);


   //   Boolean del =   new File(video).getAbsoluteFile().delete();

      //  Log.e("Deleted", new File(Uri.parse(video).getPath()).getAbsoluteFile().toString());
         context.getContentResolver().delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media.DATA + "=?", new String[]{ video } );

        al_video.remove(position);
        this.notifyItemRemoved(position);
        this.notifyItemRangeChanged(position, al_video.size());
        this.notifyDataSetChanged();

       // v.ViewHolder.setVisibility(View.GONE);
    }
     @Override
    public int getItemCount() {

        return al_video.size();
    }
    private String secToTime(int sec) {

        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(sec),
                TimeUnit.MILLISECONDS.toSeconds(sec) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sec))
        );

    }
}