package ro.cbn.it.gae2.upload;

import com.google.appengine.tools.cloudstorage.*;
import org.apache.commons.io.IOUtils;
import ro.cbn.it.goae2.utils.GcsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/upload_cloud_storage")
@MultipartConfig
public class UploadFileToCloudStorage extends HttpServlet{
    private final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.log(Level.CONFIG,"entered");


        String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
        logger.log(Level.CONFIG, "description: " + description);

        Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
        String fileName = filePart.getSubmittedFileName();
        String contentType = filePart.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }
        logger.log(Level.CONFIG,"file: "+fileName);

        InputStream fileContent = filePart.getInputStream();
        //do what you need with fileContent
        GcsFileOptions fileOptions = new GcsFileOptions.Builder()
                .mimeType(contentType)
                .contentEncoding("UTF-8")
                .build();
        GcsOutputChannel outputChannel = gcsService.createOrReplace(getFileName(fileName), fileOptions);
        IOUtils.copy(fileContent, Channels.newOutputStream(outputChannel));
        fileContent.close();
        outputChannel.close();
    }

    private GcsFilename getFileName(String fileName){
        //the recommandation is to create an unique filename.
        //one way to do this is to create an Entity that contains the file meta and use the id/hash to uniquely identify the file
        return GcsUtils.getGcsFileName(fileName);
    }
}
