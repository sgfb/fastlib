package com.fastlib.aspect;

/**
 * Created by sgfb on 2020\02\17.
 */
public class ActionResult {
    public boolean isPassed;
    public Object result;
    public MethodResultCallback rawResultCallback;

    public ActionResult() {
    }

    public ActionResult(boolean isPassed, Object result, MethodResultCallback rawResultCallback) {
        this.isPassed = isPassed;
        this.result = result;
        this.rawResultCallback = rawResultCallback;
    }
}
