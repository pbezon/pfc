package es.uc3m.manager.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Snapster on 5/2/2016.
 */
public class PhotoUtils {

    public static void drawPhoto(Uri selectedImage, ContentResolver contentResolver, ImageView imageView, Context applicationContext) {
        contentResolver.notifyChange(selectedImage, null);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            AssetFileDescriptor fileDescriptor;
            fileDescriptor = contentResolver.openAssetFileDescriptor(selectedImage, "r");
            if (fileDescriptor != null) {
                //cogemos la foto pero no la pintamos por no romper cosas en memoria
                BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                options.inSampleSize = calculateInSampleSize(options);
                options.inJustDecodeBounds = false;
                Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                imageView.setImageBitmap(actuallyUsableBitmap);
                imageView.setRotation(90);
                Toast.makeText(applicationContext, selectedImage.toString(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(applicationContext, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.e("Camera", e.toString());
        }

    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 150 || width > 150) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > 150
                    && (halfWidth / inSampleSize) > 150) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
