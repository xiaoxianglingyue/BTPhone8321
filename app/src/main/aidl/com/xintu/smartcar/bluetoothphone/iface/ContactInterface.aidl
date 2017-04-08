package com.xintu.smartcar.bluetoothphone.iface;

import com.xintu.smartcar.bluetoothphone.iface.BTCallback;
 interface ContactInterface {
                   //verson 1
    int reqQueryContact(String strInfo);         //根据条件查联系人(语音)
    void reqConnectCall();                       //接通电话
    void reqOutgoingCall(String strInfo);        //主程序或者语音拨号
    void reqHungup();                            //挂断电话
	void registerCallback(BTCallback cb);
	void unregisterCallback(BTCallback cb);
}
