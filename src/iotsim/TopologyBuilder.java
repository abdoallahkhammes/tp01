package iotsim;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.policy.AppModuleAllocationPolicy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TopologyBuilder {

    // ======= إنشاء Cloud =======
    public static FogDevice createCloud() throws Exception {
        return createFogDevice(
                "Cloud",
                44800,   // mips
                40960,   // ram (MB)
                100,     // uplink bandwidth
                10000,   // downlink bandwidth
                0.01,    // rate per mips
                16*103,  // busy power
                16*83.25 // idle power
        );
    }

    // ======= إنشاء Proxy =======
    public static FogDevice createProxy() throws Exception {
        return createFogDevice(
                "Proxy",
                2800,
                4096,
                10000,
                10000,
                0.0,
                107.339,
                83.4333
        );
    }

    // ======= إنشاء Edge Device =======
    public static FogDevice createEdge(int index) throws Exception {
        return createFogDevice(
                "Edge-" + index,
                2800,
                1024,
                10000,
                270,
                0.0,
                87.53,
                82.44
        );
    }

    // ======= الدالة الأساسية لإنشاء أي FogDevice =======
    private static FogDevice createFogDevice(
            String name, long mips, int ram,
            long upBw, long downBw,
            double ratePerMips,
            double busyPower, double idlePower) throws Exception {

        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(mips)));

        PowerHost host = new PowerHost(
                0,
                new RamProvisionerSimple(ram),
                new BwProvisionerSimple(10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList),
                new org.cloudbus.cloudsim.power.models.PowerModelLinear(busyPower, idlePower)
        );

        List<PowerHost> hostList = new ArrayList<>();
        hostList.add(host);

        FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
                "x86", "Linux", "Xen",
                host, 10, 3, 0.05, 0.001, 0.0
        );

        return new FogDevice(
                name, characteristics,
                new AppModuleAllocationPolicy(hostList),
                new LinkedList<Storage>(),
                10, upBw, downBw, 0, ratePerMips
        );
    }
}