package com.engine.batch.service.impl;

import com.alibaba.fastjson.JSON;
import com.engine.active.entity.EngineLabelConfig;
import com.engine.batch.service.IBatchService;
import com.engine.core.execute.common.service.ICommonService;
import com.engine.core.execute.node.service.INodeService;
import com.engine.core.execute.redis.service.IRedisService;
import com.engine.entity.vo.*;
import com.engine.utils.FileUtil;
import com.engine.utils.R;
import com.engine.utils.SnowflakeIdWorker;
import ma.glasnost.orika.MapperFacade;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class BatchServiceImpl implements IBatchService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private INodeService nodeService;

    @Resource
    private ICommonService commonService;

    @Resource
    private MapperFacade mapperFacade;

    @Resource
    private IRedisService redisService;

    @Override
    public R init() {
        /*taskScheduler.schedule(() -> {
            executor.execute(this::doInit);
        }, new CronTrigger("0/10 * * * * ?"));*/

        this.doInit();

        return R.success();
    }

    private void doInit() {
        String activeStr = null;
        //todo 获取活动执行唯一编号
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 3);
        String id = Long.toBinaryString(idWorker.nextId());

        try {
            activeStr = FileUtil.readFileContent("active.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*if (commonService.jsonValidator(activeStr)){
            return;
        }*/

        ActiveVO active = JSON.parseObject(activeStr, ActiveVO.class);

        assert active != null;

        List<NodeRelationVO> relations = active.getNodeRelations();
        List<EngineLabelConfig> labelConfigList = commonService.extractLabel(active.getNodes(), active.getNodeRelations());

        List<LabelInfoVO> labelInfoList = mapperFacade.mapAsList(labelConfigList, LabelInfoVO.class);

        RelevanceInfoVO associated = RelevanceInfoVO.builder()
                .labels(labelInfoList)
                .relations(relations)
                .build();

        Map<String, Object> nodeMap = new HashMap<>();
        /* ArrayList<String> nodeIds = new ArrayList<>(); */
        List<String> nextNodeIds = new ArrayList<>();
        Map<String, Object> associatedMap = new HashMap<String, Object>();
        associatedMap.put(id, associated);

        for (NodeVO node : active.getNodes()) {
            /* nodeIds.add(node.getNodeId()); */
            nodeMap.put(node.getNodeId(), node);
            if ("begin".equals(node.getProperty())) {
                nextNodeIds.add(node.getNodeId() + "_" + id);
            }
        }
        /* redisTemplate.opsForList().rightPushAll("nodeIds", nodeIds.toArray(new String[]{})); */
        redisService.initRedis(nodeMap, associatedMap, nextNodeIds);
    }

    @Override
    public R execute() {
        /*taskScheduler.schedule(() -> {
            executor.execute(this::doExecute);
        }, new CronTrigger("0/10 * * * * ?"));*/
        taskScheduler.schedule(this::doExecute, new CronTrigger("0/10 * * * * ?"));
        return R.success();
    }

    private void doExecute() {
        String nextNodeId = redisService.getNextNode();
        String[] id = nextNodeId.split("_");
        NodeVO node = redisService.getNodeFromRedis(id[0]);
        String table = redisService.getFreeTableFromRedis(id[1]);
        nodeService.execute(node, id[1], table);
        System.out.println(node);
    }
}
