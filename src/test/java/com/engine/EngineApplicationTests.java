package com.engine;

import com.alibaba.fastjson.JSON;
import com.engine.active.entity.EngineCustomerInfo;
import com.engine.active.entity.EngineCustomerRklk;
import com.engine.active.entity.EngineLabelConfig;
import com.engine.active.service.IEngineCustomerInfoService;
import com.engine.active.service.IEngineCustomerRklkService;
import com.engine.config.RedisIdMaker;
import com.engine.core.execute.common.service.ICommonService;
import com.engine.core.execute.redis.service.IRedisService;
import com.engine.entity.vo.*;
import com.engine.sql.service.ISqlService;
import com.engine.utils.FileUtil;
import com.engine.utils.JsonValidator;
import com.engine.utils.SnowflakeIdWorker;
import ma.glasnost.orika.MapperFacade;
import org.codehaus.janino.JaninoOption;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
class EngineApplicationTests {
    @Value("${engine.batch.temp-table-pre}")
    private String TEMP_TABLE_PRE;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ISqlService sqlService;

    @Resource
    private ThreadPoolTaskScheduler taskScheduler;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private ICommonService commonService;

    @Resource
    private MapperFacade mapperFacade;

    @Resource
    private IEngineCustomerInfoService engineCustomerInfoService;

    @Resource
    private IEngineCustomerRklkService engineCustomerRklkService;

    @Resource
    private IRedisService redisService;

    @Resource
    private RedisIdMaker redisIdMaker;


    @Test
    void contextLoads() throws InterruptedException {
        commonService.jsonValidator("");

        /*for (int i = 0; i < 50; i++) {
            executor.execute(() -> {
                System.out.println(redisIdMaker.generateNextId());
            });
        }
        Thread.sleep(5000);*/

        /*String activeStr = null;
        try {
            activeStr = FileUtil.readFileContent("active.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(JsonValidator.isJSON(""));
        System.out.println(JsonValidator.isJSON(activeStr));*/


        /*SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 3);
        String id = "111100001111111001100011101010110110100001100001000000000000";

        System.out.println(redisService.getFreeTableFromRedis(id));*/

        /*//实例化脚本对象
        DefaultRedisScript<String> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(String.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("thumbs.lua"));
        //key的集合
        List<String> keys = List.of("activeTable");
        //执行lua脚本
        String s = (String) stringRedisTemplate.execute(lua, keys, "111100001111111001100011101010110110100001100001000000000000", "temp_9");

        System.out.println(s);*/
    }

    @Test
    void set() {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set("a1", 10);
    }

    @Test
    void get() {
        Object codes = redisTemplate.opsForList().leftPop("nodeCodes");
        System.out.println(codes);
    }

    @Test
    void generatorData() throws InterruptedException {
        String sTime = "1999-01-01 08:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime sLocalDateTime = LocalDateTime.parse(sTime, formatter);

        Random random = new Random();

        String[] xbs = {"N", "M", "F"};
        String[] names = {"A", "B", "C", "E", "F", "J", "H", "I", "O", "P", "L", "M"};

        for (int i = 0; i < 200000; i++) {
            EngineCustomerInfo customerInfo = EngineCustomerInfo.builder()
                    .name(names[random.nextInt(12)] + "_" + (i + 1))
                    .age(random.nextInt(90))
                    .build();
            engineCustomerInfoService.save(customerInfo);

            EngineCustomerRklk customerRklk = EngineCustomerRklk.builder()
                    .khwybh(customerInfo.getId())
                    .khnl(customerInfo.getAge())
                    .xb(xbs[random.nextInt(2)])
                    .khrq(sLocalDateTime.plusDays(random.nextInt(365)))
                    .build();
            engineCustomerRklkService.save(customerRklk);
        }

        /*int index = 29634;
        while (index > 200000) {
            int finalIndex = index;
            executor.execute(() -> {
                EngineCustomerInfo customerInfo = EngineCustomerInfo.builder()
                        .name(names[random.nextInt(12)] + "_" + (finalIndex + 1))
                        .age(random.nextInt(90))
                        .build();
                engineCustomerInfoService.save(customerInfo);

                EngineCustomerRklk customerRklk = EngineCustomerRklk.builder()
                        .khwybh(customerInfo.getId())
                        .khnl(customerInfo.getAge())
                        .xb(xbs[random.nextInt(2)])
                        .khrq(sLocalDateTime.plusDays(random.nextInt(365)))
                        .build();
                engineCustomerRklkService.save(customerRklk);
            });

            index++;
        }*/
    }

    @Test
    void setNodes() throws IOException {
        String activeStr = null;
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 3);
        String id = Long.toBinaryString(idWorker.nextId());
        try {
            activeStr = FileUtil.readFileContent("active.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ActiveVO active = JSON.parseObject(activeStr, ActiveVO.class);

        assert active != null;

        List<EngineLabelConfig> labelList = commonService.extractLabel(active.getNodes(), active.getNodeRelations());
        List<LabelInfoVO> labelInfoList = mapperFacade.mapAsList(labelList, LabelInfoVO.class);
        System.out.println(labelInfoList);

        List<NodeRelationVO> relations = active.getNodeRelations();
//        List<LabelInfoVO> labelInfoList = new ArrayList<>();
        RelevanceInfoVO associated = RelevanceInfoVO.builder()
                .labels(labelInfoList)
                .relations(relations)
                .build();

        Map<String, Object> nodeMap = new HashMap<>();
        //ArrayList<String> nodeIds = new ArrayList<>();
        List<String> nextNodeIds = new ArrayList<>();
        Map<String, Object> associatedMap = new HashMap<String, Object>();
        associatedMap.put(id, associated);

        for (NodeVO node : active.getNodes()) {
            //nodeIds.add(node.getNodeId());
            nodeMap.put(node.getNodeId(), node);
            if ("begin".equals(node.getProperty())) {
                for (NodeRelationVO r : relations.stream().filter(r -> node.getNodeId().equals(r.getSourceNode())).collect(Collectors.toList())) {
                    nextNodeIds.add(r.getDestNode() + "_" + id);
                }
            }
        }
        //redisTemplate.opsForList().rightPushAll("nodeIds", nodeIds.toArray(new String[]{}));
        redisTemplate.opsForHash().putAll("nodes", nodeMap);
        redisTemplate.opsForHash().putAll("associates", associatedMap);
        redisTemplate.opsForList().rightPushAll("nextNodeIds", nextNodeIds.toArray(new String[]{}));
    }

    @Test
    void getNode() {
        //实例化脚本对象
        DefaultRedisScript<ArrayList> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(ArrayList.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("getNextNodeId.lua"));
        //key的集合
        List<String> keys = List.of("nodeCodes");
        //执行lua脚本
        ArrayList<String> nodeCodes = (ArrayList<String>) stringRedisTemplate.execute(lua, keys);
        assert nodeCodes != null;
        if (nodeCodes.size() > 0) {
            String nodeCode = nodeCodes.get(0).replaceAll("\"", "");
            NodeVO node = (NodeVO) redisTemplate.opsForHash().get("nodeMap", nodeCode);
            System.out.println(node);
        }
    }

    @Test
    void getNodes() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //实例化脚本对象
                DefaultRedisScript<ArrayList> lua = new DefaultRedisScript<>();
                //设置脚本的返回值
                lua.setResultType(ArrayList.class);
                //载入lua脚本
                lua.setLocation(new ClassPathResource("getNextNodeId.lua"));
                //key的集合
                List<String> keys = List.of("nodeCodes");
                //执行lua脚本
                ArrayList<String> nodeCodes = (ArrayList<String>) stringRedisTemplate.execute(lua, keys);
                assert nodeCodes != null;
                if (nodeCodes.size() > 0) {
                    String nodeCode = nodeCodes.get(0).replaceAll("\"", "");
                    NodeVO node = (NodeVO) redisTemplate.opsForHash().get("nodeMap", nodeCode);
                    System.out.println(node);
                }
            }
        };

        for (int i = 0; i < 20; i++) {
            threadPool.execute(runnable);
        }
        Thread.sleep(10000);
    }

    @Test
    void testDataSource() {
        Map<String, Object> map = new HashMap<>();
        map.put("sql", "SELECT * FROM sys_user");
        List<LinkedHashMap<String, Object>> list = sqlService.select(map);
        System.out.println(list);
    }

    @Test
    void initTable() {
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

    @Test
    void getTable() {
        //实例化脚本对象
        DefaultRedisScript<String> lua = new DefaultRedisScript<>();
        //设置脚本的返回值
        lua.setResultType(String.class);
        //载入lua脚本
        lua.setLocation(new ClassPathResource("getTable.lua"));
        //key的集合
        List<String> keys = List.of("tables", "usedTables");
        //执行lua脚本
        String table = ((String) stringRedisTemplate.execute(lua, keys));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        redisTemplate.opsForSet().remove("usedTables", table);
        assert table != null;
        System.out.println(table.replaceAll("\"", ""));
    }

    @Test
    void getTables() {
        taskScheduler.schedule(() -> {
            //实例化脚本对象
            DefaultRedisScript<String> lua = new DefaultRedisScript<>();
            //设置脚本的返回值
            lua.setResultType(String.class);
            //载入lua脚本
            lua.setLocation(new ClassPathResource("getTable.lua"));
            //key的集合
            List<String> keys = List.of("tables", "usedTables");
            //执行lua脚本
            String table = ((String) stringRedisTemplate.execute(lua, keys));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            assert table != null;
            table = table.replaceAll("\"", "");
            redisTemplate.opsForSet().remove("usedTables", table);
            System.out.println(table);
        }, new CronTrigger("0/10 * * * * ?"));
    }

    @Test
    public void getNextNodeId() {
        /*SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 3);
        String id = Long.toBinaryString(idWorker.nextId());

        List<String> nodeIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            nodeIds.add((i + 1) + "_" + id);
        }

        for (String nodeId : nodeIds) {
            redisTemplate.opsForList().rightPush("nextNodeIds", nodeId);
        }*/

        //redisTemplate.opsForList().rightPushAll("nextNodeIds", nodeIds.toArray(new String[]{}));


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
        System.out.println(nextNodeIds);*/

        Object nodeIds = redisTemplate.opsForList().leftPop("nextNodeIds", Duration.ZERO);
        System.out.println(nodeIds);
    }
}
