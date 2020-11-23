package ca.cmpt276.charcoal.practicalparent.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

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
            Drawable d = ContextCompat.getDrawable(context,R.drawable.editchild_default_image);
            bitmap = drawableToBitmap(d);
        } else {
            bitmap = BitmapFactory.decodeFile(imageAddress);
        }
        return bitmap;
    }

    //Code from : https://stackoverflow.com/questions/24389043/bitmapfactory-decoderesource-returns-null-for-shape-defined-in-xml-drawable
    public Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

}
