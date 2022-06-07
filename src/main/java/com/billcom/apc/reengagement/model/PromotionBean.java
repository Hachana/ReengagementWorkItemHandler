/**
 * 
 */
package com.billcom.apc.reengagement.model;

/**
 * @author arij.dhahbi
 *
 */
public class PromotionBean extends com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionBean
		implements java.io.Serializable {

	private long csId;

	private java.lang.String canalName;

	private com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionBean promotionBean;

	public PromotionBean() {
	}

	public PromotionBean(com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionBean promotionBean,
			long csId, String canalName) {
		this.promotionBean = promotionBean;
		this.csId = csId;
		this.canalName = canalName;
	}

	/**
	 * @return the csId
	 */
	public long getCsId() {
		return csId;
	}

	/**
	 * @param csId the csId to set
	 */
	public void setCsId(long csId) {
		this.csId = csId;
	}

	/**
	 * @return the canalName
	 */
	public java.lang.String getCanalName() {
		return canalName;
	}

	/**
	 * @param canalName the canalName to set
	 */
	public void setCanalName(java.lang.String canalName) {
		this.canalName = canalName;
	}

	/**
	 * @return the promotionBean
	 */
	public com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionBean getPromotionBean() {
		return promotionBean;
	}

	/**
	 * @param promotionBean the promotionBean to set
	 */
	public void setPromotionBean(
			com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionBean promotionBean) {
		this.promotionBean = promotionBean;
	}
}
