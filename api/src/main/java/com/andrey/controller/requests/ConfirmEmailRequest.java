package com.andrey.controller.requests;

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
public class ConfirmEmailRequest{
    @NotBlank
    @NotNull
    String userEmail;

    @NotBlank
    @NotNull
    String confirmationToken;

}
