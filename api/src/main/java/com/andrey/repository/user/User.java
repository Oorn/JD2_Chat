package com.andrey.repository.user;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class User {
    private Long id;

    private String username;

    private String email;

    private String passwordHash;

    private Timestamp creationDate;

    private Timestamp modificationDate;

    private Timestamp lastVisitDate;

    private String status;

    private String statusReason;
}
