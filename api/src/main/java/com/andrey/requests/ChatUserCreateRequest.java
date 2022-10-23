package com.andrey.requests;

import com.andrey.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserCreateRequest implements ValidRequest{
    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_USERNAME_LENGTH, max = Constants.MAX_USERNAME_LENGTH)
    String username;

    @NotBlank
    @NotNull
    @Size(max = Constants.MAX_EMAIL_LENGTH)
    String email;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH)
    String password;

    @Override
    public boolean isValid() {
        if (username == null)
            return false;
        if (username.length() > Constants.MAX_USERNAME_LENGTH)
            return false;

        if (email == null)
            return false;
        if (email.length() > Constants.MAX_EMAIL_LENGTH)
            return false;
        if (!EmailValidator.getInstance().isValid(email))
            return false;

        if (password == null)
            return false;
        if (password.length() > Constants.MAX_PASSWORD_LENGTH)
            return false;

        return true;
    }
}
