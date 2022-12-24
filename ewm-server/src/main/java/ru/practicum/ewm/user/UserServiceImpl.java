package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest userRequest) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userRequest)));
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> findAllUser(Long[] ids, Integer from, Integer size) {
        List<UserDto> userDtoList;
        if(ids == null){
            userDtoList = userRepository.findAll(PageRequest.of(((from) / size), size))
                    .stream().map(UserMapper::toUserDto).collect(Collectors.toList());
        } else{
            List<Long> listId = Arrays.asList(ids);
            userDtoList = userRepository.findAll().stream().filter(user -> listId.contains(user.getId()))
                    .map(UserMapper::toUserDto).collect(Collectors.toList());
        }
        return userDtoList;
    }
}
