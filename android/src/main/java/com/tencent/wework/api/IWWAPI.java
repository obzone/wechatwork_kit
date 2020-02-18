package com.tencent.wework.api;

import android.content.Intent;

import com.tencent.wework.api.model.BaseMessage;

public interface IWWAPI {
    /**
     *
     * @param schema
     * @return
     */
    boolean registerApp(String schema);

    void unregisterApp();

    boolean handleIntent(Intent var1, IWWAPIEventHandler var2);

    /**
     *
     * @return �Ƿ�װ����ҵ΢��
     */
    boolean isWWAppInstalled();

    /**
     *
     * @return �Ƿ�֧��api
     */
    boolean isWWAppSupportAPI();

    /**
     *
     * @return ��װ����ҵ΢�Ű汾
     */
    int getWWAppSupportAPI();

    /**
     * ����ҵ΢��
     * @return
     */
    boolean openWWApp();

    /**
     *
     * @param ���͵���Ϣ
     * @return ��Ϣ�Ƿ�Ϸ�
     */
    boolean sendMessage(BaseMessage var1);

    /**
     *
     * @param var1 ���͵İ�
     * @param callback �ذ�
     * @return ��Ϣ�Ƿ�Ϸ�
     */
    boolean sendMessage(BaseMessage var1, IWWAPIEventHandler callback);
    void detach();
}
