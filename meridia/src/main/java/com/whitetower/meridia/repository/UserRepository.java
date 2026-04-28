package com.whitetower.meridia.repository;

import com.whitetower.meridia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
