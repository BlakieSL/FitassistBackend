package com.example.simplefullstackproject.Services;

import com.example.simplefullstackproject.Dtos.UserDto;
import com.example.simplefullstackproject.Dtos.UserRequest;
import com.example.simplefullstackproject.Dtos.UserResponse;
import com.example.simplefullstackproject.Dtos.UserUpdateRequest;
import com.example.simplefullstackproject.Models.Role;
import com.example.simplefullstackproject.Models.User;
import com.example.simplefullstackproject.Repositories.RoleRepository;
import com.example.simplefullstackproject.Repositories.UserRepository;
import com.example.simplefullstackproject.Services.Mappers.UserDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationHelper validationHelper;
    private final CalculationsHelper calculationsHelper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserDtoMapper userDtoMapper,
                       PasswordEncoder passwordEncoder,

                       ValidationHelper validationHelper,
                       CalculationsHelper calculationsHelper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userDtoMapper = userDtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.validationHelper = validationHelper;
        this.calculationsHelper = calculationsHelper;
    }
    private Optional<UserDto> findUserCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDtoMapper::mapDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserCredentialsByEmail(username)
                .map(dto -> org.springframework.security.core.userdetails.User.builder()
                        .username(dto.getEmail())
                        .password(dto.getPassword())
                        .roles(dto.getRoles().toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
    }

    public UserResponse getUserById(Integer id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
        return userDtoMapper.map(user);
    }


    public Integer getUserIdByEmail(String email){
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElseThrow(() -> new NoSuchElementException("User with email: " + email + " not found"));
    }
    @Transactional
    public UserResponse register(UserRequest request){
        validationHelper.validate(request);

        User user = userDtoMapper.map(request);

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        user.setCalculatedCalories(calculationsHelper.calculateCaloricNeeds(
                user.getWeight(),
                user.getHeight(),
                user.getAge(),
                user.getGender(),
                request.getActivityLevel(),
                request.getGoal()));

        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        return userDtoMapper.map(savedUser);
    }

    @Transactional
    public void updateUser(Integer id, UserUpdateRequest request){
        validationHelper.validate(request);

        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()){
            throw new NoSuchElementException("there is no user with id: " + id);
        }
        User user = userOptional.get();

        if(!request.getHeight().equals(user.getHeight())){
            user.setHeight(request.getHeight());
        }
        if(!request.getHeight().equals(user.getHeight())){
            user.setWeight(request.getWeight());
        }

        user.setCalculatedCalories(calculationsHelper.calculateCaloricNeeds(
                user.getWeight(),
                user.getHeight(),
                user.getAge(),
                user.getGender(),
                request.getActivityLevel(),
                request.getGoal()
        ));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Integer id){
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with id: " + id + "not found");
        }
        userRepository.deleteById(id);
    }
}
