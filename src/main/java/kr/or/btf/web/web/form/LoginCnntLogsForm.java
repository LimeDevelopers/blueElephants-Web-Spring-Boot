package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LoginCnntLogsForm extends SearchForm{

    private LocalDateTime cnctDtm;
    private String cnctId;
    private String succesAt;
    private Long cnctIp;
    private int failCnt;
    private String rsn;
    private String[] yyyyMMddArr;
}
