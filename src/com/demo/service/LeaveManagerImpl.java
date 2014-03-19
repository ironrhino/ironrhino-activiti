package com.demo.service;

import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.service.BaseManager;
import org.ironrhino.core.service.BaseManagerImpl;

import com.demo.model.Leave;

@AutoConfig
public class LeaveManagerImpl extends BaseManagerImpl<Leave> implements
		BaseManager<Leave> {

}
