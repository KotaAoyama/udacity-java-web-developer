package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class CredentialService {

    private final UserService userService;
    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(UserService userService,
                             EncryptionService encryptionService,
                             CredentialMapper credentialMapper) {
        this.userService = userService;
        this.encryptionService = encryptionService;
        this.credentialMapper = credentialMapper;
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

        Credential tempCredential = getCredentialWithEncryptedPasswordAndKey(password);

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


        // TODO!!!!


        return credentialMapper.update(credential);
    }

    public boolean isCredentialNotAllowed(Credential targetCredential, String userName) throws Exception {
        User user = userService.getUser(userName);
        if (Objects.isNull(user)) {
            throw new Exception(String.format("User is Not Found by the userName, %s", userName));
        }

        return targetCredential.getUserId() != user.getUserId();
    }

    private Credential getCredentialWithEncryptedPasswordAndKey(String password) {

        String key = getSecureKey();

        return new Credential(
                null,
                null,
                null,
                key,
                encryptionService.encryptValue(password, key),
                null);
    }

    private String getSecureKey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128); /* 128-bit AES */
            SecretKey secret = gen.generateKey();
            byte[] binary = secret.getEncoded();
            return String.format("%032X", new BigInteger(+1, binary));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "secureKey";
    }
}
