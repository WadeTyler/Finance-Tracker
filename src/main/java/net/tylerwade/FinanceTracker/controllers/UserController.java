package net.tylerwade.FinanceTracker.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.FinanceTracker.models.User;
import net.tylerwade.FinanceTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Iterator;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Signup (CREATE)
    @PostMapping("/signup")
    private @ResponseBody User signup(@RequestBody User user, HttpServletResponse response) {
        if (user.getFirst_name() == null || user.getFirst_name().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user.getLast_name() == null || user.getLast_name().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user.getEmail() == null || user.getEmail().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (user.getPassword() == null || user.getPassword().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // Check if email exists
        Iterable<User> usersList = userRepository.findAllByEmail(user.getEmail());

        // Check Size
        int counter = 0;
        boolean exists = false;
        for (User u : usersList) {
            counter++;
            if (counter > 0) {
                exists = true;
                break;
            }
        }

        if (exists) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        userRepository.save(user);

        // Add Cookie
        Cookie user_idCookie = new Cookie("user_id", user.getUser_id().toString());
        user_idCookie.setMaxAge(604800);
        user_idCookie.setPath("/");
        response.addCookie(user_idCookie);

        return user;
    }

    // Login
    @PostMapping("/login")
    private @ResponseBody User login(@RequestBody User user, HttpServletResponse response) {
        if (user.getEmail() == null || user.getPassword() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Iterable<User> usersList = userRepository.findAllByEmail(user.getEmail());
        // Check Size
        int counter = 0;
        boolean exists = false;
        for (User u : usersList) {
            counter++;
            if (counter > 0) {
                exists = true;
                break;
            }
        }

        if (!exists) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Iterator<User> iterator = usersList.iterator();
        User targetUser = iterator.next();

        if (targetUser.getPassword().equals(user.getPassword())) {
            // Success

            // Add Cookie
            Cookie user_idCookie = new Cookie("user_id", targetUser.getUser_id().toString());
            user_idCookie.setMaxAge(604800);
            user_idCookie.setPath("/");
            response.addCookie(user_idCookie);

            return targetUser;
        }
        // Fail
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    // Logout
    @PostMapping("/logout")
    private @ResponseBody String logout(HttpServletResponse response, @CookieValue("user_id") String user_idCookie) {
        // Check for Cookie
        if (user_idCookie.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // Clear the user_id cookie
        Cookie cookie = new Cookie("user_id", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "Logout Successful";
    }

}
