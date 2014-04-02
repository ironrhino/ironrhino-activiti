package org.ironrhino.activiti.spring;

import java.util.List;

import org.activiti.engine.form.AbstractFormType;
import org.springframework.beans.factory.annotation.Autowired;

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

}
