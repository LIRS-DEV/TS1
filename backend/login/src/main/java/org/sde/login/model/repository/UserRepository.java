package org.sde.login.model.repository;

import java.util.Optional;

import org.sde.login.model.entity.LoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<LoginUser, Long> {
	Optional<LoginUser> findByUsernameAndPassword(String username, String password);
	Optional<LoginUser> findByUsername(String username);
}
