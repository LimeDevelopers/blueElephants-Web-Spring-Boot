package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.exceptions.ValidCustomException;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.MemberGroup;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.domain.web.QFileInfo;
import kr.or.btf.web.domain.web.QLoginCnntLogs;
import kr.or.btf.web.domain.web.QMemberCrew;
import kr.or.btf.web.domain.web.QMemberGroup;
import kr.or.btf.web.domain.web.QMemberSchool;
import kr.or.btf.web.domain.web.QMemberTeacher;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.GenderType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.repository.web.MemberGroupRepository;
import kr.or.btf.web.repository.web.*;
import kr.or.btf.web.utils.AESEncryptor;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MemberService extends _BaseService {

    @Value("${Globals.domain.full}")
    private String domain;

    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final MemberSchoolRepository memberSchoolRepository;
    private final MemberParentRepository memberParentRepository;
    private final MemberTeacherRepository memberTeacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final MailService mailService;
    private final MemberLogRepository memberLogRepository;
    private final MemberSchoolLogRepository memberSchoolLogRepository;
    private final MemberTeacherLogRepository memberTeacherLogRepository;
    private final LoginCnntLogsRepository loginCnntLogsRepository;
    private final MemberRollRepository memberRollRepository;
    private final FileInfoRepository fileInfoRepository;
    private final MemberGroupRepository memberGroupRepository;
    private final MemberCrewRepository memberCrewRepository;

    public List<Account> findAll(){
        return memberRepository.findAll();
    }

    public List<MemberSchool> srchTchr(String TeacherNm) {
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMemberSchool.teacherNm.eq(TeacherNm));


        List<MemberSchool> memberSchoolList = queryFactory
                .select(Projections.fields(MemberSchool.class,
                        qMemberSchool.areaNm,
                        qMemberSchool.teacherNm,
                        qMemberSchool.schlNm,
                        qMemberSchool.grade,
                        qMemberSchool.ban ))
                .from(qMemberSchool)
                .distinct()
                .where(qMemberSchool.teacherNm.contains(TeacherNm))
                .fetch();

        return memberSchoolList;
    }

    public Page<Account> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QMemberGroup qMemberGroup = QMemberGroup.memberGroup;
        QMemberCrew qMemberCrew = QMemberCrew.memberCrew;

        OrderSpecifier<Long> orderSpecifier = qAccount.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.delAt.eq("N"));

        if (searchForm.getUserRollType() != null) {
            if (searchForm.getUserRollType().equals(UserRollType.ADMIN)) {
                builder.and(qAccount.mberDvTy.in(UserRollType.TEACHER,UserRollType.STUDENT,UserRollType.PARENT,UserRollType.NORMAL,UserRollType.LECTURER,UserRollType.COUNSELOR,UserRollType.ADMIN));
            }
        }

        if (searchForm.getSrchGbn() != null) {
            if (!searchForm.getSrchGbn().isEmpty()) {
                builder.and(qAccount.mberDvTy.eq(UserRollType.valueOf(searchForm.getSrchGbn())));
            }
        }

        if (searchForm.getSrchStDt() != null && searchForm.getSrchEdDt() != null) {
            if (!searchForm.getSrchStDt().isEmpty() && !searchForm.getSrchEdDt().isEmpty()) {
                builder
                        .and(qAccount.regDtm.goe(LocalDateTime.parse(searchForm.getSrchStDt() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd H:m")))
                                .and(qAccount.regDtm.loe(LocalDateTime.parse(searchForm.getSrchEdDt() + " 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd H:m")))
                                )
                        );
            }
        }

        if (searchForm.getSrchField() != null) {
            if (searchForm.getSrchWord() != null && !searchForm.getSrchWord().isEmpty()) {
                if (searchForm.getSrchField().equals("1")) {
                    builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"))
                            .or(qAccount.loginId.like("%" + searchForm.getSrchWord() + "%"))
                            .or(qAccount.email.like("%" + searchForm.getSrchWord() + "%"));
                } else if (searchForm.getSrchField().equals("2")) {
                    builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
                } else if (searchForm.getSrchField().equals("3")) {
                    builder.and(qAccount.loginId.like("%" + searchForm.getSrchWord() + "%"));
                } else if (searchForm.getSrchField().equals("4")) {
                    builder.and(qAccount.email.like("%" + searchForm.getSrchWord() + "%"));
                }
            }
        }
        // 추가 08.26
        // 김재일
        if(searchForm.getGroupDv().equals("Y")){
            BooleanBuilder builder1 = new BooleanBuilder();
            builder.and(qAccount.approval.eq("N"));

            QueryResults<Account> groupList = queryFactory
                    .select(Projections.fields(Account.class,
                            qAccount.id, qAccount.email,
                            qAccount.loginId, qAccount.nm, qAccount.mberDvTy,
                            qAccount.approval, qAccount.crewYn, qAccount.groupYn,
                            qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm
                            ))
                    .from(qAccount)
                    .leftJoin(qMemberGroup).on(qAccount.id.eq(qMemberGroup.mberPid))
                    .leftJoin(qMemberCrew).on(qAccount.id.eq(qMemberCrew.mberPid))
                    .where(qAccount.approval.eq("N"))
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .orderBy(orderSpecifier)
                    .fetchResults();
            return new PageImpl<>(groupList.getResults(), pageable, groupList.getTotal());
        } else {
            QueryResults<Account> mngList = queryFactory
                    .select(Projections.fields(Account.class,
                            qAccount.id,
                            qAccount.loginId, qAccount.nm, qAccount.mberDvTy, qAccount.secsnDtm,
                            qAccount.sexPrTy, qAccount.moblphon, qAccount.email, qAccount.adres,
                            qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm,
                            qAccount.onlineEdu, qAccount.eduReset, qAccount.cardReset
                    ))
                    .from(qAccount)
                    .where(builder)
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .orderBy(orderSpecifier)
                    .fetchResults();
            return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
        }
    }

    public List<Account> listByAdminUser(SearchForm searchForm) {

        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        OrderSpecifier<Long> orderSpecifier = qAccount.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.delAt.eq("N"));
        builder.and(qAccount.mberDvTy.eq(UserRollType.MASTER));
        builder.or(qAccount.mberDvTy.eq(UserRollType.ADMIN));
        builder.or(qAccount.mberDvTy.eq(UserRollType.LECTURER));
        builder.or(qAccount.mberDvTy.eq(UserRollType.COUNSELOR));

        List<Account> mngList = queryFactory
                .select(Projections.fields(Account.class,
                        qAccount.id,
                        qAccount.loginId, qAccount.nm, qAccount.mberDvTy, qAccount.secsnDtm,
                        qAccount.sexPrTy, qAccount.moblphon, qAccount.email, qAccount.adres,
                        qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm
                ))
                .from(qAccount)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }

    public Boolean updateApproval(String pid) {
        Long chgStr = Long.parseLong(pid);
        int rs = memberRepository.setApproval(chgStr,"Y");
        if(rs > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean updateEduType(Long pid, String type) {
        int rs = memberRepository.setEduType(pid,type);
        if(rs > 0) {
            return true;
        } else {
            return false;
        }
    }

    public FileInfo licenseLoad(Long id) {
        QFileInfo qFileInfo = QFileInfo.fileInfo;

        FileInfo file =  queryFactory
                .select(Projections.fields(FileInfo.class,
                        qFileInfo.flNm))
                .from(qFileInfo)
                .where(qFileInfo.id.eq(id))
                .fetchFirst();
        return file;
    }

    public MemberGroup groupLoad(Long id) {
        QMemberGroup qGroup = QMemberGroup.memberGroup;

        MemberGroup group =  queryFactory
                .select(Projections.fields(MemberGroup.class,
                        qGroup.id,
                        qGroup.groupNm,
                        qGroup.chrNm,
                        qGroup.position,
                        qGroup.bNum,
                        qGroup.bLicenseAttc,
                        qGroup.flPid))
                        .from(qGroup)
                        .where(qGroup.mberPid.eq(id))
                        .fetchFirst();
        return group;
    }

    public MemberCrew crewLoad(Long id) {
        QMemberCrew qCrew = QMemberCrew.memberCrew;

        MemberCrew crew =  queryFactory
                .select(Projections.fields(MemberCrew.class,
                        qCrew.id,
                        qCrew.crewNm,
                        qCrew.rptNm,
                        qCrew.crewFNum,
                        qCrew.crewAffi))
                .from(qCrew)
                .where(qCrew.mberPid.eq(id))
                .fetchFirst();
        return crew;
    }

    public Account load(Long id) {
        /*Account account = memberRepository.findById(id).orElseGet(Account::new);

        return account;*/
        QAccount qAccount = QAccount.account;
        QLoginCnntLogs qLoginCnntLogs = QLoginCnntLogs.loginCnntLogs;
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

        OrderSpecifier<LocalDateTime> orderSpecifier = qLoginCnntLogs.cnctDtm.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.id.eq(id));

        Account account = queryFactory
                .select(Projections.fields(Account.class,
                        qAccount.id,
                        qAccount.loginId,
                        qAccount.pwd,
                        qAccount.nm,
                        qAccount.sexPrTy,
                        qAccount.mberDvTy,
                        qAccount.moblphon,
                        qAccount.email,
                        qAccount.zip,
                        qAccount.adres,
                        qAccount.dtlAdres,
                        qAccount.ncnm,
                        qAccount.brthdy,
                        qAccount.secsnDtm,
                        qAccount.sttTy,
                        qAccount.regPsId,
                        qAccount.regDtm,
                        qAccount.updPsId,
                        qAccount.updDtm,
                        qAccount.delAt,
                        qAccount.emailAttcAt,
                        qAccount.emailAttcDtm,
                        qAccount.prtctorNm,
                        qAccount.prtctorBrthdy,
                        qAccount.prtctorEmail,
                        qAccount.prtctorAttcAt,
                        qAccount.prtctorAttcDtm,
                        qAccount.approval,
                        qAccount.onlineEdu,
                        qAccount.cardReset,
                        qAccount.freeCard,
                        qLoginCnntLogs.cnctDtm,
                        qMemberSchool.no.as("mberNo")
                        ))
                .from(qAccount)
                .leftJoin(qLoginCnntLogs).on(qAccount.loginId.eq(qLoginCnntLogs.cnctId))
                .leftJoin(qMemberSchool).on(qAccount.id.eq(qMemberSchool.mberPid))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return account;
    }

    public Account loadByLoginId(String loginId) {
        Optional<Account> account = memberRepository.findByLoginId(loginId);

        return account.orElse(null);
    }

    @Transactional
    public boolean delete(Long id) {
        try {
            Account mng = memberRepository.findById(id).orElseGet(Account::new);

            mng.setSecsnDtm(LocalDateTime.now());
            //mng.setUpdDtm(LocalDateTime.now());
            //mng.setUpdPsId(account.getLoginId());
            mng.setDelAt("Y");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Account findByLoginIdAndDelAt(String loginId, String delAt) {

        /*Account byloginId = userRepository.findByloginIdAndDelYn(loginId, delYn);
        if (byloginId == null || byloginId.getId() == null) {
            byloginId = new Account();
        }*/

        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.delAt.eq(delAt));
        builder.and(qAccount.loginId.eq(loginId));

        Account byloginId = queryFactory
                .select(Projections.fields(Account.class,
                        qAccount.loginId, qAccount.id, qAccount.nm,
                        qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm,
                        qMemberSchool.areaNm, qMemberSchool.schlNm, qMemberSchool.grade, qMemberSchool.ban, qMemberSchool.no
                ))
                .from(qAccount)
                .leftJoin(qMemberSchool).on(qAccount.id.eq(qMemberSchool.mberPid))
                .where(builder)
                .fetchFirst();

        return byloginId;
    }

    public Account findByIdAndMberDvTy(Long id, UserRollType mberDvTy) {

        /*Account byloginId = userRepository.findByloginIdAndDelYn(loginId, delYn);
        if (byloginId == null || byloginId.getId() == null) {
            byloginId = new Account();
        }*/


        if (UserRollType.STUDENT.equals(mberDvTy)) {
            QAccount qAccount = QAccount.account;
            QMemberSchool qMemberSchool = QMemberSchool.memberSchool;

            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qAccount.delAt.eq("N"));
            builder.and(qAccount.id.eq(id));

            Account byId = queryFactory
                    .select(Projections.fields(Account.class,
                            qAccount.loginId, qAccount.id, qAccount.nm,
                            qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm,
                            qMemberSchool.areaNm, qMemberSchool.schlNm, qMemberSchool.grade, qMemberSchool.ban, qMemberSchool.no
                    ))
                    .from(qAccount)
                    .leftJoin(qMemberSchool).on(qAccount.id.eq(qMemberSchool.mberPid))
                    .where(builder)
                    .fetchFirst();

            return byId;
        } else if (UserRollType.TEACHER.equals(mberDvTy)) {
            QAccount qAccount = QAccount.account;
            QMemberTeacher qMemberTeacher = QMemberTeacher.memberTeacher;

            BooleanBuilder builder = new BooleanBuilder();
            builder.and(qAccount.delAt.eq("N"));
            builder.and(qAccount.id.eq(id));

            Account byId = queryFactory
                    .select(Projections.fields(Account.class,
                            qAccount.loginId, qAccount.id, qAccount.nm,
                            qAccount.regPsId, qAccount.regDtm, qAccount.updPsId, qAccount.updDtm,
                            qMemberTeacher.areaNm, qMemberTeacher.schlNm, qMemberTeacher.grade, qMemberTeacher.ban
                    ))
                    .from(qAccount)
                    .leftJoin(qMemberTeacher).on(qAccount.id.eq(qMemberTeacher.mberPid))
                    .where(builder)
                    .fetchFirst();

            return byId;
        } else {
            Account byId = findById(id);
            return byId;
        }
    }
    public void verifyPwChange(String confirmPwd, String pwd) {
        if (!passwordEncoder.matches(confirmPwd, pwd)) {
            throw new ValidCustomException("비밀번호가 일치하지 않습니다.", "pwd");
        }
    }

    public void verifyDuplicateLoginId(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new ValidCustomException("이미 사용중인 아이디입니다.", "loginId");
        }
    }

    public void verifyDuplicateEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ValidCustomException("이미 사용중인 이메일주소입니다", "email");
        }
    }

    public Account findByNmAndEmail(String nm, String email) {

        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.delAt.eq("N"));
        builder.and(qAccount.nm.eq(nm));
        builder.and(qAccount.email.eq(email));

        Account byNmAndEmail = queryFactory
                .select(Projections.fields(Account.class,
                        qAccount.loginId, qAccount.nm, qAccount.email
                ))
                .from(qAccount)
                .where(builder)
                .fetchFirst();

        return byNmAndEmail;
    }

    public Account findByLoginIdAndEmail(String loginId, String email) {

        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAccount.delAt.eq("N"));
        builder.and(qAccount.loginId.eq(loginId));
        builder.and(qAccount.email.eq(email));

        Account byNmAndEmail = queryFactory
                .select(Projections.fields(Account.class,
                        qAccount.loginId
                        , qAccount.nm
                        , qAccount.email
                        , qAccount.emailAttcAt
                        , qAccount.emailAttcDtm
                ))
                .from(qAccount)
                .where(builder)
                .fetchFirst();

        return byNmAndEmail;
    }

    /**
     * MemberService [회원관리 - 수정]
     * @auther : 고선호
     * @craete_date : 2021-08-30, 월, 18:58
    */
    @Transactional
    public Account modify(MemberForm mngForm) {
//        Account account = this.load(mngForm.getId());
        // 신규 수정 추가 08.31
        Account account = memberRepository.findById(mngForm.getId()).orElseGet(Account::new);

        account.setLoginId(mngForm.getLoginId());
        account.setPwd(mngForm.getPwd());
        account.setNm(mngForm.getNm());
        account.setSexPrTy(mngForm.getSexPrTy());
        account.setBrthdy(mngForm.getBrthdy());
        account.setMoblphon(mngForm.getMoblphon());
        account.setEmail(mngForm.getEmail().trim());

        // account.setModDtm(LocalDateTime.now());
        // account.setModId(mngForm.getModId());

        //account.encodingPwd(passwordEncoder);

        return account;
    }

    /**
     * 회원 insert
     * @param memberForm
     * @return
     */
    @Transactional
    public boolean insert(MemberForm memberForm) throws ValidCustomException {
        try {
            verifyDuplicateLoginId(memberForm.getLoginId()); //아이디 중복체크
            if (chkDvTy(memberForm.getMberDvTy())) {
                if(memberForm.getEmailAttcAt().equals("Y")) {
                    verifyDuplicateEmail(memberForm.getEmail()); //이메일 중복체크
                    memberForm.setMobileAttcAt("N");
                    memberForm.setEmailAttcDtm(LocalDateTime.now());
                } else {
                    if(memberForm.getMobileAttcAt().equals("Y")) {
                        memberForm.setEmailAttcAt("N");
                        memberForm.setMobileAttcDtm(LocalDateTime.now());
                    }
                }
            }


            memberForm.setEmailAttcDtm(LocalDateTime.now());
            memberForm.setApproval("Y");
            memberForm.setDelAt("N");
            memberForm.setPwd(passwordEncoder.encode(memberForm.getPwd()));
            memberForm.setRegDtm(LocalDateTime.now());

            if (memberForm.getPrtctorEmail() == null || "".equals(memberForm.getPrtctorEmail())) {
                memberForm.setPrtctorAttcAt("Y");
            } else {
                memberForm.setPrtctorAttcAt("N");
            }
            memberForm.setPrtctorAttcDtm(LocalDateTime.now());

            //memberForm.setPwdLstDtm(LocalDateTime.now());
            Account account = modelMapper.map(memberForm, Account.class);
            account.setBrthdy(account.getBrthdy().replaceAll("-",""));
            account.setGroupYn("N");
            account.setFreeCard("N");
            if(memberForm.getMberDvTy().equals(UserRollType.CREW)){
                account.setCrewYn("Y");
            } else {
                account.setCrewYn("N");
            }
            Account save = memberRepository.save(account);

            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(memberForm.getMberDvTy());
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);

            if (memberForm.getMberDvTy() != null) {
                if (UserRollType.STUDENT.equals(memberForm.getMberDvTy())) {
                    MemberSchool memberSchool = new MemberSchool();
                    memberSchool.setMberPid(account.getId());
                    memberSchool.setAreaNm(memberForm.getAreaNm());
                    memberSchool.setSchlNm(memberForm.getSchlNm());
                    memberSchool.setGrade(memberForm.getGrade());
                    memberSchool.setBan(memberForm.getBan());
                    memberSchool.setNo(memberForm.getNo());
                    memberSchool.setTeacherNm(memberForm.getTeacherNm());
                    memberSchool.setRegDtm(LocalDateTime.now());
                    memberSchoolRepository.save(memberSchool);
                } else if (UserRollType.TEACHER.equals(memberForm.getMberDvTy())) {
                    MemberTeacher memberTeacher = new MemberTeacher();
                    memberTeacher.setMberPid(account.getId());
                    memberTeacher.setAreaNm(memberForm.getAreaNm());
                    memberTeacher.setSchlNm(memberForm.getSchlNm());
                    memberTeacher.setGrade(memberForm.getGrade());
                    memberTeacher.setBan(memberForm.getBan());
                    memberTeacher.setRegDtm(LocalDateTime.now());
                    memberTeacherRepository.save(memberTeacher);

                } else if (UserRollType.PARENT.equals(memberForm.getMberDvTy())) {
                    MemberParent memberParent = new MemberParent();
                    memberParent.setStdnprntId(account.getLoginId());
                    memberParent.setStdntId(memberForm.getStdntId());
                    memberParent.setRegDtm(LocalDateTime.now());
                    memberParentRepository.save(memberParent);
                }
            }

//            if ("N".equals(memberForm.getEmailAttcAt())) {
//                mailService.mailSend(makeMailAuthLinkMessage(account));
//                if ("N".equals(memberForm.getPrtctorAttcAt())) {
//                    mailService.mailSend(makeParentMailAuthLinkMessage(account));
//                }
//            }

            return true;
        } catch (ValidCustomException ve) {
            throw ve;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 그룹 insert
     * @param groupForm
     * @param attachedFile
     * @return
     */
    @Transactional
    public boolean groupInsert(GroupForm groupForm, MultipartFile attachedFile) throws ValidCustomException {
        try {
            verifyDuplicateLoginId(groupForm.getLoginId());
            if(groupForm.getAuthEmailChk() == 2){
                if(UserRollType.CREW.equals(groupForm.getMberDvTy())){
                    groupForm.setEmail(groupForm.getG_email());
                    groupForm.setEmailAttcAt("Y");
                    verifyDuplicateEmail(groupForm.getG_email());
                } else {
                    verifyDuplicateEmail(groupForm.getEmail());
                }
                groupForm.setEmailAttcDtm(LocalDateTime.now());
            }

            // 일반 단체 휴대폰 인증여부
            if(UserRollType.GROUP.equals(groupForm.getMberDvTy())) {
                groupForm.setGroupYn("Y");
                groupForm.setCrewYn("N");
                if(groupForm.getMobileAttcAt().equals("Y")) {
                    groupForm.setMobileAttcDtm(LocalDateTime.now());
                }
            } else {
                groupForm.setGroupYn("N");
                groupForm.setCrewYn("Y");
            }

            // approval : N -> 로그인 불가
            groupForm.setApproval("N");
            groupForm.setDelAt("N");
            groupForm.setPwd(passwordEncoder.encode(groupForm.getPwd()));
            groupForm.setRegDtm(LocalDateTime.now());

            if(UserRollType.GROUP.equals(groupForm.getMberDvTy())){
                groupForm.setNm(groupForm.getChargerNm());
                groupForm.setNcnm(groupForm.getGroupNm());
            } else {
                groupForm.setNcnm(groupForm.getCrewNm());
                groupForm.setNm(groupForm.getRptNm());
            }
            groupForm.setSexPrTy("NONE");
            groupForm.setBrthdy("00000000");
            Account account = modelMapper.map(groupForm, Account.class);
            Account save = memberRepository.save(account);

            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(groupForm.getMberDvTy());
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);

            if (groupForm.getMberDvTy() != null) {
                if (UserRollType.GROUP.equals(groupForm.getMberDvTy())) {
                    MemberGroup memberGroup = new MemberGroup();
                    memberGroup.setMberPid(account.getId());
                    memberGroup.setGroupNm(groupForm.getGroupNm());
                    memberGroup.setChrNm(groupForm.getChargerNm());
                    memberGroup.setPosition(groupForm.getPosition());
                    memberGroup.setAttcYn("N");
                    memberGroup.setMberEmail(groupForm.getEmail());
                    memberGroup.setMberMoblphon(groupForm.getMoblphon());
                    memberGroup.setBNum(groupForm.getBNum());
                    memberGroup.setRegDtm(LocalDateTime.now());
                    memberGroup.setBLicenseAttc(groupForm.getB_license_attc());
                    MemberGroup save1 = memberGroupRepository.save(memberGroup);
                    if (groupForm.getB_license_attc().equals("Y")) {
                        if (attachedFile != null) {
                            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE, FileUtilHelper.imageExt);
                            fileInfo.setDataPid(save.getId());
                            TableNmType tblBoardData = TableNmType.TBL_MEMBER_GROUP;
                            fileInfo.setTableNm(tblBoardData.name());
                            fileInfo.setDvTy(FileDvType.LICENSE.name());

                            FileInfo pid = fileInfoRepository.save(fileInfo);
                            memberGroupRepository.updateFlPid(pid.getId(),save1.getId());
                        }
                    }

                } else if (UserRollType.CREW.equals(groupForm.getMberDvTy())) {
                    MemberCrew memberCrew = new MemberCrew();
                    memberCrew.setMberPid(account.getId());
                    memberCrew.setCrewNm(groupForm.getCrewNm());
                    memberCrew.setCrewAffi(groupForm.getCrewAffi());
                    memberCrew.setAttcYn("N");
                    memberCrew.setCrewFNum(Integer.parseInt(groupForm.getCrewFNum()));
                    memberCrew.setRptNm(groupForm.getRptNm());
                    memberCrew.setRegDtm(LocalDateTime.now());
                    memberCrewRepository.save(memberCrew);

                }
            }



            return true;
        } catch (ValidCustomException ve) {
            throw ve;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @date : 2021/08/17
     * @auther : jerry
     * @param : UserRollType dv
     * 유저 구분(관리자) 체크
    **/
    private boolean chkDvTy(UserRollType dv) {
        if(
        !UserRollType.LECTURER.equals(dv)
                && !UserRollType.COUNSELOR.equals(dv)
                && !UserRollType.ADMIN.equals(dv)){
            return true;
        } else {
            return false;
        }
    }
    @Transactional
    public boolean update(MemberForm form) throws ValidCustomException {

        try {
            Account account = memberRepository.findById(form.getId()).orElseGet(Account::new);

            if (!form.getPwd().isEmpty()) {
                if (!form.getPwdChg().isEmpty()) { //사용자 회원정보 수정시 임시 비밀번호 체크
                    verifyPwChange(form.getPwd(), account.getPwd());
                    account.setPwd(passwordEncoder.encode(form.getPwdChg()));
                } else {
                    account.setPwd(passwordEncoder.encode(form.getPwd()));
                }
                //account.setPwdLstDtm(LocalDateTime.now());
            }

            account.setUpdPsId(form.getUpdPsId());
            account.setUpdDtm(LocalDateTime.now());

            //Account account = modelMapper.map(form, Account.class);
            if (form.getNm() != null && !form.getNm().isEmpty()) {
                account.setNm(form.getNm());
            }

            //account.setMngTpCdPid(form.getMngTpCdPid());

            /*if (account.getAgreeYn().equals("N")) {
                if (form.getAgreeYn().equals("Y")) {
                    account.setAgreeDtm(LocalDateTime.now());
                }
            }*/
            /*account.setAgreeYn(form.getAgreeYn());
            account.setCnntIp(form.getCnntIp());*/
            if (form.getSexPrTy() != null && !form.getSexPrTy().isEmpty()) {
                account.setSexPrTy(form.getSexPrTy());
            }
            if (form.getMoblphon() != null) {
                account.setMoblphon(form.getMoblphon());
            }
            if (form.getEmail() != null) {
                account.setEmail(form.getEmail());
            }
            if (form.getNcnm() != null) {
                account.setNcnm(form.getNcnm());
            }
            if (form.getBrthdy() != null && !form.getBrthdy().isEmpty()) {
                account.setBrthdy(form.getBrthdy().replaceAll("-", ""));
            }
            if (form.getZip() != null) {
                account.setZip(Integer.parseInt(form.getZip()));
            }
            if (form.getAdres() != null) {
                account.setAdres(form.getAdres());
            }
            if (form.getDtlAdres() != null) {
                account.setDtlAdres(form.getDtlAdres());
            }
            //return userRepository.save(account);

            if (form.getMberDvTy() != null) {
                if (UserRollType.STUDENT.equals(form.getMberDvTy())) {
                    MemberSchool memberSchool = memberSchoolRepository.findByMberPid(form.getId());
                    if (form.getAreaNm() != null && !form.getAreaNm().isEmpty()) {
                        memberSchool.setAreaNm(form.getAreaNm());
                    }
                    if (form.getSchlNm() != null && !form.getSchlNm().isEmpty()) {
                        memberSchool.setSchlNm(form.getSchlNm());
                    }
                    if (form.getGrade() != null && form.getGrade() > 0) {
                        memberSchool.setGrade(form.getGrade());
                    }
                    if (form.getBan() != null && !form.getBan().isEmpty()) {
                        memberSchool.setBan(form.getBan());
                    }
                    if (form.getNo() != null && form.getNo() > 0) {
                        memberSchool.setNo(form.getNo());
                    }
                    if (!form.getTeacherNm().isEmpty()) {
                        memberSchool.setTeacherNm(form.getTeacherNm());
                    }
                    if (form.getRegDtm() != null) {
                        memberSchool.setRegDtm(form.getRegDtm());
                    }
                } else if (UserRollType.TEACHER.equals(form.getMberDvTy())) {
                    MemberTeacher memberTeacher = memberTeacherRepository.findByMberPid(form.getId());
                    if (form.getAreaNm() != null && !form.getAreaNm().isEmpty()) {
                        memberTeacher.setAreaNm(form.getAreaNm());
                    }
                    if (form.getSchlNm() != null && !form.getSchlNm().isEmpty()) {
                        memberTeacher.setSchlNm(form.getSchlNm());
                    }
                    if (form.getGrade() != null && form.getGrade() > 0) {
                        memberTeacher.setGrade(form.getGrade());
                    }
                    if (form.getBan() != null && !form.getBan().isEmpty()) {
                        memberTeacher.setBan(form.getBan());
                    }
                    if (form.getRegDtm() != null) {
                        memberTeacher.setRegDtm(form.getRegDtm());
                    }

                } else if (UserRollType.PARENT.equals(form.getMberDvTy())) {
                    List<MemberParent> memberParents = memberParentRepository.findByStdnprntId(form.getLoginId());
                    for (MemberParent memberParent : memberParents) {
                        memberParent.setStdntId(form.getStdntId());
                    }
                }
            }
            return true;
        } catch (ValidCustomException ve) {
            ve.printStackTrace();
            throw ve;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteUserInfo(MemberForm form) {
        try {
            Account account = memberRepository.findById(form.getId()).orElseGet(Account::new);

            account.setUpdPsId(form.getUpdPsId());
            account.setUpdDtm(LocalDateTime.now());
            account.setDelAt("Y");

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isExistMng(Long id) {
        final Optional<Account> mng = memberRepository.findById(id);
        return (mng != null);
    }


    @Transactional
    public void updateLastLogin(String loginId) {
        Optional<Account> account = memberRepository.findByLoginId(loginId);
        if (account.isPresent()) {
            //account.setLstCnntDtm(LocalDateTime.now());
        }
    }

    public Account findById(Long id) {
        Optional<Account> byId = memberRepository.findById(id);
        return byId.orElseGet(Account::new);
    }

    @Transactional
    public boolean updateTempPw(MemberForm form) { //임시 비밀번호 생성 후 저장
        try {
            Account account = memberRepository.findByLoginIdAndEmail(form.getLoginId(), form.getEmail());

            String randomTempPwd = getRandomTempPw();
            String encodedTempPwd = "";
            if (!randomTempPwd.isEmpty()) {
                encodedTempPwd = passwordEncoder.encode(randomTempPwd);
            }

            if (!encodedTempPwd.isEmpty()) {
                account.setUpdPsId("SYSTEM");
                account.setUpdDtm(LocalDateTime.now());
                account.setPwd(encodedTempPwd);
                if (!randomTempPwd.isEmpty() && !encodedTempPwd.isEmpty()) {
                    mailService.mailSend(makeMailTempPwdMessage(account, randomTempPwd));
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateEmailAttc(MemberForm form) {
        try {
            boolean result = false;
            boolean prtResult = false;
            Account account = memberRepository.findByLoginIdAndDelAt(form.getLoginId(), "N");
            if (account != null) {
                if (form.getEmailAttcDtm() != null) { // 인증완료
                    if (form.getEmail() != null) {
                        account.setEmail(form.getEmail());
                    }
                    account.setEmailAttcAt(form.getEmailAttcAt());
                    account.setEmailAttcDtm(LocalDateTime.now());
                    memberRepository.save(account);
                    result = true;
                } else { //인증메일 재발송
                    if ("N".equals(form.getEmailAttcAt())) {
                        account.setEmailAttcAt(form.getEmailAttcAt());
                        account.setEmailAttcDtm(LocalDateTime.now());
                        memberRepository.save(account);
                        mailService.mailSend(makeMailAuthLinkMessage(account));
                        result = true;
                    } else {
                        result = false;
                    }
                }
                if (form.getPrtctorAttcDtm() != null) { // 인증완료
                    if (form.getPrtctorEmail() != null) {
                        account.setPrtctorEmail(form.getPrtctorEmail());
                    }
                    account.setPrtctorAttcAt(form.getPrtctorAttcAt());
                    account.setPrtctorAttcDtm(LocalDateTime.now());
                    memberRepository.save(account);
                    prtResult = true;
                } else { //인증메일 재발송
                    if ("N".equals(form.getPrtctorAttcAt())) {
                        account.setPrtctorAttcAt(form.getPrtctorAttcAt());
                        account.setPrtctorAttcDtm(LocalDateTime.now());
                        memberRepository.save(account);
                        mailService.mailSend(makeParentMailAuthLinkMessage(account));
                        prtResult = true;
                    } else {
                        prtResult = false;
                    }
                }
                return (result || prtResult);
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateForMyPage(MemberForm form, MemberSchoolForm memberSchoolForm, MemberTeacherForm memberTeacherForm, String[] childIdArr) {
        try {
            Account account = memberRepository.findById(form.getId()).orElse(new Account());

            //log 저장
            MemberLog memberLog = modelMapper.map(account, MemberLog.class);
            memberLog.setMberPid(account.getId());
            memberLogRepository.save(memberLog);

            if (form.getPwd() != "") {
                account.setPwd(passwordEncoder.encode(form.getPwd()));
            }
            account.setMoblphon(form.getMoblphon());
            account.setUpdDtm(LocalDateTime.now());

            if (account.getMberDvTy().equals(UserRollType.STUDENT)) {
                //log 저장
                MemberSchool memberSchool = memberSchoolRepository.findByMberPid(account.getId());

                MemberSchoolLog memberSchoolLog = modelMapper.map(memberSchool, MemberSchoolLog.class);
                memberSchoolLogRepository.save(memberSchoolLog);
                //변경된 학생추가정보 업데이트
                memberSchool.setAreaNm(memberSchoolForm.getAreaNm());
                memberSchool.setSchlNm(memberSchoolForm.getSchlNm());
                memberSchool.setGrade(memberSchoolForm.getGrade());
                memberSchool.setBan(memberSchoolForm.getBan());
                memberSchool.setNo(memberSchoolForm.getNo());
                memberSchool.setTeacherNm(memberSchoolForm.getTeacherNm());
                memberSchool.setRegDtm(LocalDateTime.now());
            } else if (account.getMberDvTy().equals(UserRollType.TEACHER)) {
                //log 저장
                MemberTeacher memberTeacher = memberTeacherRepository.findByMberPid(account.getId());

                MemberTeacherLog memberTeacherLog = modelMapper.map(memberTeacher, MemberTeacherLog.class);
                memberTeacherLogRepository.save(memberTeacherLog);
                //변경된 교원정보 업데이트
                memberTeacher.setAreaNm(memberTeacherForm.getAreaNm());
                memberTeacher.setSchlNm(memberTeacherForm.getSchlNm());
                memberTeacher.setGrade(memberTeacherForm.getGrade());
                memberTeacher.setBan(memberTeacherForm.getBan());
                memberTeacher.setRegDtm(LocalDateTime.now());
            } else if (account.getMberDvTy().equals(UserRollType.PARENT)) {
                memberParentRepository.deleteByStdnprntId(account.getLoginId());
                for (String childId : childIdArr) {
                    if (childId != null) {
                        MemberParent memberParent = new MemberParent();
                        memberParent.setStdnprntId(account.getLoginId());
                        memberParent.setStdntId(childId);
                        memberParent.setRegDtm(LocalDateTime.now());
                        memberParentRepository.save(memberParent);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendEmailChangeMail(Account account, MemberForm memberForm) {
        try {
            Account load = memberRepository.findByLoginId(account.getLoginId()).orElseGet(Account::new);
            account.setEmailAttcDtm(LocalDateTime.now());
            account.setEmail(load.getEmail());
            mailService.mailSend(makeMailChangeAuthLinkMessage(account, memberForm.getEmail()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Mail makeMailChangeAuthLinkMessage(Account target, String email) throws Exception {
        Mail mail = new Mail();

        mail.setAddress(target.getEmail());
        String link = getMailChangeAuthLink(target.getLoginId(), email, target.getEmailAttcDtm());
        String name = target.getNm();
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("link", link);
        mail.setContext(context);
        mail.setTemplate("/pages/email/mailAuthForm");

        StringBuilder message = new StringBuilder();
        message.append("[푸른코끼리] " + name + "님의 메일주소 변경 인증 메일입니다.");
        mail.setTitle(message.toString());

        /*message.append(name + "님의 메일주소 변경 인증 링크 입니다.");
        message.append("\n\n");
        message.append("아래의 링크를 클릭하시면 메일주소가 변경됩니다.");
        message.append("\n\n");
        message.append("인증 링크 : " + link).append("\n");
        message.append("\n");
        message.append("인증 완료 후 푸른코끼리 로그인 페이지로 이동 됩니다.\n");
        mail.setMessage(message.toString());*/

        return mail;
    }

    private Mail makeMailAuthLinkMessage(Account target) throws Exception {
        Mail mail = new Mail();

        mail.setAddress(target.getEmail());
        String link = getMailAuthLink(target.getLoginId(), target.getEmailAttcDtm());
        String name = target.getNm();
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("link", link);
        mail.setContext(context);
        mail.setTemplate("/pages/email/mailAuthForm");

        StringBuilder message = new StringBuilder();
        message.append("[푸른코끼리] " + name + "님의 회원가입 인증 메일입니다.");
        mail.setTitle(message.toString());

        /*message.append(name + "님의 회원가입 메일 인증 링크 입니다.");
        message.append("\n\n");
        message.append("아래의 링크를 클릭하시면 인증이 완료 됩니다.");
        message.append("\n\n");
        message.append("인증 링크 : " + link).append("\n");
        message.append("\n");
        message.append("인증 완료 후 푸른코끼리 로그인 페이지로 이동 됩니다.\n");
        mail.setMessage(message.toString());*/

        return mail;
    }

    // 인증번호 난수 생성
    public int gen6Digit(){
        java.util.Random generator = new java.util.Random();
        generator.setSeed(System.currentTimeMillis());
        return generator.nextInt(1000000) % 1000000;
    }


    public Mail sendEmailTempAuthKey(Account target, int tempKey) {
        Mail mail = new Mail();
        mail.setAddress(target.getEmail());
        Map<String, Object> context = new HashMap<>();
        context.put("tempKey", tempKey);
        mail.setContext(context);
        mail.setTemplate("/pages/email/authTempKeyForm");

        StringBuilder message = new StringBuilder();
        message.append("[푸른코끼리] 이메일 인증코드입니다.");
        mail.setTitle(message.toString());

        return mail;
    }

    private Mail makeParentMailAuthLinkMessage(Account target) throws Exception {
        Mail mail = new Mail();

        mail.setAddress(target.getPrtctorEmail());
        String link = getParentMailAuthLink(target.getLoginId(), target.getPrtctorAttcDtm());
        String name = target.getNm();
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("link", link);
        mail.setContext(context);
        mail.setTemplate("/pages/email/mailAuthForm");

        StringBuilder message = new StringBuilder();
        message.append("[푸른코끼리] " + name + "님의 보호자 인증 메일입니다.");
        mail.setTitle(message.toString());

        /*message.append(name + "님의 보호자 메일 인증 링크 입니다.");
        message.append("\n\n");
        message.append("아래의 링크를 클릭하시면 인증이 완료 됩니다.");
        message.append("\n\n");
        message.append("인증 링크 : " + link).append("\n");
        message.append("\n");
        message.append("인증 완료 후 푸른코끼리 로그인 페이지로 이동 됩니다.\n");
        mail.setMessage(message.toString());*/

        return mail;
    }

    private Mail makeMailTempPwdMessage(Account target, String randomTempPwd) {
        Mail mail = new Mail();

        mail.setAddress(target.getEmail());
        String name = target.getNm();
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("password", randomTempPwd);
        mail.setContext(context);
        mail.setTemplate("/pages/email/pwChangeForm");

        StringBuilder message = new StringBuilder();
        message.append("[푸른코끼리] " + name + "님께서 요청하신 임시 비밀번호입니다.");
        mail.setTitle(message.toString());

        /*message.append(name + " 님께서 요청하신 임시 비밀번호 입니다.");
        message.append("\n\n");
        message.append("임시 비밀번호 : " + randomTempPwd).append("\n");
        message.append("\n");
        message.append("임시 비밀번호를 발급 받으신 경우 반드시 신규 비밀번호로 변경하신 후 사용해 주세요.\n");
        mail.setMessage(message.toString());*/

        return mail;
    }

    private String getMailAuthLink(String loginId, LocalDateTime emailAttcDtm) throws Exception {
        String authLink = "";

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String ecryptStr = aesEncryptor.encrypt(loginId + "|" + emailAttcDtm.toString());

        String uri = "/api/member/mailAuth";
        String param = "?authKey=" + ecryptStr;

        authLink = domain + uri + param;

        return authLink;
    }

    private String getParentMailAuthLink(String loginId, LocalDateTime emailAttcDtm) throws Exception {
        String authLink = "";

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String ecryptStr = aesEncryptor.encrypt(loginId + "|" + emailAttcDtm.toString());

        String uri = "/api/member/parentMailAuth";
        String param = "?authKey=" + ecryptStr;

        authLink = domain + uri + param;

        return authLink;
    }

    private String getMailChangeAuthLink(String loginId, String chgEmail, LocalDateTime emailAttcDtm) throws Exception {
        String authLink = "";

        AESEncryptor aesEncryptor = AESEncryptor.getInstance(Constants.AESEncryptKey);
        String ecryptStr = aesEncryptor.encrypt(loginId + "|" + chgEmail + "|" + emailAttcDtm.toString());

        String uri = "/api/myPage/mailAuth";
        String param = "?authKey=" + ecryptStr;

        authLink = domain + uri + param;

        return authLink;
    }

    private String getRandomTempPw() {
        // 비밀번호 자리수
        int pwLength = 8;

        String randomPW = "";
        List<String> randomlist = new ArrayList<String>();

        // 대문자사용 1
        //int useU = 0;
        //String upperStr = "";
        // 소문자 사용 1
        int useL = 1;
        String lowerStr = "";
        // 숫자 사용 1
        int useN = 1;
        String ranNumberStr = "";
        // 특수문자 사용 1
        int useS = 1;
        String ranSkeyStr = "";
        // 사용할 특수문자
        String specialKey = "!=#$+@%*";
        // 제외시킬 대,소문자(비슷하게 생긴것들)
        String exceptionKey = "IOiol";


        //char upperChar;
        char lowerChar;
        char ranSkey;
        int startSkey;
        int ranNumber;
        //int upperCnt = 0;
        int lowerCnt = 0;
        int ranSkeyCnt = 0;
        int ranNumberCnt = 0;
        int whileNum = 0;

        boolean whileFlag = true;

        do {
            int loopNum = (int) (Math.random() * 4);
            // 대문자생성
            /*if ((whileFlag && lowerCnt < 1) || (whileFlag && upperStr.equals("1")) || (!whileFlag && useU == 1 && loopNum == 0)){
                do {
                    upperChar = (char) (Math.random() * 26 + 65);
                    upperStr = String.valueOf(upperChar);
                } while (exceptionKey.indexOf(upperStr) != -1);
                randomlist.add(upperStr);
                whileNum++;
            }*/
            // 소문자생성
            if ((whileFlag && lowerCnt < 1) || (whileFlag && lowerStr.equals("1")) || (!whileFlag && useL == 1 && loopNum == 0)) {
                do {
                    lowerChar = (char) (Math.random() * 26 + 97);
                    lowerStr = String.valueOf(lowerChar);
                } while (exceptionKey.indexOf(lowerStr) != -1);
                randomlist.add(lowerStr);
                lowerCnt++;
                whileNum++;
            }

            // 숫자생성(숫자 0,1 제외)
            if ((whileFlag && ranNumberCnt < 1) || (whileFlag && ranNumberStr.equals("1")) || (!whileFlag && useN == 1 && loopNum == 1)) {
                ranNumber = (int) (Math.random() * 8 + 2);
                ranNumberStr = String.valueOf(ranNumber);
                randomlist.add(ranNumberStr);
                ranNumberCnt++;
                whileNum++;
            }

            // 특수문자생성
            if ((whileFlag && ranSkeyCnt < 1) || (whileFlag && ranSkeyStr.equals("1")) || (!whileFlag && useS == 1 && loopNum == 2)) {
                startSkey = (int) (Math.random() * (specialKey.length() - 1)) + 1;
                ranSkey = specialKey.charAt(startSkey);
                ranSkeyStr = String.valueOf(ranSkey);
                randomlist.add(ranSkeyStr);
                ranSkeyCnt++;
                whileNum++;
            }

            whileFlag = false;
        } while (whileNum < pwLength);

        // 섞기
        Collections.shuffle(randomlist);
        for (String string : randomlist) {
            randomPW += string;
        }

        return randomPW;
    }

    @Transactional
    public boolean checkPwd(Long id, String pwd) {
        try {
            Account load = memberRepository.findById(id).orElseGet(Account::new);
            if (!passwordEncoder.matches(pwd, load.getPwd())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean setPasswordUpdate(MemberForm form, LoginCnntLogs log) {
        try{
            Account load = memberRepository.findById(form.getId()).orElseGet(Account::new);
            load.setPwd(form.getPwd());

            loginCnntLogsRepository.save(log);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }

    }

    public List<JSONObject> sexResult() {
        QAccount qAccount = QAccount.account;

        try {
            List<Account> mngList = queryFactory
                    .select(Projections.fields(Account.class,
                            qAccount.sexPrTy,
                            qAccount.sexPrTy.count().as("cVal")
                    ))
                    .from(qAccount)
                    .groupBy(qAccount.sexPrTy)
                    .fetch();

            if (mngList != null) {
                List<JSONObject> objects = new ArrayList<>();
                int idx = 0;
                for (Account account : mngList) {
                    JSONObject json = new JSONObject();
                    json.put("category", GenderType.valueOf(account.getSexPrTy()).getName());
                    json.put("cVal", account.getCVal());

                    objects.add(json);
                    idx++;

                }

                return objects;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

    }

    public List<JSONObject> ageResult() {
        try {
            List<JSONObject> results = new ArrayList<>();

            List<Object[]> objectList = memberRepository.ageResult();
            for (Object[] objects : objectList) {
                JSONObject json = new JSONObject();

                json.put("cVal", objects[0]);
                json.put("category", objects[1]);
                results.add(json);
            }

            return results;
        } catch (Exception e) {
            return null;
        }
    }

    public List<JSONObject> typeResult() {
        try {
            List<JSONObject> results = new ArrayList<>();

            List<Object[]> objectList = memberRepository.typeResult();
            for (Object[] objects : objectList) {
                JSONObject json = new JSONObject();

                json.put("cVal", objects[0]);
                json.put("category", objects[1]);
                results.add(json);
            }

            return results;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> monthResult(String nowYear) {
        try {
            List<Object[]> objectList = memberRepository.monthResult(nowYear);
            return objectList;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> monthConnectResult(String nowYear) {
        try {
            List<Object[]> objectList = memberRepository.monthConnectResult(nowYear);
            return objectList;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object[]> dayConnectResult(String nowYearMonth) {
        try {
            List<Object[]> objectList = memberRepository.dayConnectResult(nowYearMonth);
            return objectList;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Map<String,Object>> memberTypeToAdviceResult(SearchForm searchForm) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.memberTypeToAdviceResult(searchForm);
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            map.put("cVal", objects[0]);
            map.put("category", objects[1]);
            results.add(map);
        }

        return results;
    }

    public List<Map<String,Object>> memberTypeToCourse(SearchForm searchForm) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.memberTypeToCourseResult(searchForm);
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            map.put("cVal", objects[0]);
            map.put("category", objects[1]);
            results.add(map);
        }

        return results;
    }

    public List<Map<String,Object>> courseCompleteStatus(CourseRequestForm form) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.courseCompleteStatus(form);
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            map.put("cVal", objects[0]);
            //map.put("category", objects[1]); //지역명
            map.put("category", objects[2]); //학교명
            results.add(map);
        }

        return results;
    }

    public List<Map<String,Object>> surveyStatusResult(SearchForm form) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.surveyStatusResult(form);
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            map.put("cVal", objects[0]);
            map.put("category", objects[1]);
            results.add(map);
        }

        return results;
    }

    public List<Map<String,Object>> surveyResponseResult(SearchForm form) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.surveyResponseResult(form);
        boolean[] temp = {false, false, false, false, false};
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            int grade = ((BigDecimal) objects[1]).intValue();
            map.put("cVal", objects[0]);
            map.put("category", grade+"단계");
            temp[grade-1] = true;
            results.add(map);
        }

        int idx = 1;
        for (boolean b : temp) {
            if (!b) {
                map=new HashMap<>();
                map.put("cVal", 0);
                map.put("category", idx+"단계");
                results.add((idx-1), map);
            }
            idx++;
        }

        return results;
    }

    public List<Map<String,Object>> menuStatusResult(SearchForm form) {
        List<Map<String,Object>> results = new ArrayList<>();

        Map<String, Object> map = null;
        List<Object[]> objectList = memberRepository.menuStatusResult(form);
        for (Object[] objects : objectList) {
            map=new HashMap<>();

            map.put("cVal", objects[0]);
            map.put("category", objects[1]);
            results.add(map);
        }

        return results;
    }

    public boolean existsByEmail(String email) {
        Account account = memberRepository.findByEmail(email).orElseGet(Account::new);
        return (account != null && account.getId() != null);
    }

    public boolean existsByLoginId(String loginId) {
        Account account = memberRepository.findByLoginId(loginId).orElseGet(Account::new);
        return (account != null && account.getId() != null);
    }

    public boolean existsSpace(String text) {
        if (text == null) return true;
        return text.contains(" ");
    }

    public void updateFreeCard(Long id) {
        memberRepository.updateFreeCard(id, "Y");
    }
    @Transactional
    public void batchRegister(MemberSchoolForm memberSchoolForm) {
        MemberForm memberForm = new MemberForm();
        String tempId = memberSchoolForm.getLoginId();

        for (int i = 1; i <= memberSchoolForm.getBatchArr(); i++) {
            //계정 정보 추가
            memberForm.setDelAt("N");
            memberForm.setPwd(passwordEncoder.encode(memberSchoolForm.getPwd())); //패스워드 셋
            memberForm.setRegDtm(LocalDateTime.now()); //등록일
            memberForm.setPrtctorAttcAt("Y");
            memberForm.setOnlineEdu("N"); // 현장교육 N = 오프라인
            memberForm.setApproval("Y"); // 승인여부
            memberForm.setNm("TEST");
            memberForm.setSexPrTy("MALE");


            if (i < 10) {
                tempId = memberSchoolForm.getLoginId();
                tempId += "0" + i;
            } else {
                memberSchoolForm.getLoginId();
                tempId += i;
            }
            memberForm.setLoginId(tempId);//변형된 계정 셋
            System.out.println(tempId);
            memberForm.setMberDvTy(UserRollType.BATCH);
            Account account = modelMapper.map(memberForm, Account.class);
            Account save = memberRepository.save(account);
            ApplicationForm applicationForm = new ApplicationForm();

            //로그인아이디 초기화
            tempId = "";

            MemberRoll memberRoll = new MemberRoll();
            memberRoll.setMberPid(save.getId());
            memberRoll.setMberDvTy(UserRollType.BATCH);
            memberRoll.setRegDtm(LocalDateTime.now());
            memberRoll.setRegPsId(save.getRegPsId());
            memberRollRepository.save(memberRoll);
            //memberSchool에 인서트 해주는 프로시저 호출
            memberSchoolRepository.pr_findTID(memberSchoolForm.getAreaNm(), memberSchoolForm.getSchlNm(),
                    memberSchoolForm.getGrade(), memberSchoolForm.getBan(), save.getId(), LocalDateTime.now());
        }
    }
}
