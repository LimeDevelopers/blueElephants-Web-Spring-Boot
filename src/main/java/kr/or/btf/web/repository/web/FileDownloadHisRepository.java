package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.FileDownloadHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDownloadHisRepository extends JpaRepository<FileDownloadHis, Long> {
    void deleteByFlPid (Long flPid);
}
