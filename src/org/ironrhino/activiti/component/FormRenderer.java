package org.ironrhino.activiti.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.FormType;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.EnumFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class FormRenderer {

	public List<FormElement> render(StartFormData startFormData) {
		return render(startFormData.getFormProperties());
	}

	public List<FormElement> render(TaskFormData taskFormData) {
		return render(taskFormData.getFormProperties());
	}

	@SuppressWarnings("unchecked")
	protected List<FormElement> render(List<FormProperty> formProperties) {
		List<FormElement> list = new ArrayList<FormElement>();
		for (FormProperty fp : formProperties) {
			FormElement fe = new FormElement();
			fe.setName(fp.getId());
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
				fe.addCssClass("integer");
			} else if (type instanceof StringFormType) {
			}
			list.add(fe);
		}
		return list;
	}

	public static class FormElement {

		private String label;

		private String name;

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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
