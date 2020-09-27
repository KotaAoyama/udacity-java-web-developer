package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import com.udacity.jwdnd.course1.cloudstorage.service.UserService;
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

    private final UserService userService;

    public HomeController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping()
    public String homeView(Authentication auth, Model model) {
        List<File> files = fileService.getFiles(auth.getName());
        model.addAttribute("files", files);
        return "home";
    }

    @PostMapping("/file/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload,
                             Authentication auth, Model model) {

        String uploadError = null;
        try {
            int rowsAdded = fileService.uploadFile(fileUpload, auth.getName());
            if (rowsAdded < 0) {
                uploadError = "There was sn error uploading file. Please try again.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (uploadError == null) {
            model.addAttribute("uploadSuccess", true);
            System.out.println("upload Success!!!!");
        } else {
            model.addAttribute("uploadError", true);
            System.out.println("upload Fail!!!!!");
        }

        List<File> files = fileService.getFiles(auth.getName());
        model.addAttribute("files", files);

        return "home";
    }

    @GetMapping("/file/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer fileId, Authentication auth) {

        File targetFile = fileService.getFileById(fileId);
        if (Objects.isNull(targetFile)) {
            return null;
        }
        if (!fileService.isFileDownloadable(targetFile, auth.getName())) {
            return null;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        targetFile.getFileName() + "\"")
                .contentType(MediaType.valueOf(targetFile.getContentType()))
                .body(targetFile.getFileData());
    }
}
