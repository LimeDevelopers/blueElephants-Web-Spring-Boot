package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.domain.web.Banner;
import kr.or.btf.web.domain.web.BoardData;
import kr.or.btf.web.domain.web.Campaign;
import kr.or.btf.web.domain.web.Postscript;
import kr.or.btf.web.domain.web.enums.BanDvTy;
import kr.or.btf.web.domain.web.enums.InspectionDvType;
import kr.or.btf.web.services.web.*;
import kr.or.btf.web.web.form.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

/**
 * MainController [메인 컨트롤러]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/03
 * 수정일 : 2021/08/04
 * 내용 : Constants 변수 수정함.
 * 수정일 : 2021/08/13
 * 내용 : 공지사항 리스트 리미트 3-> 5
 **/
@Controller
@RequiredArgsConstructor
// @RequiredArgsConstructor 어노테이션은 초기화 되지않은 final 필드나,
// @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다. 주로 의존성 주입(Dependency Injection) 편의성을 위해서 사용한다.
public class MainController extends BaseCont {

    // 서비스 등록
    private final BoardDataService boardDataService;
    private final CampaignService campaignService;
    private final BannerService bannerService;
    private final InspectionService inspectionService;
    private final PostscriptService postscriptService;

    @GetMapping("/naver1c813a906449890c14557110ef4af25e.html")
    public String naverSiteVerification() {
        return "/naver1c813a906449890c14557110ef4af25e";
    }

    // 인덱스!
    @GetMapping({"/", "/index"})
    public String index(Model model,
                        @PageableDefault(page = 0, size = 3) Pageable pageable,
                        HttpSession session,
                        @Value("${common.code.noticeCdPid}") Long noticeCdPid,
                        @Value("${common.code.campaignCode}") Long campaignCode,
                        @Value("${Globals.fileStoreUriPath}") String filePath,
                        @Value("${common.code.crewGalleryCdPid}") Long crewCdPid) {
        // searchFrom -> pageSize 사용처 확인해야됨
        SearchForm searchForm = new SearchForm();
        searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_5);

        // BoardDataForm -> 인덱스 페이지 최근 공지사항 노출 로직
        // searchForm -> pageSize는 limit 길이로 판단됨.
        // 공지사항 데이터 [s]
        BoardDataForm boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(noticeCdPid);
        Page<BoardData> noticeList = boardDataService.list(pageable, searchForm, boardDataForm);
        model.addAttribute("noticeList", noticeList);
        // 공지사항 데이터 [e]

        // 교육후기 데이터 [s] -- 임시주석
        /*searchForm.setPageSize(Constants.DEFAULT_PAGESIZE_3);
        Page<Postscript> postscripts = postscriptService.list(pageable, searchForm);
        model.addAttribute("postscripts", postscripts);*/
        // 교육후기 데이터 [e]

        // 뉴스레터 데이터 [s]
        boardDataForm = new BoardDataForm();
        boardDataForm.setMstPid(campaignCode);
        searchForm.setPageSize(Constants.DEFAULT_MAIN_NEWS_PAGESIZE);
        Page<BoardData> newsList = boardDataService.list(pageable, searchForm, boardDataForm);

        model.addAttribute("newsList", newsList);
        model.addAttribute("filePath", filePath + "/" + Constants.FOLDERNAME_BOARDDATA);
        // 뉴스레터 데이터 [e]

        // 캠페인 데이터 [s]
        CampaignForm campaignForm = new CampaignForm();
        campaignForm.setDvCodePid(crewCdPid);
        Campaign crew = campaignService.latestOneLoad(campaignForm);
        model.addAttribute("crew", crew);
        // 캠페인 데이터 [e]

        // 배너 데이터 [s]
        BannerForm bannerForm = new BannerForm();
        bannerForm.setBanDvTy(BanDvTy.MAIN);
        searchForm.setUseAt("Y");
        searchForm.setSrchDt(LocalDate.now());
        List<Banner> bannerList = bannerService.list(searchForm, bannerForm);
        model.addAttribute("bannerList", bannerList);
        model.addAttribute("bannerFilePath", filePath + "/" + Constants.FOLDERNAME_BANNER);
        // 배너 데이터 [e]

        //
        model.addAttribute("mc", "main");
        return "/newIndex";
    }


    /**
     * TODO 임시.. 테스트 후 삭제 예정
     */
    @ResponseBody
    @RequestMapping("/api/ins/test")
    public String authList(Model model) throws CloneNotSupportedException {

        InspectionForm commonForm = new InspectionForm();
        InspectionForm form = null;
        //공통
        commonForm.setAreaNm("경기도");
        commonForm.setSchlNm("부용고등학교");
        commonForm.setGrade(1);
        commonForm.setBan("1");
        commonForm.setCrsMstPid(12l);
        commonForm.setMberPid(41l);
        commonForm.setId(5l);
        //사전/사후
        commonForm.setInspctDvTy(InspectionDvType.BEFORE.name());
        commonForm.setSn(2);  //사전 : 2, 사후 : 6

        form = commonForm.copy();
        //가해경험 코드
        form.setDvValue1(72l, 3, 5);
        form.setDvValue2(73l, 2, 4);
        form.setDvValue3(74l, 2, 3);
        form.setDvValue4(75l, 1, 1);
        //가해경험(개인) - 언어폭력~성폭행
        JSONArray myResult1_1 = inspectionService.myInspectionResult1_1(form);
        //가해경험(학급) - 언어폭력~성폭행
        JSONArray clResult1_1 = inspectionService.classCourseInspectionResult1_1(form);

        form = commonForm.copy();
        //가해경험 > 가해정보 코드
        form.setDvCodePid1(76l);
        //가해경험(개인) - 가해정보(3문항)
        JSONArray[] myResult1_2 = inspectionService.myInspectionResult1_2(form, 3);
        //가해경험(학급) - 가해정보(3문항)
        JSONArray[] clResult1_2 = inspectionService.classCourseInspectionResult1_2(form, 3);

        form = commonForm.copy();
        //피해경험 코드/pages/activity/eduClass/{crsMstPid}/{id}/{sn}
        form.setDvValue1(77l, 3, 5);
        form.setDvValue2(78l, 2, 4);
        form.setDvValue3(79l, 2, 3);
        form.setDvValue4(80l, 1, 1);
        //피해경험(개인) - 언어폭력~성폭행
        JSONArray myResult2_1 = inspectionService.myInspectionResult1_1(form);
        //피해경험(학급) - 언어폭력~성폭행
        JSONArray clResult2_1 = inspectionService.classCourseInspectionResult1_1(form);

        form = commonForm.copy();
        //피해경험 > 가해정보 코드
        form.setDvCodePid1(81l);
        //피해경험(개인) - 피해정보(3문항)
        JSONArray[] myResult2_2 = inspectionService.myInspectionResult1_2(form, 3);
        //피해경험(학급) - 피해정보(3문항)
        JSONArray[] clResult2_2 = inspectionService.classCourseInspectionResult1_2(form, 3);

        form = commonForm.copy();
        //목적경험 코드
        form.setDvValue1(82l, 1, 1);
        form.setDvValue2(83l, 2, 3);
        form.setDvValue3(84l, 3, 5);
        form.setDvValue4(85l, 2, 4);
        //목적경험(개인) - 목격경혐여부~동조자
        JSONArray myResult3 = inspectionService.myInspectionResult1_1(form);
        //목적경험(학급) - 목격경혐여부~동조자
        JSONArray clResult3 = inspectionService.classCourseInspectionResult1_1(form);

        form = commonForm.copy();
        //사이버폭력에 대한 부정 태도 코드
        form.setDvValue1(86l, 0, 9);
        //사이버폭력에 대한 부정 태도(개인)
        JSONArray myResultBar1 = inspectionService.myInspectionResultBar(form);
        //사이버폭력에 대한 부정 태도(학급)
        JSONArray clResultBar1 = inspectionService.classCourseInspectionResultBar(form);

        form = commonForm.copy();
        //사이버폭력에 대한 대처효능감(개인수준) 코드
        form.setDvValue1(87l, 0, 6);
        //사이버폭력에 대한 대처효능감(개인)
        JSONArray myResultBar2 = inspectionService.myInspectionResultBar(form);
        //사이버폭력에 대한 대처효능감(학급)
        JSONArray clResultBar2 = inspectionService.classCourseInspectionResultBar(form);

        form = commonForm.copy();
        //사이버폭력에 대한 대처효능감(학급수준) 코드
        form.setDvValue1(137l, 0, 6);
        //사이버폭력에 대한 대처효능감(학급)
        JSONArray clResultBar3 = inspectionService.classCourseInspectionResultBar(form);

        form = commonForm.copy();
        //공격성, 공감, 자아존중감 코드
        form.setDvValue1(88l, 0, 6);
        form.setDvValue2(89l, 0, 6);
        form.setDvValue3(90l, 0, 4);
        //공격성, 공감, 자아존중감(개인)
        JSONArray myResultBar4 = inspectionService.myInspectionResultBarMulti(form);
        //공격성, 공감, 자아존중감(학급)
        JSONArray clResultBar4 = inspectionService.classCourseInspectionResultBarMulti(form);

        form = commonForm.copy();
        //정직, 약속, 용서, 책임, 배려, 소유 코드
        form.setDvValue1(91l, 0, 3);
        form.setDvValue2(92l, 0, 3);
        form.setDvValue3(93l, 0, 3);
        form.setDvValue4(94l, 0, 3);
        form.setDvValue5(95l, 0, 3);
        form.setDvValue6(96l, 0, 3);
        //정직, 약속, 용서, 책임, 배려, 소유(개인)
        JSONArray myResultBar5 = inspectionService.myInspectionResultBarMulti(form);
        //정직, 약속, 용서, 책임, 배려, 소유(학급)
        JSONArray clResultBar5 = inspectionService.classCourseInspectionResultBarMulti(form);

        form = commonForm.copy();
        //온라인 실태조사 코드
        form.setDvCodePid1(97l);
        //온라인 실태조사(개인) - 4문항
        JSONArray[] myResult4 = inspectionService.myInspectionResult1_2(form, 4);
        //온라인 실태조사(학급) - 4문항
        JSONArray[] clResult4 = inspectionService.classCourseInspectionResult1_2(form, 4);

        form = commonForm.copy();
        //사회적 바람직성 코드
        form.setDvCodePid1(99l);
        //사회적 바람직성(개인)
        JSONArray myResult5 = inspectionService.myInspectionResultAvg(form);

        return clResult1_2.toString();
    }


}
