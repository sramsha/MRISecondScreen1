package com.fyp.mrisecondscreen.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class ImageUtil {

    private ImageUtil() {

    }

    public static void downloadImage(Context context, String url, String imageName, String imgExtension){
        Picasso.with(context).load(url).into(picassoImageTarget(context, "images", imageName, imgExtension));
    }

    private static Target picassoImageTarget(Context context, final String imageDir, final String imageName, final String imgExtension) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName+"."+imgExtension); // Create image file
                        Log.e("ImageNamePath : ", "!!!!!!"+imageName+imgExtension+"!!!!!!");
                        Log.e("ImageNamePath : ", "!!!!!!"+myImageFile.getAbsolutePath());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.valueOf(imgExtension.toUpperCase()), 100, fos);
                            fos.flush();
                            fos.close();
                            Log.e("Lalala", "!!!!! Image saved with name of "+imageName+"."+imgExtension+"!!!!!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

    public static String getImageExtension(String fileName){
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }

}
