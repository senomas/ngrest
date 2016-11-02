package com.senomas.ngrest.admin;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.senomas.ngrest.Application;

@RestController
@RequestMapping("/${rest.uri}")
public class InfoRestService {

	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public String getBuildInfo() throws IOException {
		return IOUtils.toString(Application.class.getResourceAsStream("/META-INF/changelog.txt"), "UTF-8");
	}
}
