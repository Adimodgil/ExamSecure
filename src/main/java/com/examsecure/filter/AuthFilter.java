package com.examsecure.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.examsecure.model.User;

@WebFilter(urlPatterns = {"/admin/*", "/student/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req,
                          ServletResponse res,
                          FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) req;

        HttpServletResponse response =
                (HttpServletResponse) res;

        // Stop browser from caching protected pages
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader("Pragma", "no-cache");

        response.setDateHeader("Expires", 0);

        HttpSession session =
                request.getSession(false);

        User user = (session != null)
                ? (User) session.getAttribute("user")
                : null;

        // 1. Not logged in -> go to login
        if (user == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );

            return;
        }

        String path = request.getRequestURI()
                .substring(
                        request.getContextPath().length()
                );

        // 2. Admin role check
        if (path.startsWith("/admin/")
                && !"ADMIN".equals(user.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admins only"
            );

            return;
        }

        // 3. Student role check
        if (path.startsWith("/student/")
                && !"STUDENT".equals(user.getRole())) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Students only"
            );

            return;
        }

        // 4. User is allowed -> continue
        chain.doFilter(request, response);
    }
}