package kr.or.btf.web.common.intercepters;

import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.enums.ActionType;
import kr.or.btf.web.services.web.ActionLogService;
import kr.or.btf.web.services.web.MemberService;
import kr.or.btf.web.utils.RequestUtil;
import kr.or.btf.web.web.form.ActionLogForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


// intercepter 에서는 404오류(/error) 사전에 체크하기가 어려움
public class CommonIntercepter implements HandlerInterceptor {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	MemberService memberService;

	@Autowired
    ActionLogService actionLogService;

    private String serviceUrl;

    public CommonIntercepter(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    //컨트롤러(즉 RequestMapping이 선언된 메서드 진입) 실행 직전에 동작. true정상실행, false 실행멈춤.
    //Object handler 핸들러 매핑이 찾은 컨트롤러 클래스 객체
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //response.setHeader("Cache-Control", "max-age=60");// 60seconds
    	//log.debug("================= preHandle1 :{}, url:{}", handler.getClass(), request.getRequestURI());
        HttpSession session = request.getSession();

        String requestURI = request.getRequestURI(); //요청 URI

        String userAgent = RequestUtil.getUserAgent(request);;
        request.setAttribute("isMobile", (userAgent.indexOf("BTF_APP") > 0) || RequestUtil.isMobile(request));
        request.setAttribute("browser", RequestUtil.getBrowser(request));


        if (SecurityContextHolder.getContext() != null
                && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
                && !"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())) {
            try {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (user != null && user.getUsername() != null && requestURI.startsWith("/page")) {
                    String username = user.getUsername();
                    Account account = memberService.loadByLoginId(username);
                    ActionLogForm actionLogForm = new ActionLogForm();
                    actionLogForm.setActDvTy(ActionType.INNER_LINK);
                    actionLogForm.setCnctIp(RequestUtil.getClientIp(request));
                    actionLogForm.setCnctUrl(requestURI);
                    actionLogForm.setMberPid(account.getId());
                    actionLogService.insert(actionLogForm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    
    //컨트롤러 진입 후 view가 랜더링 되기 전 수행이 됩니다.
	@Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //String userAgent = RequestUtil.getUserAgent(request);
        //request.setAttribute("isMobile", (userAgent.indexOf("BTF_APP") > 0));
    }

	//컨트롤러 진입 후 view가 정상적으로 랜더링 된 후 제일 마지막에 실행이 되는 메서드입니다.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {    	
        //log.debug("================ afterCompletion :{}, url:{}", handler.getClass(), request.getRequestURI());
    }
}
