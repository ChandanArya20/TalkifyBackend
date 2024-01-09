package in.ineuron.services.impl;

import in.ineuron.dto.UserResponse;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.User;
import in.ineuron.repositories.UserRepository;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    @Autowired
    private TokenStorageService tokenService;

    @Autowired
    private UserUtils userUtils;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Boolean isUserAvailableByPhone(String phone) {

        return userRepo.existsByPhone(phone);
    }

    @Override
    public Boolean isUserAvailableByEmail(String email) {

        return userRepo.existsByEmail(email);
    }

    @Override
    public User fetchUserByPhone(String phone) {

        return userRepo.findByPhone(phone);
    }

    @Override
    public User fetchUserByEmail(String email) {

        return userRepo.findByEmail(email);
    }

    @Override
    public void registerUser(User user) {

        User regUser = userRepo.save(user);
        System.out.println(regUser);
    }

    @Override
    public void updateUserPassword(Long userId, String newPassword) {
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setPassword(newPassword);
            userRepo.save(user);
        }
    }

    @Override
    public UserResponse fetchUserById(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);

            return userResponse;
        }
        throw new UserNotFoundException("User not found with id "+userId);
    }

    @Override
    public UserResponse fetchUserByAuthToken(String token) throws UserNotFoundException, BadCredentialsException {
        Long userId = tokenService.getUserIdFromToken(token);

        if(userId==null){
            throw new BadCredentialsException("Token expired...");
        }
        Optional<User> opt = userRepo.findById(userId);

        if(opt.isPresent()){
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(opt.get(), userResponse);
            return userResponse;
        }else {
            throw new UserNotFoundException("User not found with id "+userId);
        }
    }

    @Override
    public List<UserResponse> searchUser(String query) {

        List<User> users = userRepo.searchUser(query);

        return userUtils.getUserResponse(users);
    }


}
