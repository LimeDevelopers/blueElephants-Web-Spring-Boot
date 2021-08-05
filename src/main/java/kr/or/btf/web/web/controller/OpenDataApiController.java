package kr.or.btf.web.web.controller;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
public class OpenDataApiController extends BaseCont {

    @Value("${open.neis.url}")
    private String openNeisUrl;

    @Value("${open.neis.key}")
    private String openNeisKey;

    private String JSON = "json";

    @ResponseBody
    @GetMapping("/api/openData/schoolInfoToJson")
    public ResponseEntity<String> schoolListToJson(@RequestParam(name = "scName") String scName) {
        String rtnStr = null;
        try {
            // neis : https://open.neis.go.kr/hub/
            // schoolInfo 학교정보
            // classInfo 반정보

            String params = "";
            String urlStr = openNeisUrl + "schoolInfo?";
            params += "KEY=" + openNeisKey;
            params += "&Type=" + JSON;
            //params += "pIndex=" + pIndex;
            //params += "&pSize=" + pSize;
            params += "&SCHUL_NM=" + scName;

            urlStr += URLEncoder.encode(params,"UTF-8");

            rtnStr = openApi(urlStr);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //System.out.println("rtnStr = " + rtnStr);

        //return rtnStr;

        if (rtnStr == null || "".equals(rtnStr)) {
            return null;
        } else {
            JSONObject json = null;
            try {
                json = new JSONObject(rtnStr);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
        }
    }

    @ResponseBody
    @GetMapping("/api/openData/classInfoToJson")
    public ResponseEntity<String> classInfoToJson(@RequestParam(name = "ofCode") String ofCode,
                                  @RequestParam(name = "scCode") String scCode) {
        String rtnStr = null;
        try {
            // neis : https://open.neis.go.kr/hub/
            // schoolInfo 학교정보
            // classInfo 반정보

            String params = "";
            String urlStr = openNeisUrl + "classInfo?";
            params += "KEY=" + openNeisKey;
            params += "&Type=" + JSON;
            //params += "pIndex=" + pIndex;
            //params += "&pSize=" + pSize;
            params += "&ATPT_OFCDC_SC_CODE=" + ofCode;
            params += "&SD_SCHUL_CODE=" + scCode;

            urlStr += URLEncoder.encode(params,"UTF-8");

            rtnStr = openApi(urlStr);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //System.out.println("rtnStr = " + rtnStr);

        //return rtnStr;

        if (rtnStr == null || "".equals(rtnStr)) {
            return null;
        } else {
            JSONObject json = null;
            try {
                json = new JSONObject(rtnStr);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
        }
    }


    private String openApi(String targetUrl) throws Exception{

        StringBuilder rtnStr = new StringBuilder();

        HttpURLConnection urlCon = null;
        try {
            URL url = new URL(targetUrl);
            urlCon = (HttpURLConnection) url.openConnection();

            urlCon.setDefaultUseCaches(false);
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("GET");
            urlCon.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "UTF-8"));

            String returnLine;

            while ((returnLine = br.readLine()) != null) {
                rtnStr.append(returnLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (urlCon != null) urlCon.disconnect();
        }
        return rtnStr.toString();
    }
}