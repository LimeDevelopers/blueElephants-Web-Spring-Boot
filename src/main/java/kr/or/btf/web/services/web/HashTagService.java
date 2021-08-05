package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.HashTag;
import kr.or.btf.web.domain.web.QHashTag;
import kr.or.btf.web.repository.web.HashTagRepository;
import kr.or.btf.web.web.form.HashTagForm;
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
public class HashTagService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final HashTagRepository hashTagRepository;
    private final ModelMapper modelMapper;

    public List<HashTag> list(HashTagForm hashTagForm) {

        QHashTag qHashTag = QHashTag.hashTag;

        OrderSpecifier<Long> orderSpecifier = qHashTag.id.desc();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qHashTag.dataPid.eq(hashTagForm.getDataPid()));
        builder.and(qHashTag.tableNm.eq(hashTagForm.getTableNm()));

        List<HashTag> mngList = queryFactory
                .select(Projections.fields(HashTag.class,
                        qHashTag.id,
                        qHashTag.tagNm,
                        qHashTag.regDtm,
                        qHashTag.dataPid,
                        qHashTag.tableNm
                ))
                .from(qHashTag)
                .where(builder)
                .orderBy(orderSpecifier)
                .fetch();

        return mngList;
    }
}
