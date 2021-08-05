package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.PlayLog;
import kr.or.btf.web.domain.web.QPlayLog;
import kr.or.btf.web.repository.web.PlayLogRepository;
import kr.or.btf.web.web.form.PlayLogForm;
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
public class PlayLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final PlayLogRepository playLogRepository;
    private final ModelMapper modelMapper;

    public PlayLog load(Long id) {
        PlayLog playLog = playLogRepository.findById(id).orElseGet(PlayLog::new);

        return playLog;
    }

    /**
     * @param playLogForm
     * @return
     */
    @Transactional
    public boolean insert(PlayLogForm playLogForm) {

        try {
            PlayLog playLog = modelMapper.map(playLogForm, PlayLog.class);
            playLog.setActDtm(LocalDateTime.now());
            playLogRepository.save(playLog);

            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Long count(PlayLogForm playLogForm) {
        QPlayLog qPlayLog = QPlayLog.playLog;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qPlayLog.mberPid.eq(playLogForm.getMberPid()));
        builder.and(qPlayLog.cnctUrl.like(playLogForm.getCnctUrl()));

        List<PlayLog> fetch = queryFactory
                .select(Projections.fields(PlayLog.class,
                        qPlayLog.id
                ))
                .from(qPlayLog)
                .where(builder)
                .groupBy(Expressions.stringTemplate("date_format({0}, {1})", qPlayLog.actDtm, ConstantImpl.create("%Y-%m-%d")))
                .fetch();

        return fetch == null ? 0 : fetch.stream().count();
    }
}
