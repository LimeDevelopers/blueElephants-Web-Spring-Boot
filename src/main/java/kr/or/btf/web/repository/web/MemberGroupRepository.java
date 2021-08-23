package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * MemberGroupRepository [파일 등록 처리 후 데이터 업데이트]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/22
 **/
@Repository
public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {
    @Modifying
    @Query("UPDATE MemberGroup mg SET mg.flPid = :flPid WHERE mg.id = :id")
    int updateFlPid(Long flPid, Long id);
}
