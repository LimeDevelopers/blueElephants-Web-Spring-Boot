package kr.or.btf.web.common.retrofit;

import kr.or.btf.web.common.aurora.AuroraRawData;
import kr.or.btf.web.common.aurora.AuroraForm;
import retrofit2.Call;
import retrofit2.http.*;

public interface ServerAPIs {
    @POST("/uploadMyPhoto/PUCO")
    Call<AuroraForm> uploadMyPhoto(@Body AuroraRawData rawData);
}
