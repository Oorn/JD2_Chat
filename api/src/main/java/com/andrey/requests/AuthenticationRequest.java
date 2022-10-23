package com.andrey.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements ValidRequest{

    @NotBlank
    @NotNull
    String email;

    @NotBlank
    @NotNull
    String password;

    @Override
    public boolean isValid() {
        if (email == null)
            return false;

        if (password == null)
            return false;

        return true;
    }
}
