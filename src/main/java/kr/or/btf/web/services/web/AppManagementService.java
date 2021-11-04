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
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.InstructorDvTy;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.repository.web.ApplicationRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.repository.web.MemberRollRepository;
import kr.or.btf.web.repository.web.PreventionInstructorRepository;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class AppManagementService extends _BaseService {
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;
    private final PreventionInstructorRepository preventionInstructorRepository;
    private final MemberRollRepository memberRollRepository;

    // 강사 유형 update..
    public boolean updateInsDyTy(Long id, String gbn) {
        try {
            PreventionInstructor pre = preventionInstructorRepository.findById(id).orElseGet(PreventionInstructor::new);
            pre.setInsType(InstructorDvTy.valueOf(gbn));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 강사 승인 status update..
    public boolean updateInsApporaval(Long id, String gbn, Long uid) {
        updateMemberDvType(gbn,uid);
        try {
            PreventionInstructor pre = preventionInstructorRepository.findById(id).orElseGet(PreventionInstructor::new);
            if(gbn.equals("Y")) {
                // 기본 타입 EDU
                pre.setInsType(InstructorDvTy.EDU);
            } else {
                pre.setInsType(InstructorDvTy.NO);
            }
            pre.setApproval(gbn);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 권한 update..
    public void updateMemberDvType(String gbn, Long id) {
        try {
            Account account = memberRepository.findById(id).orElseGet(Account::new);
            MemberRoll roll = memberRollRepository.findByMberPid(id);
            if (gbn.equals("Y")) {
                account.setMberDvTy(UserRollType.INSTRUCTOR);
                roll.setMberDvTy(UserRollType.INSTRUCTOR);
            } else {
                account.setMberDvTy(UserRollType.NORMAL);
                roll.setMberDvTy(UserRollType.NORMAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 신청강사 상세정보 load..
    public PreventionMaster preeduDetail(Long id) {
        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;
        OrderSpecifier<LocalDateTime> orderSpecifier = qPreventionMaster.regDtm.desc();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionMaster.id.eq(id));
        PreventionMaster instructor = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id,
                        qPreventionMaster.mberPid,
                        qPreventionMaster.schlNm,
                        qPreventionMaster.address,
                        qPreventionMaster.regDtm,
                        qPreventionMaster.tel,
                        qPreventionMaster.nm,
                        qPreventionMaster.email,
                        qPreventionMaster.moblphon,
                        qPreventionMaster.task,
                        qPreventionMaster.approval,
                        qPreventionMaster.grade,
                        qPreventionMaster.classesNum,
                        qPreventionMaster.personnel,
                        qPreventionMaster.hpSchd1Personnel,
                        qPreventionMaster.hpSchd1Et,
                        qPreventionMaster.hpSchd1Wt,
                        qPreventionMaster.hpSchd2Personnel,
                        qPreventionMaster.hpSchd2Et,
                        qPreventionMaster.hpSchd2Wt,
                        qPreventionMaster.resultQna1,
                        qPreventionMaster.resultQna2,
                        qPreventionMaster.resultQna3,
                        qPreventionMaster.resultQna4,
                        qPreventionMaster.resultQna5,
                        qPreventionMaster.tempSave
                ))
                .from(qPreventionMaster)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return instructor;
    }

    public List<PreventionInstructor> getApplyList (Long id) {
        QPrevention qPrevention = QPrevention.prevention;
        QPreventionInstructor qPreventionInstructor = QPreventionInstructor.preventionInstructor;
        QAccount qAccount = QAccount.account;
        OrderSpecifier<LocalDateTime> orderSpecifier = qPrevention.regDtm.desc();
        BooleanBuilder builder = new BooleanBuilder();
        //builder.and(qPreventionInstructor.preMstPid.eq(id));

        QueryResults<PreventionInstructor> list = queryFactory
                .select(Projections.fields(PreventionInstructor.class,
                        qPreventionInstructor.regDtm,
                        qPreventionInstructor.id,
                        qPreventionInstructor.mberPid,
                        qPreventionInstructor.approval,
                        qPreventionInstructor.awards,
                        qPreventionInstructor.eduMatters,
                        qPreventionInstructor.qualifications,
                        qPreventionInstructor.enrollPeriod,
                        qPreventionInstructor.expPeriod,
                        qPreventionInstructor.expContent,
                        qPreventionInstructor.expNm,
                        qPreventionInstructor.expPosition,
                        qPreventionInstructor.grdStatus,
                        qPreventionInstructor.major,
                        qPreventionInstructor.schlNm,
                        qPreventionInstructor.selfDrivingAt,
                        qPreventionInstructor.snsStatus,
                        qPreventionInstructor.snsUrl,
                        qPreventionInstructor.thumbImg,
                        qPreventionInstructor.updDtm,
                        qPreventionInstructor.InsType
                ))
                .from(qPreventionInstructor)
                .leftJoin(qPrevention).on(qPreventionInstructor.mberPid.eq(
                                JPAExpressions.select(qPrevention.mberPid)
                                        .from(qPrevention)
                                        .where(qPrevention.preMstPid.eq(id))))
                .orderBy(orderSpecifier)
                .fetchResults();
        return list.getResults();
    }

    // 신청강사 상세정보 load..
    public PreventionInstructor inseduDetail(Long id) {
        QPreventionInstructor qPreventionInstructor = QPreventionInstructor.preventionInstructor;
        QAccount qAccount = QAccount.account;
        OrderSpecifier<LocalDateTime> orderSpecifier = qPreventionInstructor.regDtm.desc();
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionInstructor.id.eq(id));
        PreventionInstructor instructor = queryFactory
                .select(Projections.fields(PreventionInstructor.class,
                        qAccount.loginId,
                        qAccount.nm,
                        qAccount.sexPrTy,
                        qAccount.mberDvTy,
                        qAccount.moblphon,
                        qAccount.email,
                        qAccount.brthdy,
                        qPreventionInstructor.regDtm,
                        qPreventionInstructor.id,
                        qPreventionInstructor.mberPid,
                        qPreventionInstructor.approval,
                        qPreventionInstructor.awards,
                        qPreventionInstructor.eduMatters,
                        qPreventionInstructor.qualifications,
                        qPreventionInstructor.enrollPeriod,
                        qPreventionInstructor.expPeriod,
                        qPreventionInstructor.expContent,
                        qPreventionInstructor.expNm,
                        qPreventionInstructor.expPosition,
                        qPreventionInstructor.grdStatus,
                        qPreventionInstructor.major,
                        qPreventionInstructor.schlNm,
                        qPreventionInstructor.selfDrivingAt,
                        qPreventionInstructor.snsStatus,
                        qPreventionInstructor.snsUrl,
                        qPreventionInstructor.thumbImg,
                        qPreventionInstructor.updDtm,
                        qPreventionInstructor.InsType
                ))
                .from(qPreventionInstructor)
                .leftJoin(qAccount).on(qPreventionInstructor.mberPid.eq(qAccount.id))
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return instructor;
    }

    public Page<PreventionInstructor> getPreInsList(Pageable pageable, SearchForm searchForm) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPreventionInstructor qPreventionInstructor = QPreventionInstructor.preventionInstructor;
        QAccount qAccount = QAccount.account;
        OrderSpecifier<Long> orderSpecifier = qPreventionInstructor.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionInstructor.tempSave.eq("N"));

        QueryResults<PreventionInstructor> mngList = queryFactory
                .select(Projections.fields(PreventionInstructor.class,
                        qPreventionInstructor.id,
                        qPreventionInstructor.mberPid,
                        qPreventionInstructor.approval,
                        qPreventionInstructor.regDtm,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.id.eq(qPreventionInstructor.mberPid)),
                                "nm")
                ))
                .from(qPreventionInstructor)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    public Page<PreventionMaster> getPreMasterList(Pageable pageable, SearchForm searchForm) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;

        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionMaster.delAt.eq("N"));

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
                .where(qPreventionMaster.approval.eq("N"))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<ActivityApplication> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QActivityApplication qActivityApplication = QActivityApplication.activityApplication;

        OrderSpecifier<Long> orderSpecifier = qActivityApplication.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActivityApplication.delAt.eq("N"));
        builder.and(qActivityApplication.appDvTy.eq(AppRollType.PARTNERS));

        QueryResults<ActivityApplication> mngList = queryFactory
                .select(Projections.fields(ActivityApplication.class,
                        qActivityApplication.id, qActivityApplication.mberPid,
                        qActivityApplication.flPid, qActivityApplication.nm,
                        qActivityApplication.affi, qActivityApplication.appDvTy,
                        qActivityApplication.regDtm, qActivityApplication.moblphon ,
                        qActivityApplication.approval
                ))
                .from(qActivityApplication)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    public Page<ActivityApplication> zzcrewlist(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QActivityApplication qActivityApplication = QActivityApplication.activityApplication;

        OrderSpecifier<Long> orderSpecifier = qActivityApplication.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActivityApplication.delAt.eq("N"));
        builder.and(qActivityApplication.appDvTy.eq(AppRollType.CREW));

        QueryResults<ActivityApplication> mngList = queryFactory
                .select(Projections.fields(ActivityApplication.class,
                        qActivityApplication.id, qActivityApplication.mberPid,
                        qActivityApplication.flPid, qActivityApplication.nm,
                        qActivityApplication.affi, qActivityApplication.appDvTy,
                        qActivityApplication.regDtm, qActivityApplication.moblphon ,
                        qActivityApplication.approval, qActivityApplication.crewNm
                ))
                .from(qActivityApplication)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public Page<ActivityApplication> zzdeclarationlist(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QActivityApplication qActivityApplication = QActivityApplication.activityApplication;

        OrderSpecifier<Long> orderSpecifier = qActivityApplication.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActivityApplication.delAt.eq("N"));
        builder.and(qActivityApplication.appDvTy.eq(AppRollType.DECLARE));

        QueryResults<ActivityApplication> mngList = queryFactory
                .select(Projections.fields(ActivityApplication.class,
                        qActivityApplication.id, qActivityApplication.mberPid,
                        qActivityApplication.flPid, qActivityApplication.nm,
                        qActivityApplication.affi, qActivityApplication.appDvTy,
                        qActivityApplication.regDtm, qActivityApplication.moblphon ,
                        qActivityApplication.approval
                        /**
                         * qActivityApplication.reason, qActivityApplication.schedule ,
                         * qActivityApplication.root, qActivityApplication.schlNm ,
                         * qActivityApplication.representPhone , qActivityApplication.schlAdress ,
                         * qActivityApplication.studentNum , qActivityApplication.principalNm ,
                         * qActivityApplication.principalPhone , qActivityApplication.principalEmail ,
                         * qActivityApplication.size**/
                ))
                .from(qActivityApplication)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
    public Page<ActivityApplication> event(Pageable pageable , SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() -1);
        pageable = PageRequest.of(page , Constants.DEFAULT_PAGESIZE);

        QActivityApplication qActivityApplication = QActivityApplication.activityApplication;

        OrderSpecifier<Long> orderSpecifier = qActivityApplication.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActivityApplication.delAt.eq("N"));
        builder.and(qActivityApplication.appDvTy.eq(AppRollType.EVENT));

        QueryResults<ActivityApplication> mngList = queryFactory
                .select(Projections.fields(ActivityApplication.class ,
                        qActivityApplication.id ,qActivityApplication.event_pid, qActivityApplication.mberPid,
                        qActivityApplication.nm ,qActivityApplication.moblphon , qActivityApplication.affi ,
                        qActivityApplication.regDtm , qActivityApplication.approval , qActivityApplication.flPid))
                .from(qActivityApplication)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();


        return  new PageImpl<>(mngList.getResults() , pageable, mngList.getTotal());
    }
    public Page<ActivityApplication> contest(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QActivityApplication qActivityApplication = QActivityApplication.activityApplication;

        OrderSpecifier<Long> orderSpecifier = qActivityApplication.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActivityApplication.delAt.eq("N"));
        builder.and(qActivityApplication.appDvTy.eq(AppRollType.CONTEST));

        QueryResults<ActivityApplication> mngList = queryFactory
                .select(Projections.fields(ActivityApplication.class,
                        qActivityApplication.id, qActivityApplication.mberPid,
                        qActivityApplication.flPid, qActivityApplication.nm,
                        qActivityApplication.affi, qActivityApplication.appDvTy,
                        qActivityApplication.regDtm, qActivityApplication.moblphon ,
                        qActivityApplication.approval, qActivityApplication.crewNm
                ))
                .from(qActivityApplication)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();
        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }
}
