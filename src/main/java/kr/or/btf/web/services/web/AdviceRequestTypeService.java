package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.AdviceRequestType;
import kr.or.btf.web.domain.web.QAdviceRequestType;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.repository.web.AdviceRequestTypeRepository;
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
public class AdviceRequestTypeService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final AdviceRequestTypeRepository adviceRequestTypeRepository;
    private final ModelMapper modelMapper;

    public List<AdviceRequestType> list(Long advcReqPid) {

        QAdviceRequestType qAdviceRequestType = QAdviceRequestType.adviceRequestType;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceRequestType.advcReqPid.eq(advcReqPid));

        QueryResults<AdviceRequestType> mngList = queryFactory
                .select(Projections.fields(AdviceRequestType.class,
                        qAdviceRequestType.codePid,
                        qAdviceRequestType.advcReqPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceRequestType.codePid.eq(qCommonCode.id)),
                                "codeNm")
                ))
                .from(qAdviceRequestType)
                .where(builder)
                .fetchResults();

        return mngList.getResults();
    }
}
