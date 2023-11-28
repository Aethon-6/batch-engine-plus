package com.engine.core.execute.channel.factory;

import com.engine.entity.vo.LabelInfoVO;
import com.engine.entity.vo.NodeVO;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

public abstract class ChannelHandler implements InitializingBean {
    public boolean generate(NodeVO node, String tempTable, List<LabelInfoVO> labels) {
        return true;
    }
}
