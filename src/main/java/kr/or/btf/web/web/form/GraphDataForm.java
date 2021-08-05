package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphDataForm extends SearchForm {

    private Long graphPid;
    private Long dataPid;
    private String colNm;
    private Float colValue1;
    private Float colValue2;
    private Float colValue3;
    private Integer colSno;

    private Long[] dataPidArr;
    private String[] colNmArr;
    private Float[] colValue1Arr;
    private Float[] colValue2Arr;
    private Float[] colValue3Arr;
    private Integer[] colSnoArr;

}
