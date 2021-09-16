package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.NamaneTemp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NamaneTempRepository extends JpaRepository<NamaneTemp, Long> {
    List<NamaneTemp> findAllByMberPidAndYn(Long pid, String Yn);
    @Modifying
    @Query("UPDATE NamaneTemp ac SET ac.yn = 'Y' WHERE ac.mberPid = :mberPid AND ac.id = :id")
    int updateStatus(Long mberPid, Long id);
}
