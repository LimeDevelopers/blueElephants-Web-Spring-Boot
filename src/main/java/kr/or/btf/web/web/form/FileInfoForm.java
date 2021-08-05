package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileInfoForm extends SearchForm{

    private Long id;
    private String flNm;
    private String chgFlNm;
    private Long flSz;
    private String dvTy;
    private int flSn;
    private LocalDateTime regDtm;
    private Long dataPid;
    private String tableNm;
}
