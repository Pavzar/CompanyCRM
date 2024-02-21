package com.pavzar.springboot.companycrm.service;

import com.pavzar.springboot.companycrm.entity.Role;
import com.pavzar.springboot.companycrm.entity.User;
import com.pavzar.springboot.companycrm.user.WebUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

	User findByUserName(String userName);
	List<User> findAll();
	void save(WebUser webUser);
	User findById(int id);
	Collection<Role> findAllRoles();
	void deleteUserById(int id);
	User findByEmail(String email);
	void save(User user);

}
