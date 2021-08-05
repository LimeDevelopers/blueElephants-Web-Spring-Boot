package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MobileAuthLogForm extends SearchForm {

    private Long id;
    private String decrHr;
    private String dmnNo;
    private String rspNo;
    private String attcMns;
    private String nm;
    private String dupSbscrbCfmVal;
    private String cnecInfoCfmVal;
    private String brthdy;
    private String sexdstn;
    private String iseFrerInfo;
    private String mbtlnum;
    private String telecom;
    private String failrCode;
    private String mssage;
    private String encrData;
    private LocalDateTime regDtm;

}
