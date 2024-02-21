package com.pavzar.springboot.companycrm.dao;

import com.pavzar.springboot.companycrm.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findRoleByName(String roleName);

}
