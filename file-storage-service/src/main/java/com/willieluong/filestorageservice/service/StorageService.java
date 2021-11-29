package com.willieluong.filestorageservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    private AmazonS3 s3Client;

    @Value("${directory.path}")
    private String uploadPath;

    /** S3 BUCKET SERVICE **/
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


    /** LOCAL FILESYSTEM File storage service **/

    //create a post contruct method to create a specific directory to store files
    //during the spring boot app runtime
    @PostConstruct
    public void init(){
        try{
            Files.createDirectories(Paths.get(uploadPath));
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create upload folder");
        }
    }

    //method to save a Multipart file into the internal filesystem
    public String uploadToLocal(MultipartFile file){
        try{
            Path root = Paths.get(uploadPath);
            if(!Files.exists(root)){
                init();
            }
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
            return "Files uploaded to path: " + uploadPath;
        } catch (Exception e) {
            log.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not save file to the directory");
        }

    }

    //method to load the file stored from the local filesystem from the name of the file string
    public Resource loadFromLocal(String fileName){
        try{
            Path file = Paths.get(uploadPath).resolve(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read the file");
            }

        } catch (MalformedURLException e) {
            log.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not retrieve file from give filename");
        }
    }

    //method to delete the directory with all file in it
    public String deleteAll() {
        try{
            FileSystemUtils.deleteRecursively(Paths.get(uploadPath)
                    .toFile());
            return "successfully delete all directory";
        } catch (Exception e){
            log.info("Error: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not delete the directory!");

        }
    }

    //method to load all files currently in the directory
    public List<Path> loadAll() {
        try {
            Path root = Paths.get(uploadPath);
            if (Files.exists(root)) {
                return Files.walk(root, 1)
                        .filter(path -> !path.equals(root))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }


}
