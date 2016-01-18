package ro.cbn.it.goae2.utils;

import com.google.appengine.tools.cloudstorage.*;
import com.google.apphosting.api.ApiProxy;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

public class GcsUtils {
    /**
     *
     * @return the default bucket (appID-appspot.com)
     */
    public static String getDefaultBucket() {
        return getAppId() + ".appspot.com";
    }

    public static void writeFile(String fileName, String content, String contentType) throws IOException {
        GcsFilename gcsFileName = new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
        writeFile(gcsFileName, content, contentType);
    }

    public static void writeFile(GcsFilename gcsFileName, String content, String contentType) throws IOException {
            writeFile(gcsFileName, content.getBytes("UTF-8"), contentType);
    }

    public static void writeFile(String fileName, byte[] content, String contentType) throws IOException {
        GcsFilename gcsFileName = new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
        writeFile(gcsFileName, content, contentType);
    }

    public static void writeFile(GcsFilename gcsFileName, byte[] content, String contentType) throws IOException {
            GcsFileOptions options = new GcsFileOptions.Builder().mimeType(contentType).contentEncoding("UTF-8").build();
            GcsService gcsService = GcsServiceFactory.createGcsService();
            GcsOutputChannel writeChannel;

            writeChannel = gcsService.createOrReplace(gcsFileName, options);
            writeChannel.write(ByteBuffer.wrap(content));
            writeChannel.close();
    }

    public static String readFile(String fileName) throws IOException {
        GcsFilename gcsFilename = getGcsFileName(fileName);
        return readFile(gcsFilename);
    }

    public static String readFile(GcsFilename gcsFilename) throws IOException {
            GcsService gcsService = GcsServiceFactory.createGcsService();
            StringBuilder builder = new StringBuilder();
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, 1024 * 1024);
            BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
            String aux;
            while ((aux = reader.readLine()) != null) {
                builder.append(aux).append("\r\n");
            }
            reader.close();
            return builder.toString();
    }

    /**
     * Read the file from cloud storage to a specified OutputStream<br />
     * Does not close the OutputStream<br />
     * It uses a buffer of 1MB
     * @param gcsFilename the file to be read
     * @param out The output stream where the contents of the file are written
     * @throws IOException
     */
    public static void readFile(GcsFilename gcsFilename, OutputStream out) throws IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService();
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, 1024 * 1024);
        InputStream in = Channels.newInputStream(readChannel);
        IOUtils.copy(in, out);
        in.close();
    }

    public static boolean deleteFile(String fileName) throws IOException {
        return deleteFile(getGcsFileName(fileName));
    }

    public static boolean deleteFile(GcsFilename gcsFileName) throws IOException {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
            return gcsService.delete(gcsFileName);
    }
    /**
     * @param fileName the name of the file to be used to to generate the GcsFilename<br />
     *                 The fileName can contain / (to simulate folders)
     * @return GcsFilename on the default bucket (appID-appspot.com)
     */
    public static GcsFilename getGcsFileName(String fileName) {
        return new GcsFilename(GcsUtils.getDefaultBucket(), fileName);
    }

    /**
     * @return the appID without the "s~" at the beginning
     */
    public static String getAppId() {
        String result = ApiProxy.getCurrentEnvironment().getAppId();
        //the app-id is similar to s~appid or eu~appid depending if the app is in US or Europe
        return result.substring(result.indexOf("~") + 1);
    }
}
