package kr.or.btf.web.common.aurora;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.or.btf.web.web.form.SearchForm;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class AuroraForm extends SearchForm {
    // Response Data
    @JsonProperty("rspCode")
    private int rspCode;        // 리스폰스 코드
    @JsonProperty("rspCodeMsg")
    private String rspCodeMsg;  // 상태 메세지 : success, fail
    @JsonProperty("rspDebugMsg")
    private String rspDebugMsg;
    @JsonProperty("qrcode")
    private String qrcode;      // Qr 이미지 주소
    @JsonProperty("qrstr")
    private String qrstr;       // 삽입 텍스트
    @JsonProperty("timg")
    private String timg;        // 업로드 (원본) 이미지
    @JsonProperty("uploadId")
    private int uploadId;       // uploadId
    @JsonProperty("rndNo")
    private String rndNo;       //
    @JsonProperty("myphotoType")
    private String myphotoType; // MY_PHOTO
    @JsonProperty("price")
    private int price;          // 금액

    private String rotationType = "HORIZONTAL";
    private String printText;
    private String encodeStr;
    private MultipartFile attachedFile;

    // common
    private String Domain = "https://api.sphokidz.com";
    private String testDomain = "https://stapi.sphokidz.com";
    private String URL = "/uploadMyPhoto/PUCO";
    private String base64;
    private String msg;
    private String type;
    private String gnb;
}
