package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.MobileAuthLog;
import kr.or.btf.web.repository.web.MobileAuthLogRepository;
import kr.or.btf.web.web.form.MobileAuthLogForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MobileAuthLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final MobileAuthLogRepository mobileAuthLogRepository;
    private final ModelMapper modelMapper;

    public MobileAuthLog load(Long id) {
        MobileAuthLog actionLog = mobileAuthLogRepository.findById(id).orElseGet(MobileAuthLog::new);

        return actionLog;
    }

    public MobileAuthLog load(MobileAuthLogForm form) {
        MobileAuthLog actionLog = mobileAuthLogRepository.findByDmnNoAndRspNoAndMbtlnum(form.getDmnNo(), form.getRspNo(), form.getMbtlnum());

        return actionLog;
    }

    /**
     * @param form
     * @return
     */
    @Transactional
    public boolean insert(MobileAuthLogForm form) {

        try {
            MobileAuthLog mobileAuthLog = modelMapper.map(form, MobileAuthLog.class);
            mobileAuthLog.setRegDtm(LocalDateTime.now());
            mobileAuthLogRepository.save(mobileAuthLog);

            return true;
        } catch (Exception e){
            return false;
        }
    }
}
