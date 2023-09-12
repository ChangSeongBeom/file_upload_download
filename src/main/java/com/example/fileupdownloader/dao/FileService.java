package com.example.fileupdownloader.dao;

import com.example.fileupdownloader.dto.UploadFileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    UploadFileResponseDto upload(MultipartFile file) throws IOException;

    byte[] download(String fileKey) throws IOException;
}
