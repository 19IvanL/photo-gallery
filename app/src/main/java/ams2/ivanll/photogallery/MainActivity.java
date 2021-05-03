package ams2.ivanll.photogallery;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If it's the first time opening the app,
        // the example images stored in the assets folder and its comments are imported into the internal storage
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        boolean firstStart = sharedPreferences.getBoolean("firstStart", true);
        if (firstStart)
            copyAssetsIntoInternalStorage();

        // Set up the RecyclerView with the images
        RecyclerView rv = findViewById(R.id.imageList);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ImageAdapter(getImagesData()));
    }


    /**
     * Returns the images stored in the internal storage and its comments.
     * @return An array of ImageItems.
     */
    private ImageItem[] getImagesData() {
        List<ImageItem> itemList = new ArrayList<ImageItem>();

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        InputStream is = null;
        Bitmap bitmap = null;
        try {
            String imagesDir = getFilesDir() + File.separator + "images";
            File[] imageList = new File(imagesDir).listFiles();
            for (int i = 0; i < imageList.length; i++) {
                File imageFile = imageList[i];
                if (imageFile.getName().startsWith("capture")) {
                    bitmap = BitmapFactory.decodeFile(imageFile.getPath(), bitmapOptions);
                    ImageItem imageItem = new ImageItem(bitmap, findImageComments(imageFile.getName()));
                    itemList.add(imageItem);
                }
            }

            ImageItem[] itemArray = itemList.toArray(new ImageItem[itemList.size()]);
            return itemArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String findImageComments(String imageFileName) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        AssetManager assetManager = getAssets();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(assetManager.open("example-images" + File.separator + "image_comments.xml"));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        XPathExpression xPathExpr = xpath.compile("//image_comment[@fileName = '" + imageFileName + "']/text()");

        String result = (String) xPathExpr.evaluate(doc, XPathConstants.STRING);

        return result;
    }

    /**
     * @see <a href="https://stackoverflow.com/a/19218921">https://stackoverflow.com/a/19218921</a>
     */
    private void copyAssetsIntoInternalStorage() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("example-images");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("example-images" + File.separator + filename);
                File outFile = new File(getFilesDir(), "images" + File.separator + filename);
                File imagesDir = new File(getFilesDir(), "images");
                if (!imagesDir.exists())
                    imagesDir.mkdirs();
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + "example-images" + File.separator + filename, e);
            }
        }

        // Set to run only on first start
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    /**
     * @see <a href="https://stackoverflow.com/a/19218921">https://stackoverflow.com/a/19218921</a>
     */
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}