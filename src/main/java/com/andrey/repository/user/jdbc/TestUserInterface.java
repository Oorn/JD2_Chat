package com.andrey.repository.user.jdbc;

import com.andrey.repository.user.User;
import com.andrey.repository.user.UserRepositoryInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TestUserInterface implements UserRepositoryInterface {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final UserRowMapper userRowMapper;



    @Override
    public User findById(Long id) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        final String query = "select * from chat.users where id = :id";

        mapSqlParameterSource.addValue("id", id);

        return namedParameterJdbcTemplate.queryForObject(query, mapSqlParameterSource, userRowMapper);

    }

    @Override
    public Optional<User> findOne(Long id) {
        return Optional.of(findById(id));
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findAll(int limit, int offset) {
        return null;
    }

    @Override
    public User create(User object) {
        return null;
    }

    @Override
    public User update(User object) {
        return null;
    }

    @Override
    public Long delete(Long id) {
        return null;
    }
}
