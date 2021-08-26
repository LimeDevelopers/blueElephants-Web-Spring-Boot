package kr.or.btf.web.test.web.service;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.exceptions.ValidCustomException;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.domain.web.MemberRoll;
import kr.or.btf.web.domain.web.MemberSchool;
import kr.or.btf.web.repository.web.MemberRepository;
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

    public  Page<BoardData> getNewsListData(@PageableDefault(page = 0, size = 3) Pageable pageable){
        BoardDataForm boardDataForm = new BoardDataForm();
        SearchForm searchForm = new SearchForm();
        searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_5);
        boardDataForm.setMstPid(1L);
        Page<BoardData> noticeList = boardDataService.list(pageable, searchForm, boardDataForm);
        return noticeList;
    }


    @Transactional
    public void batchJoin(MemberForm memberForm) throws ValidationException {
        MemberSchoolForm memberSchoolForm;
        PasswordEncoder passwordEncoder = null;

        for(int i =1; i > memberForm.getBatchArr(); i++) {
            try{
                //아이디 , 비밀번호를 제외한 값들은 널이 아닌 디폴트로 찍혀야 함.

                memberService.verifyDuplicateLoginId(memberForm.getLoginId()+i); //아이디

                memberForm.setDelAt("N");
                memberForm.setPwd(passwordEncoder.encode(memberForm.getPwd()));
                memberForm.setRegDtm(LocalDateTime.now());



            }catch (ValidCustomException ve) {
                throw ve;
            }
        }
    }
    @Transactional
    public void testBathRegister(MemberForm memberForm, Long tId) {
        String BATCH = "BATCH";


        for (int i = 1; i <= memberForm.getBatchArr(); i++) {
            String tempId;

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
            Account account = modelMapper.map(memberForm, Account.class);
            Account save = memberRepository.save(account);
            tempId = ""; // tempId 초기화

           /* teacherRepository.updateMberPid(save.getId(), tId);
            MemberSchool memberSchool = new MemberSchool();
            memberSchool.setTeacherNm(memberForm.getNm());
            memberSchool.setMberPid(account.getId());
            memberSchool.setAreaNm(memberForm.getAreaNm());
            memberSchool.setSchlNm(memberForm.getSchlNm());
            memberSchool.setGrade(memberForm.getGrade());
            memberSchool.setBan(memberForm.getBan());
            memberSchool.setNo(memberForm.getNo());
            memberSchool.setTeacherNm(memberForm.getTeacherNm());
            //tbl_member에 있는 선생님 pid insert
            //memberSchool.setThcrPid(tId);
            memberSchool.setRegDtm(LocalDateTime.now());
            memberSchoolRepository.save(memberSchool);*/
        }
    }
}
