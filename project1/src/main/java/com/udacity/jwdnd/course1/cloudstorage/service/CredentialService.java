package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class CredentialService {

    private final UserService userService;
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(UserService userService, CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.userService = userService;
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public List<Credential> getCredentials(String userName) {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new RuntimeException(String.format("User is Not Found by the userName, %s", userName));
        }

        return credentialMapper.getCredentials(user.getUserId());
    }

    public int createCredential(String url,
                                String username,
                                String password,
                                String userName) {

        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new RuntimeException(String.format("User is Not Found by the userName, %s", userName));
        }

        Credential tempCredential = encryptPassword(password);

        return credentialMapper.insert(new Credential(
                null,
                url,
                username,
                tempCredential.getKey(),
                tempCredential.getPassword(),
                user.getUserId()
        ));
    }

    public Credential getCredentialById(Integer credentialId) {
        return credentialMapper.getCredentialById(credentialId);
    }

    public int deleteCredential(Integer credentialId) {
        return credentialMapper.delete(credentialId);
    }

    public int updateCredential(Credential credential) {
        return credentialMapper.update(credential);
    }

    public boolean isCredentialNotAllowed(Credential targetCredential, String userName) throws Exception {
        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        return targetCredential.getUserId() != user.getUserId();
    }

    private Credential encryptPassword(String password) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String key = Base64.getEncoder().encodeToString(salt);

        return new Credential(
                null,
                null,
                null,
                key,
                encryptionService.encryptValue(password, key),
                null);
    }

    public String decryptPassword(String password, Integer credentialId) throws Exception {

        Credential targetCredential = credentialMapper.getCredentialById(credentialId);
        if (Objects.isNull(targetCredential)) {
            throw new Exception("Credential is Not Found.");
        }

        return encryptionService.decryptValue(password, targetCredential.getKey());
    }
}
