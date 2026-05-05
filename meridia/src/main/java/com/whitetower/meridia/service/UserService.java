package com.whitetower.meridia.service;

import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import com.whitetower.meridia.util.Security;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    final protected static Integer DEFAULT_SIZE = 15;

    @Autowired
    private UserRepository db;

    @Autowired
    private Security security;

    @Transactional
    public ServiceResponse<Long> createUser(User toStoreEntity){
        List<User> results = db.findUsersByEmail(toStoreEntity.getEmail());
        if (!results.isEmpty()) {
            return new ServiceResponse<>(ServiceResponseType.ENTITY_ALREADY_EXISTS,-1L);
        }
        toStoreEntity.setSizeAvailable(DEFAULT_SIZE);
        toStoreEntity.setPassword(security.getPasswordEncoder().encode(toStoreEntity.getPassword()));
        User savedUser = db.saveAndFlush(toStoreEntity);
        return new ServiceResponse<>(ServiceResponseType.OK, savedUser.getId());
    }

    public ServiceResponse<String> validateUser(User toValidateEntity) {
        List<User> results = db.findUsersByEmail(toValidateEntity.getEmail());
        if (results.isEmpty() || !results.getFirst().getEmail().equals(toValidateEntity.getEmail()) ||
                !security.getPasswordEncoder().matches(toValidateEntity.getPassword(), results.getFirst().getPassword())){
            return new ServiceResponse<>(ServiceResponseType.ENTITY_NOT_FOUND, "");
        } else {
            return new ServiceResponse<>(ServiceResponseType.OK, security.generateToken(results.getFirst().getId(), results.getFirst().getName()));
        }
    }
}
