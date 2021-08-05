package kr.or.btf.web.config;

import kr.or.btf.web.common.filters.ConnectHisFilter;
import kr.or.btf.web.common.intercepters.CommonIntercepter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String serviceUrl;
    private final String filepath;
    private final String filepathuri;

    public WebConfig(@Value("${Globals.fileStorePath}") String filepath,
                     @Value("${Globals.fileStoreUriPath}") String filepathuri,
                     @Value("${Globals.domain.full}") String serviceUrl) {
        this.filepath = filepath;
        this.filepathuri = filepathuri;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    	/*List<String> excludePatterns = new ArrayList<String>();  //제외할 목록
    	excludePatterns.add("/frontRsc/css/**css");
    	excludePatterns.add("/frontRsc/js/**js");
    	excludePatterns.add("/frontRsc/images/**png");
    	excludePatterns.add("/frontRsc/images/icon/**png");
    	excludePatterns.add("/frontRsc/fonts/**woff");
    	excludePatterns.add("/webjars/jquery/**js");*/

        registry.addInterceptor(
                commonIntercepter())//.order(0)
        .addPathPatterns("/**")
        //.excludePathPatterns(excludePatterns)
        .order(0)
        ;
    }

    @Bean
    CommonIntercepter commonIntercepter() {
        return new CommonIntercepter(serviceUrl);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(filepathuri + "/**")
                .addResourceLocations("file:///" + filepath + "/")
                .setCachePeriod(0);

    }



    @Bean
    public FilterRegistrationBean getFilterRegistrationBean(){

        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new ConnectHisFilter());
        registrationBean.addUrlPatterns("/login");

        return registrationBean;
    }
}
