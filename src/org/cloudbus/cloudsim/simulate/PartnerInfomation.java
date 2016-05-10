package org.cloudbus.cloudsim.simulate;


public class PartnerInfomation {

	private int partnerId;
	
	private double contractRatio = -1000000;
	
	private double sentApps;
	
	private double receivedApps;
	
	private double partnerVm;
	
	private CustomDatacenterBroker broker;
	
	/**
	 * L in algorithm
	 */
    //	sendRecvRatio is an derivation value. Get it "ON THE GO" instead of storing

	public PartnerInfomation(int partnerId) {
		super();
		this.partnerId = partnerId;
		this.contractRatio = 1;
		this.sentApps = 0;
		this.receivedApps = 0;
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
		this.broker = broker;
		this.setPartnerVm(partnerVm);
	}
	
	public double numOfTaskCanSatisfy() {
		double maxSatisfiable = getRequested()/getContractRatio();;
		return maxSatisfiable - getSatified();
	}
	
	public double numOfTaskCanRequest() {
		double maxRequestable = getSatified()*getContractRatio(); 
		return maxRequestable - getRequested();
	}

	@Override
	public String toString() {
		return "PartnerInfomation [partnerId=" + partnerId + ", contractRatio=" + contractRatio
				+ ", sentApps=" + sentApps + ", receivedApps=" + receivedApps + ", kRatio= " + this.getKRatio() + "]";
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
	
	public double calcLenghtRatio(double request_lenght,double satify_lenght){
		double sendRecvRatio;
		
		if (getSatified() == 0 && getRequested() == 0) {
			sendRecvRatio = 1;
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
		if(this.getContractRatio() == -1000000 ){
			k = 0;
		} else {
			k = getLenghtRatio()/getContractRatio() - 1;
		}
		return k;
	}
	
	public double getKRatioWithCurrentTask(double request_lenght,double satify_lenght) {
		double k;
		if(this.getContractRatio() == -1000000){
			k = 0;
		} else {
			k = (calcLenghtRatio(getRequested()+request_lenght, getSatified()+satify_lenght))/getContractRatio()-1;
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

