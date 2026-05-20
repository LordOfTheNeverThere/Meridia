package com.whitetower.meridia.service;

import com.whitetower.meridia.dto.UserDTO;
import com.whitetower.meridia.enumeration.ServiceResponseType;
import com.whitetower.meridia.model.User;
import com.whitetower.meridia.repository.UserRepository;
import com.whitetower.meridia.util.Security;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final protected static Integer DEFAULT_SIZE = 15;

    @Autowired
    private UserRepository db;

    @Autowired
    private Security security;

    @Transactional
    public ServiceResponse<UserDTO> createUser(User toStoreEntity){
        List<User> results = db.findUsersByEmail(toStoreEntity.getEmail());
        if (!results.isEmpty()) {
            return new ServiceResponse<>(ServiceResponseType.ENTITY_ALREADY_EXISTS,null);
        }
        toStoreEntity.setSizeAvailable(DEFAULT_SIZE);
        toStoreEntity.setPassword(security.getPasswordEncoder().encode(toStoreEntity.getPassword()));
        User savedUser = db.saveAndFlush(toStoreEntity);
        return new ServiceResponse<>(ServiceResponseType.OK, savedUser.toUserDTO());
    }

    public ServiceResponse<String> validateUser(User toValidateEntity) {
        List<User> results = db.findUsersByEmail(toValidateEntity.getEmail());
        if (results.isEmpty() || !results.getFirst().getEmail().equals(toValidateEntity.getEmail()) ||
                !security.getPasswordEncoder().matches(toValidateEntity.getPassword(), results.getFirst().getPassword())){
            return new ServiceResponse<>(ServiceResponseType.ENTITY_NOT_FOUND, "");
        } else {
            return new ServiceResponse<>(ServiceResponseType.OK, security.generateJwe(results.getFirst().getId(), results.getFirst().getName()));
        }
    }

    @Transactional
    public ServiceResponse<UserDTO> delete(Long id) {
        Optional<User> userToDelete = db.findById(id);

        if (userToDelete.isEmpty()) return new ServiceResponse<>(ServiceResponseType.ENTITY_NOT_FOUND , null);
        else {
            UserDTO dtoToSendBack = userToDelete.get().toUserDTO();
            db.delete(userToDelete.get());
            db.flush();
            return new ServiceResponse<>(ServiceResponseType.OK, dtoToSendBack);
        }
    }
}
