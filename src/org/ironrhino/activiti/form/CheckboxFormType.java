package org.ironrhino.activiti.form;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CheckboxFormType extends NamedFormType {

	public static final String DELIMITER = ",";
	public static final String DELIMITER_FOR_DISPLAY = " ";

	private static final long serialVersionUID = -8137910373239607783L;

	@Override
	public Object convertFormValueToModelValue(String str) {
		if (StringUtils.isBlank(str))
			return null;
		return str.split(DELIMITER);
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null || !(modelValue instanceof String[]))
			return null;
		return StringUtils.join((String[]) modelValue, DELIMITER);
	}

}