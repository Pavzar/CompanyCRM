package com.pavzar.springboot.companycrm.dao;

import com.pavzar.springboot.companycrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserName(String userName);
    User findByEmail(String email);

}
