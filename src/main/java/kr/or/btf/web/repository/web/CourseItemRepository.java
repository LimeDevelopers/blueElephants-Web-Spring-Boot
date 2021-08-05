package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.CourseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseItemRepository extends JpaRepository<CourseItem, Long> {

    @Query(value =
            " select" +
            "               ci1.crssq_pid," +
            "               ci1.ttl," +
            "               ci1.cn," +
            "               ci1.img_fl," +
            "               ci1.cntnts_dv_ty," +
            "               ci1.cntnts_url," +
            "               (case when ci1.cntnts_len IS NULL then 0 ELSE ci1.cntnts_len END) as 'cntnts_len'," +
            "               ci1.sno," +
            "               ci1.crs_pid," +
            "               (case when tp.procNm IS NULL then 'BEFORE' ELSE tp.procNm END) as procNm" +
            "            from" +
            "               tbl_course_item ci1" +
            "               LEFT JOIN (select" +
            "               his1.crssq_pid" +
            "                  ,(case" +
            "                  when max(his1.atnlc_hour)>=0 and max(his1.cntnts_len)>1 and max(his1.atnlc_hour)/max(his1.cntnts_len)<1 then 'APPLY'" +
            "                     when max(his1.atnlc_hour)/max(his1.cntnts_len)>=1 then 'COMPLETE'" +
            "                     else 'BEFORE'" +
            "                  END) AS procNm" +
            "                from" +
            "                    tbl_course_item ci2" +
            "                        inner join" +
            "                    tbl_course_his his1" +
            "                    on (" +
            "                                his1.crssq_pid=ci2.crssq_pid" +
            "                            and his1.mber_pid=?#{#mberPid}" +
            "                        )" +
            "                WHERE ci2.crs_pid=?#{#crsPid}" +
            "                group by" +
            "                    his1.crssq_pid ," +
            "                    his1.mber_pid ," +
            "                    his1.cntnts_len) AS tp ON tp.crssq_pid=ci1.crssq_pid" +
            "            where" +
            "                   ci1.crs_pid=?#{#crsPid}" +
            "            order by" +
            "               ci1.sno asc", nativeQuery = true)
    List<Object[]> listForProcNm(Long crsPid, Long mberPid);

}
