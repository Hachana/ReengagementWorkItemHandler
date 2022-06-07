/**
 * 
 */
package com.billcom.apc.reengagement.model;

import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.OffreBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBean;

/**
 * @author arij.dhahbi
 *
 */
	

public class ServiceToactivateContratResponse implements java.io.Serializable {
	
		private Long coId;
		
	    private OffreBean offreInitBscs;

	    private OffreBean offreTargetBscs;

	    private ServiceBean[] servicesForContract;

	    public ServiceToactivateContratResponse() {
	    }
	    
		



		/**
		 * @param coId
		 * @param offreInitBscs
		 * @param offreTargetBscs
		 * @param servicesForContract
		 */
		public ServiceToactivateContratResponse(Long coId, OffreBean offreInitBscs, OffreBean offreTargetBscs,
				ServiceBean[] servicesForContract) {
			super();
			this.coId = coId;
			this.offreInitBscs = offreInitBscs;
			this.offreTargetBscs = offreTargetBscs;
			this.servicesForContract = servicesForContract;
		}







		/**
		 * @return the coId
		 */
		public Long getCoId() {
			return coId;
		}







		/**
		 * @param coId the coId to set
		 */
		public void setCoId(Long coId) {
			this.coId = coId;
		}







		/**
		 * @return the offreInitBscs
		 */
		public OffreBean getOffreInitBscs() {
			return offreInitBscs;
		}

		/**
		 * @param offreInitBscs the offreInitBscs to set
		 */
		public void setOffreInitBscs(OffreBean offreInitBscs) {
			this.offreInitBscs = offreInitBscs;
		}

		/**
		 * @return the offreTargetBscs
		 */
		public OffreBean getOffreTargetBscs() {
			return offreTargetBscs;
		}

		/**
		 * @param offreTargetBscs the offreTargetBscs to set
		 */
		public void setOffreTargetBscs(OffreBean offreTargetBscs) {
			this.offreTargetBscs = offreTargetBscs;
		}

		/**
		 * @return the servicesForContract
		 */
		public ServiceBean[] getServicesForContract() {
			return servicesForContract;
		}

		/**
		 * @param servicesForContract the servicesForContract to set
		 */
		public void setServicesForContract(ServiceBean[] servicesForContract) {
			this.servicesForContract = servicesForContract;
		}

	    
	}
