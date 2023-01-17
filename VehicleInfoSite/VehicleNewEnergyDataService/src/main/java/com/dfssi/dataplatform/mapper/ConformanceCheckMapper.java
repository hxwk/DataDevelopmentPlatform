package com.dfssi.dataplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/5/3 9:57
 */
@Mapper
public interface ConformanceCheckMapper {
    /**
     * @author bin.Y
     * Description:查询车企总检测结果
     * Date:  2018/5/3 10:52
     */
    @Select("SELECT\n" +
            "	'00' checkItemNo,\n" +
            "	date_format(begin_time, '%Y-%m-%d %H:%i:%s') checkBeginTime,\n" +
            "	date_format(end_time, '%Y-%m-%d %H:%i:%s') checkEndTime,\n" +
            "	check_result checkResult,\n" +
            "	check_status checkStatus \n" +
            "FROM\n" +
            "	compliance_check_result \n" +
            "WHERE\n" +
            "	check_id = (\n" +
            "		SELECT\n" +
            "			max(check_id)\n" +
            "		FROM\n" +
            "			compliance_check_result\n" +
            "		WHERE\n" +
            "			carcompany_id = #{companyId}\n" +
            "	)")
    List<Map<String, String>> getCheckResult(@Param("companyId") String companyId);

    /**
     * @author bin.Y
     * Description:查询各项检测结果
     * Date:  2018/5/3 10:52
     */
    @Select("SELECT\n" +
            "	check_item_no checkItemNo,\n" +
            "	date_format(data_begin_time, '%Y-%m-%d %H:%i:%s') dataBeginTime,\n" +
            "	date_format(data_end_time, '%Y-%m-%d %H:%i:%s') dataEndTime,\n" +
            "	date_format(begin_time, '%Y-%m-%d %H:%i:%s') checkBeginTime,\n" +
            "	date_format(end_time, '%Y-%m-%d %H:%i:%s') checkEndTime,\n" +
            "	check_result checkResult \n" +
            "FROM\n" +
            "	compliance_check_result_item \n" +
            "WHERE\n" +
            "	check_id = (\n" +
            "		SELECT\n" +
            "			max(check_id)\n" +
            "		FROM\n" +
            "			compliance_check_result\n" +
            "		WHERE\n" +
            "			carcompany_id = #{companyId}\n" +
            "	)")
    List<Map<String, String>> getItemResult(@Param("companyId") String companyId);

}
