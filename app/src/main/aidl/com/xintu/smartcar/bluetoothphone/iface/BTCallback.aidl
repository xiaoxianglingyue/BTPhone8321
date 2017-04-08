package com.xintu.smartcar.bluetoothphone.iface;

import com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo;
import com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo;
import com.xintu.smartcar.bluetoothphone.iface.CurrentCall;
interface BTCallback {
           //verson 1
  void notifyBTCheckStatus(boolean isConn);          //蓝牙是否连接
  void notifyBTContact(in SelectContactInfo info);    //语音查询显示联系人列表
  void notifyBTRecordCall(in CallRecordInfo info);     //通话记录
  void notifyBTCommingCall(in CurrentCall call);     //有来电
  void notifyBTOutgoingCall (in CurrentCall call);   //播出电话
  void notifyBTConnectedCall();                      //接通电话
  void notifyBTCallTimer(String time);               //通话时间
  void notifyBTHungup();                             //挂断电话
  void notifyBTisUP(boolean isflag);                 //蓝牙主页面是否在最上面
}
