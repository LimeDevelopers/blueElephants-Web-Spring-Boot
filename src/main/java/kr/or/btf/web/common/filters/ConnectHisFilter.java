package kr.or.btf.web.common.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Order(-10)
@Component
@Slf4j
public class ConnectHisFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        filterChain.doFilter(request, response);

        //HttpSession session = request.getSession();
    }

    /*public void init(FilterConfig filterConfig) throws ServletException {

    }


    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Hello", "test");

        chain.doFilter(req, res);

        log.info("Starting a transaction for req : {}", req.getRequestURI());
        log.info("Committing a transaction for req : {}", req.getRequestURI());
    }


    public void destroy() {

    }*/
}