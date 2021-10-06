package kr.or.btf.web.services.web;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.EmotionTrafficLog;
import kr.or.btf.web.domain.web.QEmotionTraffic;
import kr.or.btf.web.domain.web.QEmotionTrafficLog;
import kr.or.btf.web.repository.web.EmotionTrafficLogRepository;
import kr.or.btf.web.repository.web.EmotionTrafficRepository;
import kr.or.btf.web.web.form.EmotionTrafficForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class EmotionTrafficService extends _BaseService {
    private final EmotionTrafficLogRepository etr_log;
    private final EmotionTrafficRepository etr;
    private final JPAQueryFactory queryFactory;

    @Transactional
    public Boolean save(Account account) {
        EmotionTrafficLog emotionTrafficLog = new EmotionTrafficLog();
        emotionTrafficLog.setEtfPid(account.getEtfPid());
        emotionTrafficLog.setMberPid(account.getId());
        emotionTrafficLog.setRegDtm(LocalDateTime.now());
        EmotionTrafficLog rs = etr_log.save(emotionTrafficLog);

        if(rs!=null){
            return true;
        } else {
            return false;
        }
    }

    // 감정신호등 load
    @Transactional
    public EmotionTrafficForm load() {
        BooleanBuilder builder = new BooleanBuilder();
        EmotionTrafficForm form = new EmotionTrafficForm();
        QEmotionTrafficLog qEmotionTrafficLog = QEmotionTrafficLog.emotionTrafficLog;
        QEmotionTraffic qEmotionTraffic = QEmotionTraffic.emotionTraffic;

        StringTemplate formattedDate = Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})"
                , qEmotionTrafficLog.regDtm
                , ConstantImpl.create("%Y-%m-%d"));

        OrderSpecifier<Long> orderSpecifier = qEmotionTrafficLog.id.desc();
        builder.and(Expressions.dateTimeTemplate(LocalDateTime.class,"CONVERT(varchar(10),{0},120)",Expressions.currentTimestamp())
                .between(
                        Expressions.dateTimeTemplate(LocalDateTime.class,"CONVERT(varchar(10),{0},120)",qEmotionTrafficLog.regDtm),
                        Expressions.dateTimeTemplate(LocalDateTime.class,"CONVERT(varchar(10),{0},120)",qEmotionTrafficLog.regDtm)
                ));

        List<EmotionTrafficLog> results = queryFactory
                .select(Projections.fields(EmotionTrafficLog.class,
                        qEmotionTrafficLog.id,
                        qEmotionTrafficLog.etfPid
                ))
                .from(qEmotionTrafficLog)
                .leftJoin(qEmotionTraffic).on(qEmotionTraffic.id.eq(qEmotionTrafficLog.etfPid))

                .groupBy(formattedDate)
                .orderBy(orderSpecifier)
                .fetch();
        // 데이터 없을경우 <보통>
        if(results.isEmpty()){
            form.setEtfNm("ordinary");
        }
        int cnt1 = 0,cnt2 = 0,cnt3 = 0,cnt4 = 0,cnt5 = 0;
        for(int i=0; i >= results.size(); i++) {
            if(results.get(i).getEtfPid() == 1) {
                cnt1++;
            }
            else if(results.get(i).getEtfPid() == 2) {
                cnt2++;
            }
            else if(results.get(i).getEtfPid() == 3) {
                cnt3++;
            }
            else if(results.get(i).getEtfPid() == 4) {
                cnt4++;
            }
            else if(results.get(i).getEtfPid() == 5) {
                cnt5++;
            }
        }
        // 값이 같을경우는 그냥 보통.
        form.setEtfNm("ordinary");
        if(cnt1 > cnt2 && cnt1 > cnt3 && cnt1 > cnt4 && cnt1> cnt5) {
            form.setEtfNm("good");
            form.setCnt(cnt1);
        }
        if(cnt2 > cnt1 && cnt2 > cnt3 && cnt2 > cnt4 && cnt2> cnt5) {
            form.setEtfNm("ordinary");
            form.setCnt(cnt2);
        }
        if(cnt3 > cnt1 && cnt3 > cnt2 && cnt3 > cnt4 && cnt3 > cnt5) {
            form.setEtfNm("sad");
            form.setCnt(cnt3);
        }
        if(cnt4 > cnt1 && cnt4 > cnt2 && cnt4 > cnt3 && cnt4 > cnt5) {
            form.setEtfNm("angry");
            form.setCnt(cnt4);
        }
        log.info("test@@@2"+ form.getEtfNm());
        return form;
    }
}
