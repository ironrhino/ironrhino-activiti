package org.ironrhino.activiti.form;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class BigDecimalFormType extends NamedFormType {

	private static final long serialVersionUID = -991358540521614893L;

	public Object convertFormValueToModelValue(String propertyValue) {
		try {
			return new BigDecimal(propertyValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}