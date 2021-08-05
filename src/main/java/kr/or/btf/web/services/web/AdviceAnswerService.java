package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.AdviceAnswer;
import kr.or.btf.web.domain.web.AdviceRequest;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QAdviceAnswer;
import kr.or.btf.web.domain.web.enums.ProcessType;
import kr.or.btf.web.repository.web.AdviceAnswerRepository;
import kr.or.btf.web.repository.web.AdviceRequestRepository;
import kr.or.btf.web.web.form.AdviceAnswerForm;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdviceAnswerService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final AdviceAnswerRepository adviceAnswerRepository;
    private final AdviceRequestRepository adviceRequestRepository;
    private final ModelMapper modelMapper;

    public Page<AdviceAnswer> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QAdviceAnswer qAdviceAnswer = QAdviceAnswer.adviceAnswer;

        OrderSpecifier<Long> orderSpecifier = qAdviceAnswer.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceAnswer.delAt.eq("N"));

        /*if (searchForm.getSrchStDt() != null && searchForm.getSrchEdDt() != null) {
            if (!searchForm.getSrchStDt().isEmpty() && !searchForm.getSrchEdDt().isEmpty()) {
                builder
                        .and(qAdviceAnswer.regDtm.goe(LocalDateTime.parse(searchForm.getSrchStDt() + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd H:m")))
                                .and(qAdviceAnswer.regDtm.loe(LocalDateTime.parse(searchForm.getSrchEdDt() + " 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd H:m")))
                                )
                        );
            }
        }*/

        QueryResults<AdviceAnswer> mngList = queryFactory
                .select(Projections.fields(AdviceAnswer.class,
                        qAdviceAnswer.id,
                        qAdviceAnswer.ttl,
                        qAdviceAnswer.cn,
                        qAdviceAnswer.processType,
                        qAdviceAnswer.advcReqPid,
                        qAdviceAnswer.mberPid
                ))
                .from(qAdviceAnswer)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public AdviceAnswer load(Long id) {
        AdviceAnswer adviceRequest = adviceAnswerRepository.findById(id).orElseGet(AdviceAnswer::new);

        return adviceRequest;
    }

    public AdviceAnswer loadByAdvcReqPid(Long id) {
        QAdviceAnswer qAdviceAnswer = QAdviceAnswer.adviceAnswer;
        QAccount qAccount = QAccount.account;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceAnswer.advcReqPid.eq(id));
        builder.and(qAdviceAnswer.delAt.eq("N"));
        AdviceAnswer adviceAnswer = queryFactory
                .select(Projections.fields(AdviceAnswer.class,
                        qAdviceAnswer.id,
                        qAdviceAnswer.ttl,
                        qAdviceAnswer.cn,
                        qAdviceAnswer.memo,
                        qAdviceAnswer.processType,
                        qAdviceAnswer.regPsId,
                        qAdviceAnswer.regDtm,
                        qAdviceAnswer.updPsId,
                        qAdviceAnswer.updDtm,
                        qAdviceAnswer.delAt,
                        qAdviceAnswer.mberPid,
                        qAdviceAnswer.advcReqPid,
                        qAccount.nm.as("nm"),
                        qAccount.loginId
                ))
                .from(qAdviceAnswer)
                .leftJoin(qAccount).on(qAdviceAnswer.mberPid.eq(qAccount.id))
                .where(builder)
                .fetchOne();
        return adviceAnswer;
    }

    @Transactional
    public boolean delete(AdviceAnswerForm adviceAnswerForm) {
        try {
            AdviceAnswer mng = this.load(adviceAnswerForm.getId());

            mng.setUpdDtm(LocalDateTime.now());
            mng.setUpdPsId(adviceAnswerForm.getUpdPsId());
            mng.setDelAt("Y");

            AdviceRequest adviceRequest = adviceRequestRepository.findById(mng.getAdvcReqPid()).orElseGet(AdviceRequest::new);
            adviceRequest.setProcessTy(ProcessType.REQUEST);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param adviceAnswerForm
     * @return
     */
    @Transactional
    public AdviceAnswer insert(AdviceAnswerForm adviceAnswerForm) {

        try {
            AdviceAnswer adviceAnswer = modelMapper.map(adviceAnswerForm, AdviceAnswer.class);
            adviceAnswer.setDelAt("N");
            if (adviceAnswerForm.getRegDtm() != null) {
                LocalDate aa = LocalDate.parse(adviceAnswerForm.getRegDtm());
                LocalDateTime bb = LocalDateTime.of(aa, LocalDateTime.now().toLocalTime());
                adviceAnswer.setRegDtm(bb);
            }
            adviceAnswer.setUpdDtm(LocalDateTime.now());
            adviceAnswerRepository.save(adviceAnswer);

            AdviceRequest adviceRequest = adviceRequestRepository.findById(adviceAnswer.getAdvcReqPid()).orElseGet(AdviceRequest::new);
            adviceRequest.setProcessTy(ProcessType.COMPLETE);

            return adviceAnswer;
        } catch (Exception e){
            return null;
        }
    }

    @Transactional
    public AdviceAnswer update(AdviceAnswerForm adviceAnswerForm) {
        try {
            AdviceAnswer adviceAnswer = adviceAnswerRepository.findById(adviceAnswerForm.getId()).orElseGet(AdviceAnswer::new);

            //adviceRequest.setTtl(adviceAnswerForm.getTtl());
            adviceAnswer.setCn(adviceAnswerForm.getCn());
            adviceAnswer.setMemo(adviceAnswerForm.getMemo());

            adviceAnswer.setUpdPsId(adviceAnswerForm.getUpdPsId());
            adviceAnswer.setUpdDtm(LocalDateTime.now());

            if (adviceAnswerForm.getRegDtm() != null) {
                LocalDate aa = LocalDate.parse(adviceAnswerForm.getRegDtm());
                LocalDateTime bb = LocalDateTime.of(aa, LocalDateTime.now().toLocalTime());
                adviceAnswer.setRegDtm(bb);
            }

            return adviceAnswer;
        } catch (Exception e) {
            return null;
        }
    }
}
