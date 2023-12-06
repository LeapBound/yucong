package yzggy.yucong.action.func.account;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import yzggy.yucong.action.utils.ldap.LdapAccountService;
import yzggy.yucong.action.utils.ldap.LdapUserVo;
import yzggy.yucong.action.webClient.GutsHubService;

/**
 * functions what handle with account services
 * arguments for methods is JSONObject
 *
 * @author yamath
 * @since 2023/7/12 10:28
 */
@Component
public class AccountFunctions {

    private static final Logger logger = LoggerFactory.getLogger(AccountFunctions.class);

    private static final String KEY_ACCOUNT = "account";
    private static final String KEY_USERNAME = "username";

    private final LdapAccountService ldapAccountService;
    private final GutsHubService gutsHubService;

    public AccountFunctions(LdapAccountService ldapAccountService,
                            GutsHubService gutsHubService) {
        this.ldapAccountService = ldapAccountService;
        this.gutsHubService = gutsHubService;
    }

    /**
     * get ldap user info by account
     *
     * @param arguments parameters include account
     * @return result
     */
    public JSONObject getUserByAccount(JSONObject arguments) {
        logger.info("execute getUserAccount arguments = {}", arguments);
        JSONObject result = this.checkArguments(arguments, KEY_ACCOUNT);
        if (result != null) {
            return result;
        }
        result = new JSONObject();
        try {
            // get ldap account
            return this.ldapAccountService.getUserByAccount(arguments.getString(KEY_ACCOUNT));
        } catch (Exception ex) {
            logger.error("get ldap account error", ex);
            result.put("操作结果", "执行异常，联系管理员。");
            return result;
        }
    }

    /**
     * get ldap user info by name
     *
     * @param arguments parameters include name
     * @return result
     */
    public JSONObject getUserByName(JSONObject arguments) {
        logger.info("execute getUserAccount arguments = {}", arguments);
        JSONObject result = this.checkArguments(arguments, KEY_USERNAME);
        if (result != null) {
            return result;
        }
        result = new JSONObject();
        try {
            // get ldap account
            return this.ldapAccountService.getUserByName(arguments.getString(KEY_USERNAME));
        } catch (Exception ex) {
            logger.error("get ldap account error", ex);
            result.put("操作结果", "执行异常，联系管理员。");
            return result;
        }
    }

    /**
     * notice to close a user's account
     *
     * @param arguments argument include `account`
     * @return result
     */
    public JSONObject closeUserAccount(JSONObject arguments) {
        logger.info("execute closeUserAccount arguments = {}", arguments);
        JSONObject result = this.checkArguments(arguments, KEY_ACCOUNT);
        if (result != null) {
            return result;
        }
        result = new JSONObject();
        try {
            // disable ldap account
            String account = arguments.getString(KEY_ACCOUNT);
            LdapUserVo vo = this.ldapAccountService.closeLdapAccountByAccount(arguments.getString(KEY_ACCOUNT));
            // disable ldap success
            if (vo != null && StrUtil.isEmptyIfStr(vo.getError())) {
                result.put("LDAP操作结果", "LDAP 用户账号已关闭");
                // close sales user account
                boolean sales = false;
                JSONObject jsonObject = this.gutsHubService.closeSalesAccount(vo.getCommonName(), account);
                if (jsonObject.containsKey("status")) {
                    // success
                    if (jsonObject.getBooleanValue("status")) {
                        sales = true;
                    }
                }
                if (sales) {
                    result.put("销售账号操作结果", "销售账号已关闭");
                } else {
                    result.put("销售账号操作结果", "销售账号关闭失败");
                }
            } else {
                result.put("操作结果", vo == null ? "LDAP 账号关闭异常" : vo.getError());
            }
            return result;
        } catch (Exception ex) {
            logger.error("disable account error", ex);
            result.put("操作结果", "执行异常，联系管理员。");
            return result;
        }
    }

    /**
     * re-enable user's account
     *
     * @param arguments arguments include `account`
     * @return result
     */
    public JSONObject enableUserAccount(JSONObject arguments) {
        logger.info("execute enableUserAccount arguments = {}", arguments);
        JSONObject result = this.checkArguments(arguments, KEY_ACCOUNT);
        if (result != null) {
            return result;
        }
        result = new JSONObject();
        try {
            // enable account
            return this.ldapAccountService.enableLdapAccount(arguments.getString(KEY_ACCOUNT));
        } catch (Exception ex) {
            logger.error("enable account error", ex);
            result.put("操作结果", "执行异常，联系管理员。");
            return result;
        }
    }

    /**
     * check function arguments which include `account`
     *
     * @param arguments account or name
     * @param key       key
     * @return json
     */
    private JSONObject checkArguments(JSONObject arguments, String key) {
        JSONObject result = new JSONObject();
        if (arguments == null) {
            result.put("错误", "没有提供足够信息。要求用户提供必要信息");
            return result;
        }
        if (!arguments.containsKey(key)
                || StrUtil.isEmptyIfStr(arguments.getString(key))) {
            result.put("错误", String.format("没有提供 %s，要求用户提供 %s.", key, key));
            return result;
        }
        return null;
    }
}
