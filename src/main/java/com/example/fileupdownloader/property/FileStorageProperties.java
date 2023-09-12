package com.example.fileupdownloader.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
* uploadDir의 file위치는 아래 코드에서 명시된 prefix값
* prefix="file"이  application.yml의 파일 위치를 따라감.*/
@Component
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
