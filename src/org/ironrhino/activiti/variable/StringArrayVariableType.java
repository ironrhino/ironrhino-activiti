package org.ironrhino.activiti.variable;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.util.JsonUtils;
import org.springframework.stereotype.Component;

@Component
public class StringArrayVariableType implements VariableType {

	@Override
	public String getTypeName() {
		return "stringArray";
	}

	@Override
	public boolean isCachable() {
		return true;
	}

	@Override
	public Object getValue(ValueFields valueFields) {
		String value = valueFields.getTextValue();
		if (StringUtils.isNotBlank(value)) {
			try {
				return JsonUtils.fromJson(value, JsonUtils.STRING_LIST_TYPE)
						.toArray(new String[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void setValue(Object value, ValueFields valueFields) {
		if (value == null) {
			valueFields.setTextValue(null);
		} else {
			valueFields.setTextValue(JsonUtils.toJson(value));
		}
	}

	@Override
	public boolean isAbleToStore(Object value) {
		return value != null && value.getClass() == String[].class;
	}
}