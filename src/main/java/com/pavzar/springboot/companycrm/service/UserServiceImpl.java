package com.pavzar.springboot.companycrm.service;


import com.pavzar.springboot.companycrm.dao.RoleRepository;
import com.pavzar.springboot.companycrm.dao.UserRepository;
import com.pavzar.springboot.companycrm.entity.Role;
import com.pavzar.springboot.companycrm.entity.User;
import com.pavzar.springboot.companycrm.user.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;



    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUserName(String userName) {
        // check the database if the user already exists
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Override
    public User findById(int id) {
        Optional<User> result = userRepository.findById(id);
        User user;

        if (result.isPresent()) {
            user = result.get();
        } else {
            throw new RuntimeException("Did not find user with id - " + id);
        }

        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public void save(WebUser webUser) {
        User user = new User();

        // assign user details to the user object
        user.setUserName(webUser.getUserName());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setFirstName(webUser.getFirstName());
        user.setLastName(webUser.getLastName());
        user.setEmail(webUser.getEmail());
        user.setEnabled(true);

        // give user default role of "employee"
        user.setUserRoles(Set.of(roleRepository.findRoleByName("ROLE_EMPLOYEE")));

        // save user in the database
        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Collection<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public void deleteUserById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getUserRoles());

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return new ArrayList<>(roles.stream()
                .map(tempRole -> new SimpleGrantedAuthority(tempRole.getName()))
                .toList());
    }
}
