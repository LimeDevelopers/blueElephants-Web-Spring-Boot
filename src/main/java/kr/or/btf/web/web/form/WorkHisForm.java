package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkHisForm extends SearchForm {

    private String tableNm;
    private String opertCn;
    private LocalDateTime opertDtm;
    private String errorCd;
    private String errorCn;
    private String cnctIp;
    private String cnctUrl;
    private Long mberPid;
}
