package org.ironrhino.activiti.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.EnumFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.eaxy.Document;
import org.eaxy.Element;
import org.eaxy.Xml;
import org.ironrhino.common.support.DictionaryControl;
import org.ironrhino.core.struts.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;

@Component
public class FormRenderer {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired(required = false)
	private List<PersistableFormType<?>> persistableFormTypeList;

	@Autowired(required = false)
	private List<org.ironrhino.activiti.form.EnumFormType<?>> enumFormTypeList;

	public Map<String, FormElement> render(StartFormData startFormData) {
		return render(startFormData.getFormProperties());
	}

	public Map<String, FormElement> render(TaskFormData taskFormData) {
		return render(taskFormData.getFormProperties());
	}

	@SuppressWarnings("unchecked")
	protected Map<String, FormElement> render(List<FormProperty> formProperties) {
		ValueStack vs = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		if (ActionContext.getContext() != null)
			vs = ActionContext.getContext().getValueStack();
		Map<String, FormElement> elements = new LinkedHashMap<String, FormElement>();
		for (FormProperty fp : formProperties) {
			if (vs != null && fp.getValue() != null)
				vs.set(fp.getId(), fp.getValue());
			FormElement fe = new FormElement();
			elements.put(fp.getId(), fe);
			fe.setValue(fp.getValue());
			if (StringUtils.isBlank(fe.getValue()) && request != null
					&& StringUtils.isNotBlank(request.getParameter(fp.getId())))
				fe.setValue(request.getParameter(fp.getId()));
			String label = fp.getName();
			if (StringUtils.isBlank(label))
				label = fp.getId();
			fe.setLabel(label);
			if (fp.isRequired()) {
				fe.setRequired(fp.isRequired());
				fe.addCssClass("required");
			}
			if (!fp.isWritable())
				fe.setDisabled(true);
			FormType type = fp.getType();
			if (type instanceof EnumFormType) {
				fe.setType("select");
				fe.setValues((Map<String, String>) type
						.getInformation("values"));
			} else if (type instanceof DateFormType) {
				DateFormType dft = (DateFormType) type;
				String datePattern = (String) dft.getInformation("datePattern");
				fe.addCssClass("date");
				fe.setDynamicAttribute("data-format", datePattern);
			} else if (type instanceof BooleanFormType) {
				fe.setType("radio");
				Map<String, String> values = new LinkedHashMap<String, String>();
				values.put("true", "true");
				values.put("false", "false");
				fe.setValues(values);
			} else if (type instanceof LongFormType
					|| type instanceof IntegerFormType) {
				fe.setInputType("number");
				fe.addCssClass("integer");
			} else if (type instanceof StringFormType) {
			} else if (type instanceof TextareaFormType) {
				fe.setType("textarea");
				fe.addCssClass("input-xxlarge");
			} else if (type instanceof DictionaryFormType) {
				try {
					DictionaryControl dc = applicationContext
							.getBean(DictionaryControl.class);
					fe.setType("select");
					Map<String, String> map = dc.getItemsAsMap(fp.getId());
					for (Map.Entry<String, String> entry : map.entrySet()) {
						if (StringUtils.isBlank(entry.getValue())) {
							String value = LocalizedTextUtil.findText(
									getClass(), entry.getKey(), ActionContext
											.getContext().getLocale(), entry
											.getKey(), null);
							entry.setValue(value);
						}
					}
					fe.setValues(map);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else if (type instanceof BigDecimalFormType) {
				fe.setInputType("number");
				if (fp.getId().toLowerCase().endsWith("rate")) {
					fe.getDynamicAttributes().put("data-scale", "8");
					fe.getDynamicAttributes().put("step", "0.00000001");
				}
				fe.addCssClass("double");
			} else if (type instanceof PersistableFormType) {
				PersistableFormType<?> pft = (PersistableFormType<?>) type;
				fe.setType("listpick");
				fe.getDynamicAttributes().put("pickUrl", pft.getPickUrl());
			} else if (type instanceof org.ironrhino.activiti.form.EnumFormType) {
				org.ironrhino.activiti.form.EnumFormType<?> eft = (org.ironrhino.activiti.form.EnumFormType<?>) type;
				fe.setType("enum");
				fe.getDynamicAttributes().put("enumType",
						eft.getEnumType().getName());
			}
		}
		return elements;
	}

	public Map<String, String> display(String processDefinitionId,
			String activityId, Map<String, String> data) {
		List<Property> formProperties = getFormProperties(processDefinitionId,
				activityId);
		if (formProperties == null || formProperties.isEmpty())
			return data;
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Property fp : formProperties) {
			String value = data.get(fp.getId());
			if (value == null)
				continue;
			String name = fp.getName();
			if (StringUtils.isBlank(name))
				name = fp.getId();
			String type = fp.getType();
			if ("boolean".equalsIgnoreCase(type)) {
				value = I18N.getText(value);
			} else if ("enum".equalsIgnoreCase(type)) {
				Map<String, String> temp = fp.getValues();
				if (temp != null) {
					String v = temp.get(value);
					if (StringUtils.isNotBlank(v))
						value = v;
				}
			} else if ("dictionary".equalsIgnoreCase(type)) {
				try {
					DictionaryControl dc = applicationContext
							.getBean(DictionaryControl.class);
					Map<String, String> temp = dc.getItemsAsMap(fp.getId());
					if (temp != null) {
						String v = temp.get(value);
						if (StringUtils.isNotBlank(v))
							value = v;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			} else {
				boolean found = false;
				if (persistableFormTypeList != null) {
					for (PersistableFormType<?> ft : persistableFormTypeList) {
						if (ft.getName().equals(type)) {
							Object v = ft.convertFormValueToModelValue(value);
							if (v != null)
								value = v.toString();
							found = true;
							break;
						}
					}
				}
				if (!found && enumFormTypeList != null) {
					for (org.ironrhino.activiti.form.EnumFormType<?> ft : enumFormTypeList) {
						if (ft.getName().equals(type)) {
							Object v = ft.convertFormValueToModelValue(value);
							if (v != null)
								value = v.toString();
							found = true;
							break;
						}
					}
				}
			}
			map.put(name, value);
		}
		return map;
	}

	Map<String, List<Property>> cache = new ConcurrentHashMap<>();

	private List<Property> getFormProperties(String processDefinitionId,
			String activityId) {
		String key = new StringBuilder(activityId).append("@")
				.append(processDefinitionId).toString();
		List<Property> list = cache.get(key);
		if (list == null) {
			list = new ArrayList<Property>();
			ProcessDefinition pd = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(processDefinitionId).singleResult();
			if (pd != null) {

				try (InputStream resourceAsStream = repositoryService
						.getResourceAsStream(pd.getDeploymentId(),
								pd.getResourceName())) {
					Document doc = Xml.read(new InputStreamReader(
							resourceAsStream, "UTF-8"));
					Element el = doc.getRootElement();
					Element elut = null;
					Iterator<Element> it = el.find("process", "startEvent")
							.iterator();
					while (it.hasNext()) {
						Element ele = it.next();
						if (activityId.equals(ele.attr("id"))) {
							elut = ele;
							break;
						}
					}
					if (elut == null) {
						it = el.find("process", "userTask").iterator();
						while (it.hasNext()) {
							Element ele = it.next();
							if (activityId.equals(ele.attr("id"))) {
								elut = ele;
								break;
							}
						}
					}
					if (elut != null) {
						it = elut.find("extensionElements", "formProperty")
								.iterator();
						while (it.hasNext()) {
							list.add(new Property(it.next()));
						}
					}
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			cache.put(key, list);
		}
		return list;
	}

	private static class Property implements Serializable {

		private static final long serialVersionUID = 3962888136575115773L;

		private String id;

		private String name;

		private String type;

		private Map<String, String> values;

		public Property(Element element) {
			this.id = element.attr("id");
			this.name = element.attr("name");
			this.type = element.attr("type");
			if ("enum".equals(this.type)) {
				Iterator<Element> it = element.find("value").iterator();
				values = new LinkedHashMap<String, String>();
				while (it.hasNext()) {
					Element ele = it.next();
					values.put(ele.attr("id"), ele.attr("name"));
				}
			}
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public Map<String, String> getValues() {
			return values;
		}

	}

}
