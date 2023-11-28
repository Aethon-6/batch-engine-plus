package com.engine.core.execute.redis.service.impl;

import com.engine.core.execute.redis.service.IRedisService;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeRelationVO;
import com.engine.entity.vo.NodeVO;
import com.engine.entity.vo.RelevanceInfoVO;
import com.engine.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements IRedisService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${engine.batch.temp-table-pre}")
    private String TEMP_TABLE_PRE;


    @Override
    public void addNodeNextIdToRedis(String nodeId, String onlyId, List<NodeRelationVO> relations) {
        List<String> nextNodeIds = new ArrayList<>();
        List<NodeRelationVO> list = relations.stream().filter(r -> nodeId.equals(r.getSourceNode())).collect(Collectors.toList());
        if (list.size() == 0) {
            return;
        }
        for (NodeRelationVO r : list) {
            nextNodeIds.add(r.getDestNode() + "_" + onlyId);
        }
        redisTemplate.opsForList().rightPushAll("nextNodeIds", nextNodeIds.toArray(new String[]{}));
        redisTemplate.expire("nextNodeIds", DateUtil.calcDayLeftoverTime(), TimeUnit.SECONDS);
    }

    @Override
    public RelevanceInfoVO queryRelevanceInfoFromRedis(String onlyId) {
        return (RelevanceInfoVO) redisTemplate.opsForHash().get("associates", onlyId);
    }

    @Override
    public void initRedis(Map<String, Object> nodeMap, Map<String, Object> associatedMap, List<String> nextNodeIds) {
        boolean r = true;
        redisTemplate.opsForHash().putAll("nodes", nodeMap);
        redisTemplate.opsForHash().putAll("associates", associatedMap);
        redisTemplate.opsForList().rightPushAll("nextNodeIds", nextNodeIds.toArray(new String[]{}));

        this.initTable();

    }

    @Override
    public NodeVO getNodeFromRedis(String nodeId) {
        return (NodeVO) redisTemplate.opsForHash().get("nodes", nodeId);
    }

    /**
     * 初始化临时表
     */
    private void initTable() {
        //实例化脚本对象
        DefaultRedisScript<String> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(String.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("initTable.lua"));
        //key的集合
        List<String> keys = List.of("tables");
        //执行lua脚本
        String table = ((String) stringRedisTemplate.execute(lua, keys, TEMP_TABLE_PRE));
    }

    @Override
    public String getFreeTableFromRedis(String onlyId) {
        //实例化脚本对象
        DefaultRedisScript<String> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(String.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("getTable.lua"));
        //key的集合
        List<String> keys = List.of("tables", "usedTables", "activeTable");
        //执行lua脚本
        String table = stringRedisTemplate.execute(lua, keys, onlyId);

        assert table != null;
        System.out.println(table.replaceAll("\"", ""));

        return table.replaceAll("\"", "");
    }

    @Override
    public void releaseTable(String table) {
        redisTemplate.opsForSet().remove("usedTables", table);
    }

    @Override
    public String getNextNode() {
        /*//实例化脚本对象
        DefaultRedisScript<List> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(List.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("getNextNodeId.lua"));
        //key的集合
        List<String> keys = List.of("nextNodeIds");
        //执行lua脚本
        List<String> nextNodeIds = (List<String>) stringRedisTemplate.execute(lua, keys);
        assert nextNodeIds != null;
        return nextNodeIds.get(0);*/
        return (String) redisTemplate.opsForList().leftPop("nextNodeIds", Duration.ZERO);
    }

    @Override
    public boolean executeNextNode(String nodeId, List<NodeRelationVO> relations, String onlyId) {
        List<NodeRelationVO> nextList = relations.stream().filter(r -> nodeId.equals(r.getSourceNode())).collect(Collectors.toList());
        if (nextList.size() <= 0) {
            return false;
        }
        String nextNodeId = nextList.get(0).getDestNode();
        List<NodeRelationVO> preList = relations.stream().filter(r -> nextNodeId.equals(r.getDestNode())).collect(Collectors.toList());
        int count = 0;
        for (NodeRelationVO r : preList) {
            if (redisTemplate.opsForList().indexOf("executedNodeIds", r.getSourceNode() + "_" + onlyId) >= 0) {
                count++;
            }
        }

        return preList.size() == count;
    }

    @Override
    public void addExecutedNode(String nodeId, String onlyId) {
        redisTemplate.opsForList().rightPush("executedNodeIds", nodeId + "_" + onlyId);
        redisTemplate.expire("nextNodeIds", DateUtil.calcDayLeftoverTime(), TimeUnit.SECONDS);
    }
}
