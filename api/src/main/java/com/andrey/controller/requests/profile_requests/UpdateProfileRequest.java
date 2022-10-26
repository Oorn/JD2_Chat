package com.andrey.controller.requests.profile_requests;

import com.andrey.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest extends CreateProfileRequest{
    @Positive
    private long id;
}
