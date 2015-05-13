package org.ironrhino.activiti.variable;

import java.math.BigDecimal;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalVariableType implements VariableType {

	@Override
	public String getTypeName() {
		return "bigDecimal";
	}

	@Override
	public boolean isCachable() {
		return true;
	}

	@Override
	public Object getValue(ValueFields valueFields) {
		return valueFields.getDoubleValue() != null ? new BigDecimal(
				valueFields.getDoubleValue()) : null;
	}

	@Override
	public void setValue(Object value, ValueFields valueFields) {
		if (value == null) {
			valueFields.setDoubleValue(null);
		} else {
			valueFields.setDoubleValue(((BigDecimal) value).doubleValue());
		}
	}

	@Override
	public boolean isAbleToStore(Object value) {
		return value != null && value.getClass() == BigDecimal.class;
	}
}