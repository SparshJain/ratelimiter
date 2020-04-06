

package com.blueoptima.ratelimiter;

import com.blueoptima.ratelimiter.rateannotation.event.RateCheckFailureEvent;
import com.blueoptima.ratelimiter.rateannotation.event.RateCheckFailureListener;
import org.springframework.stereotype.Component;

@Component
public class MyCheckFailureListener implements RateCheckFailureListener {
    public void onApplicationEvent(RateCheckFailureEvent event) {
        System.out.println("my check ####" + event.getMsg());
    }
}
