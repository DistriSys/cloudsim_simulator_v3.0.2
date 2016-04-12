package org.cloudbus.cloudsim.simulate;


public class PartnerInfomation {

	private int partnerId;
	
	private double contractRatio;
	
	private double requested;
	
	private double satified;
	
	private int partnerVm;
	
	private CustomDatacenterBroker broker;
	
	/**
	 * L in argithorm
	 */
	private double lenghtRatio;
	
	/**
	 * l in argithorm
	 */
	private double kRatio;

	
	public PartnerInfomation(int partnerId, double ratio, double requested,
			double satified,double lenghtRatio,  double kRatio) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = ratio;
		this.requested = 0;
		this.satified = 0;
		this.lenghtRatio = lenghtRatio;
		this.kRatio = kRatio;
	}

	public PartnerInfomation(int partnerId) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = 1;
		this.requested = 0;
		this.satified = 0;
		this.lenghtRatio = 0;
		this.kRatio = 0;
	}
	
	public PartnerInfomation(int partnerId, double ratio, int partnerVm, CustomDatacenterBroker broker) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = ratio;
		if (ratio == 1) {
			this.requested = 100;
			this.satified = 100;
		} else {
			this.requested = partnerVm;
			this.satified = broker.getVmSize();
		}
		this.lenghtRatio = ratio;
		this.kRatio = 0;
		this.broker = broker;
		this.setPartnerVm(partnerVm);
	}
	
	public double numOfTaskCanSatisfy() {
//		double maxSatisfiable = (getRequested() + broker.getVmSize()) / getRatio();
		double maxSatisfiable = getRequested() / getContractRatio() + broker.getVmSize();
		return maxSatisfiable - getSatified();
	}
	
	public double numOfTaskCanRequest() {
		double maxRequestable =(getSatified() + broker.getVmSize()) * getContractRatio(); 
		return maxRequestable - getRequested();
	}

	@Override
	public String toString() {
		return "PartnerInfomation [partnerId=" + partnerId + ", ratio=" + contractRatio
				+ ", requested=" + requested + ", satified=" + satified + "lenghtRatio= " + lenghtRatio + "]";
	}
	
	/**
	 * deviation  =  ti so tong do dai dung dung i goi cho j tren tong do dai j goi cho i 
	 * @param request_lenght
	 * @param satify_lenght
	 * @return
	 */
	public double updateLenghtRatio(double request_lenght,double satify_lenght){
		double deviation;
		deviation = calcLenghtRatio(this.getRequested(),this.getSatified());
		setLenghtRatio(deviation);
		return deviation;
	}
	
	public double updateLenghtRatio(){
		double deviation;
		deviation = calcLenghtRatio(0,0);
		setLenghtRatio(deviation);
		return deviation;
	}
	
	public double updateRequested(double request_lenght){
		setRequested(getRequested()+request_lenght);
		return getRequested();
	}
	
	public double updateSatified(double satify_lenght){
		setSatified(getSatified()+satify_lenght);
		return getSatified();
	}
	public double updateKRatio(){
		setkRatio(getKRatio());
		return getKRatio();
	}
	
	public double calcLenghtRatio(double request_lenght,double satify_lenght){
		double deviation;
		
		if (getSatified() == 0 && getRequested() == 0) {
			deviation = 0;
		} else if (getSatified() == 0) {
			deviation = (double) (getRequested() + request_lenght) / getPartnerVm();
		} else if (getRequested() == 0) {
			deviation = (double) getPartnerVm() / (getSatified() + satify_lenght);
		} else {
			deviation = (double) (getRequested() + request_lenght) / (this.getSatified() + satify_lenght);
		}
		
		return deviation;
	}
	
	/**
	 * K ratio = L/init_ratio
	 * @return
	 */
	public double getKRatio() {
		double k;
		if(this.getContractRatio() == 0 ){
			k = 1;
		} else {
			k = getLenghtRatio()/getContractRatio() - 1;
		}
		return k;
	}
	
	public double getKRatioWithCurrentTask(double request_lenght,double satify_lenght) {
		double k;
		if(this.getContractRatio() == 0 ){
			k = 1;
		} else {
			k = (calcLenghtRatio(request_lenght, satify_lenght))/getContractRatio()-1;
		}
		return k;
	}

	
	/**
	 * Getter & Setter Area
	 * @return
	 */
	public int getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}

	public double getContractRatio() {
		return contractRatio;
	}

	public void setContractRatio(double ratio) {
		this.contractRatio = ratio;
	}

	public double getRequested() {
		return requested;
	}

	public void setRequested(double requested) {
		this.requested = requested;
		updateLenghtRatio();
	}

	public double getSatified() {
		return satified;
	}

	public void setSatified(double satified) {
		this.satified = satified;
		updateLenghtRatio();
	}

	public double getLenghtRatio() {
		return lenghtRatio;
	}

	public void setLenghtRatio(double lenghtRatio) {
		this.lenghtRatio = lenghtRatio;
	}

	/**
	 * @return the kRatio
	 */
	public double getkRatio() {
		return kRatio;
	}

	/**
	 * @param kRatio the kRatio to set
	 */
	public double setkRatio(double kRatio) {
		this.kRatio = kRatio;
		return this.kRatio;
	}

	public int getPartnerVm() {
		return partnerVm;
	}

	public void setPartnerVm(int partnerVm) {
		this.partnerVm = partnerVm;
	}

	public CustomDatacenterBroker getBroker() {
		return broker;
	}

	public void setBroker(CustomDatacenterBroker broker) {
		this.broker = broker;
	}
	
	public boolean isBalance() {
		return (getLenghtRatio() - getContractRatio()) == 0;
	}

}

