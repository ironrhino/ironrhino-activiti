package org.ironrhino.activiti.form;

import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.StringUtils;

public abstract class NamedFormType extends AbstractFormType {

	private static final long serialVersionUID = 205082195400657629L;

	public String getName() {
		String name = getClass().getSimpleName();
		if (name.endsWith("FormType"))
			name = name.substring(0, name.length() - 8);
		return StringUtils.uncapitalize(name);
	}

	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null)
			return null;
		return modelValue.toString();
	}

}