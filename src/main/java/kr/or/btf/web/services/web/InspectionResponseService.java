package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.*;
import kr.or.btf.web.repository.web.InspectionResponseRepository;
import kr.or.btf.web.web.form.InspectionResponseForm;
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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InspectionResponseService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final InspectionResponseRepository inspectionResponseRepository;
    private final ModelMapper modelMapper;

    public Page<InspectionResponse> list(Pageable pageable, SearchForm searchForm) {

        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, Constants.DEFAULT_PAGESIZE); // <- Sort 추가

        QInspectionResponse qInspectionResponse = QInspectionResponse.inspectionResponse;
        QAccount qAccount = QAccount.account;

        OrderSpecifier<Long> orderSpecifier = qInspectionResponse.id.desc();

        BooleanBuilder builder = new BooleanBuilder();

        QueryResults<InspectionResponse> mngList = queryFactory
                .select(Projections.fields(InspectionResponse.class,
                        qInspectionResponse.id,
                        qInspectionResponse.answerCnts,
                        qInspectionResponse.rspPsPid,
                        qInspectionResponse.aswPid,
                        qInspectionResponse.qesitmPid
                ))
                .from(qInspectionResponse)
                .where(builder)
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .orderBy(orderSpecifier)
                .fetchResults();

        return new PageImpl<>(mngList.getResults(), pageable, mngList.getTotal());
    }

    public InspectionResponse load(Long id) {
        InspectionResponse load = inspectionResponseRepository.findById(id).orElseGet(InspectionResponse::new);

        return load;
    }

    public InspectionResponse load(InspectionResponseForm inspectionResponseForm, Account account, Long atnlcReqPid) {
        QInspectionResponse qInspectionResponse = QInspectionResponse.inspectionResponse;
        QInspectionResponsePerson qInspectionResponsePerson = QInspectionResponsePerson.inspectionResponsePerson;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qInspectionResponse.qesitmPid.eq(inspectionResponseForm.getQesitmPid()));
        builder.and(qInspectionResponsePerson.inspctDvTy.eq(inspectionResponseForm.getInspctDvTy()));

        InspectionResponse inspectionResponse = queryFactory
                .select(Projections.fields(InspectionResponse.class,
                        qInspectionResponse.id,
                        qInspectionResponse.answerCnts,
                        qInspectionResponse.rspPsPid,
                        qInspectionResponse.aswPid,
                        qInspectionResponse.qesitmPid
                ))
                .from(qInspectionResponse)
                .innerJoin(qInspectionResponsePerson).on(qInspectionResponsePerson.loginId.eq(account.getLoginId())
                                                        .and(qInspectionResponse.rspPsPid.eq(qInspectionResponsePerson.id))
                                                        .and(qInspectionResponsePerson.atnlcReqPid.eq(atnlcReqPid)))
                .where(builder)
                .fetchOne();

        return inspectionResponse;
    }

    @Transactional
    public void delete(InspectionResponseForm form) {
        inspectionResponseRepository.deleteById(form.getId());
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public InspectionResponse insert(InspectionResponseForm form) throws Exception {

        InspectionResponse save = modelMapper.map(form, InspectionResponse.class);
        save = inspectionResponseRepository.save(save);

        return save;
    }

    @Transactional
    public boolean update(InspectionResponseForm form) {

        try {

            InspectionResponse response = inspectionResponseRepository.findById(form.getId()).orElseGet(InspectionResponse::new);
            response.setAnswerCnts(form.getAnswerCnts());
            response.setAswPid(form.getAswPid());
            response.setQesitmPid(form.getQesitmPid());
            response.setRspPsPid(form.getRspPsPid());

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
