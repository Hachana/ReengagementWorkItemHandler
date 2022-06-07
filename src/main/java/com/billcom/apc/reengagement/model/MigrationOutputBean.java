/**
 * 
 */
package com.billcom.apc.reengagement.model;

import java.io.Serializable;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.MigrationBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;

/**
 * @author arij.dhahbi
 *
 */
public class MigrationOutputBean implements Serializable{
	
	private MigrationBean inputMigration;
	
	private GetBscsOfferAndServciesFromCrmDemandeResponse responseMigration;
	
	public MigrationOutputBean() {}

	/**
	 * @param inputMigration
	 * @param responseMigration
	 */
	public MigrationOutputBean(MigrationBean inputMigration,
			GetBscsOfferAndServciesFromCrmDemandeResponse responseMigration) {
		this.inputMigration = inputMigration;
		this.responseMigration = responseMigration;
	}

	/**
	 * @return the inputMigration
	 */
	public MigrationBean getInputMigration() {
		return inputMigration;
	}

	/**
	 * @param inputMigration the inputMigration to set
	 */
	public void setInputMigration(MigrationBean inputMigration) {
		this.inputMigration = inputMigration;
	}

	/**
	 * @return the responseMigration
	 */
	public GetBscsOfferAndServciesFromCrmDemandeResponse getResponseMigration() {
		return responseMigration;
	}

	/**
	 * @param responseMigration the responseMigration to set
	 */
	public void setResponseMigration(GetBscsOfferAndServciesFromCrmDemandeResponse responseMigration) {
		this.responseMigration = responseMigration;
	}

	
	

}
