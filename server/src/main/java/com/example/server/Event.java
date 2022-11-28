package com.example.server;

import com.alibaba.fastjson.annotation.JSONField;

public class Event {

    public Event(String event, String is_field, int update_cycle) {
        this.event = event;
        this.is_field = is_field;
        this.update_cycle = update_cycle;
    }

    @JSONField(ordinal = 1)
    String event;

    @JSONField(ordinal = 2)
    String is_field;

    @JSONField(name = "update-cycle", ordinal = 3)
    int update_cycle;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getIs_field() {
        return is_field;
    }

    public void setIs_field(String is_field) {
        this.is_field = is_field;
    }

    public int getUpdate_cycle() {
        return update_cycle;
    }

    public void setUpdate_cycle(int update_cycle) {
        this.update_cycle = update_cycle;
    }
}
