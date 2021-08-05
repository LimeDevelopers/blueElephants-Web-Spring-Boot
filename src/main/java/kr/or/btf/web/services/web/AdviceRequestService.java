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
import kr.or.btf.web.repository.web.AdviceRequestRepository;
import kr.or.btf.web.repository.web.AdviceRequestTypeRepository;
import kr.or.btf.web.repository.web.FileInfoRepository;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.web.form.AdviceRequestForm;
import kr.or.btf.web.web.form.AdviceRequestTypeForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdviceRequestService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final AdviceRequestRepository adviceRequestRepository;
    private final AdviceRequestTypeRepository adviceRequestTypeRepository;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<AdviceRequest> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QAdviceRequest qAdviceRequest = QAdviceRequest.adviceRequest;
        QAccount qAccount = QAccount.account;
        QAdviceAnswer qAdviceAnswer = QAdviceAnswer.adviceAnswer;

        OrderSpecifier<Long> orderSpecifier = qAdviceRequest.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceRequest.delAt.eq("N"));

        if (searchForm.getProcessType() != null) {
            builder.and(qAdviceRequest.processTy.eq(searchForm.getProcessType()));
        }
        if (searchForm.getSrchField() != null && searchForm.getSrchField() != "") {
            if (searchForm.getSrchField().equals("ttl")) {
                builder.and(qAdviceRequest.ttl.like("%" + searchForm.getSrchWord() + "%"));
            }
            if (searchForm.getSrchField().equals("writer")) {
                builder.and(qAccount.nm.like("%" + searchForm.getSrchWord() + "%"));
            }
        }
        if (searchForm.getSrchWord() != null && searchForm.getSrchWord() != "") {
            builder.and(qAdviceRequest.ttl.like("%" + searchForm.getSrchWord() + "%"));
        }
        if (searchForm.getUserPid() != null) {
            builder.and(qAdviceRequest.mberPid.eq(searchForm.getUserPid()));
        }

        QueryResults<AdviceRequest> mngList = queryFactory
                .select(Projections.fields(AdviceRequest.class,
                        qAdviceRequest.id,
                        qAdviceRequest.ttl,
                        qAdviceRequest.cn,
                        qAdviceRequest.incdntStYmd,
                        qAdviceRequest.incdntEndYmd,
                        qAdviceRequest.processTy,
                        qAdviceRequest.pwd,
                        qAdviceRequest.regPsId,
                        qAdviceRequest.regDtm,
                        qAdviceRequest.updPsId,
                        qAdviceRequest.updDtm,
                        qAdviceRequest.delAt,
                        qAccount.nm.as("regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qAdviceAnswer.regDtm)
                                        .from(qAdviceAnswer)
                                        .where(qAdviceRequest.id.eq(qAdviceAnswer.advcReqPid)
                                        .and(qAdviceAnswer.delAt.eq("N"))),
                                "answerDtm")
                ))
                .from(qAdviceRequest)
                .leftJoin(qAccount).on(qAdviceRequest.regPsId.eq(qAccount.loginId))
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public AdviceRequest load(Long id) {
        /*AdviceRequest adviceRequest = adviceRequestRepository.findById(id).orElseGet(AdviceRequest::new);

        return adviceRequest;*/

        QAdviceRequest qAdviceRequest = QAdviceRequest.adviceRequest;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceRequest.id.eq(id));

        AdviceRequest adviceRequest = queryFactory
                .select(Projections.fields(AdviceRequest.class,
                        qAdviceRequest.id,
                        qAdviceRequest.ttl,
                        qAdviceRequest.cn,
                        qAdviceRequest.mberDvTy,
                        qAdviceRequest.incdntStYmd,
                        qAdviceRequest.incdntEndYmd,
                        qAdviceRequest.processTy,
                        qAdviceRequest.bdyDmgeCodePid,
                        qAdviceRequest.mindDmgeCodePid,
                        qAdviceRequest.physiclDmgeCodePid,
                        qAdviceRequest.pwd,
                        qAdviceRequest.regPsId,
                        qAdviceRequest.regDtm,
                        qAdviceRequest.updPsId,
                        qAdviceRequest.updDtm,
                        qAdviceRequest.delAt,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAdviceRequest.regPsId.eq(qAccount.loginId)),
                                "regPsNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceRequest.bdyDmgeCodePid.eq(qCommonCode.id)),
                                "bdyDmgeCodeNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceRequest.mindDmgeCodePid.eq(qCommonCode.id)),
                                "mindDmgeCodeNm"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceRequest.physiclDmgeCodePid.eq(qCommonCode.id)),
                                "physiclDmgeCodeNm")
                        ))
                .from(qAdviceRequest)
                .where(builder)
                .fetchFirst();

        return adviceRequest;
    }

    @Transactional
    public void delete(AdviceRequestForm adviceRequestForm) {
        AdviceRequest mng = adviceRequestRepository.findById(adviceRequestForm.getId()).orElseGet(AdviceRequest::new);

        mng.setDelAt(adviceRequestForm.getDelAt());
    }

    /**
     * @param adviceRequestForm
     * @return
     */
    @Transactional
    public boolean insert(AdviceRequestForm adviceRequestForm, Long[] worryArr, MultipartFile attachedFile) {

        try {
            adviceRequestForm.setPwd(passwordEncoder.encode(adviceRequestForm.getPwd()));
            AdviceRequest adviceRequest = modelMapper.map(adviceRequestForm, AdviceRequest.class);
            adviceRequest = adviceRequestRepository.save(adviceRequest);

            for (Long codePid : worryArr) {
                AdviceRequestTypeForm adviceRequestTypeForm = new AdviceRequestTypeForm();
                adviceRequestTypeForm.setAdvcReqPid(adviceRequest.getId());
                adviceRequestTypeForm.setCodePid(codePid);
                adviceRequestTypeRepository.saveByCodePidAndAdvcReqPid(adviceRequestTypeForm);
            }

            if (attachedFile != null && !attachedFile.isEmpty()) {
                TableNmType tblAdviceRequest = TableNmType.TBL_ADVICE_REQUEST;
                FileInfo fileInfo = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_ADVICE);
                if (fileInfo != null) {
                    fileInfo.setDataPid(adviceRequest.getId());
                    fileInfo.setTableNm(tblAdviceRequest.name());
                    fileInfo.setDvTy(FileDvType.ATTACH.name());
                    fileInfoRepository.save(fileInfo);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(AdviceRequestForm adviceRequestForm, Long[] worryArr, MultipartFile attachedFile) {

        try {
            AdviceRequest adviceRequest = adviceRequestRepository.findById(adviceRequestForm.getId()).orElseGet(AdviceRequest::new);

            adviceRequest.setTtl(adviceRequestForm.getTtl());
            adviceRequest.setCn(adviceRequestForm.getCn());
            adviceRequest.setMberDvTy(adviceRequestForm.getMberDvTy());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            adviceRequest.setIncdntStYmd(format.parse(adviceRequestForm.getIncdntStYmd()));
            adviceRequest.setIncdntEndYmd(format.parse(adviceRequestForm.getIncdntEndYmd()));
            adviceRequest.setBdyDmgeCodePid(adviceRequestForm.getBdyDmgeCodePid());
            adviceRequest.setMindDmgeCodePid(adviceRequestForm.getMindDmgeCodePid());
            adviceRequest.setPhysiclDmgeCodePid(adviceRequestForm.getPhysiclDmgeCodePid());
            adviceRequest.setPwd(passwordEncoder.encode(adviceRequestForm.getPwd()));

            adviceRequestTypeRepository.deleteByAdvcReqPid(adviceRequest.getId());

            for (Long codePid : worryArr) {
                AdviceRequestTypeForm adviceRequestTypeForm = new AdviceRequestTypeForm();
                adviceRequestTypeForm.setAdvcReqPid(adviceRequest.getId());
                adviceRequestTypeForm.setCodePid(codePid);
                adviceRequestTypeRepository.saveByCodePidAndAdvcReqPid(adviceRequestTypeForm);
            }

            if (attachedFile.isEmpty() == false) {
                TableNmType tblAdviceRequest = TableNmType.TBL_ADVICE_REQUEST;
                FileInfo file = FileUtilHelper.writeUploadedFile(attachedFile, Constants.FOLDERNAME_ADVICE);

                if (file != null) {
                    file.setDataPid(adviceRequest.getId());
                    file.setTableNm(tblAdviceRequest.name());
                    file.setDvTy(FileDvType.ATTACH.name());

                    fileInfoRepository.deleteByData(file.getDataPid(), file.getTableNm(), file.getDvTy());
                    fileInfoRepository.save(file);
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public AdviceRequest checkPwd(AdviceRequestForm adviceRequestForm) {
        AdviceRequest adviceRequest = adviceRequestRepository.findByIdAndPwd(adviceRequestForm.getId(), adviceRequestForm.getPwd());
        return  adviceRequest;
        /*try {
            AdviceRequest adviceRequest = adviceRequestRepository.findByIdAndPwd(adviceRequestForm.getId(), adviceRequestForm.getPwd());
            if(adviceRequest == null) {
                return true;
            }else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }*/
    }

    public Long count(AdviceRequestForm adviceRequestForm) {
        QAdviceRequest adviceRequest = QAdviceRequest.adviceRequest;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(adviceRequest.delAt.eq("N"));
        builder.and(adviceRequest.mberPid.eq(adviceRequestForm.getMberPid()));

        Long count = queryFactory
                .select(Projections.fields(AdviceRequest.class,
                        adviceRequest.id
                ))
                .from(adviceRequest)
                .where(builder)
                .fetchCount();

        return count;
    }
}
