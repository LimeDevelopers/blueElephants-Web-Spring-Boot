package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.SearchLog;
import kr.or.btf.web.repository.web.SearchLogRepository;
import kr.or.btf.web.web.form.SearchLogForm;
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
public class SearchLogService extends _BaseService {

    private final JPAQueryFactory queryFactory;
    private final SearchLogRepository searchLogRepository;
    private final ModelMapper modelMapper;

    /**
     * @param searchLogForm
     * @return
     */
    @Transactional
    public boolean insert(SearchLogForm searchLogForm) {

        try {
            SearchLog searchLog = modelMapper.map(searchLogForm, SearchLog.class);
            searchLog.setRegDtm(LocalDateTime.now());
            searchLogRepository.save(searchLog);

            return true;
        } catch (Exception e){
            return false;
        }
    }
}
