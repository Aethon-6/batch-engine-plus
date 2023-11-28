package com.engine.config;

import com.engine.enums.DSTypeEnum;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

@NoArgsConstructor
public class DynamicDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<DSTypeEnum> DB_CONTEXT_HOLDER = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }

    /**
     * 设置数据源
     *
     * @param dataSource 数据源
     */
    public static void setDataSource(DSTypeEnum dataSource) {
        DB_CONTEXT_HOLDER.set(dataSource);
    }

    /**
     * 获取当前数据源
     *
     * @return 数据源
     */
    public static DSTypeEnum getDataSource() {
        return DB_CONTEXT_HOLDER.get();
    }

    /**
     * 清除上下文
     */
    public static void clearDataSource() {
        DB_CONTEXT_HOLDER.remove();
    }

    /**
     * 设置默认数据源，和可切换的数据源Map
     *
     * @param defaultTargetDataSource 默认数据源
     * @param targetDataSources       可切换的数据源Map
     */
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }
}
