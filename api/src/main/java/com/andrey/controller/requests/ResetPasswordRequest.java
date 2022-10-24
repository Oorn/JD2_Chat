package com.andrey.controller.requests;

import com.andrey.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest{

    @NotBlank
    @NotNull
    String userEmail;

    @NotBlank
    @NotNull
    String resetToken;

    @NotBlank
    @NotNull
    @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_PASSWORD_LENGTH)
    String newPassword;

}
