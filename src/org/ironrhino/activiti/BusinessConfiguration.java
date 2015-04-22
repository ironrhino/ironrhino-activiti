package org.ironrhino.activiti;

import org.ironrhino.core.sequence.CyclicSequence;
import org.ironrhino.core.sequence.cyclic.DatabaseCyclicSequenceDelegate;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusinessConfiguration {

	@Bean(autowire = Autowire.BY_NAME)
	public CyclicSequence businessKeySequence() {
		return new DatabaseCyclicSequenceDelegate();
	}

}