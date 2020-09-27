package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final FileService fileService;

    public HomeController(FileService fileService) {
        this.fileService = fileService;
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
}
