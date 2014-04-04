package org.ironrhino.activiti.form;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.ReflectionUtils;

@SuppressWarnings("unchecked")
public class EnumFormType<T extends Enum<T>> extends NamedFormType {

	private Class<T> enumType;

	@PostConstruct
	public void init() {
		Class<T> clazz = (Class<T>) ReflectionUtils.getGenericClass(getClass());
		if (clazz != null)
			enumType = clazz;
		else
			throw new NullPointerException("enumClass is null");
	}

	public Class<T> getEnumType() {
		return enumType;
	}

	@Override
	public T convertFormValueToModelValue(String value) {
		if (StringUtils.isBlank(value))
			return null;
		return Enum.valueOf(enumType, value);
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null)
			return null;
		else
			return String.valueOf(((T) modelValue).name());
	}

}