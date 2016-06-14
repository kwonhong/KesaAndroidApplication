package com.kesa.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author hongil@
 */
public class ImageManager {
    /** Used to inform that the activity has been started from picture selection. */
    public static final int PICTURE_SELECTION_REQUEST_CODE = 1000;

    private static final String IMAGE_DIRECTORY = "imageDirectory";
    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static final int QUALITY = 25;

    public String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(COMPRESS_FORMAT, QUALITY, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String uid) {
        ContextWrapper cw = new ContextWrapper(context);

        // Directory path is '/data/<app_name>/app_data/imageDirectory'
        File directory = cw.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE);
        String imageName = uid + ".jpg";
        try (FileOutputStream fileOutputStream =
                 new FileOutputStream(new File(directory, imageName))) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageAbsolutePath = directory.getAbsolutePath() + "/" + imageName;
        Picasso.with(context).invalidate(new File(imageAbsolutePath));
        return imageAbsolutePath;
    }

    public void loadImage(Context context, String profileImage, ImageView profileImageView) {
        Picasso
            .with(context)
            .load(new File(profileImage))
            .into(profileImageView);
    }

    public void selectImageFromGallery(final Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(
            Intent.createChooser(intent, null),
            PICTURE_SELECTION_REQUEST_CODE);
    }

    public String getSelectedImagePath(Context context, Uri selectedImageUri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        CursorLoader cursorLoader =
            new CursorLoader(context, selectedImageUri, projection, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
