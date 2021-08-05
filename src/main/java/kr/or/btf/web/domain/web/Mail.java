package kr.or.btf.web.domain.web;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class Mail {
    private String address;
    private String title;
    private String message;
    private Map<String, Object> context;
    private String template;
}
