package net.vpnsdk.wanve.contract;

import net.vpnsdk.wanve.base.BaseContract;
import net.vpnsdk.wanve.bean.MailBean;

import java.util.List;


/**
 * Created by zhou
 * on 2018/8/2.
 */

public interface MailContract {

    interface View extends BaseContract.BaseView {

        void onSuccess(List<MailBean> list);

        void onFailure(String errorMsg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        public abstract void getMailList(String param);
    }

}
