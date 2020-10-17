package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CredentialService {

    private final UserService userService;
    private final CredentialMapper credentialMapper;

    public CredentialService(UserService userService, CredentialMapper credentialMapper) {
        this.userService = userService;
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

        return credentialMapper.insert(new Credential(
                null,
                url,
                username,
                null,
                password,
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
}
