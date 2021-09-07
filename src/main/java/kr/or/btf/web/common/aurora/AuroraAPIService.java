package kr.or.btf.web.common.aurora;

import kr.or.btf.web.common.retrofit.ServerAPIs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Service
public class AuroraAPIService {
    @Autowired
    private ServerAPIs serverAPIs;

    public AuroraForm getBase64String(AuroraForm auroraForm) throws IOException {
        AuroraRawData rawData = new AuroraRawData();
        rawData.setRotationType(auroraForm.getRotationType());
        rawData.setPrintText(auroraForm.getPrintText());
        rawData.setEncodeStr(auroraForm.getEncodeStr());
        AuroraForm result = makeQrCodeImg(rawData);
//        byte[] bytes = auroraForm.getAttachedFile().getBytes();
//        if(bytes.length > 0){
//
////          rawData.setEncodeStr(new String(Base64.encodeBase64(bytes)));
//        } else {
//            result.setMsg("통신 에러");
//        }
        return result;
    }

    public AuroraForm makeQrCodeImg(AuroraRawData rawData){
        Call<AuroraForm> call = serverAPIs.uploadMyPhoto(rawData);
        try {
            Response<AuroraForm> response = call.execute();
            if(response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AuroraForm();
    }
}