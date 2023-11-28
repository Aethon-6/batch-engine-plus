package com.engine.core.execute.channel.factory.service;

import com.engine.active.entity.EngineCustomerIssuedBatch;
import com.engine.active.service.IEngineCustomerIssuedBatchService;
import com.engine.config.RedisIdMaker;
import com.engine.core.execute.channel.factory.ChannelFactory;
import com.engine.core.execute.channel.factory.ChannelHandler;
import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeVO;
import com.engine.sql.service.ISqlService;
import com.engine.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class APPChannelHandlerServiceImpl extends ChannelHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final String CHANNEL_TYPE = "APP";

    @Resource
    private RedisIdMaker redisIdMaker;

    @Resource
    private IEngineCustomerIssuedBatchService issuedBatchService;

    @Resource
    private ISqlService sqlService;

    public boolean generate(NodeVO node, String tempTable, List<LabelInfoVO> labels) {
        LOGGER.info("[APP渠道执行开始]");
        boolean r = true;

        String batchNo = "PC" + redisIdMaker.generateNextId();
        String content = node.getCommunicateInfo().getContent();

        String newDateTime = DateUtil.format(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");

        issuedBatchService.save(
                EngineCustomerIssuedBatch.builder()
                        .batchNo(batchNo)
                        .content(content)
                        .build()
        );

        StringBuilder contentStr = new StringBuilder("'" + content + "'");
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(content);
        StringBuilder inn = new StringBuilder();
        String mainAlia = "i";
        while (matcher.find()) {
            if (labels.stream().noneMatch(l -> matcher.group(1).equals(l.getFieldNo().toUpperCase()))) {
                continue;
            }
            LabelInfoVO label = labels.stream().filter(l -> matcher.group(1).equalsIgnoreCase(l.getFieldNo())).collect(Collectors.toList()).get(0);
            contentStr = new StringBuilder("replace('" + contentStr + "','${" + label.getFieldNo().toUpperCase() + "}'," + label.getLabelBAlias() + "." + label.getFieldNo() + ")");

            inn.append(" inner join ").append(label.getLabelBType()).append(" ").append(label.getLabelBAlias()).append(" on ").append(mainAlia).append(".id = ").append(label.getLabelBAlias()).append(".khwybh");
        }
        inn.append(" inner join ").append(tempTable).append(" t on t.customer_id = ").append(mainAlia).append(".id");

        String selSql = "SELECT '" + batchNo + "' AS batch_no ,i.id as customer_id ,'" + contentStr + "' AS content ,'" + newDateTime + "' AS create_time FROM engine_customer_info i " + inn + "where t.node_code = '" + node.getCode() + "'";

        String insSql = "INSERT INTO engine_customer_app_issued (batch_no, customer_id, content, use_flag, create_time) " + selSql;
        sqlService.insert(insSql);
        LOGGER.info("[APP渠道执行结束]");
        return r;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ChannelFactory.register(CHANNEL_TYPE, this);
    }
}
