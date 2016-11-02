package com.senomas.ngrest;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.senomas.boot.loader.DataLoader;
import com.senomas.common.U;
import com.senomas.ngrest.model.Authorities;
import com.senomas.ngrest.model.Authority;
import com.senomas.ngrest.model.AuthorityData;
import com.senomas.ngrest.model.Role;
import com.senomas.ngrest.repository.AuthorityRepository;
import com.senomas.ngrest.repository.RoleRepository;

@Component
public class InitData extends DataLoader {
	private static final Logger log = LoggerFactory.getLogger(InitData.class);
	
	@Autowired
	AuthorityRepository repo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
	@Autowired
	PlatformTransactionManager txManager;
	
	@PostConstruct
	public void init() throws Exception {
		TransactionTemplate tt = new TransactionTemplate(txManager);
		Exception ex = tt.execute(new TransactionCallback<Exception>() {
			@Override
			public Exception doInTransaction(TransactionStatus status) {
				try {
					List<AuthorityData> auths = new LinkedList<>();
					Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
					Set<String> codes = new HashSet<>();
					Set<String> removeCodes = new HashSet<>();
					for (AuthorityData a : repo.findAll()) {
						codes.add(a.getCode());
						removeCodes.add(a.getCode());
					}
					for (Entry<RequestMappingInfo, HandlerMethod> item : handlerMethods.entrySet()) {
						HandlerMethod method = item.getValue();
						Authorities ano = method.getBeanType().getAnnotation(Authorities.class);
						if (ano != null && ano.value() != null) {
							for (Authority aa : ano.value()) {
								removeCodes.remove(aa.code());
								if (!codes.contains(aa.code())) {
									codes.add(aa.code());
									AuthorityData ad = new AuthorityData();
									ad.setCode(aa.code());
									ad.setName(aa.name());
									auths.add(ad);
								}
							}
						}
					}
					repo.save(auths);
					for (Role r : roleRepo.findAll()) {
						boolean mod = false;
						for (Iterator<AuthorityData> itr = r.getAuthorities().iterator(); itr.hasNext(); ) {
							AuthorityData a = itr.next();
							if (removeCodes.contains(a.getCode())) {
								itr.remove();
								mod = true;
							}
						}
						if (mod) {
							roleRepo.save(r);
						}
					}
					for (AuthorityData a : repo.findAll()) {
						if (removeCodes.contains(a.getCode())) {
							repo.delete(a);
						}
					}
					load();
					Role root = roleRepo.findByCode("root");
					root.setAuthorities(repo.findAll());
					log.info("SAVE ROOT "+U.dump(root));
					roleRepo.save(root);
				} catch (Exception ex) {
					log.warn(ex.getMessage(), ex);
					return ex;
				}
				return null;
			}
		});
		if (ex != null) throw ex;
	}
}
