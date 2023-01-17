package com.dfssi.dataplatform.analysis.service.service;

import com.dfssi.dataplatform.analysis.service.resource.DBConnectEntity;
import com.dfssi.dataplatform.analysis.service.resource.DataResource;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/12 13:55
 */
@Service(value = "BIService")
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class BIService extends AbstractService {

    public List<LinkedHashMap<String, Object>> getTableData(String dataresourceType,
                                                            String host,
                                                            String port,
                                                            String username,
                                                            String password,
                                                            String databaseName,
                                                            String tableName,
                                                            String colNames,
                                                            int number) throws SQLException, ClassNotFoundException {
        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);
        DataResource dataResource = new DataResource(dbConnectEntity);
        return dataResource.getTableData(tableName, colNames, "", number);
    }

    public Map<String, String> getTableColumnInfo(String dataresourceType,
                                                  String host,
                                                  String port,
                                                  String username,
                                                  String password,
                                                  String databaseName,
                                                  String tableName) throws SQLException, ClassNotFoundException {
        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);
        DataResource dataResource = new DataResource(dbConnectEntity);
        return dataResource.getTableColumnInfo(tableName);
    }

    public Set<Object> getTableColumnCategory(String dataresourceType,
                                              String host,
                                              String port,
                                              String username,
                                              String password,
                                              String databaseName,
                                              String tableName,
                                              String colName) throws SQLException, ClassNotFoundException {
        List<LinkedHashMap<String, Object>> maps =
                this.getTableData(dataresourceType, host, port, username, password, databaseName, tableName, colName, 0);
        Set<Object> record = Sets.newHashSet();
        for (Map<String, Object> map : maps) {
            record.add(map.get(colName));
        }
        return record;
    }

    public Object getDataByCategory(String dataresourceType,
                                    String host,
                                    String port,
                                    String username,
                                    String password,
                                    String databaseName,
                                    String tableName,
                                    String colName,
                                    String colNames,
                                    String value) throws SQLException, ClassNotFoundException {

        DBConnectEntity dbConnectEntity = new DBConnectEntity(dataresourceType, host, port, databaseName,
                username, password);

        DataResource dataResource = new DataResource(dbConnectEntity);
        StringBuffer condition = new StringBuffer("");
        if (null != colName && null != value && !value.isEmpty()) {
            condition.append(colName + "=\"");
            condition.append(Joiner.on("\" OR " + colName + "=\"").skipNulls().join(value.split(";")));
            condition.append("\"");
        }
        return dataResource.getTableDataAsArray(tableName, colNames, condition.toString(), 0);
    }

}
