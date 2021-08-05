package kr.or.btf.web.common.filters;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxSessionCheckFilter implements Filter {
    private String ajaxHeader;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("X-Frame-Options", "ALLOW-FROM http://localhost");

        if (isAjaxRequest(req)) {
            try {
                chain.doFilter(req, res);
            } catch (AccessDeniedException e) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (AuthenticationException e) {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        return req.getHeader(ajaxHeader) != null && req.getHeader(ajaxHeader).equals(Boolean.TRUE.toString());
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    /**
     * Set AJAX Request Header (Default is AJAX)
     *
     * @param ajaxHeader
     */
    public void setAjaxHeader(String ajaxHeader) {
        this.ajaxHeader = ajaxHeader;
    }


}
