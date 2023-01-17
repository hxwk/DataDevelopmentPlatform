package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleMileageDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.mapper.IVehicleMileageMapper;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.DataSource;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleMileageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yanghs on 2018/5/10.
 */
@Service
public class VehicleMileageService implements IVehicleMileageService {

    @Autowired
    private IVehicleMileageMapper iVehicleMileageMapper;

    @Override
    @DataSource(value=DataSource.GPDATA)
    public List<VehicleMileageDTO> findMileageInfo(List<String> vidList) {
        return iVehicleMileageMapper.findMileageInfo(vidList);
    }
}
