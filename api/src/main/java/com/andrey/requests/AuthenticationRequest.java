package com.andrey.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest implements ValidRequest{
    String email;

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
