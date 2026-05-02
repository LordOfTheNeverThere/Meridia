package com.whitetower.meridia.repository;

import com.whitetower.meridia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u  WHERE (u.sizeAvailable = :size)")
    List<User> findAllUsersWithSizeAvailable(@Param("size") Integer size);

    @Query("SELECT u FROM User u WHERE (u.email = :email)")
    List<User> findUsersByEmail(@Param("email") String email);
}
