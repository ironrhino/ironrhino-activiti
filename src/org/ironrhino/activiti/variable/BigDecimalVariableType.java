package org.ironrhino.activiti.variable;

import java.math.BigDecimal;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalVariableType implements VariableType {

	public String getTypeName() {
		return "bigDecimal";
	}

	public boolean isCachable() {
		return true;
	}

	public Object getValue(ValueFields valueFields) {
		return valueFields.getDoubleValue() != null ? new BigDecimal(
				valueFields.getDoubleValue()) : null;
	}

	public void setValue(Object value, ValueFields valueFields) {
		if (value == null) {
			valueFields.setDoubleValue(null);
		} else {
			valueFields.setDoubleValue(((BigDecimal) value).doubleValue());
		}
	}

	public boolean isAbleToStore(Object value) {
		return value != null && value.getClass() == BigDecimal.class;
	}
}