package org.ironrhino.activiti.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.ironrhino.core.session.HttpSessionFilterHook;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionFilterHookForIdentityService implements HttpSessionFilterHook {

	@Autowired
	private IdentityService identityService;

	@Override
	public boolean beforeFilterChain(HttpServletRequest request, HttpServletResponse response) {
		identityService.setAuthenticatedUserId(AuthzUtils.getUsername());
		return false;
	}

	@Override
	public void afterFilterChain(HttpServletRequest request, HttpServletResponse response) {
		identityService.setAuthenticatedUserId(null);
	}

}
