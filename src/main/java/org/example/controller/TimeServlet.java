package org.example.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.context.Context;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        String parameter = req.getParameter("timezone");
        String lastTimeZone = getLastTimeZoneFromCookie(req);

        TimeZone timeZone;

        if (parameter != null && !parameter.isEmpty()) {
            timeZone = TimeZone.getTimeZone(parameter.replaceAll(" ", "+").replaceAll("UTC", "GMT"));
                saveToCookie(resp, parameter);
        } else if (lastTimeZone != null && !lastTimeZone.isEmpty()) {
            timeZone = TimeZone.getTimeZone(lastTimeZone);
        } else {
            timeZone = TimeZone.getTimeZone("UTC");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        formatter.setTimeZone(timeZone);
        String currentTime = formatter.format(new Date());

        Map<String, String[]> parameterMap = req.getParameterMap();

        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
            params.put(keyValue.getKey(), keyValue.getValue()[0]);
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("timezone", timeZone.getID().replaceAll("GMT", "UTC"), "queryParams", params, "currentTime", currentTime.replaceAll("GMT", "UTC"))
        );

        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private void saveToCookie (HttpServletResponse resp, String timezone) {
        try {
            String encodedTimeZone = URLEncoder.encode(timezone, StandardCharsets.UTF_8.toString());
            Cookie cookies = new Cookie("lastTimezone", encodedTimeZone);
            cookies.setMaxAge(1000);
            resp.addCookie(cookies);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String getLastTimeZoneFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("lastTimezone".equals(cookie.getName())) {
                   try {
                       return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
                   } catch (UnsupportedEncodingException e) {
                       e.printStackTrace();
                   }
                }
            }
        }
        return null;
    }
}


