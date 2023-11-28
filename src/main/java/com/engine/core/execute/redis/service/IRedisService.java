package com.engine.core.execute.redis.service;

import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeRelationVO;
import com.engine.entity.vo.NodeVO;
import com.engine.entity.vo.RelevanceInfoVO;

import java.util.List;
import java.util.Map;

public interface IRedisService {

    /**
     * 添加下一个执行节点到redis
     *
     * @param nodeId    当前节点编号
     * @param onlyId    唯一编号
     * @param relations 节点关联信息
     */
    void addNodeNextIdToRedis(String nodeId, String onlyId, List<NodeRelationVO> relations);

    /**
     * 获取节点关联信息
     *
     * @param onlyId 唯一编号
     * @return
     */
    RelevanceInfoVO queryRelevanceInfoFromRedis(String onlyId);

    /**
     * 初始化redis数据
     *
     * @param nodeMap       节点
     * @param associatedMap 关联信息
     * @param nextNodeIds   下一个要执行的节点
     */
    void initRedis(Map<String, Object> nodeMap, Map<String, Object> associatedMap, List<String> nextNodeIds);

    /**
     * 获取节点
     *
     * @param nodeId 节点编号
     * @return
     */
    NodeVO getNodeFromRedis(String nodeId);

    /**
     * 获取空闲的临时表
     *
     * @return 空闲的临时表
     */
    String getFreeTableFromRedis(String onlyId);

    /**
     * 释放临时表
     *
     * @param table 要释放的临时表
     */
    void releaseTable(String table);

    /**
     * 获取下一个要执行的节点ID
     *
     * @return 下一个要执行的节点ID
     */
    String getNextNode();

    boolean executeNextNode(String nodeId, List<NodeRelationVO> relations, String onlyId);

    void addExecutedNode(String nodeId, String onlyId);
}
