package com.xcxcxcxcx.demo.pub.entity;

import java.util.Date;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public final class InfoEntity {

    private int id;

    private String content;

    private Date createOn;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Date createOn) {
        this.createOn = createOn;
    }
}
