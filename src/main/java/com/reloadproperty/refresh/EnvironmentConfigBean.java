
package com.reloadproperty.refresh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentConfigBean {

	@Autowired
	private Environment environment;

	public String getProperty(String key) {
		return environment.getProperty(key);
	}
}
