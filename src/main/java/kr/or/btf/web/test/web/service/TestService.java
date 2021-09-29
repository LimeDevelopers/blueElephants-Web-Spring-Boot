package kr.or.btf.web.test.web.service;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Base;
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

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final MemberSchoolRepository memberSchoolRepository;
    private final MemberRollRepository memberRollRepository;
    private final JPAQueryFactory queryFactory;

    public  Page<BoardData> getNewsListData(@PageableDefault(page = 0, size = 3) Pageable pageable){
        BoardDataForm boardDataForm = new BoardDataForm();
        SearchForm searchForm = new SearchForm();
        searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_5);
        boardDataForm.setMstPid(1L);
        Page<BoardData> noticeList = boardDataService.list(pageable, searchForm, boardDataForm);
        return noticeList;
    }

    /*@Transactional
    public void batchRegister(MemberForm memberForm) {
        String tempId = memberForm.getLoginId();
        int cnt = 0;
        for (int i = 1; i <= memberForm.getBatchArr(); i++) {
            cnt = cnt+i;
            //계정 정보 추가
            memberForm.setDelAt("N");
            memberForm.setPwd(passwordEncoder.encode(memberForm.getPwd())); //패스워드 셋
            memberForm.setRegDtm(LocalDateTime.now()); //등록일
            memberForm.setPrtctorAttcAt("Y");
            memberForm.setNm("TEST");
            memberForm.setSexPrTy("MALE");

            memberForm.setLoginId(memberForm.getLoginId()+ i);

            memberForm.setMberDvTy(UserRollType.BATCH);
            Account account = modelMapper.map(memberForm, Account.class);
            Account save = memberRepository.save(account);

            //로그인아이디 초기화


            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(UserRollType.BATCH);
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);
            memberSchoolRepository.pr_findTID(memberForm.getAreaNm(), memberForm.getSchlNm(),
                    memberForm.getGrade(), memberForm.getBan(), save.getId(), LocalDateTime.now());
        }
    }*/
    //폼에서 데이터를 전달 받음
    /*@Transactional
    public void batchRegister(MemberForm memberForm) {
        String temp = memberForm.getLoginId();

        for (int i = 1; i <= memberForm.getBatchArr(); i++) {
            log.info(temp+i);

            memberForm.setDelAt("N");
            memberForm.setPwd(passwordEncoder.encode(memberForm.getPwd())); //패스워드 셋
            memberForm.setRegDtm(LocalDateTime.now()); //등록일
            memberForm.setOnlineEdu("N"); // 현장교육 N = 오프라인
            memberForm.setEduReset("N");
            memberForm.setCardReset("N");
            memberForm.setFreeCard("N");
            memberForm.setGroupYn("N");
            memberForm.setCrewPid(0L);
            memberForm.setApproval("Y"); // 승인여부
            memberForm.setPrtctorAttcAt("Y");
            memberForm.setNm("TEST");
            memberForm.setSexPrTy("MALE");

            if (i < 10) {
                memberForm.setLoginId(temp+"0"+i);//변형된 계정 셋
            } else {
                memberForm.setLoginId(temp+i);//변형된 계정 셋
            }

            memberForm.setMberDvTy(UserRollType.BATCH);
            Account account = modelMapper.map(memberForm, Account.class);
            Account save = memberRepository.save(account);


            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(UserRollType.BATCH);
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);
            System.out.println("지역명" + memberForm.getAreaNm());
            System.out.println("학교" + memberForm.getSchlNm());
            System.out.println("반" + memberForm.getBan());
            System.out.println("학년" + memberForm.getGrade());

            //memberSchool에 인서트 해주는 프로시저 호출
            memberSchoolRepository.pr_findTID(memberForm.getAreaNm(), memberForm.getSchlNm(),
                    memberForm.getGrade(), memberForm.getBan(), save.getId(), LocalDateTime.now());

        }
    }*/

    /*public Boolean existsByBatchLoginId(String loginId){
        Account account = memberRepository.findByLoginId(loginId).orElseGet(Account::new);
        return (account != null && account.getId() != null);
    }*/

    /*public boolean existsSpace(String text) {
        if (text == null) return true;
        return text.contains(" ");
    }*/
}
