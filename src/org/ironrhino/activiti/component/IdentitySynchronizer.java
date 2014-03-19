package org.ironrhino.activiti.component;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.ironrhino.core.event.EntityOperationEvent;
import org.ironrhino.core.model.Persistable;
import org.ironrhino.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class IdentitySynchronizer implements
		ApplicationListener<EntityOperationEvent> {

	@Autowired
	private IdentityService identityService;

	@Override
	public void onApplicationEvent(EntityOperationEvent event) {
		if (!event.isLocal())
			return;
		Persistable<?> entity = event.getEntity();
		if (entity.getClass() == User.class) {
			User user = (User) entity;
			switch (event.getType()) {
			case CREATE:
			case UPDATE:
				org.activiti.engine.identity.User u = identityService
						.createUserQuery().userId(user.getUsername())
						.singleResult();
				if (u == null) {
					u = identityService.newUser(user.getUsername());
					u.setFirstName(user.getName());
					u.setEmail(user.getEmail());
					identityService.saveUser(u);
				}
				for (Group group : identityService.createGroupQuery()
						.groupMember(u.getId()).list())
					identityService.deleteMembership(u.getId(), group.getId());
				for (String role : user.getRoles()) {
					Group g = identityService.createGroupQuery().groupId(role)
							.singleResult();
					if (g == null) {
						g = identityService.newGroup(role);
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
