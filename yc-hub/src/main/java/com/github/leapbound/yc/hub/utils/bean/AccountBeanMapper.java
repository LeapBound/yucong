package com.github.leapbound.yc.hub.utils.bean;

import com.github.leapbound.yc.hub.entities.AccountEntity;
import com.github.leapbound.yc.hub.model.AccountDto;

/**
 * @author Fred
 * @date 2024/6/30 23:51
 */
public class AccountBeanMapper {

    public static AccountDto mapEntityToModel(AccountEntity entity) {
        AccountDto dto = new AccountDto();

        dto.setAccountId(entity.getAccountId());

        return dto;
    }

}
