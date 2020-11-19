package ca.cmpt276.charcoal.practicalparent;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class EditChildImageUploadActivity extends AppCompatActivity {
    private Button uploadFromGallery_btn;
    private ImageView originalImage;
    private ImageView newImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child_image_upload);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        uploadFromGallery_btn = (Button) findViewById(R.id.uploadImage_button);
        uploadFromGallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(EditChildImageUploadActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditChildImageUploadActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //TODO: DELETE FOLLOWING LINE(S) LATER
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    public static Intent makeLaunchIntent(Context context) {
        Intent intent = new Intent(context, EditChildImageUploadActivity.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            newImage = (ImageView) findViewById(R.id.userImageUpload);

            Bitmap bitmaps = new Bitmap();
            Uri imageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                bitmaps.add(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}