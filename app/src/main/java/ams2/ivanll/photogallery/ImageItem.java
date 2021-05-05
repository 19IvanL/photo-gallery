package ams2.ivanll.photogallery;

import android.graphics.Bitmap;

public class ImageItem {

    String name;
    Bitmap bitmap;
    String comment;

    public ImageItem() {}

    public ImageItem(String name, Bitmap bitmap, String comment) {
        this.name = name;
        this.bitmap = bitmap;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
