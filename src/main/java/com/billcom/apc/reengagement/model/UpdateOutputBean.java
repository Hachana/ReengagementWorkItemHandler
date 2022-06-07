/**
 * 
 */
package com.billcom.apc.reengagement.model;

import java.io.Serializable;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.UpdateOption;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;

/**
 * @author arij.dhahbi
 *
 */
public class UpdateOutputBean implements Serializable{
	
	private UpdateOption inputUpdateOption;
	
	private GetBscsOfferAndServciesFromCrmDemandeResponse responseUpdateOption;
	
	
	public UpdateOutputBean() {
	
	}
	/**
	 * @param inputUpdateOption
	 * @param responseUpdateOption
	 */
	public UpdateOutputBean(UpdateOption inputUpdateOption,
			GetBscsOfferAndServciesFromCrmDemandeResponse responseUpdateOption) {
		this.inputUpdateOption = inputUpdateOption;
		this.responseUpdateOption = responseUpdateOption;
	}

	/**
	 * @return the inputUpdateOption
	 */
	public UpdateOption getInputUpdateOption() {
		return inputUpdateOption;
	}

	/**
	 * @param inputUpdateOption the inputUpdateOption to set
	 */
	public void setInputUpdateOption(UpdateOption inputUpdateOption) {
		this.inputUpdateOption = inputUpdateOption;
	}

	/**
	 * @return the responseUpdateOption
	 */
	public GetBscsOfferAndServciesFromCrmDemandeResponse getResponseUpdateOption() {
		return responseUpdateOption;
	}

	/**
	 * @param responseUpdateOption the responseUpdateOption to set
	 */
	public void setResponseUpdateOption(GetBscsOfferAndServciesFromCrmDemandeResponse responseUpdateOption) {
		this.responseUpdateOption = responseUpdateOption;
	}


	

}
