
package com.reloadproperty.refresh;

import lombok.SneakyThrows;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.springframework.core.env.PropertySource;

public class ReloadablePropertySource extends PropertySource {

    private ReloadingFileBasedConfigurationBuilder<?> propertyBuilder;

    public ReloadablePropertySource(String sourceName,
                                    ReloadingFileBasedConfigurationBuilder<?> propertyBuilder) {
        super(sourceName);
        this.propertyBuilder = propertyBuilder;
    }

    @SneakyThrows
    @Override
    public Object getProperty(String s) {
        return propertyBuilder.getConfiguration()
                .getProperty(s);
    }
}
