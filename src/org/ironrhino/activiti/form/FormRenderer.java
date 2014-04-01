package org.ironrhino.activiti.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

	public Map<String, FormElement> render(StartFormData startFormData) {
		return render(startFormData.getFormProperties());
	}

	public Map<String, FormElement> render(TaskFormData taskFormData) {
		return render(taskFormData.getFormProperties());
	}

	@SuppressWarnings("unchecked")
	protected Map<String, FormElement> render(List<FormProperty> formProperties) {
		ValueStack vs = null;
		if (ActionContext.getContext() != null)
			vs = ActionContext.getContext().getValueStack();
		Map<String, FormElement> elements = new LinkedHashMap<String, FormElement>();
		for (FormProperty fp : formProperties) {
			if (vs != null && fp.getValue() != null)
				vs.set(fp.getId(), fp.getValue());
			FormElement fe = new FormElement();
			elements.put(fp.getId(), fe);
			fe.setValue(fp.getValue());
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
			} else if (type instanceof LongFormType) {
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
			} else if (type instanceof DecimalFormType) {
				fe.setInputType("number");
				fe.addCssClass("double");
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
					Iterator<Element> it = el.find("process", "userTask")
							.iterator();
					while (it.hasNext()) {
						Element ele = it.next();
						if (activityId.equals(ele.attr("id"))) {
							el = ele;
							break;
						}
					}
					if (el != null) {
						it = el.find("extensionElements", "formProperty")
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

	public static class FormElement implements Serializable {

		private static final long serialVersionUID = -1192332576680858062L;

		private String label;

		private String value;

		private String type = "input"; // input textarea select radio

		private String inputType = "text";

		private boolean required;

		private boolean readonly;

		private boolean disabled;

		private Map<String, String> values;

		private Set<String> cssClasses = new LinkedHashSet<String>();

		private Map<String, String> dynamicAttributes = new LinkedHashMap<String, String>();

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getInputType() {
			return inputType;
		}

		public void setInputType(String inputType) {
			this.inputType = inputType;
		}

		public boolean isRequired() {
			return required;
		}

		public void setRequired(boolean required) {
			this.required = required;
		}

		public boolean isReadonly() {
			return readonly;
		}

		public void setReadonly(boolean readonly) {
			this.readonly = readonly;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		public Map<String, String> getValues() {
			return values;
		}

		public void setValues(Map<String, String> values) {
			this.values = values;
		}

		public Set<String> getCssClasses() {
			return cssClasses;
		}

		public void setCssClasses(Set<String> cssClasses) {
			this.cssClasses = cssClasses;
		}

		public void addCssClass(String cssClass) {
			this.cssClasses.add(cssClass);
		}

		public String getCssClass() {
			return StringUtils.join(cssClasses, " ");
		}

		public Map<String, String> getDynamicAttributes() {
			return dynamicAttributes;
		}

		public void setDynamicAttributes(Map<String, String> dynamicAttributes) {
			this.dynamicAttributes = dynamicAttributes;
		}

		public void setDynamicAttribute(String key, String value) {
			this.dynamicAttributes.put(key, value);
		}

	}

}
