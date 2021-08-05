package kr.or.btf.web.web.controller.pages;


import kr.or.btf.web.common.annotation.CurrentUser;
import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.services.web.CourseHisService;
import kr.or.btf.web.web.controller.BaseCont;
import kr.or.btf.web.web.form.CourseHisForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CourseHisController extends BaseCont {

    private final CourseHisService courseHisService;

    @PostMapping("/api/v1/videoProgressProc")
    public ResponseEntity<?> save(Model model,
                               @RequestBody CourseHisForm courseHisForm,
                               @CurrentUser Account account) {

        Map<String, Object> resultMap = new HashMap<>();

        courseHisForm.setAtnlcDtm(LocalDateTime.now());
        courseHisForm.setMberPid(account.getId());
        boolean result = courseHisService.videoProgressProc(courseHisForm);

        String msg = "fail";
        if (!result) {
            resultMap.put("msg", "fail");
            return ResponseEntity.badRequest().body(resultMap);
        } else {
            resultMap.put("msg", "ok");
            return ResponseEntity.ok().body(resultMap);
        }
    }

}
