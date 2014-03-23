package org.ironrhino.process.form;

import org.activiti.engine.form.AbstractFormType;
import org.apache.commons.lang3.StringUtils;

public abstract class NamedFormType extends AbstractFormType {

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