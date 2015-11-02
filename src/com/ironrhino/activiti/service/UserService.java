package com.ironrhino.activiti.service;

import java.util.ArrayList;
import java.util.List;

import org.ironrhino.security.model.User;
import org.ironrhino.security.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ironrhino.activiti.model.UserRole;

@Component
public class UserService {

	@Autowired
	private UserManager userManager;

	public String findLeader(String username) {
		List<String> leaders = findLeaders(username);
		return leaders.isEmpty() ? null : leaders.get(0);
	}

	public List<String> findLeaders(String username) {
		List<User> users = userManager.findListByCriteria(userManager
				.detachedCriteria(UserRole.leader));
		List<String> leaders = new ArrayList<String>(users.size());
		for (User u : users)
			leaders.add(u.getUsername());
		return leaders;
	}

}
