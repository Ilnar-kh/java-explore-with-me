package ru.practicum.main.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.main.dto.NewUserRequest;
import ru.practicum.main.dto.UserDto;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageRequest)
                    .map(UserMapper::toDto)
                    .getContent();
        }

        return userRepository.findAllByIdIn(ids, pageRequest)
                .map(UserMapper::toDto)
                .getContent();
    }

    @Transactional
    public UserDto registerUser(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email must be unique");
        }
        User user = userRepository.save(UserMapper.toEntity(dto));
        return UserMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }
}