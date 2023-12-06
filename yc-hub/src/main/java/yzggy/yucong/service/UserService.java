package yzggy.yucong.service;

public interface UserService {

    Long getUserByUsername(String username);

    Long createUser(String username);

    Long getAccountNId(Long userId, Long botId);

    Long createAccount(String accountName, Long userNId, Long botUId);

    void addAccountRoleRelation(String roleName, Long accountNId);
}
