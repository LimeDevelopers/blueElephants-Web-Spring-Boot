package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.PeerResponse;
import kr.or.btf.web.domain.web.PeerResponsePerson;
import kr.or.btf.web.domain.web.QAccount;
import kr.or.btf.web.domain.web.QPeerResponsePerson;
import kr.or.btf.web.repository.web.CertificationRepository;
import kr.or.btf.web.repository.web.CourseRequestCompleteRepository;
import kr.or.btf.web.repository.web.PeerResponsePersonRepository;
import kr.or.btf.web.repository.web.PeerResponseRepository;
import kr.or.btf.web.web.form.PeerResponseForm;
import kr.or.btf.web.web.form.PeerResponsePersonForm;
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

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PeerResponsePersonService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PeerResponsePersonRepository peerResponsePersonRepository;
    private final ModelMapper modelMapper;
    private final PeerResponseRepository peerResponseRepository;
    private final CourseRequestCompleteRepository courseRequestCompleteRepository;
    private final CertificationRepository certificationRepository;

    public Page<PeerResponsePerson> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeerResponsePerson.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<PeerResponsePerson> mngList = queryFactory
                .select(Projections.fields(PeerResponsePerson.class,
                        qPeerResponsePerson.id,
                        qPeerResponsePerson.areaNm,
                        qPeerResponsePerson.schlNm,
                        qPeerResponsePerson.grade,
                        qPeerResponsePerson.ban,
                        qPeerResponsePerson.no,
                        qPeerResponsePerson.teacherNm,
                        qPeerResponsePerson.regPsId,
                        qPeerResponsePerson.regDtm,
                        qPeerResponsePerson.updPsId,
                        qPeerResponsePerson.updDtm,
                        qPeerResponsePerson.delAt,
                        qPeerResponsePerson.peerPid,
                        qPeerResponsePerson.mberPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qPeerResponsePerson.regPsId)),
                                "regPsNm")
                ))
                .from(qPeerResponsePerson)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    /*public PeerResponsePerson load(Long id) {
        PeerResponsePerson peerResponsePerson = peerResponsePersonRepository.findById(id);

        return peerResponsePerson;
    }*/

    public PeerResponsePerson loadByForm(PeerResponsePersonForm form) {

        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qPeerResponsePerson.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        if (form.getAreaNm() != null) {
            builder.and(qPeerResponsePerson.areaNm.eq(form.getAreaNm()));
        }
        if (form.getSchlNm() != null) {
            builder.and(qPeerResponsePerson.schlNm.eq(form.getSchlNm()));
        }
        if (form.getGrade() != null) {
            builder.and(qPeerResponsePerson.grade.eq(form.getGrade()));
        }
        if (form.getBan() != null) {
            builder.and(qPeerResponsePerson.ban.eq(form.getBan()));
        }
        if (form.getNo() != null) {
            builder.and(qPeerResponsePerson.no.eq(form.getNo()));
        }
        if (form.getTeacherNm() != null) {
            builder.and(qPeerResponsePerson.teacherNm.eq(form.getTeacherNm()));
        }
        if (form.getMberPid() != null) {
            builder.and(qPeerResponsePerson.mberPid.eq(form.getMberPid()));
        }
        if (form.getPeerPid() != null) {
            builder.and(qPeerResponsePerson.peerPid.eq(form.getPeerPid()));
        }

        PeerResponsePerson mng = queryFactory
                .select(Projections.fields(PeerResponsePerson.class,
                        qPeerResponsePerson.id,
                        qPeerResponsePerson.areaNm,
                        qPeerResponsePerson.schlNm,
                        qPeerResponsePerson.grade,
                        qPeerResponsePerson.ban,
                        qPeerResponsePerson.no,
                        qPeerResponsePerson.teacherNm,
                        qPeerResponsePerson.regPsId,
                        qPeerResponsePerson.regDtm,
                        qPeerResponsePerson.updPsId,
                        qPeerResponsePerson.updDtm,
                        qPeerResponsePerson.delAt,
                        qPeerResponsePerson.peerPid
                ))
                .from(qPeerResponsePerson)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetchFirst();
        return mng;
    }

    /*public PeerResponsePerson loadByform(PeerResponsePersonForm form) {

        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qPeerResponsePerson.delAt.eq("N"));
        if (form.getLoginId() != null) {
            builder.and(qPeerResponsePerson.loginId.eq(form.getLoginId()));
        }
        if (form.getQustnrPid() != null) {
            builder.and(qPeerResponsePerson.qustnrPid.eq(form.getQustnrPid()));
        }
        if (form.getAtnlcReqPid() != null) {
            builder.and(qPeerResponsePerson.atnlcReqPid.eq(form.getAtnlcReqPid()));
        }

        List<PeerResponsePerson> list = queryFactory
                .select(Projections.fields(PeerResponsePerson.class,
                        qPeerResponsePerson.id,
                        qPeerResponsePerson.areaNm,
                        qPeerResponsePerson.schlNm,
                        qPeerResponsePerson.grade,
                        qPeerResponsePerson.ban,
                        qPeerResponsePerson.no,
                        qPeerResponsePerson.teacherNm,
                        qPeerResponsePerson.regPsId,
                        qPeerResponsePerson.regDtm,
                        qPeerResponsePerson.updPsId,
                        qPeerResponsePerson.updDtm,
                        qPeerResponsePerson.delAt,
                        qPeerResponsePerson.peerPid,
                        qPeerResponsePerson.mberPid
                ))
                .from(qPeerResponsePerson)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new PeerResponsePerson());
    }*/

    /*@Transactional
    public void delete(PeerResponsePersonForm peerResponsePersonForm) {
        PeerResponsePerson mng = this.load(peerResponsePersonForm.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(peerResponsePersonForm.getUpdPsId());
        mng.setDelAt(peerResponsePersonForm.getDelAt());
    }*/

    /**
     * @param peerResponsePersonForm
     * @return
     */
    /*@Transactional
    public boolean insert(PeerResponsePersonForm peerResponsePersonForm, List<PeerResponse> responsesList) {

        try {
            PeerResponsePerson peerResponsePerson = modelMapper.map(peerResponsePersonForm, PeerResponsePerson.class);
            PeerResponsePerson save = peerResponsePersonRepository.save(peerResponsePerson);

            if (responsesList != null) {
                for (PeerResponse peerResponse : responsesList) {
                    peerResponse.setRspPsPid(save.getId());
                    peerResponseRepository.save(peerResponse);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    /**
     * @param peerResponsePersonForm
     * @return
     */
    @Transactional
    public boolean proc(PeerResponsePersonForm peerResponsePersonForm, List<PeerResponseForm> responsesFormList) {

        try {
            PeerResponsePerson peerResponsePerson = this.loadByForm(peerResponsePersonForm);
            if (peerResponsePerson == null) {
                PeerResponsePerson person = modelMapper.map(peerResponsePersonForm, PeerResponsePerson.class);
                peerResponsePerson = peerResponsePersonRepository.save(person);
                peerResponsePerson = this.loadByForm(peerResponsePersonForm);
            }

            if (responsesFormList != null || responsesFormList.isEmpty()) {
                for (PeerResponseForm responseForm : responsesFormList) {
                    responseForm.setRspPsPid(peerResponsePerson.getId());
                    PeerResponse peerResponse = peerResponseRepository.findByQesitmPidAndRspPsPidAndAndAreaNmAndSchlNmAndGradeAndBanAndNoAndTeacherNmAndTgtMberPid(responseForm.getQesitmPid(), responseForm.getRspPsPid(), responseForm.getAreaNm(), responseForm.getSchlNm(), responseForm.getGrade(), responseForm.getBan(), responseForm.getNo(), responseForm.getTeacherNm(), responseForm.getTgtMberPid());
                    if (peerResponse == null) {
                        PeerResponse response = modelMapper.map(responseForm, PeerResponse.class);
                        peerResponseRepository.save(response);
                    } else {
                        peerResponse.setAswPid(responseForm.getAswPid());
                    }

                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

/*    @Transactional
    public boolean update(PeerResponsePersonForm peerResponsePersonForm) {

        try {

            PeerResponsePerson peerResponsePerson = peerResponsePersonRepository.findById(peerResponsePersonForm.getId()).orElseGet(PeerResponsePerson::new);
            peerResponsePerson.setLoginId(peerResponsePersonForm.getLoginId());
            peerResponsePerson.setNm(peerResponsePersonForm.getNm());
            peerResponsePerson.setMberDvty(peerResponsePersonForm.getMberDvty());
            peerResponsePerson.setQustnrPid(peerResponsePersonForm.getQustnrPid());
            peerResponsePerson.setUpdPsId(peerResponsePersonForm.getUpdPsId());
            peerResponsePerson.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }*/

    /*public Long count(PeerResponsePersonForm peerResponsePersonForm) {
        QPeer qPeer = QPeer.peer;
        QPeerResponsePerson qPeerResponsePerson = QPeerResponsePerson.peerResponsePerson;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPeerResponsePerson.delAt.eq("N"));
        builder.and(qPeerResponsePerson.loginId.eq(peerResponsePersonForm.getLoginId()));

        Long count = queryFactory
                .select(Projections.fields(PeerResponsePerson.class,
                        qPeerResponsePerson.id
                ))
                .from(qPeerResponsePerson)
                .innerJoin(qPeer).on(qPeer.id.eq(qPeerResponsePerson.qustnrPid)
                                    .and(qPeer.delAt.eq("N"))
                                    .and(qPeer.dvTy.in(peerResponsePersonForm.getPeerDvTypes())))
                .where(builder)
                .fetchCount();

        return count;
    }*/
}
