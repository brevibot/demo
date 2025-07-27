package com.example.demo;

import com.example.demo.db1.model.User;
import com.example.demo.db1.repo.UserRepository;
import com.example.demo.db2.model.Product;
import com.example.demo.db2.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * A public endpoint that can be called to establish a session and receive the initial CSRF token cookie.
     * Any initial GET request to a secured Spring endpoint will do this.
     */
    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        return Map.of("message", "Hello! CSRF protection is active. The XSRF-TOKEN cookie has been set.");
    }

    /**
     * A protected endpoint that requires a valid CSRF token in the header for POST requests.
     * Spring Security will automatically validate the token.
     */
    @PostMapping("/secure-data")
    public Map<String, String> postSecureData(@RequestBody MessagePayload payload) {
        // If the request reaches this point, the CSRF token was valid.
        return Map.of("status", "SUCCESS", "received", payload.content());
    }

    // A simple record to represent the JSON payload for the POST request.
    public record MessagePayload(String content) {}
}