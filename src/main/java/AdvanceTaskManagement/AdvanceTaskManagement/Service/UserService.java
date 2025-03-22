package AdvanceTaskManagement.AdvanceTaskManagement.Service;

import AdvanceTaskManagement.AdvanceTaskManagement.Dto.UserDTO;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.Comment;
import AdvanceTaskManagement.AdvanceTaskManagement.Entity.User;
import AdvanceTaskManagement.AdvanceTaskManagement.Repository.UserRepository;
import AdvanceTaskManagement.AdvanceTaskManagement.Response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setDeleted(true);

        userRepository.save(user);
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        if (user.isDeleted()) {
            throw new IllegalStateException("Cannot update a deleted user.");
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setUsername(userDTO.getUsername());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);
        return UserDTO.fromEntity(user);
    }
}