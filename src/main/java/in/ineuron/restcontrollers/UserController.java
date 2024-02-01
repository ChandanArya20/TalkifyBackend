package in.ineuron.restcontrollers;

import in.ineuron.annotation.ValidateRequestData;
import in.ineuron.annotation.ValidateUser;
import in.ineuron.dto.LoginRequest;
import in.ineuron.dto.RegisterRequest;
import in.ineuron.dto.UpdateUserPasswordDTO;
import in.ineuron.dto.UserResponse;
import in.ineuron.exception.BadCredentialsException;
import in.ineuron.exception.UserNotFoundException;
import in.ineuron.models.User;
import in.ineuron.services.OTPSenderService;
import in.ineuron.services.OTPStorageService;
import in.ineuron.services.TokenStorageService;
import in.ineuron.services.UserService;
import in.ineuron.utils.UserUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private OTPSenderService otpSender;

    @Autowired
    private OTPStorageService otpStorage;

    @Autowired
    private TokenStorageService tokenService;

    @Autowired
    private UserUtils userUtils;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint for registering a new user
    @PostMapping("/register")
    @ValidateRequestData
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest requestData, BindingResult result, HttpServletResponse response) {

        // Check if the email is already registered
        if (userService.isUserAvailableByEmail(requestData.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered with another account");
        }
        // Check if the phone number is already registered
        if (requestData.getPhone() != null && userService.isUserAvailableByPhone(requestData.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Phone No. already registered with another account");
        } else {
            User user = new User();
            BeanUtils.copyProperties(requestData, user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));  // Encrypt the user's password

            // Register the user in the system
            User regUser = userService.registerUser(user);

            String token = tokenService.generateToken(user.getId());
            Cookie cookie = new Cookie("auth-token", token);   //setting cookie
            cookie.setHttpOnly(true);
            int maxAge = 7 * 24 * 60 * 60;  // 7 days in seconds
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);

            return ResponseEntity.ok(userUtils.getUserResponse(regUser));
        }
    }

    @ValidateRequestData
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginData, BindingResult result, HttpServletResponse response) {

        // Login using email
        User user = userService.fetchUserByEmail(loginData.getEmail());
        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        } else {
            // Create a response object and set an authentication token cookie
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);

            String token = tokenService.generateToken(user.getId());
            Cookie cookie = new Cookie("auth-token", token);    //setting cookie
            cookie.setHttpOnly(true);
            int maxAge = 7 * 24 * 60 * 60;  // 7 days in seconds
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);

            return ResponseEntity.ok(userResponse);
        }
    }

    @ValidateUser
    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {

        String authToken = userUtils.getAuthToken(request);

        tokenService.removeToken(authToken);
        Cookie cookie = new Cookie("auth-token", null);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/profile")
    @ValidateUser
    public ResponseEntity<UserResponse> getLoginUserDetails(HttpServletRequest request) {

        Long id = userUtils.getRequestingUserId(request);
        UserResponse userResponse = userUtils.getUserResponse(userService.findUserById(id));

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping("/check-login")
    public ResponseEntity<String> checkUserLogin(HttpServletRequest request, HttpServletResponse response) {

        if (userUtils.isValidUser(request)) {
            return ResponseEntity.ok("true, User is logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User login session has expired");
        }
    }


    @GetMapping("/send-otp")
    public ResponseEntity<String> sendOTPByPhone(@RequestParam("email") String email) throws MessagingException {

        if (userService.isUserAvailableByEmail(email)) {
            Integer OTP = null;
            OTP = otpSender.sendOTPByEmail(email);
            otpStorage.storeOTP(email, String.valueOf(OTP));

            return ResponseEntity.ok("Sent OTP: " + OTP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found for " + email);
        }
    }

    @GetMapping("/verify-otp")
    public ResponseEntity<String> verifyOTPByPhone(
            @RequestParam("email") String email,
            @RequestParam String otp, HttpServletResponse response) throws MessagingException {

        if (!userService.isUserAvailableByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found for " + email);
        }

        if (otpStorage.verifyOTP(email, otp)) {
            otpStorage.removeOTP(email);

            //setting token for authorized user who wants to change password
            String token = tokenService.generateToken(null);
            Cookie cookie = new Cookie("otpVerified-token", token);
            cookie.setHttpOnly(true);
            int maxAge = 5 * 60;  // 5 minutes in seconds
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);

            return ResponseEntity.ok("verified successfully.. ");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP verification failed.. ");
        }
    }

    @ValidateUser
    @PostMapping("/otp-verified/update-password")
    public ResponseEntity<?> UpdateUserPasswordAfterOTPVerified( @Valid @RequestBody UpdateUserPasswordDTO userCredential,
            BindingResult result, HttpServletRequest request) {

        //checks the bean field errors
        Map<String, String> errorResults = userUtils.validateUserCredential(result);
        if (!errorResults.isEmpty()) {
            return ResponseEntity.badRequest().body(errorResults);
        }

        User user = userService.fetchUserByEmail(userCredential.getEmail());
        userService.updateUserPassword(user.getId(), passwordEncoder.encode(userCredential.getNewPassword()));
        return ResponseEntity.ok("Password updated successfully..");
    }

    @ValidateUser
    @GetMapping("/search-users")
    public ResponseEntity<List<UserResponse>> searchUsersHandler(@RequestParam String query) {

        List<User> users = userService.searchUser(query);
        return ResponseEntity.status(HttpStatus.OK).body(userUtils.getUserResponse(users));
    }

    @GetMapping("/test-cookie")
    public ResponseEntity<String> someOtherEndpoint(HttpServletRequest request) {

        if (userUtils.isValidUser(request)) {
            return ResponseEntity.ok("Valid token");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is expired");
        }
    }

}
