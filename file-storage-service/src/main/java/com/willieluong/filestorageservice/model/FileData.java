package com.willieluong.filestorageservice.model;

/**
 * FileData: contains fields like file name, url(link to download the file), and size
 * **/

public class FileData {
    private String fileName;
    private String url;
    private Long size;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
