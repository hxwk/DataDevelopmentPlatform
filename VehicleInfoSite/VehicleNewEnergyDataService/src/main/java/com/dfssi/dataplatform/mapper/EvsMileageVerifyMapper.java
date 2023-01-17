package com.dfssi.dataplatform.mapper;

import com.dfssi.dataplatform.entity.database.Mileage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/28 20:28
 */
@Mapper
public interface EvsMileageVerifyMapper {

    @Select("select * from evs_mileage_verification where vin = #{vin} and day >= #{startDay} and day <= #{endDay}  order by day")
    List<Mileage> searchMileageVerify(@Param("vin") String vin,
                                      @Param("startDay")String startDay,
                                      @Param("endDay")String endDay);

    @Select("select sum(verifyMile) verifyMile from evs_mileage_verification where vin = #{vin}")
    Map<String, Object> countVerifyMileage(@Param("vin") String vin);
}
