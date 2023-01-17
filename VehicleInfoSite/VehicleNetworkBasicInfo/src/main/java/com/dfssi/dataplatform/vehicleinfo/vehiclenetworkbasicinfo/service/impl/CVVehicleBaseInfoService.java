package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.CVVehicleDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.GeodeService;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.PageParam;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.ICVVehicleBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CVVehicleBaseInfoService implements ICVVehicleBaseInfoService{

    @Autowired
    private GeodeService<CVVehicleDTO> geodeService;

    private static final String REGION = "cvVehicleBaseInfo";//GEODE数据区域名称region

    @Override
    public List<CVVehicleDTO> findVehicleBaseInfo(CVVehicleDTO vehicleDTO, PageParam pageParam) {
        String initSql = this.contractCVVehicleDTO(REGION, vehicleDTO, pageParam, Constants.QUERY_ALL);
        List<CVVehicleDTO> vehicle = geodeService.findGeodeQueryResultList(initSql);
        return vehicle;
    }

    @Override
    public String deleteVehicleBaseInfo(CVVehicleDTO vehicleDTO) {
        String delResult = "";
        String initSql = contractCVVehicleDTO(REGION, vehicleDTO, null, null);

            List<CVVehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setIsValid(Constants.VALID_0);
                delResult=delResult+geodeService.saveEntry(REGION,vehicleDTO.getVid(), list.get(i));
            }
        return delResult;
    }

    @Override
    public int findVehicleCount(CVVehicleDTO vehicleDTO) {
        String initSql = contractCVVehicleDTO(REGION, vehicleDTO, null, Constants.QUERY_COUNT);
        int count = geodeService.findfindGeodeQueryResultCount(initSql);
        return count;
    }


    @Override
    public List<CVVehicleDTO> findVehicleBaseInfoByVidList(List<String> vinList) {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < vinList.size(); i++) {
            sb.append("'");
            sb.append(vinList.get(i));
            sb.append("'");
            if (i < vinList.size() - 1) {
                sb.append(",");
            }
        }
        String initSql = "select * from /" + REGION + " where vid in set(" + sb.toString() + ") and isValid='" + Constants.VALID_1 + "'";
        List<CVVehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
        return list;
    }

    @Override
    public String saveVehicleBaseInfo(CVVehicleDTO vehicleDTO) {
        String saveResult = "";
        Region<Object, Object> region = geodeService.getRegion(REGION);

        //vin绑定的数据全部置为无效
        String initSql = "select * from /" + REGION + " where vid='" + vehicleDTO.getVid() + "'";
        List<CVVehicleDTO> list = geodeService.findGeodeQueryResultList(initSql);
        for (int i = 0; i < list.size(); i++) {
            vehicleDTO.setCreateUser(list.get(0).getCreateUser());
            vehicleDTO.setCreateTime(list.get(0).getCreateTime());
            vehicleDTO.setUpdateTime(CommonUtils.getCurrentDateTime(null));
            vehicleDTO.setUpdateUser(vehicleDTO.getUserId());
            break;
        }
        //新增或修改的的数据置为有效
        vehicleDTO.setIsValid(Constants.VALID_1);
        if (vehicleDTO.getCreateTime() == null) {
            vehicleDTO.setCreateTime(CommonUtils.getCurrentDateTime(null));
            vehicleDTO.setCreateUser(vehicleDTO.getUserId());
        }
        saveResult = geodeService.saveEntry(REGION, vehicleDTO.getVid(), vehicleDTO);
        return saveResult;
    }


    /**
     * 拼接查询语句
     *
     * @param region
     * @param vehicleDTO
     * @return
     */
    private String contractCVVehicleDTO(String region, CVVehicleDTO vehicleDTO, PageParam pageParam, String type) {

        String initSql = "select * from /" + region;
        if (Constants.QUERY_ALL.equals(type)) {
            initSql = "select * from /" + region +" order by vin";
        } else if (Constants.QUERY_COUNT.equals(type)) {
            initSql = "select count(*) from /" + region;
        }
        StringBuffer queryString = new StringBuffer();

        CommonUtils.getCommonDateSql(vehicleDTO,queryString);


        if (StringUtils.isNotEmpty(vehicleDTO.getIsValid())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" isValid='");
            queryString.append(vehicleDTO.getIsValid());
            queryString.append("' ");
        }else{
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" isValid='");
            queryString.append(Constants.VALID_1);
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getDid())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" did='");
            queryString.append(vehicleDTO.getDid());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVid())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append("  and ");
            }
            queryString.append(" vid='");
            queryString.append(vehicleDTO.getVid());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVin())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and  ");
            }
            queryString.append(" vin='");
            queryString.append(vehicleDTO.getVin());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getSim())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" sim='");
            queryString.append(vehicleDTO.getSim());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getVehicleType())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vehicleType= '");
            queryString.append(vehicleDTO.getVehicleType());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getPlateNo())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" plateNo= '");
            queryString.append(vehicleDTO.getPlateNo());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(vehicleDTO.getBuyTime())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" buyTime='");
            queryString.append(vehicleDTO.getBuyTime());
            queryString.append("' ");
        }
        if (StringUtils.isNotEmpty(vehicleDTO.getUserId())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" userId= '");
            queryString.append(vehicleDTO.getUserId());
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
