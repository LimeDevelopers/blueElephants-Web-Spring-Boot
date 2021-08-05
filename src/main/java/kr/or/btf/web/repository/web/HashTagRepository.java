package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    void deleteByDataPidAndTableNm(Long dataPid, String tableNm);
}
