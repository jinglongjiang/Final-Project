package com.example.im;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.Header;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import java.io.UnsupportedEncodingException;



public class PictureView extends AppCompatActivity {
    private String result;
    private Uri photoUri;
    private String srcPath;
    private boolean success = false;
    ImageView imageView;
    TextView textView;
    private String url = "http://www.zhangmingzhe.cn:8090/upload";
    private final int REQUEST_EXTERNAL_STORAGE = 1;
    private  String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures_view);

        photoUri = getIntent().getParcelableExtra("photo");
        srcPath = getPath(photoUri);
        imageView = (ImageView)findViewById(R.id.img);

        imageView.setImageURI(photoUri);
        verifyStoragePermissions(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        connectServer();
    }

    private void connectServer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        try {
            File file = new File(srcPath);
            System.out.println(file);
            params.put("image",file);
            hasSdcard();
            client.post(url, params, new AsyncHttpResponseHandler() {

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                      Throwable arg3) {
                    System.out.println(arg0);
                }

                @Override
                public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                    try {
                        result = new String(arg2,"UTF-8");
                        textView = findViewById(R.id.type);
                        textView.setText(result);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        Log.d("!@#!@#", "ASDF");
        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private boolean hasSdcard() {
        //判断ＳＤ卡手否是安装好的　　　media_mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}
