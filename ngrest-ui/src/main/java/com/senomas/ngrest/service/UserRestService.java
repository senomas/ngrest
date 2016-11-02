package com.senomas.ngrest.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.senomas.boot.menu.Menu;
import com.senomas.boot.menu.MenuItem;
import com.senomas.boot.security.TokenAuthentication;
import com.senomas.common.persistence.BasicFilter;
import com.senomas.common.persistence.PageRequestId;
import com.senomas.common.persistence.RepositoryPageParam;
import com.senomas.common.rs.PageResponse;
import com.senomas.common.rs.ResourceNotFoundException;
import com.senomas.common.rs.Views;
import com.senomas.ngrest.model.Authority;
import com.senomas.ngrest.model.User;
import com.senomas.ngrest.repository.UserRepository;

@RestController
@RequestMapping("/${rest.uri}/admin/user")
@Menu({
	@MenuItem(order = 11, id = "Administration/Users/List", path = "/user", authorize = "hasAuthority('user-list')"),
	@MenuItem(order = 11, id = "Administration/Users/Create", path = "/user/new", authorize = "hasAuthority('user-create')")
})
@Authority(code="user-list", name="User/List")
@Authority(code="user-detail", name="User/Detail")
@Authority(code="user-save", name="User/Save")
@Authority(code="user-create", name="User/Create")
public class UserRestService {

	@Autowired
	UserRepository repo;

	@PreAuthorize("hasAuthority('user-list')")
	@JsonView(Views.List.class)
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@Transactional
	public PageResponse<User> getPage(@RequestBody RepositoryPageParam<BasicFilter<User>> param) {
		return new PageResponse<User>((PageRequestId<User>) repo.findFilter(param, User.class));
	}

	@PreAuthorize("hasAuthority('user-detail')")
	@JsonView(Views.Detail.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.GET })
	@Transactional
	public User getById(@PathVariable("id") long id) {
		User obj = repo.findOne(id);
		if (obj == null)
			throw new ResourceNotFoundException("User '" + id + "' not found.");
		return obj;
	}

	@PreAuthorize("hasAnyAuthority('user-save', 'user-create')")
	@JsonView(Views.Detail.class)
	@RequestMapping(value = "", method = { RequestMethod.POST })
	@Transactional
	public User save(@RequestBody User user) {
		TokenAuthentication auth = (TokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if (repo.exists(user)) {
			if (!auth.hasAuthority("attribute-save")) throw new AccessDeniedException("Access is Denied");
			User ori = repo.findOne(user.getId());
			if (ori != null) {
				user.setPassword(ori.getPassword());
				user.setLoginToken(ori.getLoginToken());
				user.setLastLogin(ori.getLastLogin());
			} 
		} else {
			if (!auth.hasAuthority("attribute-create")) throw new AccessDeniedException("Access is Denied");
		}
		if (user.getPassword() == null){
			user.setPlainPassword("dodol123");
		}
		return repo.save(user);
	}
}
