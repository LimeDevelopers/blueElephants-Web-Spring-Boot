package kr.or.btf.web.web.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class InspectionForm extends SearchForm implements Cloneable{
    private Long id;
    private String ttl;
    private String inspctCn;
    private String opnAt;
    private String mberDvTy;

    private String inspctDvTy;

    private Integer sectionIndex;


    //유효성검사 통계계
    private String areaNm;
    private String schlNm;
    private Integer grade;
    private String ban;
    private Integer no;
    private Long crsMstPid;
    private Long mberPid;

    private Long dvCodePid1;
    private Long dvCodePid2;
    private Long dvCodePid3;
    private Long dvCodePid4;
    private Long dvCodePid5;
    private Long dvCodePid6;

    private Integer dvLimitCnt1;
    private Integer dvLimitCnt2;
    private Integer dvLimitCnt3;
    private Integer dvLimitCnt4;
    private Integer dvLimitCnt5;
    private Integer dvLimitCnt6;

    private Integer dvQuestionCnt1;
    private Integer dvQuestionCnt2;
    private Integer dvQuestionCnt3;
    private Integer dvQuestionCnt4;
    private Integer dvQuestionCnt5;
    private Integer dvQuestionCnt6;

    private Long[] dvCodePidArr;

    private String chartId;

    public void setDvValue1(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid1 = dvCodePid;
        this.dvLimitCnt1 = dvLimitCnt;
        this.dvQuestionCnt1 = dvQuestionCnt;
    }

    public void setDvValue2(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid2 = dvCodePid;
        this.dvLimitCnt2 = dvLimitCnt;
        this.dvQuestionCnt2 = dvQuestionCnt;
    }

    public void setDvValue3(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid3 = dvCodePid;
        this.dvLimitCnt3 = dvLimitCnt;
        this.dvQuestionCnt3 = dvQuestionCnt;
    }

    public void setDvValue4(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid4 = dvCodePid;
        this.dvLimitCnt4 = dvLimitCnt;
        this.dvQuestionCnt4 = dvQuestionCnt;
    }

    public void setDvValue5(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid5 = dvCodePid;
        this.dvLimitCnt5 = dvLimitCnt;
        this.dvQuestionCnt5 = dvQuestionCnt;
    }

    public void setDvValue6(Long dvCodePid, Integer dvLimitCnt, Integer dvQuestionCnt) {
        this.dvCodePid6 = dvCodePid;
        this.dvLimitCnt6 = dvLimitCnt;
        this.dvQuestionCnt6 = dvQuestionCnt;
    }

    public InspectionForm copy() throws CloneNotSupportedException{ return (InspectionForm)clone(); }
}
