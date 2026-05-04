package com.whitetower.meridia.service;

import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    final protected static Integer DEFAULT_SIZE = 15;

    @Autowired
    private UserRepository db;

    @Transactional
    public ServiceResponse<Long> createUser(User toStoreEntity){
        List<User> results = db.findUsersByEmail(toStoreEntity.getEmail());
        if (!results.isEmpty()) {
            return new ServiceResponse<>(ServiceResponseType.ENTITY_ALREADY_EXISTS,-1L);
        }
        toStoreEntity.setSizeAvailable(DEFAULT_SIZE);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(13);
        toStoreEntity.setPassword(encoder.encode(toStoreEntity.getPassword()));
        User savedUser = db.saveAndFlush(toStoreEntity);
        return new ServiceResponse<>(ServiceResponseType.OK, savedUser.getId());
    }
}
