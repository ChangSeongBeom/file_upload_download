package com.example.fileupdownloader.api;

import com.example.fileupdownloader.dto.UploadFileResponseDto;
import com.example.fileupdownloader.dao.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class FileController {

    private static final Logger logger= LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;


    //단건 파일 업로드
    @PostMapping("/uploadFile")
    public UploadFileResponseDto uploadFile(@RequestParam("file") MultipartFile file){
        UploadFileResponseDto uploadFileResponseDto=null;
        try{
            uploadFileResponseDto=fileService.upload(file);
        }catch (IOException ex){
            logger.info("upload file exception:" +ex);
            uploadFileResponseDto=new UploadFileResponseDto("","","",0);
        }
        return uploadFileResponseDto;
    }

    //다중 파일 업로드
    @PostMapping("/uploadFiles")
    public List<UploadFileResponseDto> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files){
        return Arrays.asList(files)
                .stream()
                .map(file->uploadFile(file))
                .collect(Collectors.toList());
    }

    //파일 다운로드
    @GetMapping(path="/download/{fileName:.+}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName){

        try{
            byte[] data=fileService.download(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "utf-8") + "\"")
                    .body(resource);
        }catch (IOException ex){
            logger.info("downloadFile IOException : "+ex);
            return ResponseEntity.badRequest().contentLength(0).body(null);
        }
    }


}

