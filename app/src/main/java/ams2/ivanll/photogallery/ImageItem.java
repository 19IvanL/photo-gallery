package ams2.ivanll.photogallery;

import android.graphics.Bitmap;

public class ImageItem {

    Bitmap bitmap;
    String comment;

    public ImageItem() {}

    public ImageItem(Bitmap bitmap, String comment) {
        this.bitmap = bitmap;
        this.comment = comment;
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
