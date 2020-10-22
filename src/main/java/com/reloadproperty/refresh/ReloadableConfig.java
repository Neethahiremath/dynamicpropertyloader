
package com.reloadproperty.refresh;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilderEvent;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ReloadableConfig {

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;
    @Value("${custom.config.refresh.enabled:false}")
    private boolean enableReloading;
    @Value("${custom.config.refresh.file-path:}")
    private String reloadFromFilePath;
    @Value("${custom.config.refresh.reload-interval:60}")
    private long reloadInterval;

    private PeriodicReloadingTrigger periodicReloadingTrigger;

    @PostConstruct
    public void setReloadablePropertySource() {
        if (enableReloading && Objects.nonNull(reloadFromFilePath)) {
            try {
                File propertiesFile = new File(reloadFromFilePath.substring("file:".length()));
                if (propertiesFile.exists()) {
                    log.info("Configure dynamic reloading of properties from file path - {}",
                            reloadFromFilePath);

                    ReloadingFileBasedConfigurationBuilder<?> builder
                            = getConfigurationBuilder(propertiesFile);

                    ReloadablePropertySource reloadablePropertySource
                            = new ReloadablePropertySource("reload", builder);

                    configurableEnvironment.getPropertySources()
                            .addFirst(reloadablePropertySource);

// Register an event listener for triggering reloading checks
                    builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST,
                            new EventListener()
                            {
                                @Override
                                public void onEvent(Event event)
                                {
                                    log.info("reload started");
                                    builder.getReloadingController().checkForReloading(null);
                                }
                            });

                    periodicReloadingTrigger
                            = new PeriodicReloadingTrigger(builder.getReloadingController(), null,
                            reloadInterval, TimeUnit.SECONDS);
                    periodicReloadingTrigger.start();

                    log.info("Successfully configured dynamic reload of properties from {}",
                            reloadFromFilePath);
                } else {
                    log.warn("Reloadable property file does not exist - {}", reloadFromFilePath);
                }
            } catch (Exception e) {
                log.error("Failed to configure dynamic reloadable properties from {} : ", reloadFromFilePath, e);
            }
        } else {
            log.info("Dynamic reloadable property configuration is disabled");
        }
    }

    @PreDestroy
    public void shutDownTrigger() {
        if (Objects.nonNull(periodicReloadingTrigger)) {
            periodicReloadingTrigger.shutdown();
        }
    }

    private ReloadingFileBasedConfigurationBuilder<?> getConfigurationBuilder(File file) throws Exception {
        Parameters params = new Parameters();
        return new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(getConfigClassByFileType(file))
                .configure(params.fileBased()
                        .setFile(file));
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    private Class getConfigClassByFileType(File file) throws Exception {
        String fileType = getFileExtension(file);
        Class classType;
        switch (fileType) {
            case "yml":
            case "yaml":
                classType = YAMLConfiguration.class;
                break;
            case "props":
            case "properties":
                classType = PropertiesConfiguration.class;
                break;
            default:
                throw new Exception("invalid file type, cannot decode");
        }
        return classType;
    }

}

