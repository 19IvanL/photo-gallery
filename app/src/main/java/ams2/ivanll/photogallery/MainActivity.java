package ams2.ivanll.photogallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CAMERA = 100;
    private static final int REQUEST_IMAGE_CAMERA = 101;

    private static File photoFile;
    private static String currentPhotoPath;
    private static List<ImageItem> imageItems;
    private static RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If it's the first time opening the app,
        // the example images stored in the assets folder and their respective comments
        // are imported into the internal storage
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart)
            InternalDataAccess.copyAssetsIntoInternalStorage(this);

        // Set up camera button
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        });

        // Set up the RecyclerView
        rv = findViewById(R.id.imageList);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set image adapter
        imageItems = InternalDataAccess.getImageItemsFromStorage(this);
        ImageAdapter imageAdapter = new ImageAdapter(imageItems);
        rv.setAdapter(imageAdapter);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = createFile();
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, "ams2.ivanll.photogallery", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            }
        }
    }

    private File createFile() {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HH-mm-ss", Locale.getDefault()).format(new Date());
        String imgFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(imgFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == Activity.RESULT_OK) {
            Uri uri = Uri.parse(currentPhotoPath);
            File file = new File(uri.getPath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ImageItem it = new ImageItem(file.getName(), bitmap, null);
            imageItems.add(it);
            rv.getAdapter().notifyDataSetChanged();
        } else
            System.out.println("DELETING PHOTO... " + photoFile.delete());
    }

}