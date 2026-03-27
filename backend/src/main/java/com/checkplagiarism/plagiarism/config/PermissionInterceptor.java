package com.checkplagiarism.plagiarism.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.checkplagiarism.plagiarism.domain.Permission;
import com.checkplagiarism.plagiarism.domain.Role;
import com.checkplagiarism.plagiarism.domain.User;
import com.checkplagiarism.plagiarism.service.UserService;
import com.checkplagiarism.plagiarism.util.SecurityUtil;
import com.checkplagiarism.plagiarism.util.err.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // Bypass permission check for public endpoints or static resources
        if (path == null ||
                path.startsWith("/api/v1/auth/") ||
                path.equals("/api/v1/submissions") ||
                path.startsWith("/api/v1/submissions/") ||
                path.startsWith("/api/v1/checks/") ||
                path.equals("/")) {
            return true;
        }

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (email != null && !email.isEmpty()) {
            User user = this.userService.findByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    if ("ADMIN".equalsIgnoreCase(role.getName())) {
                        return true;
                    }

                    List<Permission> list = role.getPermissions();
                    boolean isAllow = list.stream().anyMatch(x -> x.getApiPath().equals(path) &&
                            x.getMethod().equals(httpMethod));

                    if (isAllow == false) {
                        throw new PermissionException("you don't have permission to access this endpoint");
                    }
                } else {
                    throw new PermissionException("you don't have permission to access this endpoint");
                }
            }
        }

        return true;
    }
}