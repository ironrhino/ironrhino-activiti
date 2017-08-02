package org.ironrhino.activiti.form;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class FormElement implements Serializable {

	private static final long serialVersionUID = -1192332576680858062L;

	private String id;

	private String label;

	private String value;

	private String[] arrayValues;

	private String type = "input"; // input textarea select radio

	private String inputType = "text";

	private boolean required;

	private boolean readonly;

	private boolean disabled;

	private Map<String, String> values;

	private Set<String> cssClasses = new LinkedHashSet<String>();

	private Map<String, String> dynamicAttributes = new LinkedHashMap<String, String>();

	public void addCssClass(String cssClass) {
		this.cssClasses.add(cssClass);
	}

	public String getCssClass() {
		return StringUtils.join(cssClasses, " ");
	}

	public void setDynamicAttribute(String key, String value) {
		this.dynamicAttributes.put(key, value);
	}

}