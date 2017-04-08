/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\android studio\\BTPhone83211\\app\\src\\main\\aidl\\com\\xintu\\smartcar\\bluetoothphone\\iface\\BTCallback.aidl
 */
package com.xintu.smartcar.bluetoothphone.iface;
public interface BTCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.xintu.smartcar.bluetoothphone.iface.BTCallback
{
private static final java.lang.String DESCRIPTOR = "com.xintu.smartcar.bluetoothphone.iface.BTCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.xintu.smartcar.bluetoothphone.iface.BTCallback interface,
 * generating a proxy if needed.
 */
public static com.xintu.smartcar.bluetoothphone.iface.BTCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.xintu.smartcar.bluetoothphone.iface.BTCallback))) {
return ((com.xintu.smartcar.bluetoothphone.iface.BTCallback)iin);
}
return new com.xintu.smartcar.bluetoothphone.iface.BTCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_notifyBTCheckStatus:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.notifyBTCheckStatus(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTContact:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.notifyBTContact(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTRecordCall:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.notifyBTRecordCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTCommingCall:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.CurrentCall _arg0;
if ((0!=data.readInt())) {
_arg0 = com.xintu.smartcar.bluetoothphone.iface.CurrentCall.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.notifyBTCommingCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTOutgoingCall:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.CurrentCall _arg0;
if ((0!=data.readInt())) {
_arg0 = com.xintu.smartcar.bluetoothphone.iface.CurrentCall.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.notifyBTOutgoingCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTConnectedCall:
{
data.enforceInterface(DESCRIPTOR);
this.notifyBTConnectedCall();
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTCallTimer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.notifyBTCallTimer(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTHungup:
{
data.enforceInterface(DESCRIPTOR);
this.notifyBTHungup();
reply.writeNoException();
return true;
}
case TRANSACTION_notifyBTisUP:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.notifyBTisUP(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.xintu.smartcar.bluetoothphone.iface.BTCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
//verson 1

@Override public void notifyBTCheckStatus(boolean isConn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isConn)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_notifyBTCheckStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//蓝牙是否连接

@Override public void notifyBTContact(com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyBTContact, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//语音查询显示联系人列表

@Override public void notifyBTRecordCall(com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((info!=null)) {
_data.writeInt(1);
info.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyBTRecordCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//通话记录

@Override public void notifyBTCommingCall(com.xintu.smartcar.bluetoothphone.iface.CurrentCall call) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((call!=null)) {
_data.writeInt(1);
call.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyBTCommingCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//有来电

@Override public void notifyBTOutgoingCall(com.xintu.smartcar.bluetoothphone.iface.CurrentCall call) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((call!=null)) {
_data.writeInt(1);
call.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyBTOutgoingCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//播出电话

@Override public void notifyBTConnectedCall() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyBTConnectedCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//接通电话

@Override public void notifyBTCallTimer(java.lang.String time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(time);
mRemote.transact(Stub.TRANSACTION_notifyBTCallTimer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//通话时间

@Override public void notifyBTHungup() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyBTHungup, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//挂断电话

@Override public void notifyBTisUP(boolean isflag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isflag)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_notifyBTisUP, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_notifyBTCheckStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_notifyBTContact = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_notifyBTRecordCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_notifyBTCommingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_notifyBTOutgoingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_notifyBTConnectedCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_notifyBTCallTimer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_notifyBTHungup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_notifyBTisUP = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
//verson 1

public void notifyBTCheckStatus(boolean isConn) throws android.os.RemoteException;
//蓝牙是否连接

public void notifyBTContact(com.xintu.smartcar.bluetoothphone.iface.SelectContactInfo info) throws android.os.RemoteException;
//语音查询显示联系人列表

public void notifyBTRecordCall(com.xintu.smartcar.bluetoothphone.iface.CallRecordInfo info) throws android.os.RemoteException;
//通话记录

public void notifyBTCommingCall(com.xintu.smartcar.bluetoothphone.iface.CurrentCall call) throws android.os.RemoteException;
//有来电

public void notifyBTOutgoingCall(com.xintu.smartcar.bluetoothphone.iface.CurrentCall call) throws android.os.RemoteException;
//播出电话

public void notifyBTConnectedCall() throws android.os.RemoteException;
//接通电话

public void notifyBTCallTimer(java.lang.String time) throws android.os.RemoteException;
//通话时间

public void notifyBTHungup() throws android.os.RemoteException;
//挂断电话

public void notifyBTisUP(boolean isflag) throws android.os.RemoteException;
}
