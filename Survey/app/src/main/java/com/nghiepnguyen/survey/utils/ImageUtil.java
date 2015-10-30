package com.nghiepnguyen.survey.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nghiepnguyen.survey.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nghiep on 10/29/15.
 */
public class ImageUtil {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================
    public static byte[] convertInputStreamToByteArray(InputStream inputStream) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte data[] = new byte[1024];
            int count;

            while ((count = inputStream.read(data)) != -1) {
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();
            inputStream.close();

            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] compressBitmapImage(Bitmap bm, long maxSizeInBytes,
                                             int maxWidth, int maxHeight) {

        // Scale image if image width is larger than maxWidth
        if (bm.getWidth() > maxWidth) {
            bm = Bitmap.createScaledBitmap(bm, maxWidth, (int) (1.0 * maxWidth
                    / bm.getWidth() * bm.getHeight()), false);
        }

        // Scale image if image height is larger than maxHeight
        if (bm.getHeight() > maxHeight) {
            bm = Bitmap.createScaledBitmap(bm, (int) (1.0 * maxHeight
                    / bm.getHeight() * bm.getWidth()), maxHeight, false);
        }

        // Compress image if image size is bigger than max size
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        int quality = 100;
        while (baos.size() > maxSizeInBytes) {
            quality -= 5;
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        return baos.toByteArray();

    }

    public static byte[] onCompressBitMap(Bitmap bitmap) {
        ByteArrayOutputStream ByteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, ByteStream);
        byte[] byteArray = ByteStream.toByteArray();
        return byteArray;
    }

    public static String getRandomFileName(String baseString, String ext) {
        Date date = new Date();

        String fileName = baseString + "_" + date.getTime() + "." + ext;

        return fileName;

    }

    public static String getRandomJpegImageFileName(String baseString) {
        return getRandomFileName(baseString, "jpeg");
    }

    public static Bitmap readBitmapFromUri(ContentResolver resolver, Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = resolver.openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rotateImageIfRequired(resolver, bm, selectedImage);
    }


    private static Bitmap rotateImageIfRequired(ContentResolver resolver, Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(resolver, selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }

    private static int getRotation(ContentResolver resolver, Uri selectedImage) {
        int rotation = 0;

//        Cursor mediaCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[] { "orientation", "date_added" },null, null,"date_added desc");
//
//        if (mediaCursor != null && mediaCursor.getCount() !=0 ) {
//            while(mediaCursor.moveToNext()){
//                rotation = mediaCursor.getInt(0);
//                break;
//            }
//        }
//        mediaCursor.close();

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(getRealPathFromURI(resolver, selectedImage));
            rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            return rotation;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rotation;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static String getRealPathFromURI(ContentResolver resolver, Uri contentUri) {
        String result = "";
        try {
            Cursor cursor = resolver.query(contentUri, null, null, null, null);
            if (cursor == null) {
                result = contentUri.getPath();
            } else {
                if (cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    result = cursor.getString(idx);
                    cursor.close();
                } else {
                    result = contentUri.getPath();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void loadImageView(Context context, String url, ImageView imageView) {
        // In case of the url is empty (not null), the Picasso will don't know how to do
        // Then app will crash, we need to check if url is empty, change it to null
        // So that Picasso will know how to do with null url
        if (TextUtils.isEmpty(url)) {
            url = null;
        }

        Picasso.with(context)
                .load(url)
                .placeholder(R.mipmap.img_place_holder)
                .noFade()
                .into(imageView);
    }

    public static void loadImageViewNotPlaceHolder(Context context, String url, ImageView imageView) {
        // In case of the url is empty (not null), the Picasso will don't know how to do
        // Then app will crash, we need to check if url is empty, change it to null
        // So that Picasso will know how to do with null url
        if (TextUtils.isEmpty(url)) {
            url = null;
        }

        Picasso.with(context)
                .load(url)
                .noFade()
                .into(imageView);
    }

    public static void loadImageView(Context context, String url, ImageView imageView, int resPlaceHolderId) {
        // In case of the url is empty (not null), the Picasso will don't know how to do
        // Then app will crash, we need to check if url is empty, change it to null
        // So that Picasso will know how to do with null url
        if (TextUtils.isEmpty(url)) {
            url = null;
        }

        if (resPlaceHolderId == 0) {
            Picasso.with(context)
                    .load(url)
                    .noFade()
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(url)
                    .placeholder(resPlaceHolderId)
                    .noFade()
                    .into(imageView);
        }
    }

    public static void loadImageView(Context context, Uri uri, ImageView imageView) {
        Picasso.with(context)
                .load(uri)
                .placeholder(R.mipmap.img_place_holder)
                .noFade()
                .into(imageView);
    }

    public static void loadImageViewResource(Context context, String pngName, ImageView imageView) {
        if (!TextUtils.isEmpty(pngName))
            imageView.setImageResource(context.getResources().getIdentifier("drawable/" + pngName.toLowerCase(), null, context.getPackageName()));
    }

    public static class getBitmapFromUrlAsyncTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strUrl) {
            try {
                URL url = new URL(strUrl[0]);
                Bitmap myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public static Bitmap getBitmapFromUrl(String urlImage)
    {
        URL url = null;
        Bitmap myBitmap=null;
        try {
            url = new URL(urlImage);
            myBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
