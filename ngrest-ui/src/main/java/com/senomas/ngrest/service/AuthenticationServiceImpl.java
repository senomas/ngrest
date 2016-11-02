package com.senomas.ngrest.service;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.senomas.boot.security.LoginRequest;
import com.senomas.boot.security.LoginUser;
import com.senomas.boot.security.TokenAuthentication;
import com.senomas.boot.security.domain.AuthToken;
import com.senomas.boot.security.domain.SecurityUser;
import com.senomas.boot.security.rs.InvalidRefreshTokenException;
import com.senomas.boot.security.rs.InvalidUserPasswordException;
import com.senomas.boot.security.service.AuthenticationService;
import com.senomas.boot.security.service.TokenService;
import com.senomas.common.U;
import com.senomas.ngrest.model.User;
import com.senomas.ngrest.repository.UserRepository;

@Controller
public class AuthenticationServiceImpl implements AuthenticationService {
	private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	UserRepository userRepo;

	@Autowired
	TokenService tokenService;
	
	@Override
	public SecurityUser getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) ((TokenAuthentication) auth).getUser();
		return user;
	}

	@Override
	public AuthToken login(HttpServletRequest request, LoginRequest login) {
		User user = userRepo.findByUsername(login.getLogin());

		if (user == null) {
			log.info("INVALID LOGIN [" + login.getLogin() + "]");
			throw new InvalidUserPasswordException();
		}
		
		if (!user.validatePassword(login.getPassword())) {
			throw new InvalidUserPasswordException();
		}

		LoginUser loginUser = tokenService.create(user);
		log.info("LOGIN-USER " + U.dump(loginUser));
		String token = loginUser.getToken();

		String refreshToken = UUID.randomUUID().toString();
		user.setLoginToken(refreshToken);
		user = userRepo.save(user);
		
		return new AuthToken(token, refreshToken, user);
	}

	@Override
	public AuthToken refresh(HttpServletRequest req, String login, String refreshToken) {
		User user = userRepo.findByUsername(login);

		if (user == null) {
			log.info("INVALID LOGIN [" + login + "]");
			throw new InvalidRefreshTokenException("Invalid login");
		}

		if (!refreshToken.equals(user.getLoginToken())) {
			log.info("INVALID REFRESH-TOKEN [{}] {}", refreshToken, U.dump(user));
			throw new InvalidRefreshTokenException("Invalid refreshToken");
		}
		
		LoginUser loginUser = tokenService.create(user);

		return new AuthToken(loginUser.getToken(), null, user);
	}

	@Override
	public SecurityUser logout() {
		TokenAuthentication auth = (TokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getUser();

		tokenService.remove(auth.getToken());

		user = userRepo.findByUsername(user.getLogin());

		user.setLoginToken(null);
		userRepo.save(user);

		return user;
	}

	@Override
	public SecurityUser logout(String login) {
		tokenService.getByLogin(login);
		
		User user = userRepo.findByUsername(login);

		user.setLoginToken(null);
		userRepo.save(user);

		return user;
	}
}
