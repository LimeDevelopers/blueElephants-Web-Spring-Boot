package kr.or.btf.web.config.security.direct;

import kr.or.btf.web.domain.web.Account;
import kr.or.btf.web.domain.web.CommonCode;
import kr.or.btf.web.domain.web.MemberRoll;
import kr.or.btf.web.repository.web.CommonCodeRepository;
import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.repository.web.MemberRollRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService [유저 디테일 서비스]
 * @author : jerry
 * @version : 1.0.0
 * 작성일 : 2021/08/17
**/
@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final MemberRepository memberRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final MemberRollRepository memberRollRepository;
    
    private Logger log = LoggerFactory.getLogger(getClass()); 
    

    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String loginId) {

        //boolean isEmail = loginId.indexOf("@") > 0 ? true : false;
        //boolean isChk = false;
        String msg = "error.BadCredentials";
        String unAuthMsg = "error.login.email.unAuth";
        String unParentAuthMsg = "error.login.parentEmail.unAuth";

        log.debug("**스프링시큐리터 회원정보조회 loadUserByUsername login_id:{}", loginId);
        CommonCode commonCode = new CommonCode();

        Account user = memberRepository.findByLoginIdAndDelAt(loginId, "N");

        if(user == null){
        	// InternalAuthenticationServiceException UsernameNotFoundException BadCredentialsException AuthenticationException
            //throw new InternalAuthenticationServiceException(msg);
        	//불필요한 Exception 로그가 발생되어 로그인실패에 대한 오류로 변경처리함. 2020.02.20, 
            throw new UsernameNotFoundException(msg);
        }

        List<MemberRoll> allByMberPid = memberRollRepository.findAllByMberPid(user.getId());

        Account byLoginIdAndEmail = memberRepository.findByLoginIdAndEmail(user.getLoginId(), user.getEmail());

//        if(byLoginIdAndEmail == null || !byLoginIdAndEmail.getEmailAttcAt().equals("Y")){
//            throw new LockedException(unAuthMsg);
//        }
        if(byLoginIdAndEmail == null){
            throw new LockedException(unAuthMsg);
        }
        if(!byLoginIdAndEmail.getPrtctorAttcAt().equals("Y")){
            throw new LockedException(unAuthMsg);
        }

        Account account = Account.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nm(user.getNm())
                .pwd(user.getPwd())
                .ncnm(user.getNcnm() == null || "".equals(user.getNcnm()) ? user.getNm() : user.getNcnm())
                .mberDvTy(allByMberPid.get(0).getMberDvTy())
                .authorites(allByMberPid)
                .moblphon(user.getMoblphon())
                .build();

        return new UserDetails(account);
    }

}
