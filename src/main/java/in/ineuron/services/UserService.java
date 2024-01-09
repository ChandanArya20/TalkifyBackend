package in.ineuron.services;

import in.ineuron.dto.UserResponse;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.User;

import java.util.List;

public interface UserService {

    public Boolean isUserAvailableByPhone(String phone);
    public Boolean isUserAvailableByEmail(String email);
    public User registerUser(User user);
    public User fetchUserByPhone(String phone) throws UserNotFoundException;
    public User fetchUserByEmail(String email) throws UserNotFoundException;

    public User updateUserPassword(Long userId, String newPassword) throws UserNotFoundException;

    public User fetchUserById(Long userId) throws UserNotFoundException;
    public User fetchUserByAuthToken(String token) throws UserNotFoundException, BadCredentialsException;

    public List<User> searchUser(String query);
}
