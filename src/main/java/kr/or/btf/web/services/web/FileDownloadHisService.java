package kr.or.btf.web.services.web;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.or.btf.web.domain.web.FileDownloadHis;
import kr.or.btf.web.repository.web.FileDownloadHisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileDownloadHisService {

    private final JPAQueryFactory queryFactory;
    private final FileDownloadHisRepository fileDownloadHisRepository;

    @Transactional
    public FileDownloadHis save(FileDownloadHis fileDownloadHis) {
        try {
            fileDownloadHisRepository.save(fileDownloadHis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileDownloadHis;
    }
}
