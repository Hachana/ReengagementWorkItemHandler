package com.billcom.apc.reengagement.model;


import java.io.Serializable;

public class ContractBalance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int CO_ID;
	private int TMCODE;
	private String VALUE;
	private String UNIT;
	private String TYPE;
	private int SNCODE_BALANCE;
	private String dateStart;
	private String dateEnd;
	private String dateExpiration;
	private String description;
	private String canal;
	
	public int getCO_ID() {
		return CO_ID;
	}
	public void setCO_ID(int cO_ID) {
		CO_ID = cO_ID;
	}
	public int getTMCODE() {
		return TMCODE;
	}
	public void setTMCODE(int tMCODE) {
		TMCODE = tMCODE;
	}
	public String getVALUE() {
		return VALUE;
	}
	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	public String getUNIT() {
		return UNIT;
	}
	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}
	public String getTYPE() {
		return TYPE;
	}
	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public int getSNCODE_BALANCE() {
		return SNCODE_BALANCE;
	}
	public void setSNCODE_BALANCE(int sNCODE_BALANCE) {
		SNCODE_BALANCE = sNCODE_BALANCE;
	}
	public String getDateStart() {
		return dateStart;
	}
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}
	public String getDateEnd() {
		return dateEnd;
	}
	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}
	public String getDateExpiration() {
		return dateExpiration;
	}
	public void setDateExpiration(String dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}

	

}
