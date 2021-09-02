package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.domain.web.BoardMaster;
import kr.or.btf.web.domain.web.Event;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.TableNmType;
import kr.or.btf.web.services.web.BoardDataService;
import kr.or.btf.web.services.web.BoardMasterService;
import kr.or.btf.web.services.web.EventService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.web.form.BoardDataForm;
import kr.or.btf.web.web.form.EventForm;
import kr.or.btf.web.web.form.FileInfoForm;
import kr.or.btf.web.web.form.SearchForm;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final BoardDataService boardDataService;
    private final BoardMasterService boardMasterService;
    private final FileInfoService fileInfoService;
    private final EventService eventService;

    @RequestMapping("/pages/news/list/{mstPid}")
    public String list(Model model,
                       @PathVariable(name = "mstPid") Long mstPid,
                       @PageableDefault Pageable pageable,
                       @ModelAttribute SearchForm searchForm,
                       @Value("${Globals.fileStoreUriPath}") String filePath) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(mstPid);
        searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        if (searchForm.getSorting() == null || searchForm.getSorting().isEmpty()){
            searchForm.setSorting("latest");
        }
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        model.addAttribute("mstPid", mstPid);

        BoardMaster boardMaster = boardMasterService.load(mstPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_BOARDDATA);

        model.addAttribute("mc", "news");
        model.addAttribute("pageTitle", "소식/행사");
        return "/pages/news/list";
    }

    @RequestMapping("/pages/news/event")
    public String eventList(Model model,
                            @PageableDefault Pageable pageable,
                            @ModelAttribute SearchForm searchForm,
                            @Value("${Globals.fileStoreUriPath}") String filePath) {

        searchForm.setPageSize(Constants.DEFAULT_THUMBNAIL_PAGESIZE);
        Page<Event> events = eventService.list(pageable, searchForm);
        model.addAttribute("events", events);

        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_EVENT);

        model.addAttribute("mc", "news");
        model.addAttribute("pageTitle", "캘린더");
        return "/pages/news/listForEvent";
    }

    @ResponseBody
    @PostMapping("/pages/news/event/calendarList")
    public String calendarEvents(Model model,
                              @CurrentUser Pageable pageable,
                              @RequestBody SearchForm searchForm) throws Exception {

        JSONArray array = new JSONArray();

        List<Event> events = eventService.eventList(searchForm);

        if (events != null) {
            for (Event event : events) {
                JSONObject json = new JSONObject();
                json.put("id", event.getId());
                json.put("sort", event.getFxSeTy().getName());
                json.put("sortClass", event.getFxSeTy().getClassName());
                json.put("title", event.getTtl());
                json.put("place", event.getCn());
                json.put("description", event.getSpotDtl());
                json.put("start", event.getStYmd());
                json.put("time", event.getStYmd().replaceAll("-",".") + " ~ " + event.getEdYmd().replaceAll("-","."));

                array.put(json);
            }
        }

        return array.toString();
    }

    @ResponseBody
    @PostMapping("/pages/news/event/list")
    public List<Event> events(Model model,
                         @CurrentUser Pageable pageable,
                         @RequestBody SearchForm searchForm) throws Exception {

        List<Event> arrayList = new ArrayList<>();
        List<Event> events = eventService.eventList(searchForm);
        for (Event event : events) {
            event.setFxSeTyName(event.getFxSeTy().getName());
            event.setFxSeTyClass(event.getFxSeTy().getClassName());
            arrayList.add(event);
        }

        return arrayList;
    }

    @RequestMapping("/pages/news/event/detail/{id}")
    public String eventDetail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @PathVariable(name = "id") Long id) {

        EventForm eventForm = new EventForm();
        eventForm.setId(id);
        Event load = eventService.load(eventForm);
        model.addAttribute("form", load);

        eventForm.setPrevNext("prev");
        Event prev = eventService.load(eventForm);
        model.addAttribute("prev", prev);

        eventForm.setPrevNext("next");
        Event next = eventService.load(eventForm);
        model.addAttribute("next", next);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(load.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_EVENT.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_EVENT);

        Integer readCnt = load.getReadCnt() == null ? 0 : load.getReadCnt();

        //조회수업데이트
        eventForm.setReadCnt(readCnt+1);
        eventService.updateByReadCnt(eventForm);

        model.addAttribute("mc", "news");
        model.addAttribute("pageTitle", "행사");
        return "/pages/news/detailForEvent";
    }

    @RequestMapping("/pages/news/detail/{id}")
    public String detail(Model model,
                         @Value("${Globals.fileStoreUriPath}") String filepath,
                         @PathVariable(name = "id") Long id) {

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setId(id);
        BoardData load = boardDataService.load(boardDataForm);
        model.addAttribute("form", load);

        boardDataForm.setMstPid(load.getMstPid());
        boardDataForm.setPrevNext("prev");
        BoardData prev = boardDataService.load(boardDataForm);
        model.addAttribute("prev", prev);

        boardDataForm.setPrevNext("next");
        BoardData next = boardDataService.load(boardDataForm);
        model.addAttribute("next", next);

        BoardMaster boardMaster = boardMasterService.load(load.getMstPid());
        model.addAttribute("boardMaster", boardMaster);

        FileInfoForm fileInfoForm = new FileInfoForm();
        fileInfoForm.setDataPid(load.getId());
        fileInfoForm.setTableNm(TableNmType.TBL_BOARD_DATA.name());
        List<FileInfo> fileList = fileInfoService.list(fileInfoForm);
        model.addAttribute("fileList", fileList);

        model.addAttribute("filePath", filepath+"/"+ Constants.FOLDERNAME_BOARDDATA);

        Integer readCnt = load.getReadCnt() == null ? 0 : load.getReadCnt();

        boardDataForm.setReadCnt(readCnt+1);
        boardDataService.updateByReadCnt(boardDataForm);

        model.addAttribute("mc", "news");
        model.addAttribute("pageTitle", "언론보도");
        return "/pages/news/detail";
    }

    @RequestMapping("/pages/news/notice")
    public String notice(Model model,
                         @PageableDefault Pageable pageable,
                         @ModelAttribute SearchForm searchForm,
                         @Value("${common.code.noticeCdPid}") Long noticeCdPid) {

        BoardDataForm fixBoardDataForm = new BoardDataForm();
        fixBoardDataForm.setMstPid(noticeCdPid);
        fixBoardDataForm.setFixingAt("Y");
        List<BoardData> fixList = boardDataService.listForFix(fixBoardDataForm);
        model.addAttribute("fixList", fixList);

        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(noticeCdPid);
        boardDataForm.setFixingAt("N");
        if (searchForm.getSorting() == null || searchForm.getSorting().isEmpty()){
            searchForm.setSorting("latest");
        }
        Page<BoardData> boardDatas = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("boardDatas", boardDatas);

        model.addAttribute("mstPid", noticeCdPid);

        BoardMaster boardMaster = boardMasterService.load(noticeCdPid);
        model.addAttribute("boardMaster", boardMaster);

        model.addAttribute("mc", "news");
        model.addAttribute("pageTitle", "공지사항");
        return "/pages/news/notice";
    }

}