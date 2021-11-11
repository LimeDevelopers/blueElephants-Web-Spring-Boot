package kr.or.btf.web.services.web;

import kr.or.btf.web.domain.web.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${Globals.domain.full}")
    private String domain;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String FROM_ADDRESS = "puco9128@naver.com";

    public void mailSend(Mail mail) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        InternetAddress from = new InternetAddress();

        from = new InternetAddress("푸른코끼리<puco9128@naver.com>");
        helper.setFrom(from);
        //메일 세팅
        //helper.setFrom(new InternetAddress(MailService.FROM_ADDRESS));
        helper.setTo(mail.getAddress());
        helper.setSubject(mail.getTitle());
        //helper.setText(mail.getMessage());

        //파라메터 세팅
        Context context = new Context();
        Map<String, Object> map = mail.getContext();
        map.put("domain", domain);
        context.setVariables(map);
        //템플릿 세팅
        String html = templateEngine.process(mail.getTemplate(), context);
        helper.setText(html, true);

        //메일 발송
        mailSender.send(message);
    }
}
