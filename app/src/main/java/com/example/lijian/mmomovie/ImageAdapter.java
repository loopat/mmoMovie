package com.example.loopat.mmomovie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loopat.mmomovie.R;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mImageUrl;
    private LayoutInflater mInflater;

    public ImageAdapter(Context context, String [] imageUrls){

        this.mContext = context;
        this.mImageUrl = imageUrls;
        this.mInflater = LayoutInflater.from(context);
        //Log.v("Get Info","Get Info in ImageAdapter!");

//        for(String str : mImageUrl){
//            Log.v("Get Info"," ImageAdapter --> " + str);
//        }
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    /**
     * return ImageView view
     *  get a View that displays the data at the specified position in the data set.
     *  @param position:
     *                 The position of the item within the adapter's data set of the item
     *                 whose view we want.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){

            convertView = mInflater.inflate(R.layout.list_item_layout,parent,false);

            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.list_item_image);

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //Log.v("Get Info","Get Info ==> Get View " + mImageUrl[position]);
        Picasso.with(mContext)
                .load(mImageUrl[position])
                //.resize(50,50)
                //.centerCrop()
                .into(holder.imageView);

        return convertView;
    }

    /**
     * Create ViewHolder to make the scrolling smoothly
     */
    static class ViewHolder{
        ImageView imageView;
    }
}
