package com.example.im;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;

public class PictureView extends AppCompatActivity {
    private Uri photoUri;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictures_view);

        photoUri = getIntent().getParcelableExtra("photo");
        imageView = (ImageView)findViewById(R.id.img);

        imageView.setImageURI(photoUri);

        String url = "127.0.0.1:8000/upload";

    }

    private void uploadimg(Uri uri){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder builder1 = new MultipartBody.Builder();
                builder1.setType(MultipartBody.FORM);
                for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {//没有判空
                    String key = stringObjectEntry.getKey();
                    Object value = stringObjectEntry.getValue();
                    if (value instanceof File) {//如果请求的值是文件
                        File file = (File) value;
                        //MediaType.parse("application/octet-stream")以二进制的形式上传文件
                        builder1.addFormDataPart("jokeFiles", ((File) value).getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
                    } else {//如果请求的值是string类型
                        if(!"url".equals(key)){
                            builder1.addFormDataPart(key, value.toString());
                        }
                    }
                }
            }
        });
    }
}
