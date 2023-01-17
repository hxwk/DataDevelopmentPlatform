package com.dfssi.dataplatform.abs.mapper;

import com.dfssi.dataplatform.abs.entity.VendorTestInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/29 16:02
 */
@Mapper
public interface VendorTestInfoMapper {

    VendorTestInfoEntity getCurrentVendorInfo(String date);

    String getNextVendorName(String date);
}
