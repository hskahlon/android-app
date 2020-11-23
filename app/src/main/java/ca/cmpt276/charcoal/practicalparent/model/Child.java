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
        Bitmap bitmap;
        if(imageAddress == null){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.editchild_default_image);
        } else {
            bitmap = BitmapFactory.decodeFile(imageAddress);
        }
        return bitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
