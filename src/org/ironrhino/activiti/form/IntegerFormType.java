package org.ironrhino.activiti.form;

import org.springframework.stereotype.Component;

@Component
public class IntegerFormType extends NamedFormType {

	public Object convertFormValueToModelValue(String propertyValue) {
		try {
			return Integer.valueOf(propertyValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}