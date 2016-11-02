package com.senomas.ngrest.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Lists;
import com.senomas.common.rs.ResourceNotFoundException;
import com.senomas.common.rs.Views;
import com.senomas.ngrest.model.AuthorityData;
import com.senomas.ngrest.repository.AuthorityRepository;

@RestController
@RequestMapping("/${rest.uri}/admin/authority")
@PreAuthorize("isAuthenticated()")
public class AuthorityRestService {

	@Autowired
	AuthorityRepository repo;

	@JsonView(Views.List.class)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@Transactional
	public List<AuthorityData> getList() {
		return Lists.newArrayList(repo.findAll());
	}

	@JsonView(Views.Detail.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.GET })
	@Transactional
	public AuthorityData getById(@PathVariable("id") long id) {
		AuthorityData obj = repo.findOne(id);
		if (obj == null)
			throw new ResourceNotFoundException("Authority '" + id + "' not found.");
		return obj;
	}
}
