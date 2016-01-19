package ro.cbn.it.gae2.download;

import ro.cbn.it.goae2.utils.GcsUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/download_cloud_storage")
public class DownloadFileCloudStorage extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String fileName = req.getParameter("fileName");
        String fileType = "";

        // You must tell the browser the file type you are going to send
        // for example application/pdf, text/plain, text/html, image/jpg
        resp.setContentType(fileType);

        // Make sure to show the download dialog
        resp.setHeader("Content-disposition","attachment; filename="+fileName);

        final long CACHE_DURATION_IN_SECOND = 60L * 60L * 24L * 30L; // 30 days(60sec*60min*24h*30days)
        final long CACHE_DURATION_IN_MS = CACHE_DURATION_IN_SECOND * 1000L;
        long now = new Date().getTime();
        resp.setHeader("Cache-Control", "public, max-age=" + CACHE_DURATION_IN_SECOND);
        resp.setHeader("Pragma", "Public");
        resp.setDateHeader("Last-Modified", now);
        resp.setDateHeader("Expires", now + CACHE_DURATION_IN_MS);

        ServletOutputStream out = resp.getOutputStream();
        GcsUtils.readFile(GcsUtils.getGcsFileName(fileName), out);
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req,resp);
    }

}
