/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\android studio\\BTPhone83211\\app\\src\\main\\aidl\\com\\xintu\\smartcar\\bluetoothphone\\iface\\ContactInterface.aidl
 */
package com.xintu.smartcar.bluetoothphone.iface;
public interface ContactInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.xintu.smartcar.bluetoothphone.iface.ContactInterface
{
private static final java.lang.String DESCRIPTOR = "com.xintu.smartcar.bluetoothphone.iface.ContactInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.xintu.smartcar.bluetoothphone.iface.ContactInterface interface,
 * generating a proxy if needed.
 */
public static com.xintu.smartcar.bluetoothphone.iface.ContactInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.xintu.smartcar.bluetoothphone.iface.ContactInterface))) {
return ((com.xintu.smartcar.bluetoothphone.iface.ContactInterface)iin);
}
return new com.xintu.smartcar.bluetoothphone.iface.ContactInterface.Stub.Proxy(obj);
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
case TRANSACTION_reqQueryContact:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.reqQueryContact(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_reqConnectCall:
{
data.enforceInterface(DESCRIPTOR);
this.reqConnectCall();
reply.writeNoException();
return true;
}
case TRANSACTION_reqOutgoingCall:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.reqOutgoingCall(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_reqHungup:
{
data.enforceInterface(DESCRIPTOR);
this.reqHungup();
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.BTCallback _arg0;
_arg0 = com.xintu.smartcar.bluetoothphone.iface.BTCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.xintu.smartcar.bluetoothphone.iface.BTCallback _arg0;
_arg0 = com.xintu.smartcar.bluetoothphone.iface.BTCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.xintu.smartcar.bluetoothphone.iface.ContactInterface
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
@Override public int reqQueryContact(java.lang.String strInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strInfo);
mRemote.transact(Stub.TRANSACTION_reqQueryContact, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//根据条件查联系人(语音)

@Override public void reqConnectCall() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqConnectCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//接通电话

@Override public void reqOutgoingCall(java.lang.String strInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(strInfo);
mRemote.transact(Stub.TRANSACTION_reqOutgoingCall, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//主程序或者语音拨号

@Override public void reqHungup() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_reqHungup, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
//挂断电话

@Override public void registerCallback(com.xintu.smartcar.bluetoothphone.iface.BTCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.xintu.smartcar.bluetoothphone.iface.BTCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_reqQueryContact = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_reqConnectCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_reqOutgoingCall = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_reqHungup = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public int reqQueryContact(java.lang.String strInfo) throws android.os.RemoteException;
//根据条件查联系人(语音)

public void reqConnectCall() throws android.os.RemoteException;
//接通电话

public void reqOutgoingCall(java.lang.String strInfo) throws android.os.RemoteException;
//主程序或者语音拨号

public void reqHungup() throws android.os.RemoteException;
//挂断电话

public void registerCallback(com.xintu.smartcar.bluetoothphone.iface.BTCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.xintu.smartcar.bluetoothphone.iface.BTCallback cb) throws android.os.RemoteException;
}
