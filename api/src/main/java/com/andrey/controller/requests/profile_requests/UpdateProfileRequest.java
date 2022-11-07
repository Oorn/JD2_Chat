package com.andrey.controller.requests.profile_requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Positive;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest extends CreateProfileRequest{
    @Positive
    private long id;
}
