package com.willieluong.filestorageservice.controller;

import com.willieluong.filestorageservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile file){
        return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName){
        byte[] data = storageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok().contentLength(data.length).header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(String fileName){
        return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
    }


}
