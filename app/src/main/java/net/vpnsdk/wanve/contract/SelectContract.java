package net.vpnsdk.wanve.contract;


import net.vpnsdk.wanve.base.BaseContract;
import net.vpnsdk.wanve.bean.AppInfoBean;
import net.vpnsdk.wanve.bean.SNIDList;

import java.util.List;

/**
 * Created by zhou
 * on 2018/12/12.
 */

public interface SelectContract {
    interface View extends BaseContract.BaseView {
        void getSnidListSuccess(List<SNIDList> snidList);
        String setKeywork();
        void getAppinfoSuccess(AppInfoBean appInfoBean);
        String setSNID();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getSnidList();
        String getKeywork();
        void getAppInfo();
        String getSNID();
    }
}
