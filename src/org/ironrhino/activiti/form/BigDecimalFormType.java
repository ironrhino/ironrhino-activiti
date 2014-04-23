package org.ironrhino.activiti.form;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class BigDecimalFormType extends NamedFormType {

	public Object convertFormValueToModelValue(String propertyValue) {
		try {
			return new BigDecimal(propertyValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}