package org.ironrhino.activiti.form;

public class StringFormType extends NamedFormType {

	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

}