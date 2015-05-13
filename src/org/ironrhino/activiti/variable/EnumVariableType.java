package org.ironrhino.activiti.variable;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumVariableType implements VariableType {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public String getTypeName() {
		return "enum";
	}

	@Override
	public boolean isCachable() {
		return true;
	}

	@Override
	public Object getValue(ValueFields valueFields) {
		if (valueFields.getTextValue() == null)
			return null;
		try {
			return Enum.valueOf(
					(Class) Class.forName(valueFields.getTextValue2()),
					valueFields.getTextValue());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public void setValue(Object value, ValueFields valueFields) {
		if (value == null) {
			valueFields.setTextValue(null);
		} else {
			Enum en = (Enum) value;
			valueFields.setTextValue(en.name());
			Class<?> clz = en.getClass();
			if (clz.getEnclosingClass() != null)
				clz = clz.getEnclosingClass();
			valueFields.setTextValue2(clz.getName());
		}
	}

	@Override
	public boolean isAbleToStore(Object value) {
		return value != null && value instanceof Enum;
	}
}