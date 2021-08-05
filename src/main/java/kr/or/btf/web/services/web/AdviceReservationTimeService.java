package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.AdviceReservationTime;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QAdviceReservationTime;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.repository.web.AdviceReservationTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdviceReservationTimeService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final AdviceReservationTimeRepository adviceReservationTimeRepository;

    public List<AdviceReservationTime> list(Long advcRsvPid) {

        QAdviceReservationTime qAdviceReservationTime = QAdviceReservationTime.adviceReservationTime;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        OrderSpecifier<Long> orderSpecifier = qAdviceReservationTime.id.asc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceReservationTime.advcRsvPid.eq(advcRsvPid));

        QueryResults<AdviceReservationTime> mngList = queryFactory
                .select(Projections.fields(AdviceReservationTime.class,
                        qAdviceReservationTime.id,
                        qAdviceReservationTime.hopeTimeCodeId,
                        qAdviceReservationTime.advcRsvPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceReservationTime.hopeTimeCodeId.eq(qCommonCode.id)),
                                "hopeTimeCodeNm")
                ))
                .from(qAdviceReservationTime)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchResults();

        return mngList.getResults();
    }

    /*public AdviceReservation load(Long id) {

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
        AdviceReservation mng = this.load(adviceReservationForm.getId());

        mng.setDelAt(adviceReservationForm.getDelAt());
    }

    *//**
     * @param adviceReservationForm
     * @return
     *//*
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
    }*/

}
