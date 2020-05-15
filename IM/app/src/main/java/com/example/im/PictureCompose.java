package com.example.im;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class PictureCompose extends AppCompatActivity {
    Uri photoUri;
    ImageView image;
    private String pathToFile ="http://www.zhangmingzhe.cn:8090/cut";
    private Bitmap madeupImage,uploadImage,resultImage;
    Button back,download,upload;
    private int PHOTO_FROM_ALBUM=1;
    float l=0,t=0;
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
                        resultImage = compositeImages(uploadImage,madeupImage,l,t);
                        image.setImageBitmap(resultImage);
                        break;
                    case MotionEvent.ACTION_UP:
                        System.out.println(event);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        System.out.println(event);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createImageFile();
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Bitmap compositeImages(Bitmap srcBitmap,Bitmap dstBitmap,float left,float top){

        Bitmap bmp = null;
        bmp = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawBitmap(dstBitmap, left, top, paint);
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        try {
            uploadImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            resultImage = compositeImages(uploadImage,madeupImage,l,t);
            image.setImageBitmap(resultImage);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void createImageFile() throws IOException {

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IM_" + timeStamp + ".jpg";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/IM/";

        // 이미지가 저장될 폴더 이름
        File storageDir = new File(path);
        if (!storageDir.exists()) storageDir.mkdirs();


        File file = new File(path, imageFileName);
        OutputStream fOut = new FileOutputStream(file);
        resultImage.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.close();

        MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

        Toast toast = Toast.makeText(getApplicationContext(), path + " succeed!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
