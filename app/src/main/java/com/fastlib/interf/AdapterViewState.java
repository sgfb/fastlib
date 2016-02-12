package com.fastlib.interf;

public interface AdapterViewState {
	public static final int STATE_EMPTY=1;
	public static final int STATE_LOADING=2;
	public static final int STATE_LOADED=3;
	public static final int STATE_NO_MORE=4;
	public static final int STATE_ERROR=5;
	public static final int STATE_NO_NETWORK=6;
	
	public void onStateChanged(int state);
}
