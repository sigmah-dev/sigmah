package testServlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletNew
 */
public class ServletNew extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletNew() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub

        String message = new String("BEGIN:VCALENDAR\n"
                + "VERSION:2.0\n"
                + "PRODID:-//www.marudot.com//iCal Event Maker\n"
                + "X-WR-CALNAME:Hello Alex\n"
                + "CALSCALE:GREGORIAN\n"
                + "BEGIN:VTIMEZONE\n"
                + "TZID:Asia/Baghdad\n"
                + "TZURL:http://sigmah.com\n"
                + "X-LIC-LOCATION:Asia/Baghdad\n"
                + "BEGIN:STANDARD\n"
                + "TZOFFSETFROM:+0300\n"
                + "TZOFFSETTO:+0300\n"
                + "TZNAME:AST\n"
                + "DTSTART:19700101T000000\n"
                + "END:STANDARD\n"
                + "END:VTIMEZONE\n"
                + "BEGIN:VEVENT\n"
                + "DTSTAMP:20170809T145432Z\n"
                + "UID:20170809T145432Z-1611198693@marudot.com\n"
                + "DTSTART;TZID=\"Asia/Baghdad\":20170809T120000\n"
                + "DTEND;TZID=\"Asia/Baghdad\":20170809T142000\n"
                + "SUMMARY:Hello Alex meeting!\n"
                + "DESCRIPTION:TEST SERVLET \n"
                + "LOCATION:ROOM 404\n"
                + "END:VEVENT\n"
                + "END:VCALENDAR");

        String filename = "CurrentEvents2.ics";
        File file = new File("D:\\Sigmah", filename);

        response.setContentType("calendar/ical");
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename + "\"");
        InputStream input = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = inputStream.read()) != -1) {
                sb.append((char) ch);
            }

            response.getWriter().write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //    if (output != null) try { output.close(); } catch (IOException ignore) {}
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}
