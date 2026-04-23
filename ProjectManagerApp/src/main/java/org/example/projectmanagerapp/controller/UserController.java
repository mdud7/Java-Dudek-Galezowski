package org.example.projectmanagerapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.projectmanagerapp.entity.User;
import org.example.projectmanagerapp.service.UserService;
import org.springframework.http.HttpStatus;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista uzytkownikow zostala pobrana poprawnie")
    })
    public List<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Pobierz uzytkownika po ID",
            description = "Zwraca dane uzytkownika o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uzytkownik zostal pobrany poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono uzytkownika o podanym identyfikatorze")
    })
    public User getById(@Parameter(description = "Identyfikator uzytkownika", required = true) @PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @Operation(
            summary = "Utworz uzytkownika",
            description = "Tworzy nowego uzytkownika na podstawie danych przeslanych w zadaniu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uzytkownik zostal utworzony poprawnie")
    })
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Zaktualizuj uzytkownika",
            description = "Aktualizuje dane istniejacego uzytkownika o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uzytkownik zostal zaktualizowany poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono uzytkownika o podanym identyfikatorze")
    })
    public User update(
            @Parameter(description = "Identyfikator uzytkownika", required = true) @PathVariable Long id,
            @RequestBody User user
    ) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Usun uzytkownika",
            description = "Usuwa uzytkownika o wskazanym identyfikatorze"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Uzytkownik zostal usuniety poprawnie"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono uzytkownika o podanym identyfikatorze")
    })
    public void delete(@Parameter(description = "Identyfikator uzytkownika", required = true) @PathVariable Long id) {
        userService.delete(id);
    }
}
