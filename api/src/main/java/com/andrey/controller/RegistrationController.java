package com.andrey.controller;

import com.andrey.db_entities.chat_user.ChatUser;
import com.andrey.requests.AuthenticationRequest;
import com.andrey.requests.ChatUserCreateRequest;
import com.andrey.requests.ConfirmEmailRequest;
import com.andrey.requests.ResetPasswordRequest;
import com.andrey.service.auth.AuthenticationService;
import com.andrey.service.mail.MailSenderService;
import com.andrey.service.registration.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    private final MailSenderService mailSenderService;

    @PostMapping("/createNewUser")
    @Transactional
    public ResponseEntity<Object> createNewUser(@RequestBody ChatUserCreateRequest createRequest){
        if (!createRequest.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Object> confirmEmail(@RequestParam String email, @RequestParam String token){

        ConfirmEmailRequest request = new ConfirmEmailRequest(email, token);
        if (!request.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        boolean result = registrationService.confirmEmail(request);
        if (!result)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sendPasswordRecovery")
    @Transactional
    public ResponseEntity<Object> sendPasswordRecovery(@RequestBody String email){
        Optional<String> resetToken = registrationService.createPasswordResetToken(email);
        if (resetToken.isEmpty())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        mailSenderService.sendMail(email, "JD2_Chat password rest", "reset token: "
                + resetToken.get());

        return new ResponseEntity<>(resetToken.get(), HttpStatus.OK);

    }
    @PostMapping("/resetPassword")
    @Transactional
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        if (!resetPasswordRequest.isValid())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        boolean result = registrationService.resetPassword(resetPasswordRequest);
        if (!result)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
