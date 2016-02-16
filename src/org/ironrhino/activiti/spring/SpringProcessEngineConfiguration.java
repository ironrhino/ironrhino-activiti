package org.ironrhino.activiti.spring;

import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.variable.VariableType;
import org.activiti.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

public class SpringProcessEngineConfiguration extends org.activiti.spring.SpringProcessEngineConfiguration {

	@Autowired(required = false)
	private List<AbstractFormType> formTypeList;

	@Autowired(required = false)
	private List<VariableType> variableTypeList;

	@Autowired(required = false)
	private List<ActivitiEventListener> listeners;

	public ProcessEngineConfiguration setAnnotationFontName(String annotationFontName) {
		// hack
		try {
			ProcessEngineConfiguration.class.getDeclaredField("annotationFontName");
			this.annotationFontName = annotationFontName;
		} catch (NoSuchFieldException e) {

		}
		return this;
	}

	@Override
	protected void initFormTypes() {
		super.initFormTypes();
		if (formTypeList != null)
			for (AbstractFormType customFormType : formTypeList)
				formTypes.addFormType(customFormType);
	}

	@Override
	protected void initVariableTypes() {
		super.initVariableTypes();
		if (variableTypeList != null)
			for (VariableType customVariableType : variableTypeList)
				variableTypes.addType(customVariableType, 1);
	}

	@Override
	protected void initEventDispatcher() {
		super.initEventDispatcher();
		if (listeners != null)
			for (ActivitiEventListener listener : listeners)
				this.eventDispatcher.addEventListener(listener);
	}

	@Override
	protected void autoDeployResources(ProcessEngine processEngine) {
		if (deploymentResources != null && deploymentResources.length > 0) {
			for (Resource resource : deploymentResources) {
				RepositoryService repositoryService = processEngine.getRepositoryService();
				String resourceName = resource.getFilename();
				try {
					DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
							.enableDuplicateFiltering().name(resourceName);
					if (resourceName.endsWith(".bar") || resourceName.endsWith(".zip")
							|| resourceName.endsWith(".jar")) {
						deploymentBuilder.addZipInputStream(new ZipInputStream(resource.getInputStream()));
					} else {
						deploymentBuilder.addInputStream(resourceName, resource.getInputStream());
					}
					deploymentBuilder.deploy();
				} catch (IOException e) {
					throw new ActivitiException("couldn't auto deploy resource '" + resource + "': " + e.getMessage(),
							e);
				}

			}
		}
	}

}
