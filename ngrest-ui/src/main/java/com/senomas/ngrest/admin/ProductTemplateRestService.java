package com.senomas.ngrest.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.senomas.common.rs.ResourceNotFoundException;
import com.senomas.common.rs.Views;

import id.co.hanoman.avatar.model.ProductTemplate;

@RestController
@RequestMapping("/${rest.uri}/product-template")
@PreAuthorize("isAuthenticated()")
public class ProductTemplateRestService {

	@Autowired
	List<ProductTemplate> _productTemplates;

	Map<String, ProductTemplate> productTemplates;

	@PostConstruct
	public void init() {
		productTemplates = new LinkedHashMap<>();
		List<ProductTemplate> sorted = new LinkedList<>(_productTemplates);
		Collections.sort(sorted, new Comparator<ProductTemplate>() {
			@Override
			public int compare(ProductTemplate o1, ProductTemplate o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ProductTemplate pt : sorted) {
			productTemplates.put(pt.getCode(), pt);
		}
	}

	@JsonView(Views.List.class)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@Transactional
	public List<ProductTemplate> getList() {
		return new LinkedList<>(productTemplates.values());
	}

	@JsonView(Views.Detail.class)
	@RequestMapping(value = "/{code}", method = { RequestMethod.GET })
	@Transactional
	public ProductTemplate getByCode(@PathVariable("code") String code) {
		ProductTemplate obj = productTemplates.get(code);
		if (obj == null)
			throw new ResourceNotFoundException("ProductTemplate '" + code + "' not found.");
		return obj;
	}
}
