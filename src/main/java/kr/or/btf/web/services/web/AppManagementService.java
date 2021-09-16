package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.domain.web.enums.AppRollType;
import kr.or.btf.web.domain.web.enums.UserRollType;
import kr.or.btf.web.repository.web.ApplicationRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class AppManagementService extends _BaseService {
    private final JPAQueryFactory queryFactory;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;

    public Page<PreventionMaster> getPreMasterList(Pageable pageable, SearchForm searchForm) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPreventionMaster qPreventionMaster = QPreventionMaster.preventionMaster;

        OrderSpecifier<Long> orderSpecifier = qPreventionMaster.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPreventionMaster.delAt.eq("N"));

        QueryResults<PreventionMaster> mngList = queryFactory
                .select(Projections.fields(PreventionMaster.class,
                        qPreventionMaster.id, qPreventionMaster.mberPid
                ))
                .from(qPreventionMaster)
                .where(builder)
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
