package ams2.ivanll.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.pictureList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PictureAdapter(getDummyData()));
    }

    private PictureItem[] getDummyData() {
        PictureItem[] itemList = new PictureItem[5];

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture1);
        PictureItem pictureItem1 = new PictureItem(bitmap, "");
        itemList[0] = pictureItem1;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture2);
        PictureItem pictureItem2 = new PictureItem(bitmap, "Vaya cola...");
        itemList[1] = pictureItem2;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture4);
        PictureItem pictureItem3 = new PictureItem(bitmap, "¡Con la tripulación!");
        itemList[2] = pictureItem3;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture5);
        PictureItem pictureItem4 = new PictureItem(bitmap, "");
        itemList[3] = pictureItem4;

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.capture6);
        PictureItem pictureItem5 = new PictureItem(bitmap, "");
        itemList[4] = pictureItem5;

        return itemList;
    }

}