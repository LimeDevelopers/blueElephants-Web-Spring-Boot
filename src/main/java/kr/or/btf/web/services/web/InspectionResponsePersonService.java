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
import kr.or.btf.web.repository.web.CourseRequestCompleteRepository;
import kr.or.btf.web.repository.web.InspectionResponsePersonRepository;
import kr.or.btf.web.repository.web.InspectionResponseRepository;
import kr.or.btf.web.web.form.CourseRequestCompleteForm;
import kr.or.btf.web.web.form.InspectionResponseForm;
import kr.or.btf.web.web.form.InspectionResponsePersonForm;
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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InspectionResponsePersonService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final InspectionResponsePersonRepository inspectionResponsePersonRepository;
    private final ModelMapper modelMapper;
    private final InspectionResponseRepository inspectionResponseRepository;
    private final CourseRequestCompleteRepository courseRequestCompleteRepository;

    public Page<InspectionResponsePerson> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QInspectionResponsePerson qInspectionResponsePerson = QInspectionResponsePerson.inspectionResponsePerson;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qInspectionResponsePerson.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<InspectionResponsePerson> mngList = queryFactory
                .select(Projections.fields(InspectionResponsePerson.class,
                        qInspectionResponsePerson.id,
                        qInspectionResponsePerson.loginId,
                        qInspectionResponsePerson.nm,
                        qInspectionResponsePerson.mberDvty,
                        qInspectionResponsePerson.inspctDvTy,
                        qInspectionResponsePerson.regPsId,
                        qInspectionResponsePerson.regDtm,
                        qInspectionResponsePerson.inspctPid,
                        qInspectionResponsePerson.atnlcReqPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qAccount.nm)
                                        .from(qAccount)
                                        .where(qAccount.loginId.eq(qInspectionResponsePerson.regPsId)),
                                "regPsNm")
                ))
                .from(qInspectionResponsePerson)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public InspectionResponsePerson load(Long id) {
        InspectionResponsePerson load = inspectionResponsePersonRepository.findById(id).orElseGet(InspectionResponsePerson::new);

        return load;
    }

    public InspectionResponsePerson loadByform(InspectionResponsePersonForm form) {

        QInspectionResponsePerson qInspectionResponsePerson = QInspectionResponsePerson.inspectionResponsePerson;
        QAccount qAccount = QAccount.account;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(qInspectionResponsePerson.delAt.eq("N"));
        if (form.getLoginId() != null) {
            builder.and(qInspectionResponsePerson.loginId.eq(form.getLoginId()));
        }
        if (form.getInspctPid() != null) {
            builder.and(qInspectionResponsePerson.inspctPid.eq(form.getInspctPid()));
        }
        if (form.getInspctDvTy() != null) {
            builder.and(qInspectionResponsePerson.inspctDvTy.eq(form.getInspctDvTy()));
        }
        if (form.getAtnlcReqPid() != null) {
            builder.and(qInspectionResponsePerson.atnlcReqPid.eq(form.getAtnlcReqPid()));
        }

        List<InspectionResponsePerson> list = queryFactory
                .select(Projections.fields(InspectionResponsePerson.class,
                        qInspectionResponsePerson.id,
                        qInspectionResponsePerson.loginId,
                        qInspectionResponsePerson.nm,
                        qInspectionResponsePerson.mberDvty,
                        qInspectionResponsePerson.inspctDvTy,
                        qInspectionResponsePerson.regPsId,
                        qInspectionResponsePerson.regDtm,
                        qInspectionResponsePerson.inspctPid,
                        qInspectionResponsePerson.atnlcReqPid
                ))
                .from(qInspectionResponsePerson)
                .where(builder)
                .fetch();

        return (list != null && list.size() > 0 ? list.get(0) : new InspectionResponsePerson());
    }

    @Transactional
    public void delete(InspectionResponsePersonForm form) {
        InspectionResponsePerson mng = this.load(form.getId());

        mng.setUpdDtm(LocalDateTime.now());
        mng.setUpdPsId(form.getUpdPsId());
        mng.setDelAt(form.getDelAt());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public boolean insert(InspectionResponsePersonForm form, List<List<InspectionResponseForm>> responseFormList, Integer sectionIndex, CourseRequestCompleteForm courseRequestCompleteForm) {

        try {
            InspectionResponsePerson loadPerson = this.loadByform(form);
            if (loadPerson.getId() == null) {
                loadPerson = modelMapper.map(form, InspectionResponsePerson.class);
                loadPerson = inspectionResponsePersonRepository.save(loadPerson);
            }

            if (responseFormList != null && responseFormList.size() > 0) {
                for (List<InspectionResponseForm> inspectionResponseForms : responseFormList) {
                    Long qesitmPid = inspectionResponseForms.get(0).getQesitmPid();
                    inspectionResponseRepository.deleteByRspPsPidAndQesitmPid(loadPerson.getId(), qesitmPid);
                    for (InspectionResponseForm inspectionResponseForm : inspectionResponseForms) {
                        InspectionResponse response = modelMapper.map(inspectionResponseForm, InspectionResponse.class);
                        response.setRspPsPid(loadPerson.getId());
                        inspectionResponseRepository.save(response);
                    }
                }
            }
            //유효성검사 완료 처리
            if (sectionIndex.equals(5)) {
                CourseRequestComplete complete = courseRequestCompleteRepository.findByAtnlcReqPidAndCrsMstPidAndCrsPidAndSn(courseRequestCompleteForm.getAtnlcReqPid(), courseRequestCompleteForm.getCrsMstPid(), courseRequestCompleteForm.getCrsPid(), courseRequestCompleteForm.getSn());
                complete.setCmplSttTy(CompleteStatusType.COMPLETE.name());
                complete.setCmplPrsDtm(LocalDateTime.now());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean update(InspectionResponsePersonForm form) {

        try {

            InspectionResponsePerson responsePerson = inspectionResponsePersonRepository.findById(form.getId()).orElseGet(InspectionResponsePerson::new);
            responsePerson.setLoginId(form.getLoginId());
            responsePerson.setNm(form.getNm());
            responsePerson.setMberDvty(form.getMberDvty());
            responsePerson.setInspctDvTy(form.getInspctDvTy());
            responsePerson.setInspctPid(form.getInspctPid());
            responsePerson.setUpdPsId(form.getUpdPsId());
            responsePerson.setUpdDtm(LocalDateTime.now());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
