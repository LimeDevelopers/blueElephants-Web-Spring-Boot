package kr.or.btf.web.domain.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiReturn {
    private String return_code;
    private UserInfo userInfo;
}
