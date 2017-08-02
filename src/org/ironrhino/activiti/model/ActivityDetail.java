package org.ironrhino.activiti.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;

import lombok.Data;

@Data
public class ActivityDetail implements Serializable {

	private static final long serialVersionUID = 6503050675393528348L;

	private String name;

	private String assignee;

	private Date startTime;

	private Date endTime;

	private Map<String, String> data = new LinkedHashMap<String, String>();

	private List<Attachment> attachments;

	private List<Comment> comments;

}
