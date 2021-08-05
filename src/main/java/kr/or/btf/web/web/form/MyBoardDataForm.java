package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MyBoardDataForm extends SearchForm {

    private Long id;
    private LocalDateTime regDtm;
    private Long mberPid;
    private Long dataPid;

}
