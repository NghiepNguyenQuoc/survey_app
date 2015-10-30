package com.nghiepnguyen.survey.Interface;

import com.nghiepnguyen.survey.model.CommonErrorModel;

/**
 * Created by nghiep on 10/29/15.
 */
public interface ICallBack {
    public void onSuccess(Object data);
    public void onFailure(CommonErrorModel error);
    public void onCompleted();
}
