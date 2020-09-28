package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;

    public HomeController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping()
    public String homeView(Authentication auth, Model model) {
        showFiles(auth.getName(), model);
        return "home";
    }

    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload,
                             Authentication auth, Model model) {

        if (fileUpload.getSize() == 0) {
            showFiles(auth.getName(), model);
            return "home";
        }

        String uploadError = null;
        try {
            int rowsAdded = fileService.uploadFile(fileUpload, auth.getName());
            if (rowsAdded < 0) {
                uploadError = "There was an error uploading file. Please try again.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (uploadError == null) {
            model.addAttribute("uploadSuccess", true);
        } else {
            model.addAttribute("uploadError", true);
            model.addAttribute("uploadErrorMessage", uploadError);
        }
        showFiles(auth.getName(), model);

        return "home";
    }

    @GetMapping("/file/upload/error")
    public String uploadError(Authentication auth, Model model) {

        showFiles(auth.getName(), model);
        model.addAttribute("uploadError", true);
        model.addAttribute("uploadErrorMessage", "File size is too large.");

        return "home";
    }

    @GetMapping("/file/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer fileId, Authentication auth) {

        File targetFile = fileService.getFileById(fileId);
        if (Objects.isNull(targetFile) || !fileService.isFileAllowed(targetFile, auth.getName())) {
            return null;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        targetFile.getFileName() + "\"")
                .contentType(MediaType.valueOf(targetFile.getContentType()))
                .body(targetFile.getFileData());
    }

    @GetMapping("/file/{fileId}/delete")
    public String deleteFile(@PathVariable Integer fileId, Authentication auth, Model model) {

        File targetFile = fileService.getFileById(fileId);
        if (Objects.isNull(targetFile) || !fileService.isFileAllowed(targetFile, auth.getName())) {
            return "404";
        }

        String deleteError = null;
        try {
            int rowsDeleted = fileService.deleteFile(fileId);
            if (rowsDeleted < 0) {
                deleteError = "There was an error deleting file. Please try again.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (deleteError == null) {
            model.addAttribute("deleteSuccess", true);
        } else {
            model.addAttribute("deleteError", true);
            model.addAttribute("deleteErrorMessage", deleteError);
        }

        showFiles(auth.getName(), model);
        return "home";
    }


    private void showFiles(String userName, Model model) {
        List<File> files = fileService.getFiles(userName);
        model.addAttribute("files", files);
    }
}
