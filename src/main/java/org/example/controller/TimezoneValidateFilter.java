package org.example.controller;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.TimeZone;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String parameter = req.getParameter("timezone");

        if (parameter != null && !isValidTimeZone(parameter)) {
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setContentType("text/html");
            res.getWriter().write("Invalid timezone");
            res.getWriter().close();
        }
        chain.doFilter(req, res);
    }

    private boolean isValidTimeZone(String timezone) {
        if (!timezone.startsWith("UTC")) {
            return false;
        }

        if (timezone.length() <= 3) {
            return false;
        }

        String numberPart = timezone.substring(4);

        try {
            int offset = Integer.parseInt(numberPart);
            return offset >= 0 && offset <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
