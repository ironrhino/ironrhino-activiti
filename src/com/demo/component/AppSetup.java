package com.demo.component;

import java.util.ArrayList;
import java.util.List;

import org.ironrhino.common.model.Dictionary;
import org.ironrhino.core.metadata.Setup;
import org.ironrhino.core.model.LabelValue;
import org.ironrhino.core.service.EntityManager;
import org.ironrhino.security.model.User;
import org.ironrhino.security.service.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.model.UserRole;

@Component
public class AppSetup {

	@Autowired
	private UserManager userManager;

	@Autowired
	private EntityManager<Dictionary> entityManager;

	@Setup
	public void setup() throws Exception {
		User employee = new User();
		employee.setUsername("employee");
		employee.setLegiblePassword("password");
		employee.setEnabled(true);
		employee.getRoles().add(UserRole.employee);
		userManager.save(employee);

		User leader = new User();
		leader.setUsername("leader");
		leader.setLegiblePassword("password");
		leader.setEnabled(true);
		leader.getRoles().add(UserRole.leader);
		userManager.save(leader);

		User hr = new User();
		hr.setUsername("hr");
		hr.setLegiblePassword("password");
		hr.setEnabled(true);
		hr.getRoles().add(UserRole.hr);
		userManager.save(hr);

		Dictionary dictionary = new Dictionary();
		dictionary.setName("leaveType");
		dictionary.setDescription("请假类型");
		List<LabelValue> items = new ArrayList<>();
		items.add(new LabelValue("年假", "0"));
		items.add(new LabelValue("事假", "1"));
		items.add(new LabelValue("病假", "2"));
		dictionary.setItems(items);
		entityManager.save(dictionary);
	}

}
