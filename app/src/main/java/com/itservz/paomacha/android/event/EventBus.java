
package com.itservz.paomacha.android.event;

import com.squareup.otto.Bus;


public class EventBus {

    private static final Bus sBus = new Bus();

    public static Bus getInstance() {
        return sBus;
    }

    private EventBus() {
    }

}


