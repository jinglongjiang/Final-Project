package com.example.im;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;



public class MainActivity extends AppCompatActivity {
    private Button addButton,uploadButton,onepic,album;
    private ImageView menu,mypic;
    private int PHOTO_FROM_ALBUM=1;
    private ImageView photo;
    private Uri imageUri;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photo = (ImageView)findViewById(R.id.mypic);

        showandhidemenu();
        showmenu();
        openAblum();
    }
    private void showandhidemenu(){
        addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                menu = (ImageView) findViewById(R.id.add_menu);
                onepic = (Button) findViewById(R.id.onepic);
                album = (Button) findViewById(R.id.album);
                switch (menu.getVisibility()){
                    case 0:
                        menu.setVisibility(View.GONE);
                        onepic.setVisibility(View.GONE);
                        album.setVisibility(View.GONE);
                        break;
                    case 8:
                        menu.setVisibility(View.VISIBLE);
                        onepic.setVisibility(View.VISIBLE);
                        album.setVisibility((View.VISIBLE));
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void showmenu(){
        uploadButton = (Button) findViewById(R.id.upload_image);
        uploadButton.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View v) {
                menu = (ImageView) findViewById(R.id.add_menu);
                onepic = (Button) findViewById(R.id.onepic);
                album = (Button) findViewById(R.id.album);
                menu.setVisibility(View.VISIBLE);
                onepic.setVisibility(View.VISIBLE);
                album.setVisibility((View.VISIBLE));
            }
        }));
    }

    private void openAblum(){
        onepic = (Button) findViewById(R.id.onepic);
        onepic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PHOTO_FROM_ALBUM);
            }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        Intent intent = new Intent(this, PictureView.class);
        intent.putExtra("photo", uri);
        startActivity(intent);
    }
}
