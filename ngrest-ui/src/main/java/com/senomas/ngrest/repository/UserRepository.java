package com.senomas.ngrest.repository;

import com.senomas.common.persistence.PageRepository;
import com.senomas.ngrest.model.User;

public interface UserRepository extends PageRepository<User, Long> {
	
	User findByUsername(String username);

	User findByLoginToken(String loginToken);
}
