package com.senomas.ngrest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.ResourceType;

@Controller
public class IndexController {
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	ResourcePatternResolver resolver;

	@Autowired
	private Environment environment;
	
	@Autowired
	private WroModelFactory wroModelFactory;
	
	@RequestMapping("/")
	public String index(Model model, HttpServletRequest request) throws Exception {
		if (Arrays.asList(environment.getActiveProfiles()).contains("devjs")) {
			WroModel wrom = wroModelFactory.create();
			List<String> stylesheets = new LinkedList<>();
			List<String> scripts = new LinkedList<>();
			wrom.getGroups().forEach(new Consumer<Group>() {
				@Override
				public void accept(Group g) {
					log.info("GROUP "+g);
					if (g.getName().equals("all")) {
						g.getResources().forEach(new Consumer<ro.isdc.wro.model.resource.Resource>() {
							@Override
							public void accept(ro.isdc.wro.model.resource.Resource r) {
								log.info("RES ["+r.getType()+"] ["+r.getUri()+"]");
								String uri = null;
								if (r.getUri().startsWith("classpath:static/")) {
									uri = r.getUri().substring(16);
								} else if (r.getUri().startsWith("classpath:META-INF/resources/")) {
									uri = r.getUri().substring(28);
								} else {
									log.warn("INVALID URI ["+r.getUri()+"]");
								}
								if (uri != null) {
									if (r.getType() == ResourceType.CSS) {
										stylesheets.add(uri);
									} else if (r.getType() == ResourceType.JS) {
										scripts.add(uri);
									} else {
										log.warn("INVALID TYPE ["+r.getType()+"]");
									}
								}
							}
						});
					}
				}
			});
			model.addAttribute("stylesheets", stylesheets);
			model.addAttribute("scripts", scripts);
		} else {
			model.addAttribute("stylesheets", new String[] {"/wro/all.css"});
			model.addAttribute("scripts", new String[] {"/wro/all.js"});
		}
		return "/index";
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping("/webjars/springfox-swagger-ui/springfox.js")
	public void springfoxJs(HttpServletResponse response) throws Exception {
		Resource res = resolver.getResource("classpath:/static/springfox.js");
		response.setContentType("application/javascript");
		IOUtils.copy(res.getInputStream(), response.getOutputStream());
	}
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping("/webjars/Semantic-UI/2.2.4/semantic.css")
	public void semanticUiCss(HttpServletResponse response) throws Exception {
		Resource res = resolver.getResource("classpath:/static/semantic.css");
		response.setContentType("text/css");
		IOUtils.copy(res.getInputStream(), response.getOutputStream());
	}
}