package com.dfssi.dataplatform.ide.service.bi.converters;


import com.dfssi.dataplatform.ide.service.bi.converters.bar.BarConverter;
import com.dfssi.dataplatform.ide.service.bi.converters.heat.HeatConverter;
import com.dfssi.dataplatform.ide.service.bi.converters.line.LineConverter;
import com.dfssi.dataplatform.ide.service.bi.converters.pie.PieConverter;
import com.dfssi.dataplatform.ide.service.bi.converters.scatter.ScatterConverter;
import com.dfssi.dataplatform.ide.service.bi.sources.database.DataBaseConf;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/11 19:04
 */
public enum ConveterType {
    BAR{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new BarConverter(conf);
        }
    }, LINE{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new LineConverter(conf);
        }
    }, PIE{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new PieConverter(conf);
        }
    }, SCATTER{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new ScatterConverter(conf);
        }
    }, HEAT{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new HeatConverter(conf);
        }
    }, NONE{
        @Override
        public DataConverter converter(DataBaseConf conf) {
            return new DataConverter(conf){
                @Override
                public Object convet(Iterable records) {
                    return null;
                }
            };
        }
    };

    public abstract DataConverter converter(DataBaseConf conf);

    public static ConveterType getConverterByName(String name){
        if(name != null) {
            switch (name.toUpperCase()) {
                case "BAR": return BAR;
                case "LINE" : return LINE;
                case "PIE": return PIE;
                case "SCATTER": return SCATTER;
                case "HEAT" : return HEAT;
            }
        }
        return NONE;
    }
}
