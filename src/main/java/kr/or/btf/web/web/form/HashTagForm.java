package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HashTagForm extends SearchForm{

    private Long id;
    private String tagNm;
    private LocalDateTime regDtm;
    private Long dataPid;
    private String tableNm;

}
