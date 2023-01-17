package com.dfssi.elasticsearch.utils.log4j2;

import com.dfssi.common.Dates;

import java.util.Date;

public enum IndexCreateRule {
	NONE{
		@Override
		public String getIndexName(String baseName, Date date) {
			return baseName;
		}
	}, DAY{
		@Override
		public String getIndexName(String baseName, Date date) {
		  String prefix = Dates.nowStr(date, "yyyy.MM.dd");
		  return String.format("%s-%s", baseName, prefix);
		}
	}, MONTH{
		@Override
		public String getIndexName(String baseName, Date date) {
			String prefix = Dates.nowStr(date, "yyyy.MM");
			return String.format("%s-%s", baseName, prefix);
		}
	}, YEAR{
		@Override
		public String getIndexName(String baseName, Date date) {
			String prefix = Dates.nowStr(date, "yyyy");
			return String.format("%s-%s", baseName, prefix);
		}
	};

	public abstract String getIndexName(String baseName, Date date);

	public static IndexCreateRule getRule(String ruleName){
		if(ruleName != null){
			switch (ruleName.toUpperCase()){
				case "DAY": return DAY;
				case "MONTH": return MONTH;
				case "YEAR": return YEAR;
				case "NONE": return NONE;
				default:
					throw new IllegalArgumentException(String.format("不识别的Index创建规则", ruleName));
			}
		}
		return null;
	}
}
 