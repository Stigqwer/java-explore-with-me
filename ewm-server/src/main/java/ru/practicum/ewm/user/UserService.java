package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest userRequest);

    void deleteUser(long id);

    List<UserDto> findAllUser(Long[] ids, Integer from, Integer size);
}
