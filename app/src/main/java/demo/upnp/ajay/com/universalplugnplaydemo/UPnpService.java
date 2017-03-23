package demo.upnp.ajay.com.universalplugnplaydemo;


import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDAServiceType;

/**
 * Created by Ajay on 3/23/2017.
 */
public class UPnpService extends AndroidUpnpServiceImpl {

    public static final int REG_MAINTAINANCE_INTERVAL = 5000;

    @Override
    protected UpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {

            @Override
            public ServiceType[] getExclusiveServiceTypes() {
                return new ServiceType[]{
                        new UDAServiceType("SwitchPower")
                };
            }

            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return REG_MAINTAINANCE_INTERVAL;
            }
        };
    }
}
