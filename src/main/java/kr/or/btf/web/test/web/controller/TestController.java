package kr.or.btf.web.test.web.controller;

import kr.or.btf.web.common.aurora.AuroraAPIService;
import kr.or.btf.web.test.web.service.TestService;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.common.aurora.AuroraForm;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/test")
public class TestController extends BaseCont {
    @Autowired
    TestService testService;

    private final AuroraAPIService auroraAPIService;


    @GetMapping(value = "/page")
    public String getMenuList(){
        return "/pages/blueElephant/testPage";
    }

    @GetMapping(value = "/namane")
    public String namaneTestPage(Model model){
        model.addAttribute("mc","memberJoin");
        return "/pages/blueElephant/namane";
    }

    @PostMapping(value = "/getQrImg")
    public String getQrImg(Model model, @ModelAttribute AuroraForm auroraForm) throws IOException {
        AuroraForm result = auroraAPIService.getBase64String(auroraForm);
        model.addAttribute("mc","memberJoin");
        model.addAttribute("aurora",result);
        return "/pages/blueElephant/namane_result";
    }
    // 엑셀 다운로드 시작
    @GetMapping(value = "/page/excel/download")
//    public String testExcel(Model model){
//        Pageable pageable = null;
//        model.addAttribute("noticeList", testService.getNewsListData(pageable));
//        return "/pages/blueElephant/testPage";
//    }
    public void excelDownload(HttpServletResponse response) throws IOException {
//        Workbook wb = new HSSFWorkbook();
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("첫번째 시트");
        Row row = null;
        Cell cell = null;
        int rowNum = 0;

        // Header
        row = sheet.createRow(rowNum++);
        cell = row.createCell(0);
        cell.setCellValue("번호");
        cell = row.createCell(1);
        cell.setCellValue("이름");
        cell = row.createCell(2);
        cell.setCellValue("제목");

        // Body
        for (int i=0; i<3; i++) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(i+"_name");
            cell = row.createCell(2);
            cell.setCellValue(i+"_title");
        }

        // 컨텐츠 타입과 파일명 지정
        response.setContentType("ms-vnd/excel");
//        response.setHeader("Content-Disposition", "attachment;filename=example.xls");
        response.setHeader("Content-Disposition", "attachment;filename=example.xlsx");

        // Excel File Output
        wb.write(response.getOutputStream());
        wb.close();
    }
    // 엑셀 다운로드 끝

    //디자인페이지 리다이렉션 start
    @GetMapping(value = "/_temp/_index.html")
    public String openIndex() { return "_temp/_index"; }

    @GetMapping(value = "/_temp/_batchManagement")
    public String openBatchManagement(){
        return "_temp/_batchManagement";
    }

    @GetMapping(value = "/_temp/_intro")
    public String openIntro(){
        return "_temp/_intro";
    }

    @GetMapping(value = "/_temp/_character")
    public String openCharacter(){ return "_temp/_character"; }

    @GetMapping(value = "/_temp/_friends")
    public String openFriends(){
        return "_temp/_friends";
    }

    @GetMapping(value = "/_temp/_activity")
    public String openActivity(){
        return "_temp/_activity";
    }

    @GetMapping(value = "/_temp/_schedule")
    public String openSchedule(){
        return "_temp/_schedule";
    }

    @GetMapping(value = "/_temp/_location")
    public String openLocation(){
        return "_temp/_location";
    }

    @GetMapping(value = "/_temp/_idFind")
    public String openIdfind(){
        return "_temp/_idFind";
    }

    @GetMapping(value = "/_temp/_pwFind")
    public String openPwfind(){
        return "_temp/_pwFind";
    }

    @GetMapping(value = "/_temp/_header_gnb")
    public String openHeader(){ return "_temp/_header_gnb";}
    //디자인페이지 리다이렉션 end
}
