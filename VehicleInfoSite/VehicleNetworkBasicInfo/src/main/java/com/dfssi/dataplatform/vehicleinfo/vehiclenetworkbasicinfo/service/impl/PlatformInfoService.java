package com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleInfoModel.entity.PlatformDTO;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.GeodeService;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.model.PageParam;
import com.dfssi.dataplatform.vehicleinfo.vehiclenetworkbasicinfo.service.IPlatformInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformInfoService implements IPlatformInfoService {

    @Autowired
    private GeodeService<PlatformDTO> geodeService;

    private static final String REGION = "platformInfo";

    @Override
    public List<PlatformDTO> findPlatformInfo(PlatformDTO platformDTO,PageParam pageParam) {
        String oql = contractPlatformDTO(REGION, platformDTO,pageParam,Constants.QUERY_ALL);
        return  geodeService.findGeodeQueryResultList(oql);
    }

    @Override
    public String deletePlatformInfo(PlatformDTO platformDTO) {
        return geodeService.destroyEntry(REGION, platformDTO.getUserName());
    }

    @Override
    public String savePlatformInfo(PlatformDTO platformDTO) {
        String initSql = "select * from /" + REGION + " where userName='" + platformDTO.getUserName() + "'";
        List<PlatformDTO> list = geodeService.findGeodeQueryResultList(initSql);
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                platformDTO.setCreateTime(list.get(i).getCreateTime());
                platformDTO.setCreateUser(list.get(i).getCreateUser());
                platformDTO.setUpdateTime(CommonUtils.getCurrentDateTime(null));
                platformDTO.setUpdateUser(platformDTO.getUserId());//?????
            }
        }else{
            platformDTO.setCreateTime(CommonUtils.getCurrentDateTime(null));
            platformDTO.setCreateUser(platformDTO.getUserId());//?????
        }
        String result = geodeService.saveEntry(REGION, platformDTO.getUserName(), platformDTO);
        return result;
    }

    @Override
    public int findPlatformCount(PlatformDTO platformDTO) {
        String oql = contractPlatformDTO(REGION, platformDTO,null,Constants.QUERY_COUNT);
        return  geodeService.findfindGeodeQueryResultCount(oql);
    }

    /**
     * 拼接查询语句
     *
     * @param region
     * @param platformDTO
     * @return
     */
    private String contractPlatformDTO(String region, PlatformDTO platformDTO,PageParam pageParam,String type) {
        String initSql = "select * from /" + region;
        if (Constants.QUERY_ALL.equals(type)) {
            initSql = "select * from /" + region;
        } else if (Constants.QUERY_COUNT.equals(type)) {
            initSql = "select count(*) from /" + region;
        }
        StringBuffer queryString = new StringBuffer();

        CommonUtils.getCommonDateSql(platformDTO,queryString);

        if (StringUtils.isNotEmpty(platformDTO.getUserName())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" userName='");
            queryString.append(platformDTO.getUserName());
            queryString.append("' ");
        }

        if (StringUtils.isNotEmpty(platformDTO.getVehicleCompany())) {
            if (StringUtils.isNotEmpty(queryString.toString())) {
                queryString.append(" and ");
            }
            queryString.append(" vehicleCompany='");
            queryString.append(platformDTO.getVehicleCompany());
            queryString.append("'  ");
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
