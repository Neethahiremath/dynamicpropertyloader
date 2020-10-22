package com.reloadproperty.controller;

import com.reloadproperty.refresh.EnvironmentConfigBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/refresh")
@Slf4j
public class RefreshController {

    @Autowired
    EnvironmentConfigBean environmentConfigBean;
    @Autowired
    private ConversionService conversionService;

    @GetMapping()
    public Integer getConfig() {
        return getValue("kafkaconfiguration.session-timeout-ms", Integer.class);
    }

    private <T> T getValue(String propertyName, Class<T> objectType) {
        String value = environmentConfigBean.getProperty(propertyName);
        if (Objects.isNull(value)) {
          //  log.info("value is null");
        }
        return conversionService.convert(value, objectType);
    }
}
