package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
public class FileService {
    private final UserService userService;
    private final FileMapper fileMapper;

    public FileService(UserService userService, FileMapper fileMapper) {
        this.userService = userService;
        this.fileMapper = fileMapper;
    }

    public int uploadFile(MultipartFile fileUpload, String userName) throws Exception {
        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        Integer userId = user.getUserId();
        byte[] fileData = null;
        try {
            fileData = fileUpload.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileMapper.insert(new File(
                null,
                fileUpload.getName(),
                fileUpload.getContentType(),
                fileUpload.getSize(),
                userId,
                fileData));
    }
}
