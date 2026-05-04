package iotsim;

import org.fog.entities.FogDevice;
import java.util.List;

public class NetworkAnalyzer {

    // ======= تحليل حمل الشبكة =======
    public static void analyzeNetworkLoad(List<FogDevice> devices) {
        System.out.println("\n========================================");
        System.out.println("        NETWORK LOAD ANALYSIS           ");
        System.out.println("========================================");

        for (FogDevice device : devices) {
            System.out.println("\n📡 Device: " + device.getName());
            System.out.println("   ├── Uplink Bandwidth  : " + device.getUplinkBandwidth() + " Mbps");
            System.out.println("   ├── Downlink Bandwidth: " + device.getDownlinkBandwidth() + " Mbps");
            System.out.println("   ├── Uplink Latency    : " + device.getUplinkLatency() + " ms");
            System.out.println("   └── Total MIPS        : " + device.getHost().getTotalMips());
        }

        System.out.println("\n========================================");
        System.out.println("        DATA FLOW SUMMARY               ");
        System.out.println("========================================");
        analyzeDataFlow(devices);
    }

    // ======= تحليل تدفق البيانات =======
    private static void analyzeDataFlow(List<FogDevice> devices) {
        double totalUplink = 0;
        double totalDownlink = 0;

        for (FogDevice device : devices) {
            totalUplink += device.getUplinkBandwidth();
            totalDownlink += device.getDownlinkBandwidth();
        }

        System.out.println("\n📊 Total Uplink Bandwidth   : " + totalUplink + " Mbps");
        System.out.println("📊 Total Downlink Bandwidth : " + totalDownlink + " Mbps");
        System.out.println("📊 Number of Devices        : " + devices.size());
        System.out.println("📊 Avg Uplink per Device    : " + (totalUplink / devices.size()) + " Mbps");
        System.out.println("📊 Avg Downlink per Device  : " + (totalDownlink / devices.size()) + " Mbps");

        // تحديد أكثر جهاز حمل
        FogDevice mostLoaded = devices.stream()
                .max((a, b) -> (int)(a.getUplinkBandwidth() - b.getUplinkBandwidth()))
                .orElse(null);

        if (mostLoaded != null) {
            System.out.println("\n⚠️  Most Loaded Device: " + mostLoaded.getName()
                    + " (" + mostLoaded.getUplinkBandwidth() + " Mbps uplink)");
        }

        System.out.println("========================================\n");
    }

    // ======= مقارنة Edge vs Cloud =======
    public static void compareEdgeVsCloud(FogDevice edge, FogDevice cloud) {
        System.out.println("\n========================================");
        System.out.println("       EDGE vs CLOUD COMPARISON         ");
        System.out.println("========================================");
        System.out.printf("%-25s %-15s %-15s%n", "Metric", "Edge", "Cloud");
        System.out.println("------------------------------------------");
        System.out.printf("%-25s %-15s %-15s%n",
                "MIPS",
                edge.getHost().getTotalMips(),
                cloud.getHost().getTotalMips());
        System.out.printf("%-25s %-15s %-15s%n",
                "Uplink BW (Mbps)",
                edge.getUplinkBandwidth(),
                cloud.getUplinkBandwidth());
        System.out.printf("%-25s %-15s %-15s%n",
                "Downlink BW (Mbps)",
                edge.getDownlinkBandwidth(),
                cloud.getDownlinkBandwidth());
        System.out.printf("%-25s %-15s %-15s%n",
                "Uplink Latency (ms)",
                edge.getUplinkLatency(),
                cloud.getUplinkLatency());
        System.out.println("========================================\n");
    }
}