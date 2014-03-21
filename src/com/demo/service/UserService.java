package com.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserService {

	public String findDeptLeader(String username) {
		List<String> leaders = findDeptLeaders(username);
		return leaders.isEmpty() ? null : leaders.get(0);
	}

	public List<String> findDeptLeaders(String username) {
		List<String> leaders = new ArrayList<String>();
		//TODO
		leaders.add("deptleader");
		leaders.add("leaderuser");
		return leaders;
	}

}
