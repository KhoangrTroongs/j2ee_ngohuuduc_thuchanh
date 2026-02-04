package thuchanh.ngohuuduc.validators;

import thuchanh.ngohuuduc.services.UserService;
import thuchanh.ngohuuduc.validators.annotations.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (userService == null) {
            return true; // Skip validation if service is not available
        }
        return userService.findByUsername(username).isEmpty();
    }
}
