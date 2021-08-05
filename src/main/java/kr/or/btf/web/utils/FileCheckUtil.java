package kr.or.btf.web.utils;

import kr.or.btf.web.domain.web.enums.FileType;
import kr.or.btf.web.domain.web.enums.ResultMessageType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class FileCheckUtil {

    private static String[] notAllowExt = new String[]{".js", ".jsp", ".php", ".php3", ".asp", ".aspx", ".cer", ".cdx", ".asa", ".war", ".jar"};

    private void FileCheckUtil() {}

    public static ResultMessageType checkFile(FileType fileType, Boolean isRequired, MultipartFile ...multipartFiles) {
        if (multipartFiles == null) {
            return (isRequired ? ResultMessageType.FAIL : ResultMessageType.SUCCESS);
        }

        ResultMessageType result = ResultMessageType.SUCCESS;

        try {
            for (MultipartFile file : multipartFiles) {
                if (file != null && !file.isEmpty()) {
                    String fileNm = file.getOriginalFilename();
                    for (String e : notAllowExt) {
                       if (fileNm.contains(e)) {
                           result = ResultMessageType.FAIL;
                           break;
                       }
                    }
                    String ext = FileUtilHelper.getFileExtension(fileNm);
                    ext = ext.replace(".", "");
                    if (fileType.getAllowExt() == null || !fileType.getAllowExt().contains(ext)) {
                        result = ResultMessageType.NOT_ALLOW_EXT;
                        break;
                    }
                    if (file.getSize() > fileType.getFileMaxSize()) {
                        result = ResultMessageType.FILE_SIZE_OVER;
                        break;
                    }
                    if (fileType.getWidthMin() > 0 && fileType.getWidthMax() > 0) {
                        BufferedImage image = ImageIO.read(file.getInputStream());
                        Integer width = image.getWidth();
                        if (width < fileType.getWidthMin() || width > fileType.getWidthMax()) {
                            result = ResultMessageType.IMAGE_SIZE_OVER;
                            break;
                        }
                    }
                    if (fileType.getHeightMin() > 0 && fileType.getHeightMax() > 0) {
                        BufferedImage image = ImageIO.read(file.getInputStream());
                        Integer height = image.getHeight();
                        if (height < fileType.getHeightMin() || height > fileType.getHeightMax()) {
                            result = ResultMessageType.IMAGE_SIZE_OVER;
                            break;
                        }
                    }
                } else {
                    result = (isRequired ? ResultMessageType.FAIL : ResultMessageType.SUCCESS);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMessageType.FAIL;
        }

        return result;
    }
}
