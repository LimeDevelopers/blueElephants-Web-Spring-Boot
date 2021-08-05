package kr.or.btf.web.web.form;

import kr.or.btf.web.domain.web.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
public class SearchForm {
    private Integer pageSize;
    private Integer page;
    private String srchField;
    private String srchFile;
    private String srchWord;
    private Integer srchYear;
    private Integer srchMonth;
    private String srchStDt;
    private String srchEdDt;
    private String srchEventStDt;
    private String srchEventEdDt;
    private LocalDate srchDt;
    private LocalDateTime srchDtHms;
    private String srchOrder;
    private String srchGigan;
    private String srchCdNm;

    private String totalSrchWord;

    private String srchStRegYear;
    private String srchStRegMonth;
    private String srchEdRegYear;
    private String srchStBirthYear;
    private String srchEdBirthYear;
    private String srchStVisitYear;
    private String srchEdVisitYear;

    //Front total Search word
    private String srchTotWord;

    private Integer limitRow;
    private Long limitRowLong;


    private String regPsId;
    private String regPsNm;
    private LocalDateTime regDtm;
    private String updPsId;
    private String updPsNm;
    private LocalDateTime updDtm;
    private String delAt;
    private String srchDelAt;
    private String useAt;

    private Integer nowYear;
    private String nowYearMonth;

    private Boolean preYn;
    private Boolean useYn;

    private Long mngPid;
    private Long userPid;
    private String loginId;
    private String myBoard;

    @Enumerated(EnumType.STRING)
    private GenderType genderType;

    @Enumerated(EnumType.STRING)
    private UserStatusType userStatusType;

    @Enumerated(EnumType.STRING)
    private ApprovalType approvalType;

    @Enumerated(EnumType.STRING)
    private UserRollType userRollType;

    @Enumerated(EnumType.STRING)
    private MberDvType mberDvType;

    @Enumerated(EnumType.STRING)
    private SurveyDvType surveyDvType;

    @Enumerated(EnumType.STRING)
    private BanDvTy banDvTy;

    private String srchGbn;
    private Long srchMnGbnCdPid;
    private String srchappOS;

    private String eduTarget;
    private String dataType;
    private String sorting;
    @Enumerated(EnumType.STRING)
    private AdviceReservationDvType reservationDvTy;
    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    private String[] surveyDvTypes;
    private String surveyDvTypeStr;
    private String userRollTypeStr;
    private String[] userRollTypes;

    private Boolean applyAble;

    private Long crsMstPid;
    private Integer sn;
    private String fixingAt;
    private LocalDateTime ntceDt;
    private String sntncHead;

    private Long srchCodePid;
    private List<Long> srchCodePidArr;
    private List<String> srchWordArr;
    private Long srchGbnParent;
    private Long srchGbnChild;

}
