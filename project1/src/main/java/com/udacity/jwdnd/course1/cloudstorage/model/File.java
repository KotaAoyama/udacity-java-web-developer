package com.udacity.jwdnd.course1.cloudstorage.model;

import java.util.Arrays;
import java.util.Objects;

public class File {

    private Integer fileId;
    private String fileName;
    private String contentType;
    private long fileSize;
    private Integer userId;
    private byte[] fileData;

    public File(Integer fileId, String fileName, String contentType, long fileSize, Integer userId, byte[] fileData) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.userId = userId;
        this.fileData = fileData;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return fileSize == file.fileSize &&
                Objects.equals(fileId, file.fileId) &&
                Objects.equals(fileName, file.fileName) &&
                Objects.equals(contentType, file.contentType) &&
                Objects.equals(userId, file.userId) &&
                Arrays.equals(fileData, file.fileData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileId, fileName, contentType, fileSize, userId);
        result = 31 * result + Arrays.hashCode(fileData);
        return result;
    }

    @Override
    public String toString() {
        return "File{" +
                "fileId=" + fileId +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", userId=" + userId +
                ", fileData=" + Arrays.toString(fileData) +
                '}';
    }
}
