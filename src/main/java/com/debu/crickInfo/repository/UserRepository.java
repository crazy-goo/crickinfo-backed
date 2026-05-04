package com.debu.crickInfo.repository;

import com.debu.crickInfo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsernameIgnoreCase(String username);
    boolean existsByRole(String role);
    boolean existsByEmailIgnoreCase(String email);
    java.util.List<User> findByRoleOrderByCreatedAtDesc(String role);
    java.util.List<User> findByRoleAndAccountStatusOrderByCreatedAtDesc(String role, String accountStatus);
    java.util.List<User> findByRoleInOrderByCreatedAtDesc(java.util.Collection<String> roles);
    java.util.List<User> findByRoleInAndAccountStatusOrderByCreatedAtDesc(java.util.Collection<String> roles, String accountStatus);
}
