package in.ineuron.aspect;

import in.ineuron.exception.TokenException;
import in.ineuron.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserValidationAspect {

    private final UserUtils userUtils;

    public UserValidationAspect(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @Before("@annotation(in.ineuron.annotation.ValidateUser) && args(request, ..)")
    public void validateUserBeforeMethodExecution( HttpServletRequest request) {
        System.out.println(userUtils.getAuthToken(request));
        if (!userUtils.isValidUser(request)) {
            throw new TokenException("Session is expired");
        }
    }
}
