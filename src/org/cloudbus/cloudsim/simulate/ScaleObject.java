package org.cloudbus.cloudsim.simulate;

import java.util.List;

import org.cloudbus.cloudsim.Vm;

public class ScaleObject {
	private int appendMips = 0;
	private List<Vm> vmList;
	private int mips;
	private double scaleTime;
	
	public ScaleObject(List<Vm> vmList, double scaleTime, int appendMips, Boolean added) {
		this.scaleTime = scaleTime;
		this.appendMips = appendMips;
		this.vmList = vmList;
	}

	public double getScaleTime() {
		return scaleTime;
	}
	public void setScaleTime(double scaleTime) {
		this.scaleTime = scaleTime;
	}
	public int getMips() {
		return appendMips;
	}
	public int getAppendMips() {
		return appendMips;
	}
	public void setAppendMips(int mips) {
		this.appendMips = mips;
	}
	public List<Vm> getVmList() {
		return vmList;
	}
	public void setVmList(List<Vm> vmList) {
		this.vmList = vmList;
	}
}
