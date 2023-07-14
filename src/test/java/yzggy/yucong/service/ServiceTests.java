package yzggy.yucong.service;

import com.unfbx.chatgpt.entity.chat.Functions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class ServiceTests {

    @Autowired
    private FuncService funcService;
    private final String botId = "bot002";
    private final String userId = "user001";

    @Test
    public void getFuncList() {
        List<Functions> functions = this.funcService.getListByUserIdAndBotId(userId, botId);
        log.info("{}", functions);
    }
}
