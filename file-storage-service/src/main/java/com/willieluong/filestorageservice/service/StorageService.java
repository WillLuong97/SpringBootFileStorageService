package com.willieluong.filestorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    private AmazonS3 s3Client;

    //Upload a file onto the s3 bucket
    public String uploadFile(MultipartFile file){
        File fileObj = convertMultiPartToFileObject(file);
        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
        //put the object into the s3 bucket
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        //delete the file once it has been persisted to the bucket
        fileObj.delete();

        return "File uploaded: " + fileName;
    }

    //Download a file from the s3 bucket
    public byte[] downloadFile(String fileName){
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //method to delete a file from the bucket
    public String deleteFile(String fileName){
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";

    }




    //Method to convert multipart file object into a file object
    private File convertMultiPartToFileObject(MultipartFile file){
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        } catch (IOException e){
            log.error("Error converting multipart file to file ", e);
        }
        return convertedFile;
    }
}
