package com.example.multitenant.customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer save(String name) {
        String sql = "INSERT INTO customers(name) VALUES (:name)";
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", name);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, params, keyHolder, new String[] {"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        return new Customer(id, name);
    }

    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM customers ORDER BY id", new CustomerRowMapper());
    }

    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM customers", new MapSqlParameterSource());
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Customer(rs.getLong("id"), rs.getString("name"));
        }
    }
}
