package com.udacity.jwdnd.course1.cloudstorage.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("MultipartException");
        return "redirect:/home/file/upload/error";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("MaxUploadSizeExceededException");
        return "redirect:/home/file/upload/error";
    }
}
