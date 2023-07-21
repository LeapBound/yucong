package yzggy.yucong.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import yzggy.yucong.entities.RoleEntity;

import java.util.List;

@Slf4j
@SpringBootTest
public class MapperTests {

    @Autowired
    private RoleMapper botRoleMapper;

    @Test
    public void getRoleByBot() {
        List<RoleEntity> roleEntityList = this.botRoleMapper.listRoleByBotId("bot001");
        log.info("{}", roleEntityList);
    }
}
