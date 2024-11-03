package org.example.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;


@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String parameter = req.getParameter("timezone");

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        String displayTimeZone = "UTC";

        if (parameter != null && !parameter.isEmpty()) {
            timeZone = TimeZone.getTimeZone(parameter.replace("UTC", "GMT"));
            displayTimeZone = parameter;
        }

        ZonedDateTime currentTime = LocalDateTime.now().atZone(timeZone.toZoneId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = currentTime.format(formatter) + " " + displayTimeZone;

        resp.setContentType("text/html");
        resp.getWriter().write(formattedTime);
        resp.getWriter().close();
    }
}


