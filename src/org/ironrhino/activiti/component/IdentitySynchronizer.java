package org.ironrhino.activiti.component;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.ironrhino.core.event.EntityOperationEvent;
import org.ironrhino.core.model.Persistable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class IdentitySynchronizer implements
		ApplicationListener<EntityOperationEvent<?>> {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void onApplicationEvent(EntityOperationEvent<?> event) {
		if (!event.isLocal())
			return;
		Persistable<?> entity = event.getEntity();
		if (entity instanceof UserDetails) {
			UserDetails user = (UserDetails) entity;
			user = userDetailsService.loadUserByUsername(user.getUsername());
			switch (event.getType()) {
			case CREATE:
			case UPDATE:
				org.activiti.engine.identity.User u = identityService
						.createUserQuery().userId(user.getUsername())
						.singleResult();
				if (u == null) {
					u = identityService.newUser(user.getUsername());
					identityService.saveUser(u);
				}
				for (Group group : identityService.createGroupQuery()
						.groupMember(u.getId()).list())
					identityService.deleteMembership(u.getId(), group.getId());
				for (GrantedAuthority ga : user.getAuthorities()) {
					Group g = identityService.createGroupQuery()
							.groupId(ga.getAuthority()).singleResult();
					if (g == null) {
						g = identityService.newGroup(ga.getAuthority());
						identityService.saveGroup(g);
					}
					identityService.createMembership(u.getId(), g.getId());
				}
				break;
			case DELETE:
				identityService.deleteUser(user.getUsername());
				break;
			default:
				break;
			}
		}
	}

}
