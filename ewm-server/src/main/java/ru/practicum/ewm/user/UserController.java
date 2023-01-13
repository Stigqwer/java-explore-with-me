package ru.practicum.ewm.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping()
@AllArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/admin/users")
    public UserDto createUser(@RequestBody @Valid NewUserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/admin/users")
    public List<UserDto> findAllUser(@RequestParam(name = "ids", required = false) Long[] ids,
                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.findAllUser(ids, from, size);
    }

    @GetMapping("users/{userId}/userRating")
    public List<UserDto> findAllUserWithRating(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return userService.findAllUserWithRating(userId, from, size);
    }
}