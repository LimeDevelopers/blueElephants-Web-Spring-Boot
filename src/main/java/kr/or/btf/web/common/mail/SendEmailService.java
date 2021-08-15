package kr.or.btf.web.common.mail;

import kr.or.btf.web.domain.web.dto.MailDto;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

public class SendEmailService {

    private JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "본인의 이메일 주소를 입력하세요.";

}
