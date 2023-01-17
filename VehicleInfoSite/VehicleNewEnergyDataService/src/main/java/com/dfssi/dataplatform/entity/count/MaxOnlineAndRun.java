package com.dfssi.dataplatform.entity.count;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/24 18:55
 */
public class MaxOnlineAndRun {

    private long date;
    private Long maxOnline;
    private Long maxRun;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getMaxOnline() {
        return (maxOnline == null) ? 0 : maxOnline;
    }

    public void setMaxOnline(Long maxOnline) {
        this.maxOnline = maxOnline;
    }

    public long getMaxRun() {
        return (maxRun == null) ? 0 : maxRun;
    }

    public void setMaxRun(Long maxRun) {
        this.maxRun = maxRun;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MaxOnlineAndRun{");
        sb.append("date=").append(date);
        sb.append(", maxOnline=").append(maxOnline);
        sb.append(", maxRun=").append(maxRun);
        sb.append('}');
        return sb.toString();
    }
}
