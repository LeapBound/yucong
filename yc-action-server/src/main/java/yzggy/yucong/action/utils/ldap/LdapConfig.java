package yzggy.yucong.action.utils.ldap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.Hashtable;

/**
 * @author yamath
 * @since 2023/7/17 18:01
 */
@Configuration
public class LdapConfig {

    @Value("${spring.ldap.base}")
    private String base;
    @Value("${spring.ldap.urls}")
    private String urls;
    @Value("${spring.ldap.username}")
    private String userDn;
    @Value("${spring.ldap.password}")
    private String password;

    @Bean
    LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUserDn(userDn);
        ldapContextSource.setBase(base);
        ldapContextSource.setUrl(urls);
        ldapContextSource.setPassword(password);
        //
        Hashtable<String, Object> baseEnvMaps = new Hashtable<>();
        baseEnvMaps.put("java.naming.ldap.attributes.binary", "objectSid");
        ldapContextSource.setBaseEnvironmentProperties(baseEnvMaps);
        return ldapContextSource;
    }

    @Bean
    LdapTemplate ldapTemplate(@Qualifier("ldapContextSource") LdapContextSource ldapContextSource) {
        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        ldapTemplate.setIgnorePartialResultException(true);
        return ldapTemplate;
    }
}
