package kr.or.btf.web.test.web.service;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.web.form.BoardDataForm;
import kr.or.btf.web.web.form.SearchForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    BoardDataService boardDataService;

    public  Page<BoardData> getNewsListData(@PageableDefault(page = 0, size = 3) Pageable pageable){
        BoardDataForm boardDataForm = new BoardDataForm();
        SearchForm searchForm = new SearchForm();
        searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_5);
        boardDataForm.setMstPid(1L);
        Page<BoardData> noticeList = boardDataService.list(pageable, searchForm, boardDataForm);
        return noticeList;
    }
}
