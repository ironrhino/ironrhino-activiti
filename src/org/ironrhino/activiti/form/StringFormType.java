package org.ironrhino.activiti.form;

public class StringFormType extends NamedFormType {

	private static final long serialVersionUID = 7285802771387869197L;

	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

}