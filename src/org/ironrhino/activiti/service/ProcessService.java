package org.ironrhino.activiti.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcessService {

	@Autowired
	protected RepositoryService repositoryService;

	public Map<String, String> findStartableProcessMap(String userId) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		List<ProcessDefinition> processDefinitions = repositoryService
				.createProcessDefinitionQuery().active()
				.startableByUser(userId).orderByProcessDefinitionKey().asc()
				.orderByProcessDefinitionVersion().desc().list();
		for (ProcessDefinition processDefinition : processDefinitions)
			if (!map.containsKey(processDefinition.getKey()))
				map.put(processDefinition.getKey(), processDefinition.getName());
		return map;
	}

}