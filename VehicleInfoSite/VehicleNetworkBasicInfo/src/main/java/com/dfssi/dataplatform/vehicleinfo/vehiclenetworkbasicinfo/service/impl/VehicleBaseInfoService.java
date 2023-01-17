package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.VehicleDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.GeodeService;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.PageParam;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.IVehicleBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleBaseInfoService implements IVehicleBaseInfoService {

    @Autowired
    private GeodeService<VehicleDTO> geodeService;

    private static final String REGION = "vehicleBaseInfo";//GEODE数据区域名称region

    @Override
    public List<VehicleDTO> findVehicleBaseInfo(VehicleDTO vehicleDTO, PageParam pageParam) {
        String initSql = this.contractVehicleDTO(REGION, vehicleDTO, pageParam, Constants.QUERY_ALL);
        List<VehicleDTO> vehicle = geodeService.findGeodeQueryResultList(initSql);
        return vehicle;
    }

    @Override
    public String deleteVehicleBaseInfo(VehicleDTO vehicleDTO) {
        String delResult = "";
        String initSql = contractVehicleDTO(REGION, vehicleDTO, null, Constants.QUERY_ALL);

            List<VehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setIsValid(Constants.VALID_0);
                delResult=delResult+geodeService.saveEntry(REGION,vehicleDTO.getVin() + list.get(i).getIccId(), list.get(i));
            }
        return delResult;
    }

    @Override
    public int findVehicleCount(VehicleDTO vehicleDTO) {
        String initSql = contractVehicleDTO(REGION, vehicleDTO, null, Constants.QUERY_COUNT);
        int count = geodeService.findfindGeodeQueryResultCount(initSql);
        return count;
    }


    @Override
    public List<VehicleDTO> findVehicleBaseInfoByVinList(List<String> vinList) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < vinList.size(); i++) {
            sb.append("'");
            sb.append(vinList.get(i));
            sb.append("'");
            if (i < vinList.size() - 1) {
                sb.append(",");
            }
        }
        String initSql = "select * from /" + REGION + " where vin in set(" + sb.toString() + ") and isValid='" + Constants.VALID_1 + "'";
        List<VehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
        return list;
    }

    @Override
    public String saveVehicleBaseInfo(VehicleDTO vehicleDTO) {
        String saveResult = "";
        Region<Object, Object> region = geodeService.getRegion(REGION);

        //vin绑定的数据全部置为无效
        String initSql = "select * from /" + REGION + " where vin='" + vehicleDTO.getVin() + "'";
        List<VehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIsValid(Constants.VALID_0);
            region.put(vehicleDTO.getVin() + list.get(i).getIccId(), list.get(i));
            if (list.get(i).getIccId().equals(vehicleDTO.getIccId())) {
                vehicleDTO.setCreateUser(list.get(i).getCreateUser());
                vehicleDTO.setCreateTime(list.get(i).getCreateTime());
                vehicleDTO.setUpdateTime(CommonUtils.getCurrentDateTime(null));
                vehicleDTO.setUpdateUser(vehicleDTO.getUserId());
            }
        }
        //IccId绑定的数据全部置为无效
        String initSql2 = "select * from /" + REGION + " where iccId='" + vehicleDTO.getIccId() + "'";
        List<VehicleDTO> list2 = geodeService.findGeodeQueryResultList(initSql2);
        for (int i = 0; i < list2.size(); i++) {
            list2.get(i).setIsValid(Constants.VALID_0);
            region.put(list2.get(i).getVin() + list2.get(i).getIccId(), list2.get(i));
        }
        //新增或修改的的数据置为有效
        vehicleDTO.setIsValid(Constants.VALID_1);
        if (vehicleDTO.getUpdateTime() == null) {
            vehicleDTO.setCreateTime(CommonUtils.getCurrentDateTime(null));
            vehicleDTO.setCreateUser(vehicleDTO.getUserId());
        }
        saveResult = geodeService.saveEntry(REGION, vehicleDTO.getVin() + vehicleDTO.getIccId(), vehicleDTO);
        return saveResult;
    }


    /**
     * 拼接查询语句
     *
     * @param region
     * @param vehicleDTO
     * @return
     */
    private String contractVehicleDTO(String region, VehicleDTO vehicleDTO, PageParam pageParam, String type) {

        String initSql = "select * from /" + region;
        if (Constants.QUERY_ALL.equals(type)) {
            initSql = "select * from /" + region;
        } else if (Constants.QUERY_COUNT.equals(type)) {
            initSql = "select count(*) from /" + region;
        }
        StringBuffer queryString = new StringBuffer();

        CommonUtils.getCommonDateSql(vehicleDTO,queryString);

        if (StringUtils.isNotEmpty(vehicleDTO.getIsValid())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and  ");
            }
            queryString.append(" isValid='");
            queryString.append(vehicleDTO.getIsValid());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getVin())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vin='");
            queryString.append(vehicleDTO.getVin());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getAppId())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" appId='");
            queryString.append(vehicleDTO.getAppId());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getIccId())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" iccId='");
            queryString.append(vehicleDTO.getIccId());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getUserId())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" userId='");
            queryString.append(vehicleDTO.getUserId());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" plateNo='");
            queryString.append(vehicleDTO.getPlateNo());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleType())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vehicleType='");
            queryString.append(vehicleDTO.getVehicleType());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleCompany())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vehicleCompany='");
            queryString.append(vehicleDTO.getVehicleCompany());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleUse())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vehicleUse='");
            queryString.append(vehicleDTO.getVehicleUse());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(queryString.toString())) {
            initSql = initSql + " where " + queryString.toString();
        }

        if (pageParam != null) {
            /*排序太耗时，暂时不使用*/
//            if (StringUtils.isNotEmpty(pageParam.getSort())){
//                initSql= initSql + " order by "+pageParam.getSort();
//            }else{
//                initSql= initSql + " order by vin ";
//            }
//            if (StringUtils.isNotEmpty(pageParam.getOrder())){
//                initSql= initSql +" "+ pageParam.getSort();
//            }else{
//                initSql= initSql +" desc";
//            }
            Integer startIndex = pageParam.getPageNum() * pageParam.getPageSize();
            initSql = initSql + " limit " + " " + (startIndex + pageParam.getPageSize());
        }
        return initSql;
    }


}
