package com.johnmillercoding.slidingpuzzle.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import static com.johnmillercoding.slidingpuzzle.activities.MainActivity.sessionManager;

public class PuzzleFunctions {

    /**
     * Decodes and returns a bitmap from filepath.
     * @return the decoded bitmap.
     */
    public Bitmap getPuzzle(Context context){
        if (sessionManager.getPuzzlePath() != null) {
            return fixOrientation(context, BitmapFactory.decodeFile(sessionManager.getPuzzlePath()));
        }
        return null;
    }

    /**
     * Rotates bitmap to correct orientation.
     * @param bitmap the image bitmap.
     * @return the properly oriented bitmap.
     */
    private Bitmap fixOrientation(Context context, Bitmap bitmap){

        try {
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Uri imageUri = getImageContentUri(context, new File(sessionManager.getPuzzlePath()));
            assert imageUri != null;
            Cursor cursor = context.getContentResolver().query(imageUri, orientationColumn, null, null, null);
            int orientation = -1;
            if (cursor != null && cursor.moveToFirst()) {
                orientation = cursor.getInt(cursor.getColumnIndex(orientationColumn[0]));
                cursor.close();
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        catch (Exception e) {
            Log.d(PuzzleFunctions.class.getSimpleName(), e.getMessage());
        }
        return bitmap;
    }

    /**
     * Gets the uri associated with the image.
     * @param context the context.
     * @param imageFile the image file.
     * @return the uri.
     */
    private Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}