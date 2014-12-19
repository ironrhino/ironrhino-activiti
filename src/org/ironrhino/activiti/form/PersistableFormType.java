package org.ironrhino.activiti.form;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.model.Persistable;
import org.ironrhino.core.service.EntityManager;
import org.ironrhino.core.struts.EntityClassHelper;
import org.ironrhino.core.util.ReflectionUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

@SuppressWarnings("unchecked")
public class PersistableFormType<T extends Persistable<?>> extends
		NamedFormType {

	private static final long serialVersionUID = 252383786159480042L;

	@Autowired
	private EntityManager<T> entityManager;

	@Autowired(required = false)
	private ConversionService conversionService;

	private Class<T> entityClass;

	private BeanWrapperImpl bw;

	@PostConstruct
	public void init() {
		Class<T> clazz = (Class<T>) ReflectionUtils.getGenericClass(getClass());
		if (clazz != null)
			entityClass = clazz;
		else
			throw new NullPointerException("entityClass is null");
		try {
			bw = new BeanWrapperImpl(entityClass.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		bw.setConversionService(conversionService);
	}

	public String getPickUrl() {
		return EntityClassHelper.getPickUrl(entityClass);
	}

	@Override
	public T convertFormValueToModelValue(String id) {
		if (StringUtils.isBlank(id))
			return null;
		entityManager.setEntityClass(entityClass);
		bw.setPropertyValue("id", id);
		return entityManager.get((Serializable) bw.getPropertyValue("id"));
	}

	@Override
	public String convertModelValueToFormValue(Object modelValue) {
		if (modelValue == null)
			return null;
		else
			return String.valueOf(((T) modelValue).getId());
	}

}