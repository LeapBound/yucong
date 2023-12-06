package yzggy.yucong.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yzggy.yucong.entities.BotEntity;
import yzggy.yucong.entities.ChannelEntity;
import yzggy.yucong.mapper.BotMapper;
import yzggy.yucong.mapper.ChannelMapper;
import yzggy.yucong.model.BotModel;
import yzggy.yucong.service.BotService;
import yzggy.yucong.utils.bean.BotBeanMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    private final BotMapper botMapper;
    private final ChannelMapper channelMapper;

    @Override
    public List<BotModel> listAll() {
        QueryWrapper<BotEntity> qw = new QueryWrapper<>();
        List<BotEntity> botEntityList = this.botMapper.selectList(qw);
        if (botEntityList != null && !botEntityList.isEmpty()) {
            List<BotModel> botModelList = new ArrayList<>(botEntityList.size());
            botEntityList.forEach(botEntity -> botModelList.add(mapBotEntityToModel(botEntity)));
            return botModelList;
        }
        return null;
    }

    @Override
    public String getBotId(String corpId, String agentId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, corpId)
                .eq(ChannelEntity::getAgentId, agentId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);
        if (channelEntity == null) {
            return null;
        }

        return channelEntity.getBotId();
    }

    @Override
    public String getBotId(String appId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<ChannelEntity>()
                .eq(ChannelEntity::getCorpId, appId)
                .last("limit 1");
        ChannelEntity channelEntity = this.channelMapper.selectOne(queryWrapper);
        if (channelEntity == null) {
            return null;
        }

        return channelEntity.getBotId();
    }

    @Override
    public Long getBotNIdByBotId(String botId) {
        LambdaQueryWrapper<BotEntity> queryWrapper = new LambdaQueryWrapper<BotEntity>()
                .eq(BotEntity::getBotId, botId)
                .last("limit 1");
        BotEntity botEntity = this.botMapper.selectOne(queryWrapper);
        if (botEntity == null) {
            return null;
        }

        return botEntity.getId();
    }

    @Override
    public void create(BotModel botModel) {
        BotEntity botEntity = BotBeanMapper.mapModelToEntity(botModel);
        botEntity.setCreateTime(new Date());
        this.botMapper.insert(botEntity);
    }

    private BotModel mapBotEntityToModel(BotEntity botEntity) {
        BotModel botModel = new BotModel();
        botModel.setBotId(botEntity.getBotId());
        botModel.setBotName(botEntity.getBotName());
        botModel.setInitContent(botEntity.getInitRoleContent());
        botModel.setCreateTime(botEntity.getCreateTime());
        return botModel;
    }
}
