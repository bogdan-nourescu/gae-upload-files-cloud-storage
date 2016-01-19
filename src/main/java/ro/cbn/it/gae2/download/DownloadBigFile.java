package ro.cbn.it.gae2.download;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/download_over32mb")
public class DownloadBigFile extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setHeader("Content-disposition","attachment; filename=over32mb.txt");
        /*This is only a simple example to test if you can serve over 32 MB of data.*/
        for (int i = 0; i < 1024 * 1024 * 2; i++) {
            out.print("col1,col2,col3,col4,col5,col6,col6\r\n");
        }
        out.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req,resp);
    }

}
