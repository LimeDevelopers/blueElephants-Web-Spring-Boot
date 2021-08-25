package kr.or.btf.web.common.aurora;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class AuroraRawData {
    // RequestBody
    private String rotationType;
    private String printText;
    private String encodeStr;

    public AuroraRawData(String rotationType, String printText, String encodeStr){
        this.rotationType = rotationType;
        this.printText = printText;
        this.encodeStr = encodeStr;
    }

    public AuroraRawData() {

    }
}
