package kr.or.btf.web.repository.web;

import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.web.form.SearchForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardDataRepository extends JpaRepository<BoardData, Long> {

    @Query(value = " SELECT" +
            "    dataId" +
            "    ,ttl" +
            "    ,cn" +
            "    ,regDtm" +
            "    ,menuNm" +
            "    ,url" +
            "    ,tag" +
            " FROM" +
            " (" +
            "     (" +
            "     SELECT" +
            "         menu.menu_pid AS dataId" +
            "         ,menu.menu_nm as ttl" +
            "         ,'' as cn" +
            "         ,null as regDtm" +
            "         ,concat('메뉴 ', fn_getParentMenuList(menu.menu_pid)) AS menuNm" +
            "         ,menu.menu_url AS url" +
            "         ,'' as tag" +
            "     FROM tbl_user_menu AS menu" +
            "    WHERE menu.del_at = 'N'" +
            "     AND menu.menu_url is not null" +
            "     AND menu.menu_nm LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%')" +
            "     )" +
            "     UNION ALL" +
            "     (" +
            "     SELECT" +
            "         evt.event_pid AS dataId" +
            "         ,evt.ttl" +
            "         ,evt.cn" +
            "         ,evt.reg_dtm as regDtm" +
            "         ,'푸코소식 행사' AS menuNm" +
            "         ,concat('/pages/news/event/detail/', evt.event_pid) AS url" +
            "         ,'' as tag" +
            "     FROM tbl_event AS evt" +
            "    WHERE evt.del_at = 'N'" +
            "     AND (evt.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR evt.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
            "     )" +
            "     UNION ALL" +
            "     (" +
            "     SELECT" +
            "         expr.exprn_pid AS dataId" +
            "         ,expr.ttl" +
            "         ,expr.cn" +
            "         ,expr.reg_dtm as regDtm" +
            "         ,concat('푸른코끼리활동 체험 ', cd.code_nm) AS menuNm" +
            "         ,concat('/pages/activity/experienceDetail/', expr.exprn_pid) AS url" +
            "         ,'' as tag" +
            "     FROM tbl_experience AS expr" +
            "     INNER JOIN tbl_common_code AS cd ON expr.dv_code_pid = cd.code_pid" +
            "    WHERE expr.del_at = 'N'" +
            "     AND (expr.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR expr.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
            "     )" +
            "     UNION ALL" +
            "     (" +
            "     SELECT" +
            "         cmp.cmpgn_pid AS dataId" +
            "         ,cmp.ttl" +
            "         ,cmp.cn" +
            "         ,cmp.reg_dtm as regDtm" +
            "         ,concat('푸른코끼리활동 문화 ', cd.code_nm) AS menuNm" +
            "         ,concat('/pages/activity/cultureDetail/', cmp.cmpgn_pid) AS url" +
            "         ,'' as tag" +
            "     FROM tbl_campaign AS cmp" +
            "     INNER JOIN tbl_common_code AS cd ON cmp.dv_code_pid = cd.code_pid" +
            "    WHERE cmp.del_at = 'N'" +
            "     AND (cmp.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR cmp.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
            "     )" +
            "     UNION ALL" +
            "     (" +
            "     SELECT" +
            "         bd.data_pid AS dataId" +
            "         ,bd.ttl" +
            "         ,bd.cn" +
            "         ,bd.reg_dtm as regDtm" +
            "         ,concat(case when bd.mst_pid = 1 then '푸코소식 '" +
            "           when bd.mst_pid = 3 then '푸른코끼리활동 '" +
            "           when bd.mst_pid = 5 then '푸코소식 '" +
            "           when bd.mst_pid = 6 then '푸코소식 '" +
            "           when bd.mst_pid = 7 then '푸코소식 '" +
            "           when bd.mst_pid = 9 then '푸른코끼리활동 제안 '" +
            "           end, mst.bbs_nm) AS menuNm" +
            "         ,concat((case when bd.mst_pid = 3 then '/pages/activity/eduDataRoomDetail/'" +
            "           when bd.mst_pid = 9 then '/pages/activity/policyProposalDetail/'" +
            "           else '/pages/news/detail/'" +
            "           end), bd.data_pid) AS url" +
            "         ,(SELECT GROUP_CONCAT(CONCAT('#',tag_nm) SEPARATOR ' ')" +
            "           FROM tbl_hash_tag" +
            "           where table_nm = 'TBL_BOARD_DATA'" +
            "           AND data_pid = bd.data_pid) as tag" +
            "     FROM tbl_board_data AS bd" +
            "     INNER JOIN tbl_board_master AS mst ON bd.mst_pid = mst.mst_pid" +
            "     WHERE bd.mst_pid IN (1,3,4,5,6,7,9)" +
            "    AND bd.del_at = 'N'" +
            "     AND (bd.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') " +
            "           OR bd.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%')" +
            "           OR ?#{#searchForm.totalSrchWord} IN (SELECT tag_nm FROM tbl_hash_tag WHERE bd.data_pid = data_pid AND table_nm = 'TBL_BOARD_DATA'))" +
            "     )" +
            " ) AS sch" +
            " ORDER BY menuNm ASC, dataId DESC" +
            " limit ?#{#pageable.pageSize} offset ?#{#pageable.offset}",
            countQuery = " SELECT" +
                    "     SUM(cnt)" +
                    " FROM" +
                    " (" +
                    "     (" +
                    "     SELECT" +
                    "         COUNT(*) AS cnt" +
                    "     FROM tbl_user_menu AS menu" +
                    "    WHERE menu.del_at = 'N'" +
                    "     AND menu.menu_url is not null" +
                    "     AND menu.menu_nm LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%')" +
                    "     )" +
                    "     UNION ALL" +
                    "     (" +
                    "     SELECT" +
                    "         COUNT(*) AS cnt" +
                    "     FROM tbl_event AS evt" +
                    "    WHERE evt.del_at = 'N'" +
                    "     AND (evt.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR evt.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
                    "     )" +
                    "     UNION ALL" +
                    "     (" +
                    "     SELECT" +
                    "         COUNT(*) AS cnt" +
                    "     FROM tbl_experience AS expr" +
                    "     INNER JOIN tbl_common_code AS cd ON expr.dv_code_pid = cd.code_pid" +
                    "    WHERE expr.del_at = 'N'" +
                    "     AND (expr.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR expr.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
                    "     )" +
                    "     UNION ALL" +
                    "     (" +
                    "     SELECT" +
                    "         COUNT(*) AS cnt" +
                    "     FROM tbl_campaign AS cmp" +
                    "     INNER JOIN tbl_common_code AS cd ON cmp.dv_code_pid = cd.code_pid" +
                    "    WHERE cmp.del_at = 'N'" +
                    "     AND (cmp.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR cmp.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
                    "     )" +
                    "     UNION ALL" +
                    "     (" +
                    "     SELECT" +
                    "         COUNT(*) AS cnt" +
                    "     FROM tbl_board_data AS bd" +
                    "     INNER JOIN tbl_board_master AS mst ON bd.mst_pid = mst.mst_pid" +
                    "     WHERE bd.mst_pid IN (1,2,3,5,6,7,9,10)" +
                    "    AND bd.del_at = 'N'" +
                    "     AND (bd.ttl LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%') OR bd.cn LIKE CONCAT('%', ?#{#searchForm.totalSrchWord}, '%'))" +
                    "     )" +
                    " ) AS sch",
            nativeQuery = true)
    Page<Object[]> totalSearchList(Pageable pageable, SearchForm searchForm);
}
