package tastetrail_backend.controller;

import java.util.List;
import java.util.Map;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tastetrail_backend.entity.User;
import tastetrail_backend.repository.UserRepository;
import tastetrail_backend.security.JwtService;
import tastetrail_backend.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(
            UserRepository userRepository,
            UserService userService,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }

 // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @PreAuthorize("hasRole('ADMIN')")

    // Update user
    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user) {

        return userService.updateUser(id, user);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    
 // Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
    }
    
   
    
    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam String username,
            @RequestParam String password) {

        User user = userService.login(username, password);

        String token = jwtService.generateToken(user.getUsername());

        return Map.of(
                "message", "Login successful",
                "username", user.getUsername(),
                "token", token
        );
        
        
    }
    
    
    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
    	return userService.createUser(user);
    }
    
    
}