package com.example.saycheese;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {


    ImageView img_logo;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;

    String selectedImagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.main1); //Maybe this?

        img_logo= (ImageView) findViewById(R.id.imageView1);
        img_logo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDialog();
            }

        });
    }




        private void startDialog() {
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                    getActivity());
            myAlertDialog.setTitle("Upload Pictures Option");
            myAlertDialog.setMessage("How do you want to set your picture?");

            myAlertDialog.setPositiveButton("Gallery",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent pictureActionIntent = null;

                            pictureActionIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(
                                    pictureActionIntent,
                                    GALLERY_PICTURE);

                        }
                    });

            myAlertDialog.setNegativeButton("Camera",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            Intent intent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment
                                    .getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(f));

                            startActivityForResult(intent,
                                    CAMERA_REQUEST);

                        }
                    });
            myAlertDialog.show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            bitmap = null;
            selectedImagePath = null;

            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }

                if (!f.exists()) {

                    Toast.makeText(getBaseContext(),

                            "Error while capturing image", Toast.LENGTH_LONG)

                            .show();

                    return;

                }

                try {

                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                    int rotate = 0;
                    try {
                        ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                        int orientation = exif.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL);

                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotate = 270;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotate = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotate = 90;
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);



                    img_logo.setImageBitmap(bitmap);
                    //storeImageTosdCard(bitmap);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
                if (data != null) {

                    Uri selectedImage = data.getData();
                    String[] filePath = { MediaStore.Images.Media.DATA };
                    Cursor c = getContentResolver().query(selectedImage, filePath,
                            null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    selectedImagePath = c.getString(columnIndex);
                    c.close();

                    if (selectedImagePath != null) {
                        txt_image_path.setText(selectedImagePath);
                    }

                    bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                    // preview image
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);



                    img_logo.setImageBitmap(bitmap);

                } else {
                    Toast.makeText(getApplicationContext(), "Cancelled",
                            Toast.LENGTH_SHORT).show();
                }

        }


    }

    private void storeImageTosdCard(Bitmap processedBitmap) {
        try {
            // TODO Auto-generated method stub

            OutputStream output;
            // Find the SD Card path
            File filepath = Environment.getExternalStorageDirectory();
            // Create a new folder in SD Card
            File dir = new File(filepath.getAbsolutePath() + "/appName/");
            dir.mkdirs();

            String imge_name = "appName" + System.currentTimeMillis()
                    + ".jpg";
            // Create a name for the saved image
            File file = new File(dir, imge_name);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();

            }

            try {

                output = new FileOutputStream(file);

                // Compress into png format image from 0% - 100%
                processedBitmap
                        .compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                output.close();

                int file_size = Integer
                        .parseInt(String.valueOf(file.length() / 1024));
                System.out.println("size ===>>> " + file_size);
                System.out.println("file.length() ===>>> " + file.length());

                selectedImagePath = file.getAbsolutePath();



            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
