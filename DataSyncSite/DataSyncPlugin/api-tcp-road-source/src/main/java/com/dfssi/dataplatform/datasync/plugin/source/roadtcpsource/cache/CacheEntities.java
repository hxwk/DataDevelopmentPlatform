package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.cache;


import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.bean.Vehicle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Hannibal on 2018-02-03.
 */
public class CacheEntities {

    public static Map<String, Vehicle> sim2VehicleMap = new HashMap<>();

    public static Set<String> vidSets = new HashSet<>();


}
