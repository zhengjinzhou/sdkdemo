/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\dev\\Android-L3SDK\\android_sdk_package_eclipse\\src\\net\\arraynetworks\\vpn\\IVpnService.aidl
 */
package net.vpnsdk.vpn;

public interface IVpnService extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements net.vpnsdk.vpn.IVpnService {
        private static final java.lang.String DESCRIPTOR = "net.vpnsdk.vpn.IVpnService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an net.arraynetworks.vpn.IVpnService interface,
         * generating a proxy if needed.
         */
        public static net.vpnsdk.vpn.IVpnService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof net.vpnsdk.vpn.IVpnService))) {
                return ((net.vpnsdk.vpn.IVpnService) iin);
            }
            return new net.vpnsdk.vpn.IVpnService.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_protectSocket: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _result = this.protectSocket(_arg0);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getVpnInterface: {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    int _result = this.getVpnInterface(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_stop: {
                    data.enforceInterface(descriptor);
                    this.stop();
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements net.vpnsdk.vpn.IVpnService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public int protectSocket(int socket) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(socket);
                    mRemote.transact(Stub.TRANSACTION_protectSocket, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public int getVpnInterface(java.lang.String configuration, java.lang.String host) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(configuration);
                    _data.writeString(host);
                    mRemote.transact(Stub.TRANSACTION_getVpnInterface, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void stop() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_stop, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_protectSocket = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_getVpnInterface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_stop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    }

    public int protectSocket(int socket) throws android.os.RemoteException;

    public int getVpnInterface(java.lang.String configuration, java.lang.String host) throws android.os.RemoteException;

    public void stop() throws android.os.RemoteException;
}
