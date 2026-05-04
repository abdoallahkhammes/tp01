package iotsim;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.Actuator;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.Sensor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IoTSimulation {

    static List<FogDevice> fogDevices = new ArrayList<>();
    static List<Sensor> sensors = new ArrayList<>();
    static List<Actuator> actuators = new ArrayList<>();

    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("   IoT Simulation Using iFogSim         ");
            System.out.println("========================================\n");

            // ======= 1. تهيئة CloudSim =======
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);
            Log.disable(); // إخفاء logs الداخلية

            // ======= 2. إنشاء Broker =======
            FogBroker broker = new FogBroker("IoT-Broker");

            // ======= 3. بناء الطوبولوجيا =======
            System.out.println("🔧 Building Topology...");
            FogDevice cloud = TopologyBuilder.createCloud();
            FogDevice proxy = TopologyBuilder.createProxy();
            FogDevice edge  = TopologyBuilder.createEdge(0);

            // ربط الأجهزة مع بعض
            proxy.setParentId(cloud.getId());
            proxy.setUplinkLatency(100); // ms بين proxy و cloud

            edge.setParentId(proxy.getId());
            edge.setUplinkLatency(2);   // ms بين edge و proxy

            // إضافة للقائمة
            fogDevices.add(cloud);
            fogDevices.add(proxy);
            fogDevices.add(edge);

            // ======= 4. إنشاء Sensors =======
            System.out.println("📡 Creating IoT Sensors...");
            String[] sensorTypes = {
                    "Temperature", "Humidity", "Pressure", "Motion", "Light"
            };

            for (int i = 0; i < 5; i++) {
                Sensor sensor = new Sensor(
                        "Sensor-" + sensorTypes[i],  // اسم
                        "IoT-APP",                    // tuple type
                        broker.getId(),               // broker
                        // جديد
                        String.valueOf(edge.getId()),            // متصل بـ edge
                        new org.fog.utils.distribution.DeterministicDistribution(5) // كل 5 ثواني
                );
                sensor.setLatency(1.0); // latency بين sensor و edge
                sensors.add(sensor);
                System.out.println("   ✅ " + sensorTypes[i] + " Sensor created");
            }

            // ======= 5. إنشاء Actuator =======
            Actuator actuator = new Actuator(
                    "Actuator-0",
                    broker.getId(),
                    // جديد
                    String.valueOf(edge.getId()),
                    "IoT-ACTUATOR"
            );
            actuators.add(actuator);

            // ======= 6. عرض الطوبولوجيا =======
            System.out.println("\n🗺️  Topology:");
            System.out.println("   [Sensors x5] → [Edge] → [Proxy] → [Cloud]");
            System.out.println("   Sensor→Edge  latency : 1 ms");
            System.out.println("   Edge→Proxy   latency : 2 ms");
            System.out.println("   Proxy→Cloud  latency : 100 ms");

            // ======= 7. تحليل الشبكة =======
            NetworkAnalyzer.analyzeNetworkLoad(fogDevices);
            NetworkAnalyzer.compareEdgeVsCloud(edge, cloud);

            // ======= 8. تشغيل المحاكاة =======
            System.out.println("🚀 Starting Simulation...\n");
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            System.out.println("✅ Simulation Finished Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}