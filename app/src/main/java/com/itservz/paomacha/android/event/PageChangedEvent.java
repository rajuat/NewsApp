package com.itservz.paomacha.android.event;

public class PageChangedEvent {

    private boolean mHasVerticalNeighbors = true;

    public PageChangedEvent(boolean hasVerticalNeighbors) {
        mHasVerticalNeighbors = hasVerticalNeighbors;
    }

    public boolean hasVerticalNeighbors() {
        return mHasVerticalNeighbors;
    }

}

