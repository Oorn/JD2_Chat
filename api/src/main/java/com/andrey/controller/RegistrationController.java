package com.andrey.controller;

import com.andrey.Constants;
import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.requests.AuthenticationRequest;
import com.andrey.requests.ChatUserCreateRequest;
import com.andrey.requests.ConfirmEmailRequest;
import com.andrey.requests.ResetPasswordRequest;
import com.andrey.service.auth.AuthenticationService;
import com.andrey.service.mail.MailSenderService;
import com.andrey.service.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
@Validated
public class RegistrationController implements WebMvcConfigurer {

    private final RegistrationService registrationService;

    private final MailSenderService mailSenderService;

    @PostMapping("/createNewUser")
    @Transactional
    @Operation(summary = "creates user with given credentials and status EMAIL_VERIFICATION_REQUIRED and sends verification email")
    public ResponseEntity<Object> createNewUser(@RequestBody @Valid ChatUserCreateRequest createRequest){

        Optional<ChatUser> optionalUser = registrationService.createNewUser(createRequest);
        if (optionalUser.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); //TODO specific message about using existing email?
        ChatUser user = optionalUser.get();

        mailSenderService.sendMail(user.getEmail(), "JD2_Chat Registration", "confirmation link: "
                + "http://localhost:8080/registration/confirmEmail"
                + "?email=" + user.getEmail()
                + "&token=" + user.getEmailConfirmationToken());



        return new ResponseEntity<>(user.getEmailConfirmationToken(), HttpStatus.OK);
    }

    @GetMapping("/confirmEmail")
    @Transactional
    @Operation(summary = "if confirmation token matches, changes user status form EMAIL_VERIFICATION_REQUIRED to OK")
    public ResponseEntity<Object> confirmEmail(@RequestParam @NotBlank @NotNull  String email, @RequestParam @NotBlank @NotNull  String token){

        ConfirmEmailRequest request = new ConfirmEmailRequest(email, token);

        boolean result = registrationService.confirmEmail(request);
        if (!result)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sendPasswordRecovery")
    @Transactional
    @Operation(summary = "send password reset token to the registered email")
    public ResponseEntity<Object> sendPasswordRecovery(@RequestBody
                                                           @NotBlank @NotNull String email){
        Optional<String> resetToken = registrationService.createPasswordResetToken(email);
        if (resetToken.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        mailSenderService.sendMail(email, "JD2_Chat password rest", "reset token: "
                + resetToken.get());

        return new ResponseEntity<>(resetToken.get(), HttpStatus.OK);

    }
    @PostMapping("/resetPassword")
    @Transactional
    @Operation(summary = "updates password to now if token matches, and invalidates all previously issued JWTs")
    public ResponseEntity<Object> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest){

        boolean result = registrationService.resetPassword(resetPasswordRequest);
        if (!result)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
