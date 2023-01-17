package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.mapper;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleMileageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 查询里程
 * Created by yanghs on 2018/5/15.
 */
@Mapper
public interface IVehicleMileageMapper {
    @Select("<script>" +
            " select * from vehicle_total_fuel " +
            "        where vid in " +
            "        <foreach collection='vidList' open='(' close=')' item='id' separator=','> " +
            "            #{id} " +
            "        </foreach>"+
            "</script>")
    List<VehicleMileageDTO> findMileageInfo(@Param("vidList") List<String> vidList);
}
