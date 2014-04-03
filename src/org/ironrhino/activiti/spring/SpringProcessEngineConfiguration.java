package org.ironrhino.activiti.spring;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class SpringProcessEngineConfiguration extends
		org.activiti.spring.SpringProcessEngineConfiguration {

	@Autowired(required = false)
	private List<AbstractFormType> formTypeList;

	protected void initFormTypes() {
		super.initFormTypes();
		if (formTypeList != null)
			for (AbstractFormType customFormType : formTypeList)
				formTypes.addFormType(customFormType);
	}

	protected void autoDeployResources(ProcessEngine processEngine) {
		if (deploymentResources != null && deploymentResources.length > 0) {
			for (Resource resource : deploymentResources) {
				RepositoryService repositoryService = processEngine
						.getRepositoryService();
				String resourceName = resource.getFilename();
				try {
					DeploymentBuilder deploymentBuilder = repositoryService
							.createDeployment().enableDuplicateFiltering()
							.name(resourceName);
					if (resourceName.endsWith(".bar")
							|| resourceName.endsWith(".zip")
							|| resourceName.endsWith(".jar")) {
						deploymentBuilder.addZipInputStream(new ZipInputStream(
								resource.getInputStream()));
					} else {
						deploymentBuilder.addInputStream(resourceName,
								resource.getInputStream());
					}
					deploymentBuilder.deploy();
				} catch (IOException e) {
					throw new ActivitiException(
							"couldn't auto deploy resource '" + resource
									+ "': " + e.getMessage(), e);
				}

			}
		}
	}

}
