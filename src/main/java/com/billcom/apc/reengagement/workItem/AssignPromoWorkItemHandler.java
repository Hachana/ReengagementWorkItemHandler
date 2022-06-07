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
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class AssignPromoWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(AssignPromoWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> AssignPromoWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();
		List<Map<String, Object>> listRbfMap;
		String taskName;
		String waitFailurePromotion;
		Integer retryNbrePromotion;
		AutoRecycle autoRecycle;
		Integer scCode;
		logger.info("Reengagement in progress... Assign Promo");
		waitFailurePromotion = "0s";
		retryNbrePromotion = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbrePromotion = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbrePromotion"));

			taskName = reengagementService.getConfig().getPropValues("assignPromoWIH");
			scCode = (Integer) wi.getParameter("scCode");
			logger.info("taskName::" + taskName);
			listRbfMap = (List<Map<String, Object>>) wi.getParameter("listRbfMap");
			String dateMig = (String) wi.getParameter("dueDate");
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date date = format.parse(dateMig);

			Calendar dateAssign = Calendar.getInstance();
			dateAssign.setTime(date);
			dateAssign.add(Calendar.MONTH, 1);
			dateAssign.set(Calendar.DAY_OF_MONTH, 01);
			logger.info("date to assign Promotion  = " + dateAssign.getTime());

			// waitfailure
			waitFailurePromotion = autoRecycle.palierwaittime(waitFailurePromotion, retryNbrePromotion);
			this.logger.debug("waitFailure from config = " + waitFailurePromotion);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws AssignPromo

			if (CollectionUtils.isNotEmpty(listRbfMap)) {

				for (Map<String, Object> map : listRbfMap) {
					InjectRbfBean injectRbfBean = new ObjectMapper().convertValue(map, InjectRbfBean.class);

					AssignPromoRequest assignPromoRequest = new AssignPromoRequest(injectRbfBean.getAssignMode(),
							injectRbfBean.getCanalName(), Long.parseLong(injectRbfBean.getCsId()),
							Long.parseLong(injectRbfBean.getPackId()), Integer.valueOf(injectRbfBean.getPeriod()),
							scCode.longValue(), injectRbfBean.getUserName(), dateAssign);
					this.logger.info("request=" + new ObjectMapper().convertValue(assignPromoRequest, Map.class));
					AssignPromoResponse response = reengagementService.assignPromo(assignPromoRequest);

					logger.info("response Assign promotion=" + response.getIsSuccessful());
					if (response.getIsSuccessful().booleanValue()) {
						wim.completeWorkItem(wi.getId(),
								reengagementService.sucessOutputAssignPromo(waitFailurePromotion, taskName));

						this.logger.info("assign promotion with success");
					} else {
						this.logger.info("respone=" + response.getIsSuccessful());
						this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
						this.logger.info("comment= " + response.getComment());
						this.logger.info("<= AssignPromoWorkItemHandler with error out: Process Id::  "
								+ wi.getProcessInstanceId());

						wim.completeWorkItem(wi.getId(), reengagementService.failOutputAssignPromo(retryNbrePromotion,
								waitFailurePromotion, taskName));
					}
				}

			}

			else {
				this.logger.info("List RBF is empty");
			}

		} catch (Exception e) {
			this.logger
					.info("<= AssignPromoWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputAssignPromo(retryNbrePromotion, waitFailurePromotion, taskName));
		}

	}

}