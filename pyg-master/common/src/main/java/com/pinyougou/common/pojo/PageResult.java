package com.pinyougou.common.pojo;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {
    private List<?> dataList;//分页数据
    private Long totalItems;//总记录数

    public PageResult() {
    }

    public PageResult(List<?> dataList, Long totalItems) {
        this.dataList = dataList;
        this.totalItems = totalItems;
    }

    public List<?> getDataList() {
        return dataList;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }
}
