package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.PageParam;
import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;

import java.util.List;

/**
 * 车辆基础信息录入处理服务
 *
 * @author yanghs
 * @since 2018-4-2 11:41:44
 */
public interface IVehicleBaseInfoService {

    /**
     * 查询车辆基础信息
     *
     * @param vehicleDTO
     * @return
     */
    List<CVVehicleDTO> findVehicleBaseInfo(CVVehicleDTO vehicleDTO, PageParam pageParam);

    /**
     * 删除车辆基础信息(逻辑删除)
     *
     * @param vehicleDTO
     * @return
     */
    String deleteVehicleBaseInfo(CVVehicleDTO vehicleDTO);

    /**
     * 统计车辆总数
     *
     * @param vehicleDTO
     * @return
     */
    int findVehicleCount(CVVehicleDTO vehicleDTO);


    /**
     * 多个vin查询车辆基础信息
     *
     * @param vinList
     * @return
     */
    List<CVVehicleDTO> findVehicleBaseInfoByVidList(List<String> vinList);


    /**
     * 保存数据到Geode
     * 根据vin与iccId判断数据是否存在
     * 存在有效数据，则修改有效数据
     * 不存在有效数据则新增
     * 历史数据相同vin数据置为无效，保留最新vin、iccId相同数据为有效
     *
     * @param vehicleDTO
     */
    String saveVehicleBaseInfo(CVVehicleDTO vehicleDTO);

}
