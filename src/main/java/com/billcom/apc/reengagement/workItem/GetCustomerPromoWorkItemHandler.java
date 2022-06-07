package com.billcom.apc.reengagement.workItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.InjectRbfBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.GetCustomerPromoResponse;
import com.billcom.apc.reengagement.model.PromotionBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class GetCustomerPromoWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(GetCustomerPromoWorkItemHandler.class);
	private List<Map<String, Object>> listPromotions;
	private String retriesConfigNbre = null;
	private boolean toSuccess = false;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
/*
 * empty bloc
 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetCustomerPromoWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		 ReengagementService reengagementService = new ReengagementServiceImp();

		List<Map<String, Object>> listRbfMap = new ArrayList<>();
		List<InjectRbfBean> listRbfBeans = new ArrayList<>();

		String taskName = "";
		this.logger.info("Reengagement in progress...Get Customer Promo");
		String waitFailurePromotion = "0s";
		Integer retryNbrePromotion = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		
		try {
			// retryNbre
			retryNbrePromotion = reengagementService
					.getRetryNumber((Integer) wi.getParameter("retryNbrePromotion"));

			listRbfMap = (List<Map<String, Object>>) wi.getParameter("listRbfMap");
			
			this.logger.info("listRbfMap=" + listRbfMap);
			
			String dateMig = (String) wi.getParameter("dueDate");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date date = format.parse(dateMig);

			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(date);
			for (Map<String, Object> map : listRbfMap) {
				listRbfBeans.add(new ObjectMapper().convertValue(map, InjectRbfBean.class));
			}

			taskName = reengagementService.getConfig().getPropValues("getCustomerPromoWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailurePromotion = autoRecycle.palierwaittime(waitFailurePromotion, retryNbrePromotion);
			this.logger.debug("waitFailure from config = " + waitFailurePromotion);

			retriesConfigNbre = reengagementService.getConfig().getPropValues("retriesConfigNbre").trim();

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws getCustomerPromo

			Iterator<InjectRbfBean> iterator = listRbfBeans.iterator();
			while (iterator.hasNext()) {
				InjectRbfBean injectRbfBean = iterator.next();
				String csId = injectRbfBean.getCsId();
				this.logger.info("custId==" + csId);
				GetCustomerPromoResponse response = reengagementService.getCustomerPromo(Long.parseLong(csId));

				if (response.getIsSuccessful().booleanValue()) {
					toSuccess = true;
					if (response.getPromotions().length != 0) {
						listPromotions = new ArrayList<>();
						for (int i = 0; i < response.getPromotions().length; i++) {
							if( (response.getPromotions()[i].getDeleteDate() != null && response.getPromotions()[i].getDeleteDate().compareTo(dueDate)>0)
									|| (response.getPromotions()[i].getDeleteDate()==null)) {
								
							
							listPromotions
									.add(new ObjectMapper().convertValue(new PromotionBean(response.getPromotions()[i],
											Long.parseLong(csId), injectRbfBean.getCanalName()), Map.class));
							}
						}
						this.logger.info("listPromotions " + listPromotions);
					}

				} else {
					this.logger.info("response=" + response.getIsSuccessful());
					this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
					this.logger.info("comment= " + response.getComment());
				}

			}

			if (toSuccess) {
				wim.completeWorkItem(wi.getId(),reengagementService.sucessOutputGetCustomerPromo(

						waitFailurePromotion, taskName, listPromotions, retriesConfigNbre));
			} else {
				logger.info("<= GetCustomerPromoWorkItemHandler out with error: " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(), reengagementService.failOutputGetCustomerPromo(
						retryNbrePromotion, waitFailurePromotion, taskName, retriesConfigNbre));

			}

		} catch (Exception e) {
			logger.info("<= GetCustomerPromoWorkItemHandler out with error: " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failOutputGetCustomerPromo(
					retryNbrePromotion, waitFailurePromotion, taskName, retriesConfigNbre));
		}
	}

}
