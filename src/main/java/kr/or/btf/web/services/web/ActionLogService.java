package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.ActionLog;
import kr.or.btf.web.domain.web.QActionLog;
import kr.or.btf.web.repository.web.ActionLogRepository;
import kr.or.btf.web.web.form.ActionLogForm;
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
public class ActionLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final ActionLogRepository actionLogRepository;
    private final ModelMapper modelMapper;

    public ActionLog load(Long id) {
        ActionLog actionLog = actionLogRepository.findById(id).orElseGet(ActionLog::new);

        return actionLog;
    }

    /**
     * @param actionLogForm
     * @return
     */
    @Transactional
    public boolean insert(ActionLogForm actionLogForm) {

        try {
            ActionLog actionLog = modelMapper.map(actionLogForm, ActionLog.class);
            actionLog.setActDtm(LocalDateTime.now());
            actionLogRepository.save(actionLog);

            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Long count(ActionLogForm actionLogForm) {
        QActionLog qActionLog = QActionLog.actionLog;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qActionLog.mberPid.eq(actionLogForm.getMberPid()));
        builder.and(qActionLog.actDvTy.eq(actionLogForm.getActDvTy()));
        builder.and(qActionLog.cnctUrl.like(actionLogForm.getCnctUrl()));

        List<ActionLog> fetch = queryFactory
                .select(Projections.fields(ActionLog.class,
                        qActionLog.id
                ))
                .from(qActionLog)
                .where(builder)
                .groupBy(Expressions.stringTemplate("date_format({0},{1})", qActionLog.actDtm, ConstantImpl.create("%Y-%m-%d")))
                .fetch();

        return fetch == null ? 0 : fetch.stream().count();
    }
}
