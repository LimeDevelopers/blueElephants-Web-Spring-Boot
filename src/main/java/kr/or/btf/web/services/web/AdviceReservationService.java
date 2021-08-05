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
import kr.or.btf.web.domain.web.enums.FileDvType;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.repository.web.AdviceReservationRepository;
import kr.or.btf.web.repository.web.AdviceReservationTimeRepository;
import kr.or.btf.web.repository.web.AdviceReservationTypeRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.AdviceReservationForm;
import kr.or.btf.web.web.form.AdviceReservationTimeForm;
import kr.or.btf.web.web.form.AdviceReservationTypeForm;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdviceReservationService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final AdviceReservationRepository adviceReservationRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;
    private final AdviceReservationTypeRepository adviceReservationTypeRepository;
    private final AdviceReservationTimeRepository adviceReservationTimeRepository;

    public Page<AdviceReservation> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QAdviceReservation qAdviceReservation = QAdviceReservation.adviceReservation;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        OrderSpecifier<Long> orderSpecifier = qAdviceReservation.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceReservation.delAt.eq("N"));

        if (searchForm.getProcessType() != null) {
            builder.and(qAdviceReservation.processTy.eq(searchForm.getProcessType()));
        }

        if (searchForm.getSrchWord() != null && searchForm.getSrchWord() != "") {
            builder.and(qAdviceReservation.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }

        if (searchForm.getReservationDvTy() != null) {
            builder.and(qAdviceReservation.dvTy.eq(searchForm.getReservationDvTy()));
        }
        if (searchForm.getUserPid() != null) {
            builder.and(qAdviceReservation.mberPid.eq(searchForm.getUserPid()));
        }

        QueryResults<AdviceReservation> mngList = queryFactory
                .select(Projections.fields(AdviceReservation.class,
                        qAdviceReservation.id,
                        qAdviceReservation.dvTy,
                        qAdviceReservation.mberDvTy,
                        qAdviceReservation.nm,
                        qAdviceReservation.areaCodePid,
                        qAdviceReservation.ttl,
                        qAdviceReservation.cnts,
                        qAdviceReservation.telno,
                        qAdviceReservation.hopeStYmd,
                        qAdviceReservation.hopeEndYmd,
                        qAdviceReservation.processTy,
                        qAdviceReservation.regPsId,
                        qAdviceReservation.regDtm,
                        qAdviceReservation.updPsId,
                        qAdviceReservation.updDtm,
                        qAdviceReservation.delAt,
                        qAdviceReservation.mberPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAdviceReservation.regPsId.eq(qAccount.loginId)),
                                "regPsNm")
                ))
                .from(qAdviceReservation)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public AdviceReservation load(Long id) {

        QAdviceReservation qAdviceReservation = QAdviceReservation.adviceReservation;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;
        QAdviceReservationTime qAdviceReservationTime = QAdviceReservationTime.adviceReservationTime;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceReservation.id.eq(id));
        builder.and(qAdviceReservation.delAt.eq("N"));

        AdviceReservation adviceReservation = queryFactory
                .select(Projections.fields(AdviceReservation.class,
                        qAdviceReservation.id,
                        qAdviceReservation.dvTy,
                        qAdviceReservation.mberDvTy,
                        qAdviceReservation.nm,
                        qAdviceReservation.areaCodePid,
                        qAdviceReservation.ttl,
                        qAdviceReservation.cnts,
                        qAdviceReservation.telno,
                        qAdviceReservation.hopeStYmd,
                        qAdviceReservation.hopeEndYmd,
                        qAdviceReservation.processTy,
                        qAdviceReservation.regPsId,
                        qAdviceReservation.regDtm,
                        qAdviceReservation.updPsId,
                        qAdviceReservation.updDtm,
                        qAdviceReservation.delAt,
                        qAdviceReservation.mberPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAdviceReservation.regPsId.eq(qAccount.loginId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceReservation.areaCodePid.eq(qCommonCode.id)),
                                "areaCodeNm")
                ))
                .from(qAdviceReservation)
                .where(builder)
                .fetchFirst();

        return adviceReservation;
    }

    @Transactional
    public void delete(AdviceReservationForm adviceReservationForm) {
        AdviceReservation mng = adviceReservationRepository.findById(adviceReservationForm.getId()).orElseGet(AdviceReservation::new);

        mng.setDelAt(adviceReservationForm.getDelAt());
    }

    /**
     * @param adviceReservationForm
     * @return
     */
    @Transactional
    public AdviceReservation insert(AdviceReservationForm adviceReservationForm, Long[] worryArr, Long[] hopeTimeCodeIdArr, MultipartFile attachedFile) {

        try {
            AdviceReservation adviceReservation = modelMapper.map(adviceReservationForm, AdviceReservation.class);
            adviceReservation = adviceReservationRepository.save(adviceReservation);

            for (Long codePid : worryArr) {
                AdviceReservationTypeForm adviceReservationTypeForm = new AdviceReservationTypeForm();
                adviceReservationTypeForm.setAdvcRsvPid(adviceReservation.getId());
                adviceReservationTypeForm.setCodePid(codePid);
                adviceReservationTypeRepository.saveByCodePidAndAdvcReqPid(adviceReservationTypeForm);
            }

            for (Long hopeTimeCodeId : hopeTimeCodeIdArr) {
                AdviceReservationTimeForm adviceReservationTimeForm = new AdviceReservationTimeForm();
                adviceReservationTimeForm.setHopeTimeCodeId(hopeTimeCodeId);
                adviceReservationTimeForm.setAdvcRsvPid(adviceReservation.getId());
                AdviceReservationTime adviceReservationTime = modelMapper.map(adviceReservationTimeForm, AdviceReservationTime.class);
                adviceReservationTimeRepository.save(adviceReservationTime);
            }

            if (attachedFile != null && !attachedFile.isEmpty()) {
                TableNmType tblAdviceReservation = TableNmType.TBL_ADVICE_REQUEST;
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_ADVICE);
                if (fileInfo != null) {
                    fileInfo.setDataPid(adviceReservation.getId());
                    fileInfo.setTableNm(tblAdviceReservation.name());
                    fileInfo.setDvTy(FileDvType.ATTACH.name());
                    fileInfoRepository.save(fileInfo);
                }
            }

            return adviceReservation;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public boolean update(AdviceReservationForm adviceReservationForm, Long[] worryArr, Long[] hopeTimeCodeIdArr) {

        try {
            AdviceReservation adviceReservation = adviceReservationRepository.findById(adviceReservationForm.getId()).orElseGet(AdviceReservation::new);

            adviceReservation.setAreaCodePid(adviceReservationForm.getAreaCodePid());
            adviceReservation.setMberDvTy(adviceReservationForm.getMberDvTy());
            adviceReservation.setTtl(adviceReservationForm.getTtl());
            adviceReservation.setCnts(adviceReservationForm.getCnts());
            adviceReservation.setTelno(adviceReservationForm.getTelno());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            adviceReservation.setHopeStYmd(format.parse(adviceReservationForm.getHopeStYmd()));
            adviceReservation.setHopeEndYmd(format.parse(adviceReservationForm.getHopeEndYmd()));
            adviceReservation.setUpdPsId(adviceReservationForm.getUpdPsId());
            adviceReservation.setUpdDtm(LocalDateTime.now());

            adviceReservationTypeRepository.deleteByAdvcRsvPid(adviceReservation.getId());

            for (Long codePid : worryArr) {
                AdviceReservationTypeForm adviceReservationTypeForm = new AdviceReservationTypeForm();
                adviceReservationTypeForm.setAdvcRsvPid(adviceReservation.getId());
                adviceReservationTypeForm.setCodePid(codePid);
                adviceReservationTypeRepository.saveByCodePidAndAdvcReqPid(adviceReservationTypeForm);
            }

            adviceReservationTimeRepository.deleteByAdvcRsvPid(adviceReservation.getId());

            for (Long hopeTimeCodeId : hopeTimeCodeIdArr) {
                AdviceReservationTimeForm adviceReservationTimeForm = new AdviceReservationTimeForm();
                adviceReservationTimeForm.setHopeTimeCodeId(hopeTimeCodeId);
                adviceReservationTimeForm.setAdvcRsvPid(adviceReservation.getId());
                AdviceReservationTime adviceReservationTime = modelMapper.map(adviceReservationTimeForm, AdviceReservationTime.class);
                adviceReservationTimeRepository.save(adviceReservationTime);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean updateProcessType(AdviceReservationForm adviceReservationForm) {

        try {
            AdviceReservation adviceReservation = adviceReservationRepository.findById(adviceReservationForm.getId()).orElseGet(AdviceReservation::new);

            adviceReservation.setProcessTy(adviceReservationForm.getProcessTy());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long count(AdviceReservationForm adviceReservationForm) {
        QAdviceReservation qAdviceReservation = QAdviceReservation.adviceReservation;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceReservation.delAt.eq("N"));
        builder.and(qAdviceReservation.mberPid.eq(adviceReservationForm.getMberPid()));

        Long count = queryFactory
                .select(Projections.fields(AdviceReservation.class,
                        qAdviceReservation.id
                ))
                .from(qAdviceReservation)
                .where(builder)
                .fetchCount();

        return count;
    }
}
