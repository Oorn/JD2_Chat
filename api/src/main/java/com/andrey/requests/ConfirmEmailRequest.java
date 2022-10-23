package com.andrey.requests;

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
public class ConfirmEmailRequest implements ValidRequest{
    @NotBlank
    @NotNull
    String userEmail;

    @NotBlank
    @NotNull
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
