package com.smallcake.temp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ConstactBean {
    @Id(autoincrement = true)  //主键，自增
    private Long id;
    private String name;
    @Generated(hash = 1267762723)
    public ConstactBean(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 985095275)
    public ConstactBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConstactBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
