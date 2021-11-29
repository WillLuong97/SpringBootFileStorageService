package com.willieluong.filestorageservice.model;

/**
 *
 * UploadResponseMessage: will be used to return information about how uploading process ran
 * **/
public class UploadResponseMessage {

    private final String responseMessage;

    public UploadResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
