package com.github.leapbound.yc.action.listen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import com.github.leapbound.yc.action.service.YcFunctionGroovyService;

/**
 * @author yamath
 * @date 2023/7/12 14:18
 */
@Component
public class ApplicationListenerImpl implements ApplicationListener<ApplicationStartedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationListenerImpl.class);

    private final YcFunctionGroovyService ycFunctionGroovyService;

    public ApplicationListenerImpl(YcFunctionGroovyService ycFunctionGroovyService) {
        this.ycFunctionGroovyService = ycFunctionGroovyService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        logger.info("Spring application starting ...");
        logger.info("Checking function groovy scripts ...");
        this.ycFunctionGroovyService.checkFunctionGroovyScripts();
        logger.info("Spring application started !");
    }
}
