package com.example.im;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class PictureCompose extends AppCompatActivity {
    Uri photoUri;
    ImageView image;
    private String pathToFile ="http://www.zhangmingzhe.cn:8090/cut";
    private Bitmap madeupImage,uploadImage,resultImage;
    Button back,download,upload;
    private int PHOTO_FROM_ALBUM=1;
    float l=0,t=0,scale;
    View img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures_compose);

        photoUri = getIntent().getParcelableExtra("photo");

        image = findViewById(R.id.remove);
        image.setImageURI(photoUri);

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask();
        downloadTask.execute(pathToFile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listeningbtns();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap result) {
            madeupImage = result;
            image.setImageBitmap(madeupImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void listeningbtns(){
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PictureCompose.this,MainActivity.class);
                startActivity(intent);
            }
        });
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PHOTO_FROM_ALBUM);
            }
        });
        img = findViewById(R.id.remove);
        img.setOnTouchListener(new View.OnTouchListener() {
            float x1,y1,x2,y2;
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x2 = event.getX();
                        y2 = event.getY();
                        l = x2 - x1;
                        t = y2 - y1;
                        resultImage = compositeImages(uploadImage,madeupImage,l,t,scale);
                        image.setImageBitmap(resultImage);
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println(event);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        System.out.println(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private static Bitmap compositeImages(Bitmap srcBitmap,Bitmap dstBitmap,float left,float top,float scale){

        Bitmap bmp = null;
        bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(dstBitmap, 0, 0, dstBitmap.getWidth(), dstBitmap.getHeight(), matrix,
                true);
        canvas.drawBitmap(newbm, left, top, paint);
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        try {
            uploadImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            resultImage = compositeImages(uploadImage,madeupImage,l,t,scale);
            image.setImageBitmap(resultImage);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }
}
