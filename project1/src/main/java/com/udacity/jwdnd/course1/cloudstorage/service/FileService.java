package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {
    private final UserService userService;
    private final FileMapper fileMapper;

    public FileService(UserService userService, FileMapper fileMapper) {
        this.userService = userService;
        this.fileMapper = fileMapper;
    }

    public List<File> getFiles(String userName) {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            return null;
        }

        return fileMapper.getFiles(user.getUserId());
    }

    public File getFileById(Integer fileId) {
        return fileMapper.getFileById(fileId);
    }

    public int uploadFile(MultipartFile fileUpload, String userName) throws Exception {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        byte[] fileData = null;
        try {
            fileData = fileUpload.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileMapper.insert(new File(
                null,
                fileUpload.getOriginalFilename(),
                fileUpload.getContentType(),
                fileUpload.getSize(),
                user.getUserId(),
                fileData));
    }

    public int deleteFile(Integer fileId) {
        return fileMapper.delete(fileId);
    }

    public boolean isFileNotAllowed(File targetFile, String userName) throws Exception {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        return targetFile.getUserId() != user.getUserId();
    }

    public boolean isFileDuplicated(MultipartFile targetFile, String userName) throws Exception {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }
        List<File> files = fileMapper.getFiles(user.getUserId());

        return files.stream().anyMatch(file -> file.getFileName().equals(targetFile.getOriginalFilename()));
    }
}
