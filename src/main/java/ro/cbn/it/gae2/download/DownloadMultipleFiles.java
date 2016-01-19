package ro.cbn.it.gae2.download;

import ro.cbn.it.goae2.utils.GcsUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@WebServlet("/download_multiple_files")
public class DownloadMultipleFiles extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] fileNames = req.getParameterValues("fileName");

        // You must tell the browser the file type you are going to send
        resp.setContentType("application/zip");

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition", "attachment; filename=download.zip");
        //Always set the headers before opening the output stream
        ZipOutputStream out = new ZipOutputStream(resp.getOutputStream());
        for (String filename : fileNames) {
            try {
                out.putNextEntry(new ZipEntry(filename));
            } catch (ZipException e) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.SEVERE, "error on file: "+filename,e);
            }
            GcsUtils.readFile(GcsUtils.getGcsFileName(filename), out);
        }
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
