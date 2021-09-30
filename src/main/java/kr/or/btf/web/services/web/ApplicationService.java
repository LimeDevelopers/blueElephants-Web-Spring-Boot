package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.QPrevention;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.InstructorDvTy;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.*;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class ApplicationService extends _BaseService {
    private final PreventionInstructorRepository preventionInstructorRepository;
    private final PreventionRepository preventionRepository;
    private final PreventionMasterRepository preventionMasterRepository;
    private final ApplicationRepository applicationRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;
    private final EventRepository eventRepository;
    private final ContestRepository contestRepository;
    private final TeamMemberRepository teamMemberRepository;


    public MemberTeacher getSchoolData(Long id) {
        QMemberTeacher qMemberTeacher = QMemberTeacher.memberTeacher;
        OrderSpecifier<LocalDateTime> orderSpecifier = qMemberTeacher.regDtm.desc();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qMemberTeacher.mberPid.eq(id));
        MemberTeacher teacher = queryFactory
                .select(Projections.fields(MemberTeacher.class,
                        qMemberTeacher.areaNm,
                        qMemberTeacher.ban,
                        qMemberTeacher.schlNm,
                        qMemberTeacher.grade
                ))
                .from(qMemberTeacher)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return teacher;
    }


    public boolean registerPreIns(PreventionInstructorForm preventionInstructorForm) {
        preventionInstructorForm.setApproval("W");
        preventionInstructorForm.setDelAt("N");
        preventionInstructorForm.setRegDtm(LocalDateTime.now());
        PreventionInstructor preventionInstructor = modelMapper.map(preventionInstructorForm, PreventionInstructor.class);
        preventionInstructor.setInsType(InstructorDvTy.NO);
        PreventionInstructor save = preventionInstructorRepository.save(preventionInstructor);
        if(save.getId()!=null){
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePreIns(PreventionInstructorForm preventionInstructorForm) {
        if(preventionInstructorForm.getApproval() == null) {
            preventionInstructorForm.setApproval("N");
        }
        if(preventionInstructorForm.getDelAt() == null) {
            preventionInstructorForm.setApproval("N");
        }
        try {
            PreventionInstructor pre = preventionInstructorRepository.findById(preventionInstructorForm.getPreInsPid()).orElseGet(PreventionInstructor::new);
            pre.setInsType(InstructorDvTy.NO);
            pre.setAwards(preventionInstructorForm.getAwards());
            pre.setEduMatters(preventionInstructorForm.getEduMatters());
            pre.setEnrollPeriod(preventionInstructorForm.getEnrollPeriod());
            pre.setExpContent(preventionInstructorForm.getExpContent());
            pre.setExpNm(preventionInstructorForm.getExpNm());
            pre.setExpPeriod(preventionInstructorForm.getExpPeriod());
            pre.setGrdStatus(preventionInstructorForm.getGrdStatus());
            pre.setMajor(preventionInstructorForm.getMajor());
            pre.setQualifications(preventionInstructorForm.getQualifications());
            pre.setSchlNm(preventionInstructorForm.getSchlNm());
            pre.setSelfDrivingAt(preventionInstructorForm.getSelfDrivingAt());
            pre.setSnsStatus(preventionInstructorForm.getSnsStatus());
            pre.setSnsUrl(preventionInstructorForm.getSnsUrl());
            pre.setTempSave(preventionInstructorForm.getTempSave());
            pre.setUpdDtm(LocalDateTime.now());
            pre.setThumbImg(preventionInstructorForm.getThumbImg());
            pre.setApproval(preventionInstructorForm.getApproval());
            pre.setDelAt(preventionInstructorForm.getDelAt());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public PreventionInstructor getPreIns(Long mberPid) {
//        QPreventionInstructor qPreventionInstructor = QPreventionInstructor.preventionInstructor;
//        PreventionInstructor results = queryFactory
//                .select(Projections.fields(PreventionInstructor.class,
//                        qPreventionInstructor.id,
//                        qPreventionInstructor.mberPid
//                ))
//                .from(qPreventionInstructor)
//                .where(qPreventionInstructor.mberPid.eq(mberPid))
//                .fetchFirst();
        return preventionInstructorRepository.findByMberPid(mberPid);
    }

    public Prevention getPreAt(Long id, Long mberPid) {
        return preventionRepository.findByPreMstPidAndMberPid(id,mberPid);
    }

    public PreventionMaster getPreEduMstData(Long id) {
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionMaster.mberPid.eq(id));
        PreventionMaster results = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id,
                        qPreventionMaster.mberPid,
                        qPreventionMaster.address,
                        qPreventionMaster.approval,
                        qPreventionMaster.classesNum,
                        qPreventionMaster.personnel,
                        qPreventionMaster.regDtm,
                        qPreventionMaster.schlNm,
                        qPreventionMaster.tempSave,
                        qPreventionMaster.updDtm,
                        qPreventionMaster.hpSchd1Et,
                        qPreventionMaster.hpSchd1Personnel,
                        qPreventionMaster.hpSchd1Wt,
                        qPreventionMaster.hpSchd2Wt,
                        qPreventionMaster.hpSchd2Personnel,
                        qPreventionMaster.hpSchd2Et,
                        qPreventionMaster.resultQna1,
                        qPreventionMaster.resultQna2,
                        qPreventionMaster.resultQna3,
                        qPreventionMaster.resultQna4,
                        qPreventionMaster.resultQna5
                ))
                .from(qPreventionMaster)
                .where(qPreventionMaster.id.eq(id))
                .orderBy(orderSpecifier)
                .fetchFirst();
        return results;
    }

    public PreventionMaster getPreEduMst(Long id, Long mberPid) {
        return preventionMasterRepository.findByPrePidAndMberPid(id, mberPid);
    }

    public Page<PreventionMaster> getMyPreEduMstList(Pageable pageable, Long id) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        QAccount qAccount = QAccount.account;
        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();

        QueryResults<PreventionMaster> mngList = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id,
                        qPreventionMaster.address,
                        qPreventionMaster.approval,
                        qPreventionMaster.mberPid,
                        qPreventionMaster.classesNum,
                        qPreventionMaster.schlNm,
                        qPreventionMaster.tempSave,
                        qPreventionMaster.personnel,
                        qPreventionMaster.prePid,
                        qPreventionMaster.regDtm,
                        qPreventionMaster.updDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.id.eq(qPreventionMaster.mberPid)),
                                "nm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.moblphon)
                                        .from(qAccount)
                                        .where(qAccount.id.eq(qPreventionMaster.mberPid)),
                                "moblphon"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.email)
                                        .from(qAccount)
                                        .where(qAccount.id.eq(qPreventionMaster.mberPid)),
                                "email")

                ))
                .from(qPreventionMaster)
                .where(qPreventionMaster.prePid.eq(id))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<Prevention> getMyApplyPreEduList(Pageable pageable, Long id) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가
        QPrevention qPrevention = QPrevention.prevention;
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPrevention.id.desc();

        QueryResults<Prevention> mngList = queryFactory
                .select(Projections.fields(Prevention.class,
                        qPrevention.id,
                        qPrevention.approval,
                        qPrevention.preMstPid,
                        qPrevention.regDtm,
                        qPreventionMaster.tel.as("tel"),
                        qPreventionMaster.address.as("address"),
                        qPreventionMaster.classesNum.as("classesNum"),
                        qPreventionMaster.schlNm.as("schlNm")
                ))
                .from(qPrevention)
                .where(qPrevention.mberPid.eq(id))
                .leftJoin(qPreventionMaster).on(qPrevention.preMstPid.eq(qPreventionMaster.id))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<PreventionMaster> getPreEduMstList(Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPrevention qPrevention = QPrevention.prevention;
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();

        QueryResults<PreventionMaster> mngList = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id,
                        qPreventionMaster.tel,
                        qPreventionMaster.address,
                        qPreventionMaster.approval,
                        qPreventionMaster.mberPid,
                        qPreventionMaster.classesNum,
                        qPreventionMaster.schlNm,
                        qPreventionMaster.tempSave,
                        qPreventionMaster.personnel,
                        qPreventionMaster.prePid,
                        qPreventionMaster.regDtm,
                        qPreventionMaster.updDtm
                ))
                .from(qPreventionMaster)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public boolean setPreeducation(Long id, Long mberId) {
        PreventionForm form = new PreventionForm();
        form.setMberPid(mberId);
        form.setPreMstPid(id);
        form.setRegDtm(LocalDateTime.now());
        form.setApproval("N");
        form.setDelAt("N");
        Prevention prevention = modelMapper.map(form, Prevention.class);
        Prevention save = preventionRepository.save(prevention);
        if(save.getId()!=null){
            return true;
        } else {
            return false;
        }
    }

    public boolean registerPreEdu(PreventionMasterForm preventionMasterForm) {
        preventionMasterForm.setApproval("W");
        preventionMasterForm.setDelAt("N");
        preventionMasterForm.setRegDtm(LocalDateTime.now());
        PreventionMaster preventionMaster = modelMapper.map(preventionMasterForm, PreventionMaster.class);
        PreventionMaster save = preventionMasterRepository.save(preventionMaster);
        if(save.getId()!=null){
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePreEdu(PreventionMasterForm preventionMasterForm) {
        if(preventionMasterForm.getApproval() == null) {
            preventionMasterForm.setApproval("N");
        }
        if(preventionMasterForm.getDelAt() == null) {
            preventionMasterForm.setApproval("N");
        }
        try {
            PreventionMaster pre = preventionMasterRepository.findById(preventionMasterForm.getPreMstPid()).orElseGet(PreventionMaster::new);
            pre.setAddress(preventionMasterForm.getAddress());
            pre.setClassesNum(preventionMasterForm.getClassesNum());
            pre.setTel(preventionMasterForm.getTel());
            pre.setResultQna1(preventionMasterForm.getResultQna1());
            pre.setResultQna2(preventionMasterForm.getResultQna2());
            pre.setResultQna3(preventionMasterForm.getResultQna3());
            pre.setResultQna4(preventionMasterForm.getResultQna4());
            pre.setResultQna5(preventionMasterForm.getResultQna5());
            pre.setHpSchd1Personnel(preventionMasterForm.getHpSchd1Personnel());
            pre.setHpSchd1Wt(preventionMasterForm.getHpSchd1Wt());
            pre.setHpSchd1Et(preventionMasterForm.getHpSchd1Et());
            pre.setHpSchd2Personnel(preventionMasterForm.getHpSchd2Personnel());
            pre.setHpSchd2Wt(preventionMasterForm.getHpSchd2Wt());
            pre.setHpSchd2Et(preventionMasterForm.getHpSchd2Et());
            pre.setUpdDtm(LocalDateTime.now());
            pre.setApproval(preventionMasterForm.getApproval());
            pre.setDelAt(preventionMasterForm.getDelAt());
            pre.setTempSave(preventionMasterForm.getTempSave());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Page<Prevention> getMyPreEduList(Pageable pageable,
                                          Long id) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가
        QPrevention qPrevention = QPrevention.prevention;
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<Long> orderSpecifier = qPrevention.id.desc();
        JPAQuery<Prevention> list = queryFactory
                .select(Projections.fields(Prevention.class,
                        qPrevention.id,
                        qPrevention.regDtm,
                        qPrevention.delAt,
                        qPrevention.approval,
                        ExpressionUtils.as(
                                JPAExpressions.select(qPreventionMaster.id.count())
                                        .from(qPreventionMaster)
                                        .where(qPreventionMaster.prePid.eq(qPrevention.id)),
                                "mstCnt")
                ))
                .from(qPrevention)
                .where(qPrevention.mberPid.eq(id))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        list.orderBy(orderSpecifier);
        QueryResults<Prevention> mngList = list.fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    /**
     * 예방교육 리스트
     * @author : jerry
     * @version : 1.0.0
     * 작성일 : 2021/09/15
    **/
//    public Page<Prevention> getPreEduList(Pageable pageable,
//                                          SearchForm searchForm) {
//        if(searchForm.getSrchWord() == null || searchForm.getSrchWord().equals("")){
//            searchForm.setSrchWord("");
//        }
//        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
//        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize()));
//        QPrevention qPrevention = QPrevention.prevention;
//        OrderSpecifier<Long> orderSpecifier = qPrevention.id.desc();
//        JPAQuery<Prevention> list = queryFactory
//                .select(Projections.fields(Prevention.class,
//                        qPrevention.id,
//                        qPrevention.schlNm,
//                        qPrevention.address,
//                        qPrevention.tel,
//                        qPrevention.regDtm,
//                        qPrevention.delAt,
//                        qPrevention.approval))
//                .from(qPrevention)
//                .where(qPrevention.schlNm.contains(searchForm.getSrchWord())
//                        .or(qPrevention.address.contains(searchForm.getSrchWord()))
//                        .or(qPrevention.tel.contains(searchForm.getSrchWord())))
//                .where()
//                .limit(pageable.getPageSize())
//                .offset(pageable.getOffset());
//                list.orderBy(orderSpecifier);
//        QueryResults<Prevention> mngList = list.fetchResults();
//        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
//    }

    public Prevention getPreEdu(Long id) {
        return preventionRepository.findByIdAndDelAt(id,"N");
    }

    //파트너스 신청
    public boolean partnersRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
          applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.PARTNERS);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
        //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }

    //지지크루 신청
    public boolean zzcrewRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
            applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.CREW);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
            //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }
    public boolean zzdeclareRegister(ApplicationForm applicationForm, MultipartFile attachedFile) throws Exception {
        if(applicationForm.getMberPid() == null) {
            applicationForm.setMberPid(0L);
        }
        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.DECLARE);

        //첨부파일이 있을 때
        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
            //첨부파일 없을 때
        } else {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }
        return true;
    }

    public boolean eventRegister(ApplicationForm applicationForm , MultipartFile attachedFile) throws Exception {

        if(applicationForm.getMberPid() == null){
            applicationForm.setMberPid(0L);
        }

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setUpdDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.EVENT);

        if (attachedFile != null && !attachedFile.isEmpty()) {
            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());
            FileInfo fileInfo = new FileInfo();
            try {
                fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_LICENSE , FileUtilHelper.imageExt);
            } catch (IOException e) {
                e.setStackTrace(e.getStackTrace());
            }
            fileInfo.setDataPid(save.getId());
            TableNmType tblApplication = TableNmType.TBL_APPLICATION;
            fileInfo.setTableNm(tblApplication.name());
            fileInfo.setDvTy(FileDvType.LICENSE.name());
            FileInfo pid = fileInfoRepository.save(fileInfo);
            applicationRepository.updateFlPid(pid.getId(), save.getId());
            applicationRepository.save(application);
            //첨부파일 없을 때
        } else {

            ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
            ActivityApplication save = applicationRepository.save(application);
            application.setId(save.getId());

            applicationRepository.save(application);
        }

        return true;
    }
    public Event getEventData(Long id) {
        Optional<Event> result = eventRepository.findById(id);
        return result.orElse(null);
    }

    public Contest getContestData(Long id) {
        Optional<Contest> result = contestRepository.findById(id);
        return result.orElse(null);
    }

    public Page<Contest> getContestList(Pageable pageable ,
                                        SearchForm searchForm) {
        if (searchForm.getSrchWord() == null || searchForm.getSrchWord().equals("")) {
            searchForm.setSrchWord("");
        }
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize()));

        QContest qContest = QContest.contest;
        OrderSpecifier<Long> orderSpecifier = qContest.id.desc();
        JPAQuery<Contest> list = queryFactory
                .select(Projections.fields(Contest.class,
                        qContest.id ,qContest.ttl , qContest.cn , qContest.stYmd , qContest.edYmd ,
                        qContest.updDtm , qContest.updPsId ,qContest.regDtm ,qContest.regPsId , qContest.delAt ,
                        qContest.readCnt, qContest.fieldDv ,qContest.organ_dtl , qContest.imgFl))
                .from(qContest)
                .where(qContest.delAt.eq("N"))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        list.orderBy(orderSpecifier);
        QueryResults<Contest> mngList = list.fetchResults();
        return new PageImpl<>(mngList.getResults() , pageable , mngList.getTotal());
    }

    public Page<Event> getEventList(Pageable pageable ,
                                    SearchForm searchForm) {
        if(searchForm.getSrchWord() == null || searchForm.getSrchWord().equals("")){
            searchForm.setSrchWord("");
        }
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize()));

        QEvent qEvent = QEvent.event;
        OrderSpecifier<Long> orderSpecifier = qEvent.id.desc();
        JPAQuery<Event> list = queryFactory
                .select(Projections.fields(Event.class ,
                        qEvent.id, qEvent.imgFl ,qEvent.stYmd, qEvent.edYmd, qEvent.ttl ,qEvent.cn ,
                        qEvent.readCnt , qEvent.spotDtl , qEvent.regDtm))
                .from(qEvent)
                .where(qEvent.delAt.eq("N"))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset());
        list.orderBy(orderSpecifier);
        QueryResults<Event> mngList = list.fetchResults();
        return new PageImpl<>(mngList.getResults() , pageable, mngList.getTotal());
    }

    public List<Contest> getContestDetail(Long id){

        QContest qContest = QContest.contest;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qContest.id.eq(id));

        List<Contest> contestList = queryFactory
                .select(Projections.fields(Contest.class ,
                        qContest.ttl , qContest.cn, qContest.stYmd , qContest.edYmd , qContest.imgFl ,
                        qContest.organ_dtl , qContest.fieldDv , qContest.readCnt ,qContest.regPsId ,
                        qContest.regDtm , qContest.updPsId , qContest.updDtm))
                .from(qContest)
                .where(builder)
                .fetch();

        return contestList;
    }

    public List<Event> getEventDetail(Long id) {
        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.id.eq(id));

        List<Event> eventList = queryFactory
                .select(Projections.fields(Event.class ,
                        qEvent.id, qEvent.ttl, qEvent.cn, qEvent.stYmd, qEvent.edYmd, qEvent.spotDtl,
                        qEvent.statusType, qEvent.imgFl, qEvent.cntntsUrl, qEvent.fxSeTy, qEvent.regPsId,
                        qEvent.readCnt, qEvent.regDtm, qEvent.updPsId, qEvent.updDtm, qEvent.delAt))
                .from(qEvent)
                .where(builder)
                .fetch();

        return eventList;
    }

    public Boolean updateApproval(String pid) {
        Long chgStr = Long.parseLong(pid);
        int rs = applicationRepository.setApproval(chgStr,"Y");
        if(rs > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean contestRegister(ApplicationForm applicationForm) throws Exception {

        if(applicationForm.getMberPid() == null) {
            applicationForm.setMberPid(0L);
        }
        TeamMember teamMember = new TeamMember();

        applicationForm.setApproval("N");
        applicationForm.setDelAt("N");
        applicationForm.setRegDtm(LocalDateTime.now());
        applicationForm.setAppDvTy(AppRollType.CONTEST);

        ActivityApplication application = modelMapper.map(applicationForm, ActivityApplication.class);
        ActivityApplication save = applicationRepository.save(application);
        application.setId(save.getId());

        //teamMember insert
        teamMember.setContset_pid(applicationForm.getContestPid());
        //app_id insert
        teamMember.setApp_pid(save.getId());

        teamMember.setTnm(applicationForm.getT_nm());
        teamMember.setTaffi(applicationForm.getT_affi());
        teamMember.setTbrthday(applicationForm.getT_brthday());
        teamMember.setTmoblphon(applicationForm.getT_moblphon());

        teamMember.setTnm1(applicationForm.getT_nm1());
        teamMember.setTaffi1(applicationForm.getT_affi1());
        teamMember.setTbrthday1(applicationForm.getT_brthday1());
        teamMember.setTmoblphon1(applicationForm.getT_moblphon1());

        teamMember.setTnm2(applicationForm.getT_nm2());
        teamMember.setTaffi2(applicationForm.getT_affi2());
        teamMember.setTbrthday2(applicationForm.getT_brthday2());
        teamMember.setTmoblphon2(applicationForm.getT_moblphon2());

        teamMember.setTnm3(applicationForm.getT_nm3());
        teamMember.setTaffi3(applicationForm.getT_affi3());
        teamMember.setTbrthday3(applicationForm.getT_brthday3());
        teamMember.setTmoblphon3(applicationForm.getT_moblphon3());

        teamMember.setTnm4(applicationForm.getT_nm4());
        teamMember.setTaffi4(applicationForm.getT_affi4());
        teamMember.setTbrthday4(applicationForm.getT_brthday4());
        teamMember.setTmoblphon4(applicationForm.getT_moblphon4());

        teamMember.setTnm5(applicationForm.getT_nm5());
        teamMember.setTaffi5(applicationForm.getT_affi5());
        teamMember.setTbrthday5(applicationForm.getT_brthday5());
        teamMember.setTmoblphon5(applicationForm.getT_moblphon5());
        teamMemberRepository.save(teamMember);

        return true;
    }
}
