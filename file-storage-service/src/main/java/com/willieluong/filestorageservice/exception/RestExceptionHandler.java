package com.willieluong.filestorageservice.exception;

import com.willieluong.filestorageservice.model.UploadResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 *
 *
 * The class annotated with @ControllerAdvice is responsible for handling specific exceptions that may occur during uploading/downloading files.
 * RestExceptionHandler class beside the special annotation should also extend RestExceptionHandler.
 * To handle the exception when uploading too large files we need to handle MaxUploadSizeExceededException like in the following:
 *
 *
 *
 **/
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<UploadResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new UploadResponseMessage("Unable to upload. File is too large!"));
    }
}
