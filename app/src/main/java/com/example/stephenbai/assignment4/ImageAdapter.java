package com.example.stephenbai.assignment4;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by stephenbai on 2016-11-01.
 */

// This adapter is strictly to interface with the GridView and doesn't
// particular show much interesting Realm functionality.

// Alternatively from this example,
// a developer could update the getView() to pull items from the Realm.

public class ImageAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Picture> pictureList = null;

    public ImageAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Picture> details) {
        this.pictureList = details;
    }

    @Override
    public int getCount() {
        if (pictureList == null) {
            return 0;
        }
        return pictureList.size();
    }

    @Override
    public Object getItem(int position) {
        if (pictureList == null || pictureList.get(position) == null) {
            return null;
        }
        return pictureList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View currentView, ViewGroup parent) {
        if (currentView == null) {
            currentView = inflater.inflate(R.layout.show_gallery_row, parent, false);
        }

        Picture mpicture = pictureList.get(position);

        if (mpicture != null) {
            ((TextView) currentView.findViewById(R.id.item_show_gallery_text)).setText(mpicture.getName());

            // use set image Bitmap to set as 50x 50 thumbnail
            Bitmap bitmap = imageHelper.decodeSampledBitmapFromFileDefault(mpicture.getPath(), 50, 50);

            ((ImageView) currentView.findViewById(R.id.item_show_gallery_view)).setImageBitmap(bitmap);

        }

        return currentView;
    }
}
