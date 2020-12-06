package com.example.cotton.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.loader.content.AsyncTaskLoader;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 웹 URL에서 이미지를 불러와 이미지를 보여줄 객체에 넣어줍니다.
 * 사용법!
 * new ImageLoadTask(웹사이트URL(String), 이미지를 넣을 객체(ImageView)).execute();
 */
public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {


    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}
