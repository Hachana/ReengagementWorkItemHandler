package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetOrcreateCugForcustomerResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class GetCugByCustomerWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(GetCugByCustomerWorkItemHandler.class);
	private List<Map<String, Object>> listCugs = new ArrayList<>();

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetCugByCustomerWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		String taskName = "";
		this.logger.info("Reengagement in progress...Get Cug By Customer");
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			Long custId = Long.parseLong((String) wi.getParameter("custId"));
			taskName = reengagementService.getConfig().getPropValues("GetCugByCustomerWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			this.logger.info("list cugs size =" + listCugs.size());

			// consume ws getOrcreateCugForcustomer
			GetOrcreateCugForcustomerResponse response = reengagementService.getOrcreateCugForcustomer(custId);
			if (response.getIsSuccessful().booleanValue()) {
				for (CUGBean cug : response.getCug()) {
					listCugs.add(new ObjectMapper().convertValue(cug, Map.class));
				}

				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputGetOrcreateCugForcustomer(waitFailure, taskName, listCugs));
			} else {
				this.logger.info("respone=" + response.getIsSuccessful());
				this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
				this.logger.info("comment= " + response.getComment());
				logger.info("<= GetCugByCustomerWorkItemHandler out with error : " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputGetOrcreateCugForcustomer(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			logger.info("<= GetCugByCustomerWorkItemHandler out with error : " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputGetOrcreateCugForcustomer(retryNbre, waitFailure, taskName));
		}

	}

}
