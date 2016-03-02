package com.fastlib.interf;

public interface AdapterViewState {
	int STATE_EMPTY=1;
	int STATE_LOADING=2;
	int STATE_LOADED=3;
	int STATE_NO_MORE=4;
	int STATE_ERROR=5;
	int STATE_NO_NETWORK=6;
	
	void onStateChanged(int state);
}
