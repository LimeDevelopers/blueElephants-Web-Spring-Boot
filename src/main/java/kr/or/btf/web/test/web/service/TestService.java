package kr.or.btf.web.test.web.service;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.exceptions.ValidCustomException;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.domain.web.MemberRoll;
import kr.or.btf.web.domain.web.MemberSchool;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.repository.web.MemberRollRepository;
import kr.or.btf.web.repository.web.MemberSchoolRepository;
import kr.or.btf.web.repository.web.MemberTeacherRepository;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.services.web._BaseService;
import kr.or.btf.web.web.form.BoardDataForm;
import kr.or.btf.web.web.form.MemberForm;
import kr.or.btf.web.web.form.MemberSchoolForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TestService extends _BaseService {



    @Autowired
    BoardDataService boardDataService;

    private final MemberTeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final ModelMapper modelMapper;
    private final MemberSchoolRepository memberSchoolRepository;
    private final MemberRollRepository memberRollRepository;

    public  Page<BoardData> getNewsListData(@PageableDefault(page = 0, size = 3) Pageable pageable){
        BoardDataForm boardDataForm = new BoardDataForm();
        SearchForm searchForm = new SearchForm();
        searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_5);
        boardDataForm.setMstPid(1L);
        Page<BoardData> noticeList = boardDataService.list(pageable, searchForm, boardDataForm);
        return noticeList;
    }


    @Transactional
    public void testBathRegister(MemberForm memberForm, MemberSchoolForm memberSchoolForm) {
        String BATCH = "BATCH";


        for (int i = 1; i <= memberForm.getBatchArr(); i++) {
            String tempId;
            //계정 정보 추가
            memberForm.setDelAt("N");
            memberForm.setPwd(passwordEncoder.encode(memberForm.getPwd())); //패스워드 셋
            memberForm.setRegDtm(LocalDateTime.now()); //등록일
            memberForm.setPrtctorAttcAt("N");

            if(i<10){
                tempId = memberForm.getLoginId();
                tempId+="0"+i;
            } else {
                tempId = memberForm.getLoginId();
                tempId+=i;
            }
            memberForm.setLoginId(tempId);//변형된 계정 셋
            System.out.println(tempId);
            memberForm.setMberDvTy(UserRollType.BATCH);
            Account account = modelMapper.map(memberForm, Account.class);
            Account save = memberRepository.save(account);
            tempId = ""; // tempId 초기화

            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(UserRollType.BATCH);
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);


            //스쿨 테이블에 정보 등록
            MemberSchool memberSchool = new MemberSchool();
            memberSchool.setMberPid(account.getId());
            memberSchool.setAreaNm(memberSchoolForm.getAreaNm());
            memberSchool.setSchlNm(memberSchoolForm.getSchlNm());
            memberSchool.setGrade(memberSchoolForm.getGrade());
            memberSchool.setBan(memberSchoolForm.getBan());
            memberSchool.setNo(memberSchoolForm.getNo());
            memberSchool.setTeacherNm(memberSchoolForm.getTeacherNm());
            memberSchool.setRegDtm(LocalDateTime.now());
            memberSchoolRepository.pr_findTid(memberSchool.getSchlNm(), memberSchool.getBan(), memberSchool.getBan());
            memberSchoolRepository.save(memberSchool);

        }
    }
}
