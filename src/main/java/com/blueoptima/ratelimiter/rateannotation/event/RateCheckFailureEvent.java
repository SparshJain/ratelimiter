package com.blueoptima.ratelimiter.rateannotation.event;

import org.springframework.context.ApplicationEvent;

public final class RateCheckFailureEvent extends ApplicationEvent {
    private String msg;
    public RateCheckFailureEvent (Object source, String msg) {
        super(source);
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
}