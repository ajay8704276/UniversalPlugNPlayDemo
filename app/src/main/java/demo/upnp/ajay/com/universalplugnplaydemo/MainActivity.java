package demo.upnp.ajay.com.universalplugnplaydemo;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.*;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class MainActivity extends ListActivity {

    public ProgressDialog mProgressDialog ;
    public ListView mListView = null;

    private ArrayAdapter<DisplayDeviceData> listAdapter = null;

    private DeviceRegistryListener deviceRegistryListener = new DeviceRegistryListener();

    private AndroidUpnpService androidUpnpService = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            if(mProgressDialog!=null){
                if(mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                    androidUpnpService = (AndroidUpnpService) service;

                    // Clear the list
                    listAdapter.clear();

                    // getting ready for listening devices
                    androidUpnpService.getRegistry().addListener(deviceRegistryListener);

                    // Adding All devices to the list
                    for (Device device : androidUpnpService.getRegistry().getDevices()) {
                        deviceRegistryListener.deviceAdded(device);
                    }

                    // Searching all nearby devices
                    androidUpnpService.getControlPoint().search();
                }
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            // Unregister upnp service on disconnection
            androidUpnpService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please Wait!!");
        mProgressDialog.setMessage("Please wait searching For UPnp devices !!!!");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        listAdapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_2);
        setListAdapter(listAdapter);

        // This will start the UPnP service if it wasn't already started
        getApplicationContext().bindService(
                new Intent(this, UPnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(R.string.deviceDetails);
        DisplayDeviceData deviceDisplay = (DisplayDeviceData) l.getItemAtPosition(position);
        dialog.setMessage(deviceDisplay.getDetailsMessage());
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(12);
        super.onListItemClick(l, v, position, id);
    }

    protected class DeviceRegistryListener extends DefaultRegistryListener {

        @Override
        public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
            deviceAdded(device);
        }


        @Override
        public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(
                            MainActivity.this,
                            "Discovery failed of '" + device.getDisplayString() + "': "
                                    + (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
            deviceRemoved(device);

        }


        @Override
        public void remoteDeviceAdded(Registry registry, RemoteDevice device) {

            deviceAdded(device);
        }


        @Override
        public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {

            deviceRemoved(device);
        }

        @Override
        public void localDeviceAdded(Registry registry, LocalDevice device) {

            deviceRemoved(device);
        }

        @Override
        public void localDeviceRemoved(Registry registry, LocalDevice device) {
            deviceRemoved(device);
        }

        private void deviceRemoved(final Device device) {
            runOnUiThread(new Runnable() {
                public void run() {
                    listAdapter.remove(new DisplayDeviceData(device, MainActivity.this));
                }
            });
        }

        public void deviceAdded(final Device device) {

            runOnUiThread(new Runnable() {
                public void run() {
                    DisplayDeviceData d = new DisplayDeviceData(device, MainActivity.this);
                    int position = listAdapter.getPosition(d);
                    if (position >= 0) {
                        // Device already in the list, re-set new value at same position
                        listAdapter.remove(d);
                        listAdapter.insert(d, position);
                    } else {
                        listAdapter.add(d);
                    }
                }
            });
        }

    }

}
