package com.mng.ftptest.service;


import com.mng.ftptest.utils.RemoteFileUtil;
import org.springframework.stereotype.Service;

@Service
public class FtpService {
    public boolean uploadFile(String filePath, String context) {
        return RemoteFileUtil.uploadFile(filePath, context);
    }

}
