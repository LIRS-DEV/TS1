package org.sde.login.controller;

import org.sde.login.controller.response.UserResponse;
import org.sde.login.model.entity.LoginUser;
import org.sde.login.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

@RestController
public class LoginController {

	@Autowired
	private UserRepository userRepository;
	private static String SALT = BCrypt.gensalt();

	@PostMapping("/user/register")
	public ResponseEntity<UserResponse> register(@RequestBody LoginUser user) {
		if (user == null || user.getUsername() == null || user.getUsername() == "" || user.getPassword() == null
				|| user.getPassword() == "") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are mandatory");
		} else {
			user.setPassword(BCrypt.hashpw(user.getPassword(), SALT));
			user.setSalt(SALT);
			user = userRepository.save(user);
			return new ResponseEntity<>(new UserResponse(user), HttpStatus.CREATED);
		}
	}

	@DeleteMapping("/user/{id}")
	public ResponseEntity<String> register(@PathVariable Long id) {
		if (id == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is mandatory");

		} else {
			Optional<LoginUser> optionalUser = userRepository.findById(id);
			if (optionalUser.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, "User does not exist");

			} else {
				UserResponse user = new UserResponse(optionalUser.get());
				userRepository.deleteById(id);
				return new ResponseEntity<>(
						String.format("%s correctly deleted (ID %d)", user.getUsername(), user.getId()), HttpStatus.OK);
			}
		}
	}

	@PostMapping("/user/login")
	public ResponseEntity<UserResponse> login(@RequestBody LoginUser user) {
		if (user == null || user.getUsername() == null || user.getUsername() == "" || user.getPassword() == null
				|| user.getPassword() == "") {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are mandatory");

		} else {
			// Optional<LoginUser> optionalUser =
			// userRepository.findByUsernameAndPassword(user.getUsername(),user.getSalt());
			Optional<LoginUser> optionalUser = userRepository.findByUsername(user.getUsername());
			if (optionalUser.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username does not exist");
			} else {
				LoginUser loggedUser = optionalUser.get();
				if (loggedUser.getPassword().equals(BCrypt.hashpw(user.getPassword(), loggedUser.getSalt()))) {
					loggedUser.setLastLogin(Instant.now());
					loggedUser = userRepository.save(loggedUser);
					return new ResponseEntity<>(new UserResponse(loggedUser), HttpStatus.OK);
				}else {
					throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password not valid");
				}
			}

		}
	}
}
