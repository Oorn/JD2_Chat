package com.andrey.controller.requests;

import com.andrey.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.context.annotation.Bean;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserCreateRequest {
    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_USERNAME_LENGTH, max = Constants.MAX_USERNAME_LENGTH)
    String username;

    @NotBlank
    @NotNull
    @Size(max = Constants.MAX_EMAIL_LENGTH)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE)
    String email;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH)
    String password;

}
