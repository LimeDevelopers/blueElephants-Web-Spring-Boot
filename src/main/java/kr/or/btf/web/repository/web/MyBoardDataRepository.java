package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MyBoardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyBoardDataRepository extends JpaRepository<MyBoardData, Long> {
    MyBoardData findByDataPidAndMberPid(Long dataPid, Long MberPid);
    void deleteByDataPidAndMberPid(Long dataPid, Long MberPid);
}
