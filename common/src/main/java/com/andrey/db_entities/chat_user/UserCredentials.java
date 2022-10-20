package com.andrey.db_entities.chat_user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Indexed;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class UserCredentials {

    private String email;

    @JsonIgnore
    private String passwordHash;
}
//not used because Spring Data doesn't see fields of embedded objects :(