package kr.or.btf.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServerInitializer [ServerInitializer]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/04
**/
public class ServerInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}
