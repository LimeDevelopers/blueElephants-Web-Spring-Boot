package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StatisticLogForm extends SearchForm {

    private Long id;
    private String cnctIp;
    private LocalDateTime cnctDtm;
    private int allCnt;
    private String cnctSess;
    private String cnctEqpmn;
    private String refrnUrl;
    private String cnctUrl;

}
