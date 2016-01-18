package ro.cbn.it.gae2.download;

import ro.cbn.it.goae2.utils.GcsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@WebServlet("/download_multiple_files")
@MultipartConfig
public class DownloadMultipleFiles extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] fileNames = req.getParameterValues("fileName");
        String fileType = "";

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType(fileType);

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename=download.zip");

        ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
        Map<String, Integer> fileNamesMap = new LinkedHashMap<>();
        for (String filename : fileNames) {
            try {
                String fileNameWithoutExtension = filename.substring(0, filename.lastIndexOf("."));
                String extension = filename.substring(filename.lastIndexOf("."));
                if (fileNamesMap.containsKey(fileNameWithoutExtension)) {
                    fileNamesMap.put(fileNameWithoutExtension, fileNamesMap.get(fileNameWithoutExtension) + 1);
                    out.putNextEntry(new ZipEntry(fileNameWithoutExtension + fileNamesMap.get(fileNameWithoutExtension) + extension));
                } else {
                    fileNamesMap.put(fileNameWithoutExtension, 0);
                    out.putNextEntry(new ZipEntry(filename));
                }
            } catch (ZipException e) {
                if (e.getMessage().contains("duplicate entry")) {
                   //Something went wrong
                }
            }
            GcsUtils.readFile(GcsUtils.getGcsFileName(filename), out);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req,resp);
    }

}
