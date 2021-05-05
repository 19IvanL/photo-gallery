package ams2.ivanll.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class InternalDataAccess {

    /**
     * Returns the images stored in the internal storage and their respective comments.
     * @return An array of ImageItems.
     */
    public static ImageItem[] getImageItemsFromStorage(Context context) {
        List<ImageItem> itemList = new ArrayList<ImageItem>();

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap;
        try {
            String imagesDir = context.getFilesDir() + File.separator + "images";
            File[] imageList = new File(imagesDir).listFiles();
            for (File imageFile : imageList) {
                if (imageFile.getName().startsWith("capture")) {
                    bitmap = BitmapFactory.decodeFile(imageFile.getPath(), bitmapOptions);
                    ImageItem imageItem = new ImageItem(imageFile.getName(), bitmap, InternalDataAccess.findComment(context, imageFile.getName()));
                    itemList.add(imageItem);
                }
            }

            return itemList.toArray(new ImageItem[itemList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Copy the assets (example images and their comments) into the internal storage.
     * @see <a href="https://stackoverflow.com/a/19218921">https://stackoverflow.com/a/19218921</a>
     */
    public static void copyAssetsIntoInternalStorage(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("example-images");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for (String filename : files) {
            InputStream in;
            OutputStream out;
            try {
                in = assetManager.open("example-images" + File.separator + filename);
                File outFile = new File(context.getFilesDir(), "images" + File.separator + filename);
                File imagesDir = new File(context.getFilesDir(), "images");
                if (!imagesDir.exists())
                    imagesDir.mkdirs();
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + "example-images" + File.separator + filename, e);
            }
        }

        // Set to run only on first start
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    /**
     * Finds the comment of a given image name.
     * @param context
     * @param imageFileName
     * @return The image's comment, or null if there is no comment.
     */
    public static String findComment(Context context, String imageFileName) {
        String comment = null;

        Document commentsDocument = null;
        try {
            commentsDocument = getCommentsDocument(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDocument != null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression xPathExpr = null;
            try {
                xPathExpr = xpath.compile("//image_comment[@fileName = '" + imageFileName + "']/text()");
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            try {
                if (xPathExpr != null)
                    comment = (String) xPathExpr.evaluate(commentsDocument, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }

        return comment;
    }

    /**
     * Saves the comment of a given image name.
     * @param context
     * @param name
     * @param comment
     */
    public static void saveComment(Context context, String name, String comment) {
        Document commentsDocument = null;

        try {
            commentsDocument = getCommentsDocument(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDocument != null) {
            try {
                XPath xPath = XPathFactory.newInstance().newXPath();
                Node node = (Node) xPath.compile("//image_comment[@fileName = '" + name + "']").evaluate(commentsDocument, XPathConstants.NODE);
                if (node != null)
                    node.setTextContent(comment);
                else {
                    Element element = commentsDocument.createElement("image_comment");
                    element.setAttribute("fileName", name);
                    element.setTextContent(comment);
                    Element root = commentsDocument.getDocumentElement();
                    root.appendChild(element);
                }
                Transformer transformer = createXmlTransformer();
                overwriteXmlFile(new File(context.getFilesDir(), "images" + File.separator + "image_comments.xml"), commentsDocument, transformer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @see <a href="https://stackoverflow.com/a/19218921">https://stackoverflow.com/a/19218921</a>
     */
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private static Document getCommentsDocument(Context context) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "images" + File.separator + "image_comments.xml"));
        return builder.parse(fis);
    }

    private static Transformer createXmlTransformer() throws Exception {
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    private static void overwriteXmlFile(File xmlFile, Document document, Transformer transformer)
            throws FileNotFoundException, TransformerException {
        StreamResult result = new StreamResult(new PrintWriter(
                new FileOutputStream(xmlFile, false)));
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
    }

}
