package org.ironrhino.activiti.variable;

import java.io.Serializable;

import org.activiti.engine.impl.variable.ValueFields;
import org.activiti.engine.impl.variable.VariableType;
import org.ironrhino.core.model.Persistable;
import org.ironrhino.core.service.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@SuppressWarnings("unchecked")
@Component
public class PersistableVariableType implements VariableType {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EntityManager<?> entityManager;

	@Override
	public String getTypeName() {
		return "persistable";
	}

	@Override
	public boolean isCachable() {
		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getValue(ValueFields valueFields) {
		String entityClassName = valueFields.getTextValue2();
		Class entityClass = null;
		Class idType = null;
		try {
			entityClass = Class.forName(entityClassName);
			idType = entityClass.getMethod("getId").getReturnType();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (entityClass == null)
			return null;
		Serializable id = null;
		if (valueFields.getLongValue() != null) {
			Long value = valueFields.getLongValue();
			if (value != null
					&& (idType == Integer.class || idType == Integer.TYPE))
				id = value.intValue();
			else
				id = value;
		} else if (valueFields.getTextValue() != null) {
			id = valueFields.getTextValue();
		}
		entityManager.setEntityClass(entityClass);
		return entityManager.get(id);
	}

	@Override
	public void setValue(Object value, ValueFields valueFields) {
		if (value == null) {
			valueFields.setLongValue(null);
			valueFields.setTextValue(null);
			valueFields.setTextValue2(null);
		} else {
			valueFields.setTextValue2(value.getClass().getName());
			Persistable<?> entity = (Persistable<?>) value;
			Serializable id = entity.getId();
			if (id != null) {
				if (id instanceof Number) {
					valueFields.setLongValue(((Number) id).longValue());
				} else {
					valueFields.setTextValue(id.toString());
				}
			}
		}
	}

	@Override
	public boolean isAbleToStore(Object value) {
		return value != null
				&& Persistable.class.isAssignableFrom(value.getClass());
	}
}