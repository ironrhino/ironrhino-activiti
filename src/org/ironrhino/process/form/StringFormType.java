package org.ironrhino.process.form;

public class StringFormType extends NamedFormType {

	public Object convertFormValueToModelValue(String propertyValue) {
		return propertyValue;
	}

}