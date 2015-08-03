package org.ironrhino.activiti.component;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.ironrhino.core.event.EntityOperationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class IdentitySynchronizer {

	@Autowired
	private IdentityService identityService;

	@Autowired
	private UserDetailsService userDetailsService;

	@EventListener
	public void onApplicationEvent(EntityOperationEvent<? extends UserDetails> event) {
		if (!event.isLocal())
			return;
		UserDetails user = event.getEntity();
		user = userDetailsService.loadUserByUsername(user.getUsername());
		switch (event.getType()) {
		case CREATE:
		case UPDATE:
			org.activiti.engine.identity.User u = identityService.createUserQuery().userId(user.getUsername())
					.singleResult();
			if (u == null) {
				u = identityService.newUser(user.getUsername());
				identityService.saveUser(u);
			}
			for (Group group : identityService.createGroupQuery().groupMember(u.getId()).list())
				identityService.deleteMembership(u.getId(), group.getId());
			for (GrantedAuthority ga : user.getAuthorities()) {
				Group g = identityService.createGroupQuery().groupId(ga.getAuthority()).singleResult();
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
