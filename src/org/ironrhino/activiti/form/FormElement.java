package org.ironrhino.activiti.form;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class FormElement implements Serializable {

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