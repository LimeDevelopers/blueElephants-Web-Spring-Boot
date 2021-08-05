package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.LoginCnntLogs;
import kr.or.btf.web.domain.web.PointHis;
import kr.or.btf.web.domain.web.QPointHis;
import kr.or.btf.web.repository.web.PointHisRepository;
import kr.or.btf.web.web.form.PointHisForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHisService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PointHisRepository pointHisRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public boolean insert(PointHisForm form) {

        try {
            PointHis pointHis = modelMapper.map(form, PointHis.class);
            pointHis.setRegDtm(LocalDateTime.now());
            pointHisRepository.save(pointHis);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public long count(PointHisForm form) {
        QPointHis qPointHis = QPointHis.pointHis;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPointHis.mberPid.eq(form.getMberPid()));
        builder.and(qPointHis.pontDvTy.eq(form.getPontDvTy()));
        builder.and(Expressions.stringTemplate("date_format({0},{1})", qPointHis.regDtm, ConstantImpl.create("%Y%m%d")).eq(form.getYyyyMMdd()));

        List<LoginCnntLogs> fetch = queryFactory
                .select(Projections.fields(LoginCnntLogs.class,
                        qPointHis.id
                ))
                .from(qPointHis)
                .where(builder)
                .fetch();

        return fetch == null ? 0 : fetch.stream().count();
    }
}
