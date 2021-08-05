package kr.or.btf.web.web.controller;

import kr.or.btf.web.common.Constants;
import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.FileDownloadHis;
import kr.or.btf.web.domain.web.FileInfo;
import kr.or.btf.web.domain.web.enums.ActionType;
import kr.or.btf.web.services.web.ActionLogService;
import kr.or.btf.web.services.web.FileDownloadHisService;
import kr.or.btf.web.services.web.FileInfoService;
import kr.or.btf.web.services.web.PlayLogService;
import kr.or.btf.web.utils.FileUtilHelper;
import kr.or.btf.web.utils.RequestUtil;
import kr.or.btf.web.utils.StringHelper;
import kr.or.btf.web.web.form.ActionLogForm;
import kr.or.btf.web.web.form.FileInfoForm;
import kr.or.btf.web.web.form.PlayLogForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommonController {

    private final FileInfoService fileInfoService;
    private final FileDownloadHisService fileDownloadHisService;
    private final ActionLogService actionLogService;
    private final PlayLogService playLogService;

    //private final NoticeService noticeService;
    @RequestMapping("/access-denied")
    public String accessDenied() {
        return "/access-denied";
    }

    /**
     * 준비중
     * @return
     */
    @RequestMapping("/pages/preparing")
    public String preparing(Model model) {
        model.addAttribute("mc", "customerCenter");
        model.addAttribute("pageTitle", "준비중");
        return "/pages/preparing";
    }

    /*@RequestMapping("/error")
    public String error() {
        return "/error";
    }*/

    @ResponseBody
    @PostMapping("/api/common/file/delete")
    public String deleteFile(Model model,
                             @RequestBody FileInfoForm fileInfoForm,
                             @CurrentUser Account account,
                             HttpServletResponse response) throws Exception {

        boolean result = fileInfoService.delete(fileInfoForm);
        String msg = "fail";
        if (result) {
            msg = "ok";
            response.setStatus(200);
        } else {
            msg = "fail";
            response.setStatus(401);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        return msg;
    }

    @ResponseBody
    @PostMapping("/api/common/file/list")
    public List<FileInfo> fileList(Model model,
                                   @RequestBody FileInfoForm fileInfoForm,
                                   @CurrentUser Account account,
                                   HttpServletResponse response) throws Exception {

        List<FileInfo> list = fileInfoService.list(fileInfoForm);

        return list;
    }

    @GetMapping(value = "/api/common/download")
    public void fileDownLoad(Model model,
                             @RequestParam(name = "path") String path,
                             @RequestParam(name = "name") String name,
                             @CurrentUser Account account,
                             HttpServletRequest request, HttpServletResponse response) {

        if (account == null) {
            returnMessage(response, "로그인 후 이용 가능합니다.");
            return;
        }
        name = URLDecoder.decode(name);
        FileInfo load = fileInfoService.findByFlPthAndChgFlNm(path, name);
        if (load != null && load.getId() != null) {

            FileDownloadHis fileDownloadHis = new FileDownloadHis();
            fileDownloadHis.setFlPid(load.getId());
            fileDownloadHis.setCnctIp(StringHelper.getClientIP(request));
            fileDownloadHis.setMberPid(account.getId());
            fileDownloadHisService.save(fileDownloadHis);

            request.setAttribute("downFile", load.getChgFlNm());
            request.setAttribute("orgFileName", load.getFlNm());
            try {
                FileUtilHelper.download(request, response, path);
            } catch (Exception e) {
                e.printStackTrace();
                returnMessage(response, "잘못된 접근입니다.");
            }
        } else {
            returnMessage(response, "잘못된 접근입니다.");
            return;
        }
    }

    @RequestMapping("/popup/video")
    public String video(Model model,
                        HttpServletRequest request,
                        @Value("${Globals.fileStoreUriPath}") String filePath,
                        /*@PathVariable(name = "par") String par,*/
                        @CurrentUser Account account) throws Exception {


        model.addAttribute("par", filePath+"/courseItem/강사양성교육영상 1차시.mp4");

        return "/popup/video";
    }

    private void returnMessage(HttpServletResponse response, String msg) {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.println("<script>" +
                    "alert('" + msg + "');" +
                    "history.back();" +
                    "</script>");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @PostMapping("/api/openData/extraLink")
    public boolean extraLink(Model model,
                             @RequestBody ActionLogForm actionLogForm,
                             @CurrentUser Account account,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        if (account != null && account.getId() != null) {
            actionLogForm.setActDvTy(ActionType.EXTRA_LINK);
            actionLogForm.setCnctIp(RequestUtil.getClientIp(request));
            actionLogForm.setMberPid(account.getId());
            return actionLogService.insert(actionLogForm);
        }

        return false;
    }

    @ResponseBody
    @PostMapping("/api/openData/viewVideo")
    public boolean viewVideo(Model model,
                             @RequestBody PlayLogForm playLogForm,
                             @CurrentUser Account account,
                             @Value("${Globals.domain.full}") String serviceUrl,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        if (account != null && account.getId() != null) {
            String referrer = request.getHeader("Referer");
            if (referrer != null) {
                referrer = referrer.replace(serviceUrl, "");
                playLogForm.setCnctUrl(referrer);
            }
            playLogForm.setCnctIp(RequestUtil.getClientIp(request));
            playLogForm.setMberPid(account.getId());

            return playLogService.insert(playLogForm);
        }

        return false;
    }

    @ResponseBody
    @RequestMapping("/api/common/ckeditor/fileUpload")
    public String ckeditorFileUpload(@RequestParam(name = "upload") MultipartFile uploadFile,
                                     @Value("${Globals.fileStoreUriPath}") String filePath,
                                     HttpServletRequest request) throws Exception {

        FileInfo fileInfo = FileUtilHelper.writeUploadedFile(uploadFile, Constants.FOLDERNAME_EDITOR);

        if (fileInfo != null) {
            String domain = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
            return "{\"fileName\":\""+fileInfo.getChgFlNm()+"\",\"uploaded\":1, \"url\":\"" + domain+filePath+"/"+Constants.FOLDERNAME_EDITOR+"/"+fileInfo.getChgFlNm() + "\"}";
        } else {
            return null;
        }
    }
}
