package yzggy.yucong.action.utils.classloader;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yzggy.yucong.action.model.vo.ResponseVo;

/**
 * @author yamath
 * @since 2023/7/5 10:12
 */
@RestController
@RequestMapping("/classloader/hot")
public class HotController {

    private static final Logger logger = LoggerFactory.getLogger(HotController.class);

    @PostMapping("/load/jar")
    public ResponseVo<Void> loadJar() {
        try {
            String msg = HotClassLoader.loadJar("D:\\var\\hotest\\hotest-1.0-SNAPSHOT.jar", null);
            if (!StrUtil.isEmptyIfStr(msg)) {
                return ResponseVo.fail(null, msg);
            }
        } catch (Exception ex) {
            logger.error("load jar error, {}", ex.getMessage(), ex);
            return ResponseVo.fail(null, "load jar error, " + ex.getMessage());
        }

        return ResponseVo.success(null, "load class success", null);
    }

    @PostMapping("/unload/jar")
    public ResponseVo<Void> unloadJar() {
        try {
            String msg = HotClassLoader.unloadJar("D:\\var\\hotest\\hotest-1.0-SNAPSHOT.jar");
            if (!StrUtil.isEmptyIfStr(msg)) {
                return ResponseVo.fail(null, msg);
            }
        } catch (Exception ex) {
            logger.error("unload jar error, {}", ex.getMessage(), ex);
            return ResponseVo.fail(null, "unload jar error," + ex.getMessage());
        }

        return ResponseVo.success(null, "unload class success", null);
    }
}
