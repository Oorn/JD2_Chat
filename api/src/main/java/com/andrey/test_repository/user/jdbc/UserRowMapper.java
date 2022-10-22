package com.andrey.test_repository.user.jdbc;

import com.andrey.test_repository.user.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.andrey.test_repository.user.UsersTableColumns.CREATED;
import static com.andrey.test_repository.user.UsersTableColumns.EMAIL;
import static com.andrey.test_repository.user.UsersTableColumns.ID;
import static com.andrey.test_repository.user.UsersTableColumns.MODIFIED;
import static com.andrey.test_repository.user.UsersTableColumns.PASSWORD;
import static com.andrey.test_repository.user.UsersTableColumns.STATUS;
import static com.andrey.test_repository.user.UsersTableColumns.STATUS_REASON;
import static com.andrey.test_repository.user.UsersTableColumns.USERNAME;
import static com.andrey.test_repository.user.UsersTableColumns.VISITED;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        return User.builder().id(resultSet.getLong(ID))
                .username(resultSet.getString(USERNAME))
                .email(resultSet.getString(EMAIL))
                .passwordHash(resultSet.getString(PASSWORD))
                .creationDate(resultSet.getTimestamp(CREATED))
                .modificationDate(resultSet.getTimestamp(MODIFIED))
                .lastVisitDate(resultSet.getTimestamp(VISITED))
                .status(resultSet.getString(STATUS))
                .statusReason(resultSet.getString(STATUS_REASON))
                .build();
    }
}
