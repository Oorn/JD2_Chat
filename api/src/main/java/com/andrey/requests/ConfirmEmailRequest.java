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
public class ConfirmEmailRequest implements ValidRequest{
    String userEmail;
    String confirmationToken;

    @Override
    public boolean isValid() {
        if (userEmail == null)
            return false;

        if (confirmationToken == null)
            return false;

        return true;
    }
}
