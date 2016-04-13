package org.cloudbus.cloudsim.simulate;


public class PartnerInfomation {

	private int partnerId;
	
	private double contractRatio;
	
	private double sentApps;
	
	private double receivedApps;
	
	private double partnerVm;
	private double ownVm;
	
	private CustomDatacenterBroker broker;
	
	/**
	 * L in argithorm
	 */
    //	sendRecvRatio is an derivation value. Get it "ON THE GO" instead of storing

	/**
	 * l in argithorm
	 */
	private double kRatio;

	
	public PartnerInfomation(int partnerId, double ratio, double sentApps,
			double receivedApps,double sendRecvRatio,  double kRatio) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = ratio;
		this.sentApps = 0;
		this.receivedApps = 0;
		this.kRatio = kRatio;
	}

	public PartnerInfomation(int partnerId) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = 1;
		this.sentApps = 0;
		this.receivedApps = 0;
		this.kRatio = 0;
	}
	
	public PartnerInfomation(int partnerId, double ratio, int partnerVm, CustomDatacenterBroker broker) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = ratio;
		if (ratio == 1) {
			this.sentApps = 100;
			this.receivedApps = 100;
		} else {
			this.sentApps = partnerVm;
			this.receivedApps = broker.getVmSize();
		}
		this.kRatio = 0;
		this.broker = broker;
		this.setPartnerVm(partnerVm);
	}
	
	public double numOfTaskCanSatisfy() {
//OLD		double maxSatisfiable = (getRequested() + broker.getVmSize()) / getRatio();
//		double maxSatisfiable = getRequested() / getContractRatio() + broker.getVmSize();
//		return maxSatisfiable - getSatified();
		double ret = 0.5*partnerVm + getRequested()/getContractRatio();;
		return ret - getSatified();
	}
	
	public double numOfTaskCanRequest() {
//		double maxRequestable =(getSatified() + broker.getVmSize()) * getContractRatio(); 

//		return maxRequestable - getRequested();
		double ret = 2*partnerVm + getSatified()*getContractRatio(); 
		return ret - getRequested();
	}

	@Override
	public String toString() {
		return "PartnerInfomation [partnerId=" + partnerId + ", ratio=" + contractRatio
				+ ", sentApps=" + sentApps + ", receivedApps=" + receivedApps + "sendRecvRatio= " + this.calcLenghtRatio() + "]";
	}
	
	/**
	 * sendRecvRatio  =  ti so tong do dai dung dung i goi cho j tren tong do dai j goi cho i 
	 * @param request_lenght
	 * @param satify_lenght
	 * @return
	 */
	public double updateLenghtRatio(double request_lenght,double satify_lenght){
		updateRequested(request_lenght);
		updateSatified(satify_lenght);

		return calcLenghtRatio(this.getRequested(),this.getSatified());
	}
	
	public double updateLenghtRatio(){
		return calcLenghtRatio();
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
		double sendRecvRatio;
		
		if (getSatified() == 0 && getRequested() == 0) {
			sendRecvRatio = 0;
		} else if (getSatified() == 0 && satify_lenght == 0) {
			sendRecvRatio =  1000000000; // huge value
		} else {
			sendRecvRatio = (double) (getRequested() + request_lenght) / (this.getSatified() + satify_lenght);
		}
		
		return sendRecvRatio;
	}

    public double calcLenghtRatio(){
    	return calcLenghtRatio(0,0);
    }
	
	/**
	 * K ratio = L/init_ratio
	 * @return
	 */
	public double getKRatio() {
		double k;
		if(this.getContractRatio() > -0.0001 && this.getContractRatio() < 0.0001 ){
			k = 1000000000;
		} else {
			k = getLenghtRatio()/getContractRatio() - 1;
		}
		return k;
	}
	
	public double getKRatioWithCurrentTask(double request_lenght,double satify_lenght) {
		double k;
		if(this.getContractRatio() > -0.0001 && this.getContractRatio() < 0.0001 ){
			k = 1000000000;
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

	public void setContractRatio(double partnerVm, double ownVm) {
		this.partnerVm = partnerVm;
		this.ownVm = ownVm;
		this.contractRatio = partnerVm/ownVm;
	}

	public double getRequested() {
		return sentApps;
	}

	public void setRequested(double sentApps) {
		this.sentApps = sentApps;
	}

	public double getSatified() {
		return receivedApps;
	}

	public void setSatified(double receivedApps) {
		this.receivedApps = receivedApps;
	}

	public double getLenghtRatio() {
		return this.calcLenghtRatio();
	}

//	public void setLenghtRatio(double sendRecvRatio) {
//		this.sendRecvRatio = sendRecvRatio;
//	}

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
		return (int) partnerVm;
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
		double delta = (double) getLenghtRatio() - getContractRatio();
		/* To avoid decimal, we replace number equal 0 by epsilon range compare*/
		return delta > -0.0001 && delta < 0.001;
	}

}

