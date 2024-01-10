package in.ineuron.aspect;

import in.ineuron.exception.TokenExpiredException;
import in.ineuron.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
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
    public void validateUserBeforeMethodExecution(JoinPoint joinPoint, HttpServletRequest request) {
        if (!userUtils.isValidUser(request)) {
            throw new TokenExpiredException("Session is expired");
        }
    }
}
