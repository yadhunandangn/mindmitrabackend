package com.MindMitra.Rolebased.Repository;

import com.MindMitra.Rolebased.Entity.Role;
import com.MindMitra.Rolebased.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<User, Long> {
     Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
   boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    @Query("SELECT u.username FROM User u WHERE u.email = :email")
    Optional<String> findUsernameByEmail(@Param("email") String email);
    List<User> findByRole(Role role);

}
