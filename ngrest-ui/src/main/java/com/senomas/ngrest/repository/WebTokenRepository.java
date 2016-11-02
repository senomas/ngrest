package com.senomas.ngrest.repository;

import org.springframework.data.repository.CrudRepository;

import com.senomas.ngrest.model.WebToken;

public interface WebTokenRepository extends CrudRepository<WebToken, String> {
	
	WebToken findByUsername(String username);

	WebToken findByRefreshToken(String refreshToken);

}
