package yzggy.yucong.action.utils.classloader;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author yamath
 * @since 2023/7/4 17:09
 */
@Configuration
public class HotClassLoaderAutoConfigure {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HotClassLoader hotClassLoader() {
        return new HotClassLoader(this.getClass().getClassLoader());
    }
}
