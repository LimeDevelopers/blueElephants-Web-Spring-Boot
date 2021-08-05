package kr.or.btf.web.web.validator;

import kr.or.btf.web.repository.web.MemberRepository;
import kr.or.btf.web.web.form.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MemberFormValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(MemberForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MemberForm userForm = (MemberForm)o;

        if(memberRepository.existsByLoginId(userForm.getLoginId())){
            errors.rejectValue("loginId", "invalid ID", new Object[]{userForm.getLoginId()}, "이미 사용중인 아이디입니다.");
        }
        if(memberRepository.existsByEmailAndEmailAttcAt(userForm.getEmail(),"Y")){
            errors.rejectValue("email", "invalid EMAIL", new Object[]{userForm.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }

    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }

        return validatorResult;
    }

}
