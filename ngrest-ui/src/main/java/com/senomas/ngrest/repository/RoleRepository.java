package com.senomas.ngrest.repository;

import com.senomas.common.persistence.PageRepository;
import com.senomas.ngrest.model.Role;

public interface RoleRepository extends PageRepository<Role, Long> {
	
	Role findByCode(String code);
	
}
