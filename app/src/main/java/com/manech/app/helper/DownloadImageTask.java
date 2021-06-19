package com.manech.app.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.manech.app.R;

import java.io.File;
import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap thumbnail = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            thumbnail = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            try {
                thumbnail = (BitmapFactory.decodeFile(url));
            }catch (Exception e1){
                Log.e("Error", e1.getMessage());
                e1.printStackTrace();
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return thumbnail;
    }

    protected void onPostExecute(Bitmap result) {
        if(result!=null)
        bmImage.setImageBitmap(result);
    }
}