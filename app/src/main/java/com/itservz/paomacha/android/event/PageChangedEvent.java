package com.itservz.paomacha.android.event;

/**
 * Dispatched when the current selected page of the application navigation changed. E.g. user swipes from the center
 * page to the left page.
 */
public class PageChangedEvent {
	/**
	 * @param hasVerticalNeighbors
	 *            true if the current selected page has vertical (below and/or above) neighbor pages, false - if not.
	 */
	public PageChangedEvent(boolean hasVerticalNeighbors) {
		mHasVerticalNeighbors = hasVerticalNeighbors;
	}

	private boolean mHasVerticalNeighbors = true;

	/**
	 * @return true if the page has vertical (below and/or above) neighbor pages, false - if not.
	 */
	public boolean hasVerticalNeighbors() {
		return mHasVerticalNeighbors;
	}

}
