package com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaAlarmEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaLinkEntity;
import com.google.common.base.Joiner;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/10 8:24
 */
@Mapper
public interface AreaAlarmMapper {

    /** 区域的增删改查  */
    @InsertProvider(type=SQLProvider.class, method="addAreasSQL")
    int addAreas(@Param("alarmEntities") List<AreaAlarmEntity> alarmEntities);

    @UpdateProvider(type=SQLProvider.class, method="deleteAreaSQL")
    int deleteArea(@Param("id") String id);

    @UpdateProvider(type=SQLProvider.class, method="updateAreaSQL")
    int updateArea(@Param("alarmEntity") AreaAlarmEntity alarmEntity);

    @SelectProvider(type=SQLProvider.class, method="queryAreaSQL")
    List<Map<String, Object>> queryArea(@Param("id") String id,
                                        @Param("name") String name,
                                        @Param("type")Integer type);

    /** 区域与vid关联 的增删查 */
    @InsertProvider(type=SQLProvider.class, method="addLinksSQL")
    int addLinks(@Param("linkEntities") List<AreaLinkEntity> linkEntities);

    @UpdateProvider(type=SQLProvider.class, method="deleteLinkSQL")
    int deleteLink(@Param("id") String id);

    @SelectProvider(type=SQLProvider.class, method="queryLinkSQL")
    List<Map<String, Object>> queryLink(@Param("id") String id,
                                        @Param("vid") String vid,
                                        @Param("areaId")String areaId,
                                        @Param("areaType")Integer areaType);

    @SelectProvider(type=SQLProvider.class, method="queryLinkAreaSQL")
    List<Map<String, Object>> queryLinkArea(@Param("vid") String vid, @Param("type")Integer type);

    class SQLProvider {
        public String addAreasSQL(@Param("alarmEntities") List<AreaAlarmEntity> alarmEntities){

            List<String> values = alarmEntities.stream().map(areaAlarmEntity ->
                    String.format("('%s', '%s', '%s', %s, %s, %s)",
                            areaAlarmEntity.getId(), areaAlarmEntity.getName(), areaAlarmEntity.getLocations(),
                            areaAlarmEntity.getType(), areaAlarmEntity.getCreateTime(), areaAlarmEntity.getCreateTime())
            ).collect(Collectors.toList());

            return   String.format(
                    "INSERT INTO road_vehicle_area (id, name, locations, type, create_time, update_time) VALUES %s",
                    Joiner.on(",").join(values));
        }

        public String addLinksSQL(@Param("linkEntities") List<AreaLinkEntity> linkEntities){

            List<String> values = linkEntities.stream().map(linkEntity ->
                    String.format("('%s', '%s', '%s', '%s', '%s', '%s', %s, %s, %s, %s, %s)",
                            linkEntity.getId(), linkEntity.getVid(), linkEntity.getAreaId(), linkEntity.getAlarmDesc(),
                            linkEntity.getAreaAlarmType(), linkEntity.getAreaName(), linkEntity.getAreaType(), linkEntity.getStartTime(),
                            linkEntity.getStopTime(), linkEntity.getStayTime(), linkEntity.getCreateTime())
            ).collect(Collectors.toList());

            return   String.format(
                    "INSERT INTO road_vehicle_area_link (id, vid, area_id, alarm_desc, area_alarm_type, area_name, area_type, start_time, stop_time, stay_time, create_time) VALUES %s",
                    Joiner.on(",").join(values));
        }

        public String deleteAreaSQL(@Param("id") String id){
            return new SQL(){
                {
                    UPDATE("road_vehicle_area");
                    SET(String.format("delete=%s", 1));
                    SET(String.format("update_time=%s", System.currentTimeMillis()));
                    WHERE(String.format("id='%s'", id));
                }
            }.toString();
        }

        public String deleteLinkSQL(@Param("id") String id){
            return new SQL(){
                {
                    DELETE_FROM("road_vehicle_area_link");
                    WHERE(String.format("id='%s'", id));
                }
            }.toString();
        }

        public String updateAreaSQL(@Param("alarmEntity") AreaAlarmEntity alarmEntity){
            return new SQL(){
                {
                    UPDATE("road_vehicle_area");
                    SET(String.format("locations='%s'", alarmEntity.getLocations()));
                    SET(String.format("name='%s'", alarmEntity.getName()));
                    SET(String.format("update_time=%s", System.currentTimeMillis()));
                    WHERE(String.format("id='%s'", alarmEntity.getId()));
                }
            }.toString();
        }

        public String queryAreaSQL(@Param("id") String id,
                                   @Param("name") String name,
                                   @Param("type")Integer type){
            return new SQL(){
                {
                    SELECT("id", "name", "locations", "create_time", "update_time")
                            .FROM("road_vehicle_area")
                            .WHERE("delete = 0");

                    if(type != null){
                        WHERE(String.format("type=%s", type));
                    }

                    if(id != null){
                        WHERE("id=#{id}");
                    }
                    if(name != null){
                        WHERE("name like '%" + name + "%'");
                    }
                    ORDER_BY("update_time desc");
                }
            }.toString();
        }

        public String queryLinkSQL(@Param("id") String id,
                                   @Param("vid") String vid,
                                   @Param("areaId")String areaId,
                                   @Param("areaType")Integer areaType){
            return new SQL(){
                {
                    SELECT("*")
                            .FROM("road_vehicle_area_link");
                    if(id != null){
                        WHERE("id=#{id}");
                    }
                    if(vid != null){
                        WHERE("vid=#{vid}");
                    }

                    if(areaId != null){
                        WHERE("area_id=#{areaId}");
                    }

                    if(areaType != null){
                        WHERE("area_type=#{areaType}");
                    }

                    ORDER_BY("create_time desc");
                }
            }.toString();
        }

        public String queryLinkAreaSQL(@Param("vid") String vid,
                                       @Param("type")Integer type){
           String leftSQL = new SQL(){
                {
                    SELECT("id", "vid", "area_id", "alarm_desc", "area_alarm_type", "area_type", "start_time", "stop_time", "stay_time")
                            .FROM("road_vehicle_area_link");
                    if(vid != null){
                        WHERE("vid='" + vid + "'");
                    }
                }
            }.toString();

           String rightSQL = new SQL(){
                {
                    SELECT("id", "name", "locations", "create_time")
                            .FROM("road_vehicle_area")
                            .WHERE("delete = 0");
                    if(type != null){
                        WHERE(String.format("type=%s", type));
                    }
                }
            }.toString();

            return new SQL(){
                {
                    SELECT("t2.id", "t2.vid", "t2.area_id", "t2.area_type", "t2.alarm_desc", "t2.start_time", "t2.stop_time", "t2.stay_time",
                            "t2.area_alarm_type", "t1.name", "t1.locations", "t1.create_time")
                            .FROM(String.format("(%s) t2 left join (%s) t1 on t1.area_id = t2.id", leftSQL, rightSQL));
                }
            }.toString();
        }
    }
}
