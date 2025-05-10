package com.codelab.users.controllers;

import com.codelab.users.dto.CreateUserRequest;
import com.codelab.users.dto.UserResponse;
import com.codelab.users.entities.User;
import com.codelab.users.repositories.RoleRepository;
import com.codelab.users.repositories.UserRepository;
import com.codelab.users.services.UserService;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200")
@RestController()
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    @PostMapping("/users")
    @Transactional
    public ResponseEntity<Void> newUser(@RequestBody CreateUserRequest userReq){
        var basicRole = roleRepository.findById(2L);

        var existentUser = userRepository.findByEmail(userReq.email());
        if(existentUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new User();
        user.setEmail(userReq.email());
        user.setPassword(bCryptPasswordEncoder.encode(userReq.password()));
        user.setRoles(Set.of(basicRole.get()));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MANAGER')")
    @GetMapping("/users")
    public ResponseEntity<PagedModel<UserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "email") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ){
        PagedModel<UserResponse> usersPage = userService.getAllUsers(page, size, sortBy, ascending);
        return ResponseEntity.ok(usersPage);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_MANAGER') or hasAuthority('SCOPE_BASIC')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable String userId
    ){
        UserResponse user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }
}
