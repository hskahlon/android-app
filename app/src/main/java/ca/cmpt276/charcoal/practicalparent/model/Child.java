package ca.cmpt276.charcoal.practicalparent.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;

import ca.cmpt276.charcoal.practicalparent.R;

/**
 *  Child activity allows for setting and getting information for child class
 */
public class Child {
    private String name;
    private String imageAddress;

    public Child(String name, String imageAddress) {
        this.name = name;
        this.imageAddress = imageAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public Bitmap getChildImage(Context context) {
        File img = new File(imageAddress);
        Bitmap bitmap;
        if (img.exists()) {
            bitmap = BitmapFactory.decodeFile(imageAddress);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.editchild_default_image);
            Toast.makeText(context, "Image was deleted", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
