package com.example.im;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

//import com.androidquery.AQuery;

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
        //final AQuery aq=new AQuery(MainActivity.this);
    }
}
