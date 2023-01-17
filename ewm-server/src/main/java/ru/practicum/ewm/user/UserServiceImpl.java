package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.UniqueDataException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userRequest)));
        } catch (DataIntegrityViolationException e) {
            throw new UniqueDataException("Пользователь с таким именем или почтой уже существует");
        }
        return userDto;
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> findAllUser(Long[] ids, Integer from, Integer size) {
        List<UserDto> userDtoList;
        if (ids == null) {
            userDtoList = userRepository.findAll(PageRequest.of(((from) / size), size))
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        } else {
            List<Long> listId = Arrays.asList(ids);
            userDtoList = userRepository.getUserByIdIsIn(listId).stream()
                    .map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        return userDtoList;
    }

    @Override
    public List<UserDto> findAllUserWithRating(Long userId, Integer from, Integer size) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден",
                    userId));
        }
        Pageable pageable = PageRequest.of(((from) / size), size, Sort.by("rating").descending());
        List<User> users = userRepository.findAll(pageable).toList();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
