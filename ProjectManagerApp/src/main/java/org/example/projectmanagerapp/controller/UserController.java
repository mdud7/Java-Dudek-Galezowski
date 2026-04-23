package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.User;
import org.example.projectmanagerapp.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operacje na uzytkownikach")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Pobierz wszystkich uzytkownikow",
            description = "Zwraca liste wszystkich uzytkownikow zapisanych w systemie"
    )
    public List<User> getAll() {
        return userService.getAll();
    }

    @PostMapping
    @Operation(
            summary = "Utworz uzytkownika",
            description = "Tworzy nowego uzytkownika na podstawie danych przeslanych w zadaniu"
    )
    public User create(@RequestBody User user) {
        return userService.create(user);
    }
}
