package net.vpnsdk.wanve.contract;


import net.vpnsdk.wanve.base.BaseContract;
import net.vpnsdk.wanve.bean.NumBean;

/**
 * Created by zhou
 * on 2018/12/4.
 */

public interface WebContract {
    interface View extends BaseContract.BaseView {

        void GetPhoneAppNumSuccess(NumBean numBean);

        String setUserId();
        String setSignature();
        String setTimestamp();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void GetPhoneAppNum();

        String getUserId();
        String getSignature();
        String getTimestamp();
    }
}
