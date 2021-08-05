package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.AdviceReservationType;
import kr.or.btf.web.domain.web.QAdviceReservationType;
import kr.or.btf.web.domain.web.QCommonCode;
import kr.or.btf.web.repository.web.FileInfoRepository;
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
public class AdviceReservationTypeService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final FileInfoRepository fileInfoRepository;

    public List<AdviceReservationType> list(Long advcRsvPid) {

        QAdviceReservationType qAdviceReservationType = QAdviceReservationType.adviceReservationType;
        QCommonCode qCommonCode = QCommonCode.commonCode;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qAdviceReservationType.advcRsvPid.eq(advcRsvPid));

        QueryResults<AdviceReservationType> mngList = queryFactory
                .select(Projections.fields(AdviceReservationType.class,
                        qAdviceReservationType.codePid,
                        qAdviceReservationType.advcRsvPid,
                        ExpressionUtils.as(
                                JPAExpressions.select(qCommonCode.codeNm)
                                        .from(qCommonCode)
                                        .where(qAdviceReservationType.codePid.eq(qCommonCode.id)),
                                "codeNm")
                ))
                .from(qAdviceReservationType)
                .where(builder)
                .fetchResults();

        return mngList.getResults();
    }

}
