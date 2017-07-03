package com.yonyou.diamondrank.bean;

import java.util.ArrayList;

/**
 * Created by libo on 2017/1/24.
 */

public class DepartmentBean {

    private int id;
    private String departmentName;
    private ArrayList<PositionBean> positions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public ArrayList<PositionBean> getPositionBeen() {
        return positions;
    }

    public void setPositionBeen(ArrayList<PositionBean> positions) {
        this.positions = positions;
    }
}
