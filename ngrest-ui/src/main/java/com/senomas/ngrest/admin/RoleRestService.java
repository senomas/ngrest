package com.senomas.ngrest.admin;

import java.util.List;

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
import com.google.common.collect.Lists;
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
import com.senomas.ngrest.model.Role;
import com.senomas.ngrest.repository.RoleRepository;

@RestController
@RequestMapping("/${rest.uri}/admin/role")
@Menu({
	@MenuItem(order = 11, id = "Administration/Roles/List", path = "/role", authorize = "hasAuthority('role-list')"),
	@MenuItem(order = 11, id = "Administration/Roles/Create", path = "/role/new", authorize = "hasAuthority('role-create')")
})
@Authority(code="role-list", name="Role/List")
@Authority(code="role-detail", name="Role/Detail")
@Authority(code="role-save", name="Role/Save")
@Authority(code="role-create", name="Role/Create")
@Authority(code="role-delete", name="Role/Delete")
public class RoleRestService {

	@Autowired
	RoleRepository repo;

	@PreAuthorize("hasAnyAuthority('role-list', 'user-detail')")
	@JsonView(Views.List.class)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@Transactional
	public List<Role> getList() {
		return Lists.newArrayList(repo.findAll());
	}

	@PreAuthorize("hasAuthority('role-list')")
	@JsonView(Views.List.class)
	@RequestMapping(value = "/page", method = RequestMethod.POST)
	@Transactional
	public PageResponse<Role> getPage(@RequestBody RepositoryPageParam<BasicFilter<Role>> param) {
		return new PageResponse<Role>((PageRequestId<Role>) repo.findFilter(param, Role.class));
	}

	@PreAuthorize("hasAuthority('role-detail')")
	@JsonView(Views.Detail.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.GET })
	@Transactional
	public Role getById(@PathVariable("id") long id) {
		Role obj = repo.findOne(id);
		if (obj == null)
			throw new ResourceNotFoundException("Authority '" + id + "' not found.");
		return obj;
	}

	@PreAuthorize("hasAnyAuthority('role-save', 'role-create')")
	@JsonView(Views.Detail.class)
	@RequestMapping(value = "", method = { RequestMethod.POST })
	@Transactional
	public Role save(@RequestBody Role role) {
		TokenAuthentication auth = (TokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		if (repo.exists(role)) {
			if (!auth.hasAuthority("attribute-save")) throw new AccessDeniedException("Access is Denied");
		} else {
			if (!auth.hasAuthority("attribute-create")) throw new AccessDeniedException("Access is Denied");
		}
		return repo.save(role);
	}

	@PreAuthorize("hasAuthority('role-delete')")
	@JsonView(Views.Detail.class)
	@RequestMapping(value = "/{id}", method = { RequestMethod.DELETE })
	@Transactional
	public Role delete(@PathVariable("id") long id) {
		Role obj = repo.findOne(id);
		repo.delete(id);
		return obj;
	}
}
