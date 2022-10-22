package com.andrey.requests;

import com.andrey.Constants;
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
public class ResetPasswordRequest implements ValidRequest{
    String userEmail;
    String resetToken;
    String newPassword;

    @Override
    public boolean isValid() {
        if (userEmail == null)
            return false;

        if (resetToken == null)
            return false;

        if (newPassword == null)
            return false;
        if (newPassword.length() > Constants.MAX_PASSWORD_LENGTH)
            return false;

        return true;
    }
}
