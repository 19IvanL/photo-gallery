package ams2.ivanll.photogallery;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

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

        // Set up the RecyclerView with the images
        RecyclerView rv = findViewById(R.id.imageList);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ImageAdapter(InternalDataAccess.getImageItemsFromStorage(this)));
    }

}