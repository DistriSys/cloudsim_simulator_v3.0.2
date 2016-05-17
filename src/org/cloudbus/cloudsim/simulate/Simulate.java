package org.cloudbus.cloudsim.simulate;

import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * A simple example showing how to create a data center with one host and run
 * one cloudlet on it.
 */
public class Simulate {
	
	private static final String testcaseFilePath = "D:\\PD Nguyen\\workspace\\2016CSCloud\\src\\cloudsim_simulator_v3.0.2\\testcases\\testcase_upscale\\testcase_ISPDC_upcale_2x_200.json";
//	private static final String testcaseFilePath = "/home/ngtrieuvi92/zz/eclipse/cloudsim_simulator_v2.0/testcases/testcase_1.json";
	
	/**
	 * if USER_ALPHA_RATIO = true; simulate will apply alpha ratio to calc, 
	 * if USER_ALPHA_RATIO = false alpha ratio will be set 1:1
	 */
	
	public static final boolean USER_ALPHA_RATIO = true;
	public static final boolean SCALABLE = true;
	public static final boolean UPDATE_SCALE_PARTNER = true;
	public static final boolean PARTNER_BARTERING = true;
	public static final double PARTNER_BARTERING_THRESHOLD = 2;

	public static int cloudletLength = 100;
	
	private static List<DatacenterBroker> brokersList;

	/**
	 * Creates main() to run this example.
	 *
	 * @param args
	 *            the args
	 */
//	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample1...");
		brokersList = new ArrayList<DatacenterBroker>();

		try {
			// Initialize CloudSim
			int num_user = 1;
			Calendar calendar = Calendar.getInstance(); 
			boolean trace_flag = false;
			CloudSim.init(num_user, calendar, trace_flag);
			
			
			// Read data from json file
			FileReader reader = new FileReader(testcaseFilePath);

            JSONParser jsonParser = new JSONParser();
            JSONArray members = (JSONArray) jsonParser.parse(reader);

            // Create Datacenterbrokers
            for (int i = 0; i < members.size(); i++) {
            	JSONObject member = (JSONObject) members.get(i);
            	
            	String m_name = (String) member.get("name");
            	Log.printLine(m_name);
            	CustomDatacenterBroker broker = createBroker(m_name);
            	brokersList.add(broker);
            	
//            	List<Vm> vmList = new ArrayList<Vm>();
            	List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
            	
            	// Create datacenters
            	JSONObject m_datacenters = (JSONObject) member.get("datacenters");
            	createDatacenter(m_datacenters, broker);
            	
            	JSONArray m_cloudlets = (JSONArray) member.get("cloudlets");
            	if (m_cloudlets == null) {
            		Log.printLine(broker.getName() + ": There is no cloudlet");
            		continue;
            	}
            	
            	for (int j = 0; j < m_cloudlets.size(); j++) {
            		JSONObject m_cloudlet = (JSONObject) m_cloudlets.get(j);
	            	int cloudlet_quantity = ((Long) m_cloudlet.get("quantity")).intValue();
	            	int cloudletId_prefix = broker.getId() * 1000000 + j * 1000;
	            	
	        		long length = (Long) m_cloudlet.get("long");
	        		if (cloudletLength == 0) cloudletLength = (int) length;
	        		long fileSize = (Long) m_cloudlet.get("fileSize");
	        		long outputSize = (Long) m_cloudlet.get("outputSize");
	        		int pesNumber = ((Long) m_cloudlet.get("pesNumber")).intValue();
	        		double deadline = ((Long) m_cloudlet.get("deadline")).doubleValue();
	        		double userRequestTime = ((Long) m_cloudlet.get("userRequestTime")).doubleValue();
	            	
	            	for (int k = 0; k < cloudlet_quantity; k++) {          		
	            		UtilizationModel utilizationModel = new UtilizationModelFull();
	            		
	            		Cloudlet cloudlet = new Cloudlet(cloudletId_prefix + k, length, pesNumber, fileSize, outputSize, 
	            				utilizationModel, utilizationModel, utilizationModel, deadline, userRequestTime);
	            		cloudlet.setUserId(broker.getId());
	            		cloudletList.add(cloudlet);
	            	}
            	}
            	
            	JSONArray scaleArr = (JSONArray) member.get("scale");
            	if (scaleArr != null) {
            		for (int j = 0; j < scaleArr.size(); j++) {
            			int index = 0;
            			JSONObject m_scale = (JSONObject) scaleArr.get(j);
            			double scaleTime = ((Long) m_scale.get("scaleTime")).doubleValue();

            			List<Vm> vmList = new ArrayList<Vm>();
                    	JSONObject m_vm = (JSONObject) m_scale.get("vms");
                    	int appendMips = createVmList(vmList, m_vm, broker, i);
                    	
            			ScaleObject newso = new ScaleObject(vmList, scaleTime, appendMips, true);

            			/* Instead of using sorted support data structure
            			 * We apply the sorted insertion approach 
            			 */
            			for(ScaleObject so: broker.getScaleList()){
            				if (newso.getScaleTime() > so.getScaleTime())
            					 index++;
            			}
            			broker.getScaleList().add(index,newso);
            		}
            	}

            	broker.submitCloudletList(cloudletList);
            }
            
            
			CloudSim.startSimulation();
			CloudSim.stopSimulation();

			for (DatacenterBroker broker : brokersList) {
				List<Cloudlet> newList = broker.getCloudletList();
				printCloudletList(newList);
			}
			
			for (DatacenterBroker broker : brokersList) {
				CustomDatacenterBroker cusBroker = (CustomDatacenterBroker) broker;
				List<Cloudlet> newList = broker.getCloudletList();
				List<PartnerInfomation> partnerInfo = cusBroker.getPartnersList();
				printResult(newList, partnerInfo, cusBroker.getName());
				
			}
			printTestCaseResult();
				
			Log.printLine("CloudSimExample1 finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
	}
	
	private static void createDatacenter(JSONObject d_info, CustomDatacenterBroker broker) {
		String name = broker.getName();
		Log.printLine(name + ": creating datacenters");
		
		int datacenter_quantity = ((Long) d_info.get("quantity")).intValue();
		
		String arch = (String) d_info.get("arch");
		String os = (String) d_info.get("os");
		String vmm = (String) d_info.get("vmm");
		double time_zone = ((Long) d_info.get("time_zone")).doubleValue();
		double cost = ((Long) d_info.get("cost")).doubleValue();
		double costPerMem = (double) d_info.get("costPerMem");
		double costPerStorage = (double) d_info.get("costPerStorage");
		double costPerBw = ((Long) d_info.get("costPerBw")).doubleValue();
		JSONObject hosts = (JSONObject) d_info.get("hosts");
		int hosts_quantity = ((Long) hosts.get("quantity")).intValue();
		
		for (int i = 0; i < datacenter_quantity; i++) {
			List<Host> hostList = new ArrayList<Host>();
			
			for (int j = 0; j < hosts_quantity; j++) {
				int ram = ((Long) hosts.get("ram")).intValue();
				long storage = (Long) hosts.get("storage");
				int bw = ((Long) hosts.get("bw")).intValue();
				
				JSONObject pes = (JSONObject) hosts.get("pes");
				int pes_quantity = ((Long) pes.get("quantity")).intValue();
				long pes_mips = (Long) pes.get("mips");
				List<Pe> peList = new ArrayList<Pe>();
				for (int k = 0; k < pes_quantity; k++) {
					peList.add(new Pe(k, new PeProvisionerSimple(pes_mips)));
				}
				
				hostList.add(new Host(j, new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), 
						storage , peList, new VmSchedulerTimeShared(peList)));
			}
		
			LinkedList<Storage> storageList = new LinkedList<Storage>();
		
			DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
					arch, os, vmm, hostList, time_zone, cost, costPerMem,
					costPerStorage, costPerBw);

		// 6. Finally, we need to create a Datacenter object.
			try {
				CustomDatacenter datacenter = new CustomDatacenter(name, characteristics,
						new VmAllocationPolicySimple(hostList), storageList, 0, broker);
				
				broker.addDatacenter(datacenter.getId());
				
				List<Vm> vmList = new ArrayList<Vm>();
            	JSONObject m_vm = (JSONObject) d_info.get("vms");
            	createVm(vmList, m_vm, broker, i);
            	broker.submitVmList(vmList);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int createVmList(List<Vm> vmList, JSONObject m_vm, CustomDatacenterBroker broker, int datacenterIndex) {
		int total_mips = 0;
		String brokerName = broker.getName();
		int vm_quantity = ((Long) m_vm.get("quantity")).intValue();
		int vmId_prefix = broker.getId() * 1000 + datacenterIndex * 100;
		for (int i = 0; i < vm_quantity; i++) {
			int mips = ((Long) m_vm.get("mips")).intValue();
			long size = (Long) m_vm.get("size"); // image size (MB)
			int ram = ((Long) m_vm.get("ram")).intValue(); // vm memory (MB)
			long bw = (Long) m_vm.get("bw");
			int pesNumber = ((Long) m_vm.get("pesNumber")).intValue(); // number of cpus
			String vmm = "Xen"; // VMM name
			int vmId = vmId_prefix + i;
			
			CloudletSchedulerSpaceShared scheduler = new CloudletSchedulerSpaceShared(mips);
//			scheduler.setMips(mips);

			// create VM
			Vm vm = new Vm(vmId, broker.getId(), mips, pesNumber, ram, bw, size, vmm,
					scheduler);
			
			vmList.add(vm);

			total_mips += mips;
			
			Log.printLine(brokerName + ": creating Vm #" + vmId);
		}

		return total_mips;
	}

	private static void createVm(List<Vm> vmList, JSONObject m_vm, CustomDatacenterBroker broker, int datacenterIndex) {
			int appendMips = createVmList(vmList, m_vm, broker, datacenterIndex);
			
			broker.setVmSize(appendMips);
	}

	
	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private static CustomDatacenterBroker createBroker(String name) {
		CustomDatacenterBroker broker = null;
		try {
			broker = new CustomDatacenterBroker(name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list
	 *            list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + indent + "VM ID" + indent  +"Time" + indent
				 + "Lengh"+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent  
						+ cloudlet.getCloudletLength()
						+ indent + indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			} else if (cloudlet.getCloudletStatus() == Cloudlet.FAILED) {
					Log.print("FAILED");

					Log.printLine(indent + indent + cloudlet.getResourceId()
							+ indent + indent + indent + cloudlet.getVmId()
							+ indent + indent
							+ dft.format(cloudlet.getActualCPUTime()) + indent
							+ indent + dft.format(cloudlet.getExecStartTime())
							+ indent + indent
							+ dft.format(cloudlet.getFinishTime()));
			} else {
				Log.print("NOT RETURN");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
	
	private static void printResult(List<Cloudlet> list, List<PartnerInfomation> partnerInfo, String name) {
		int totalCloudlet = list.size();
		int totalPartner = partnerInfo.size();
		int successCloudlet = 0;
		double totalKRatio = 0;
		Cloudlet cloudlet;
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== RESULT ==========");
		Log.printLine("Name" + indent + "Total" + indent  + "Success" + indent
				+ "%_success" + indent + "K" + indent);
		Log.printLine();
		DecimalFormat dft = new DecimalFormat("###.##");
		
		for (int i = 0; i < totalCloudlet; i++) {
			cloudlet = list.get(i);
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				successCloudlet++;
			}
		}
		
		for (int i = 0; i < totalPartner; i++) {
			PartnerInfomation pInfo = partnerInfo.get(i);
//			Log.printLine(pInfo.getPartnerId()+":"+pInfo.getkRatio());
			totalKRatio += pInfo.getKRatio();
//			Log.printLine(pInfo.getRequested() + " / " + pInfo.getSatified());
		}
		
		Log.printLine(name+ indent + totalCloudlet + indent  + indent + successCloudlet + indent + indent +  dft.format((double)successCloudlet / totalCloudlet * 100) + "%"
				+ indent + indent + dft.format(totalKRatio / totalPartner * 100) + "%");
	}
	
	private static void printTestCaseResult() {
		String indent = "    ";
		Log.printLine();
		Log.printLine("========== TESTCASE RESULT ==========");
		Log.printLine("Total" + indent  + "Success" + indent
				+ "%_success" + indent + "K" + indent);
		Log.printLine();
		DecimalFormat dft = new DecimalFormat("###.##");
		
		int total_cloudlet = 0;
		int total_partner = 0;
		double total_partner_k_ratio = 0;
		int total_lenght = 0;
		int total_cloulet_success = 0;
		int total_cloulet_lengh_success = 0;
		for (DatacenterBroker broker : brokersList) {
			CustomDatacenterBroker cusBroker = (CustomDatacenterBroker) broker;
			List<Cloudlet> newList = broker.getCloudletList();
			total_cloudlet += newList.size();
			
			for (int i = 0; i < newList.size(); i++) {
				Cloudlet cloudlet = newList.get(i);
				total_lenght += cloudlet.getCloudletLength(); 
				if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
					total_cloulet_success++;
					total_cloulet_lengh_success += cloudlet.getCloudletLength();
				}
			}
			
			List<PartnerInfomation> partnerInfo = cusBroker.getPartnersList();
			total_partner +=partnerInfo.size();
			for (int i = 0; i < partnerInfo.size(); i++) {
				PartnerInfomation pInfo = partnerInfo.get(i);
//				Log.printLine(pInfo.getPartnerId()+":"+pInfo.getkRatio());
				total_partner_k_ratio += Math.abs(pInfo.getKRatio());
			}
		}
		
		Log.printLine(total_lenght + indent  + indent + total_cloulet_lengh_success + indent + indent +  dft.format((double)total_cloulet_lengh_success / total_lenght * 100) + "%"
				+ indent + indent + dft.format(total_partner_k_ratio / total_partner * 100) + "%");
	}
}

