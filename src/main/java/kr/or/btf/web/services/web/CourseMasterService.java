package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.CompleteStatusType;
import kr.or.btf.web.repository.web.CourseMasterRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.CourseMasterForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseMasterService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final CourseMasterRepository courseMasterRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    public Page<CourseMaster> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, (searchForm.getPageSize() == null ? Constants.DEFAULT_PAGESIZE : searchForm.getPageSize())); // <- Sort 추가

        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourseMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseMaster.delAt.eq("N"));

        if (searchForm.getApplyAble() != null && searchForm.getApplyAble()) {
            builder.and(JPAExpressions.select(qCourseMasterRel.count())
                    .from(qCourseMasterRel)
                    .where(qCourseMasterRel.crsMstPid.eq(qCourseMaster.id)).eq(Constants.satisfSvySn.longValue()));
        }
        if (searchForm.getSrchGbn() != null && searchForm.getSrchGbn() != "") {
            builder.and(qCourseMaster.mberDvTy.eq(searchForm.getSrchGbn()));
        }
        if (searchForm.getSrchWord() != null && searchForm.getSrchWord() != "") {
            builder.and(qCourseMaster.crsNm.like("%" + searchForm.getSrchWord() + "%"));
        }
        if (searchForm.getUseAt() != null) {
            builder.and(qCourseMaster.openAt.eq(searchForm.getUseAt()));
        }

        QueryResults<CourseMaster> mngList = queryFactory
                .select(Projections.fields(CourseMaster.class,
                        qCourseMaster.id,
                        qCourseMaster.mberDvTy,
                        qCourseMaster.crsNm,
                        qCourseMaster.crsCn,
                        qCourseMaster.imgFl,
                        qCourseMaster.regPsId,
                        qCourseMaster.regDtm,
                        qCourseMaster.updPsId,
                        qCourseMaster.updDtm,
                        qCourseMaster.openAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourseMaster.regPsId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseMasterRel.count())
                                        .from(qCourseMasterRel)
                                        .where(qCourseMasterRel.crsMstPid.eq(qCourseMaster.id)
                                                .and(qCourseMasterRel.sn.eq(Constants.fieldCrsSn))),
                                "fieldCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseRequest.count())
                                        .from(qCourseRequest)
                                        .where(qCourseRequest.crsMstPid.eq(qCourseMaster.id)),
                                "requestCnt")

                ))
                .from(qCourseMaster)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<CourseMaster> listTchr(Pageable pageable , SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);

        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE_3); // <- Sort 추가

        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourseMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseMaster.delAt.eq("N")); // 삭제여부

        if (searchForm.getApplyAble() != null && searchForm.getApplyAble()) {
            builder.and(JPAExpressions.select(qCourseMasterRel.count())
                    .from(qCourseMasterRel)
                    .where(qCourseMasterRel.crsMstPid.eq(qCourseMaster.id)).eq(Constants.satisfSvySn.longValue()));
            log.info("********* 선생권한 으로 분류됨 *********" + builder);
        }

        if (searchForm.getMberDvType() != null) {
            builder.and(qCourseMaster.mberDvTy.eq(searchForm.getMberDvType().name()));
            log.info("********* 선생권한 으로 분류됨 *********" + builder);
        }
        if (searchForm.getUseAt() != null) {
            builder.and(qCourseMaster.openAt.eq(searchForm.getUseAt()));
            log.info("********* 선생권한 으로 분류됨 *********" + builder);
        }

        QueryResults<CourseMaster> mngList = queryFactory
                .select(Projections.fields(CourseMaster.class,
                        qCourseMaster.id,
                        qCourseMaster.mberDvTy,
                        qCourseMaster.crsNm,
                        qCourseMaster.crsCn,
                        qCourseMaster.imgFl,
                        qCourseMaster.regPsId,
                        qCourseMaster.regDtm,
                        qCourseMaster.updPsId,
                        qCourseMaster.updDtm,
                        qCourseMaster.openAt,
                        qCourseRequest.id.as("atnlcReqPid"),
                        ///qCourseRequestComplete.count().as("completeCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourseMaster.regPsId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseRequestComplete.count())
                                        .from(qCourseRequestComplete)
                                        .where(qCourseRequestComplete.cmplSttTy.eq(CompleteStatusType.COMPLETE.name()).and(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id)).and(qCourseRequestComplete.crsMstPid.eq(qCourseMaster.id))),
                                "completeCnt")

                ))
                .from(qCourseMaster)
                .leftJoin(qCourseRequest).on(qCourseMaster.id.eq(qCourseRequest.crsMstPid)
                        .and(qCourseRequest.mberPid.eq(searchForm.getUserPid())))
                .where(builder)

                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())

                .orderBy(orderSpecifier)
                .fetchResults();

        System.out.println("mngList : " + mngList.getResults());
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<CourseMaster> listByRequest(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        // 수정중 김재일
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE_3); // <- Sort 추가

        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourseMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseMaster.delAt.eq("N")); // 삭제여부

        // 수정중 김재일
        if (searchForm.getApplyAble() != null && searchForm.getApplyAble()) {
            builder.and(JPAExpressions.select(qCourseMasterRel.count())
                    .from(qCourseMasterRel)
                    .where(qCourseMasterRel.crsMstPid.eq(qCourseMaster.id)).eq(Constants.satisfSvySn.longValue()));
        }
        if (searchForm.getMberDvType() != null) {
            builder.and(qCourseMaster.mberDvTy.eq(searchForm.getMberDvType().name()));
        }
        if (searchForm.getUseAt() != null) {
            builder.and(qCourseMaster.openAt.eq(searchForm.getUseAt()));
        }



        QueryResults<CourseMaster> mngList = queryFactory
                .select(Projections.fields(CourseMaster.class,
                        qCourseMaster.id,
                        qCourseMaster.mberDvTy,
                        qCourseMaster.crsNm,
                        qCourseMaster.crsCn,
                        qCourseMaster.imgFl,
                        qCourseMaster.regPsId,
                        qCourseMaster.regDtm,
                        qCourseMaster.updPsId,
                        qCourseMaster.updDtm,
                        qCourseMaster.openAt,
                        qCourseRequest.id.as("atnlcReqPid"),
                        ///qCourseRequestComplete.count().as("completeCnt"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qCourseMaster.regPsId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseRequestComplete.count())
                                        .from(qCourseRequestComplete)
                                        .where(qCourseRequestComplete.cmplSttTy.eq(CompleteStatusType.COMPLETE.name()).and(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id)).and(qCourseRequestComplete.crsMstPid.eq(qCourseMaster.id))),
                                "completeCnt")

                ))
                .from(qCourseMaster)
                .leftJoin(qCourseRequest).on(qCourseMaster.id.eq(qCourseRequest.crsMstPid)
                        .and(qCourseRequest.mberPid.eq(searchForm.getUserPid())))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    public int cntCompleteSn4(Long reqId){
        return courseMasterRepository.cntCompleteSn4(reqId);
    }
    public Page<CourseMaster> listForMyPage(Pageable pageable, Long mberPid) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseRequestComplete qCourseRequestComplete = QCourseRequestComplete.courseRequestComplete;
        QCourseMasterRel qCourseMasterRel = QCourseMasterRel.courseMasterRel;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QCertificationHis qCertificationHis = QCertificationHis.certificationHis;
        QCertification qCertification = QCertification.certification;
        QSurvey qSurvey = QSurvey.survey;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qCourseMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qCourseRequest.mberPid.eq(mberPid));
        builder.and(qCourseMaster.delAt.eq("N"));
        builder.and(qCourseMaster.openAt.eq("Y"));

        QueryResults<CourseMaster> mngList = queryFactory
                .select(Projections.fields(CourseMaster.class,
                        qCourseMaster.id,
                        qCourseMaster.mberDvTy,
                        qCourseMaster.crsNm,
                        qCourseMaster.crsCn,
                        qCourseMaster.imgFl,
                        qCourseMaster.regPsId,
                        qCourseMaster.regDtm,
                        qCourseMaster.updPsId,
                        qCourseMaster.updDtm,
                        qCourseMaster.openAt,
                        qCourseRequest.regDtm.as("requestRegDtm"),
                        qCourseRequest.id.as("atnlcReqPid"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCourseRequestComplete.count())
                                        .from(qCourseRequestComplete)
                                        .where(qCourseRequestComplete.crsMstPid.eq(qCourseMaster.id)
                                                .and(qCourseRequestComplete.atnlcReqPid.eq(qCourseRequest.id))
                                                .and(qCourseRequestComplete.sn.ne(1))
                                                .and(qCourseRequestComplete.cmplSttTy.eq(CompleteStatusType.APPLY.name()))),
                                "ingCnt"), //수강상태 출력용
                        ExpressionUtils.as(
                                JPAExpressions.select(qCertificationHis.count())
                                        .from(qCertificationHis)
                                        .innerJoin(qAccount).on(qAccount.id.eq(mberPid))
                                        .innerJoin(qCertification).on(qCertificationHis.ctfhvPid.eq(qCertification.id).and(qCertification.mberPid.eq(qAccount.id)))
                                        .where(qCourseRequest.id.eq(qCertification.atnlcReqPid)),
                                "downloadCnt") //교육이수증 다운로드수

                ))
                .from(qCourseMaster)
                .innerJoin(qCourseRequest).on(qCourseRequest.crsMstPid.eq(qCourseMaster.id)
                                            .and(qCourseRequest.mberPid.eq(mberPid)))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public CourseMaster load(Long id) {
        CourseMaster seq = courseMasterRepository.findById(id).orElseGet(CourseMaster::new);

        return seq;
    }

    public CourseMaster loadByform(CourseMasterForm form, SearchForm searchForm) {

        QCourseMaster qCourseMaster = QCourseMaster.courseMaster;
        QCourseRequest qCourseRequest = QCourseRequest.courseRequest;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qCourseMaster.id.eq(form.getId()));

        List<CourseMaster> list = queryFactory
                .select(Projections.fields(CourseMaster.class,
                        qCourseMaster.id,
                        qCourseMaster.mberDvTy,
                        qCourseMaster.crsNm,
                        qCourseMaster.crsCn,
                        qCourseMaster.imgFl,
                        qCourseMaster.regPsId,
                        qCourseMaster.regDtm,
                        qCourseMaster.updPsId,
                        qCourseMaster.updDtm,
                        qCourseMaster.openAt,
                        qCourseRequest.id.as("atnlcReqPid")
                ))
                .from(qCourseMaster)
                .leftJoin(qCourseRequest).on(qCourseMaster.id.eq(qCourseRequest.crsMstPid)
                        .and(qCourseRequest.mberPid.eq(searchForm.getUserPid())))
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new CourseMaster());
    }

    @Transactional
    public void delete(CourseMasterForm form) {
        CourseMaster load = courseMasterRepository.findById(form.getId()).orElseGet(CourseMaster::new);
        load.setUpdDtm(form.getUpdDtm());
        load.setUpdPsId(form.getUpdPsId());
        load.setDelAt(form.getDelAt());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public CourseMaster insert(CourseMasterForm form, MultipartFile attachImgFl) throws Exception {

        if (attachImgFl.isEmpty() == false) {
            FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSEMASTERSEQ);
            if (fileInfo.getFlNm() != null) {
                form.setImgFl(fileInfo.getFlNm());
            }
        }

        CourseMaster seq = modelMapper.map(form, CourseMaster.class);
        CourseMaster save = courseMasterRepository.save(seq);

        return save;
    }

    @Transactional
    public boolean update(CourseMasterForm form, MultipartFile attachImgFl) {

        try {
            FileInfo fileInfo = new FileInfo();
            if (attachImgFl.isEmpty() == false) {
                fileInfo = FileUtilHelper.writeUploadedFile(attachImgFl, Constants.FOLDERNAME_COURSEMASTERSEQ);
            }

            CourseMaster masterSeq = courseMasterRepository.findById(form.getId()).orElseGet(CourseMaster::new);
            masterSeq.setMberDvTy(form.getMberDvTy());
            masterSeq.setCrsNm(form.getCrsNm());
            masterSeq.setCrsCn(form.getCrsCn());
            if (fileInfo.getFlNm() != null) {
                masterSeq.setImgFl(fileInfo.getFlNm());
            }
            masterSeq.setUpdDtm(form.getUpdDtm());
            masterSeq.setUpdPsId(form.getUpdPsId());
            masterSeq.setOpenAt(form.getOpenAt());


            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
