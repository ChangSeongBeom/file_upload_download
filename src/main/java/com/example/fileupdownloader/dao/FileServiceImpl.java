package com.example.fileupdownloader.dao;

import com.example.fileupdownloader.dto.UploadFileResponseDto;
import com.example.fileupdownloader.exception.FileStorageException;
import com.example.fileupdownloader.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {


    private final Path fileStorageLocation;

    /* FileStroageProperties에 있는 값을 사용하지 않고 직접 적을떄 사용하는 법.
        private final Path fileStorageLocation = Paths.get("/${user.home}/~~")
                .toAbsolutePath()
                .normalize();
    */
    @Autowired

    public FileServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize();


        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }

    }

    @Override
    public UploadFileResponseDto upload(MultipartFile file) {
        UploadFileResponseDto uploadFileResponse = null;
        String fileName =  storeFile(file);
        try {
            if(fileName.equals("")){
                new Exception("The file name does not exist.");
            }else {
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(fileName)
                        .toUriString();
                uploadFileResponse = new UploadFileResponseDto(fileName,fileDownloadUri,file.getContentType(),file.getSize());
            }
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
        return uploadFileResponse;
    }

    @Override
    public byte[] download(String fileKey) throws IOException {
        byte[] data = null;
        try {
            Path path = Paths.get(this.fileStorageLocation.resolve(fileKey).normalize().toString());
            data = Files.readAllBytes(path);
        }catch (IOException ex){
            throw new IOException("IOE Error Message= " + ex.getMessage());
        }
        return data;
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {

            if(fileName.contains("..")) {
                throw new FileStorageException("invalid Path " + fileName);
            }


            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            //같은 이름의 파일이 있으면 붙여넣겠다.
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
