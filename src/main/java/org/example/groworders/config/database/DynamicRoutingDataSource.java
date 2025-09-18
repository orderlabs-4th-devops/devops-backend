package org.example.groworders.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        Boolean isReadOnly = isCurrentTransactionReadOnly();
        String dataSourceName;
        if(isReadOnly) {
            dataSourceName = "SLAVE";
        } else {
            dataSourceName = "MASTER";
        }
        System.out.println("dataSourceName : " + dataSourceName);

        return dataSourceName;
    }
}