package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.PlatformDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.PageParam;

import java.util.List;

/**
 * 平台信息处理服务
 *
 * @author 杨海松
 * @since 2018-4-11 14:57:43.
 */
public interface IPlatformInfoService {

    /**
     * 查询平台信息
     *
     * @param platformDTO
     * @return
     */
    List<PlatformDTO> findPlatformInfo(PlatformDTO platformDTO, PageParam pageParam);

    /**
     * 删除平台信息
     *
     * @param platformDTO
     * @return
     */
    String deletePlatformInfo(PlatformDTO platformDTO);

    /**
     * 保存平台信息
     *
     * @param platformDTO
     * @return
     */
    String savePlatformInfo(PlatformDTO platformDTO);


    /**
     * 查询总数
     * @param platformDTO
     * @return
     */
    int findPlatformCount(PlatformDTO platformDTO);
}
