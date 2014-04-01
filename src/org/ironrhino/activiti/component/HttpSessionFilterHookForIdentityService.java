package org.ironrhino.activiti.component;

import org.activiti.engine.IdentityService;
import org.ironrhino.core.session.HttpSessionFilterHook;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpSessionFilterHookForIdentityService implements
		HttpSessionFilterHook {

	@Autowired
	private IdentityService identityService;

	@Override
	public void beforeDoFilter() {
		identityService.setAuthenticatedUserId(AuthzUtils.getUsername());
	}

	@Override
	public void afterDoFilter() {
		identityService.setAuthenticatedUserId(null);
	}

}
