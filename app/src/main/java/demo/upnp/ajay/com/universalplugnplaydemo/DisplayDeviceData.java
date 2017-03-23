package demo.upnp.ajay.com.universalplugnplaydemo;

import android.content.Context;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;

/**
 * Created by Ajay on 3/23/2017.
 */
public class DisplayDeviceData {

    private Device device;
    private Context mContext = null;

    public DisplayDeviceData(Device device, Context mContext) {
        this.device = device;
        this.mContext = mContext;
    }

    public Device getDevice() {
        return device;
    }

    // DOC:DETAILS
    public String getDetailsMessage() {
        StringBuilder sb = new StringBuilder();
        if (getDevice().isFullyHydrated()) {
            sb.append(getDevice().getDisplayString());
            sb.append("\n\n");
            for (Service service : getDevice().getServices()) {
                sb.append(service.getServiceType()).append("\n");
            }
        } else {
            sb.append(mContext.getString(R.string.deviceDetailsNotYetAvailable));
        }
        return sb.toString();
    }
    // DOC:DETAILS

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DisplayDeviceData that = (DisplayDeviceData) object;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public String toString() {
        String name =
                getDevice().getDetails() != null && getDevice().getDetails().getFriendlyName() != null
                        ? getDevice().getDetails().getFriendlyName()
                        : getDevice().getDisplayString();
        // Display a little star while the device is being loaded (see performance optimization earlier)
        return device.isFullyHydrated() ? name : name + " *";
    }
}

