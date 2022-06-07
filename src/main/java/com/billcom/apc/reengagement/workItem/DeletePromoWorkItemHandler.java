package com.billcom.apc.reengagement.workItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.InjectRbfBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.DeletePromoResponse;
import com.billcom.apc.reengagement.model.PromotionBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class DeletePromoWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(DeletePromoWorkItemHandler.class);
	List<Map<String, Object>> listPromotions;
	private String taskName;

	private boolean toSuccess = false;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty code
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> DeletePromoWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());

		logger.info("Reengagement in progress... Delete Promo");
		ReengagementService reengagementService = new ReengagementServiceImp();
		String waitFailurePromotion;
		Integer retryNbrePromotion;
		AutoRecycle autoRecycle;
		List<Map<String, Object>> listRbfMap;

		waitFailurePromotion = "0s";
		retryNbrePromotion = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbrePromotion = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbrePromotion"));

			taskName = reengagementService.getConfig().getPropValues("deletePromoWIH");
			logger.info("taskName::" + taskName);
			listRbfMap = (List<Map<String, Object>>) wi.getParameter("listRbfMap");
			listPromotions = (List<Map<String, Object>>) wi.getParameter("listPromotions");
			String dateMig = (String) wi.getParameter("dueDate");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date date = format.parse(dateMig);

			Calendar dateDelete = Calendar.getInstance();
			dateDelete.setTime(date);
			logger.info("date to delete Promotion  = " + dateDelete.getTime());

			// waitfailure
			waitFailurePromotion = autoRecycle.palierwaittime(waitFailurePromotion, retryNbrePromotion);
			this.logger.debug("waitFailure from config = " + waitFailurePromotion);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws deletePromo
			if (CollectionUtils.isNotEmpty(listRbfMap)) {
				for (Map<String, Object> mapRBF : listRbfMap) {
					InjectRbfBean injectRbfBean = new ObjectMapper().convertValue(mapRBF, InjectRbfBean.class);
					if (CollectionUtils.isNotEmpty(listPromotions)) {
						for (Map<String, Object> map : listPromotions) {
							PromotionBean promotionBean = new ObjectMapper().convertValue(map, PromotionBean.class);
							DeletePromoResponse response = reengagementService.deletePromo(
									promotionBean.getPromotionBean().getAssignSeqNo(), promotionBean.getCanalName(),
									promotionBean.getCsId(), dateDelete, promotionBean.getPromotionBean().getPackId(),
									injectRbfBean.getUserName()); // a
																	// verifier
							this.logger.info("delete username =" + injectRbfBean.getUserName());
							this.logger
									.info("delete AssignSeqNo =" + promotionBean.getPromotionBean().getAssignSeqNo());

							if (response.getIsSuccessful().booleanValue()) {
								toSuccess = true;
								this.logger.info("delete promotion with success");

							} else {
								toSuccess = false;
								this.logger.info("respone=" + response.getIsSuccessful());
								this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
								this.logger.info("comment= " + response.getComment());

							}
						}

					} else {
						this.logger.info("List promotions is empty");
					}
				}
			}
			if (toSuccess) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputDeletePromo(waitFailurePromotion, taskName));
			} else {
				this.logger.info(
						"<= DeletePromoWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputDeletePromo(retryNbrePromotion, waitFailurePromotion, taskName));
			}

		} catch (Exception e) {
			this.logger
					.info("<= DeletePromoWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputDeletePromo(retryNbrePromotion, waitFailurePromotion, taskName));
		}

	}

}