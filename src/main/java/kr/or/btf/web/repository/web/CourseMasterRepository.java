package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CourseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseMasterRepository extends JpaRepository<CourseMaster, Long> {

    @Query("SELECT COUNT(rc.id) FROM  CourseRequestComplete rc WHERE rc.id = :rid AND rc.sn = 4 AND rc.cmplSttTy = 'COMPLETE'")
    int cntCompleteSn4(Long rid);
}
