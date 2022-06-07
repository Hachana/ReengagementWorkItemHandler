package com.billcom.apc.reengagement.workItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.GetBalanceAndDateBeanOutputV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.OptionV2;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi.Hachana
 *
 */

public class GetBalancesINWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(GetBalancesINWorkItemHandler.class);

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetBalancesINWorkItemHandler in:  Process Id::  " + wi.getProcessInstanceId());

		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		String taskName = "";
		this.logger.info("Reengagement in progress...Get Balances IN");
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		boolean insertBalance;
		Long mainBalanceAmount = null;
		Map<String, Object> resultsMap = new HashMap<>();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			taskName = reengagementService.getConfig().getPropValues("getBalanceINWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			GetBalanceAndDateBeanOutputV2 response = reengagementService.getBalanceAndDate(contractMap);
			if (response.getOperationResponse().isIsSuccessful().booleanValue()) {
				if (null != response.getAmountMainBalance() && response.getAmountMainBalance() >= 0) { // was >0 only
					mainBalanceAmount = response.getAmountMainBalance();
					logger.info("Main Balance value = " + mainBalanceAmount);
				}
				if (null != response.getOptions() && !response.getOptions().getItem().isEmpty()) {
					insertBalance = true;
					List<OptionV2> listOptions = response.getOptions().getItem();
					logger.info("<= GetBalancesINWorkItemHandler out: " + wi.getProcessInstanceId());
					resultsMap = reengagementService.successOutputGetBalancesIN(waitFailure, taskName, insertBalance,
							listOptions, mainBalanceAmount + "");
					wim.completeWorkItem(wi.getId(), resultsMap);
				} else {
					insertBalance = false;

					logger.info(" No IN balances to get ");
					logger.info("<= GetBalancesINWorkItemHandler out: " + wi.getProcessInstanceId());

					resultsMap = reengagementService.successOutputGetBalancesIN(waitFailure, taskName, insertBalance,
							null, mainBalanceAmount + "");
					wim.completeWorkItem(wi.getId(), resultsMap);
				}
			} else {
				logger.info("wsdl call getBalanceAndDate is  " + response.getOperationResponse().isIsSuccessful());
				logger.info("ErrorCode getBalanceAndDate is  " + response.getOperationResponse().getErrorCode());
				logger.info("Comment getBalanceAndDate is  " + response.getOperationResponse().getComment());
				logger.info("<= GetBalancesINWorkItemHandler out with error : " + wi.getProcessInstanceId());

				resultsMap = reengagementService.failOutputGetBalancesIN(retryNbre, waitFailure, taskName);
				wim.completeWorkItem(wi.getId(), resultsMap);
			}

		} catch (Exception e) {
			// ko .
			logger.info("<= GetBalancesINWorkItemHandler out with error : " + wi.getProcessInstanceId());

			logger.error("", e);
			resultsMap = reengagementService.failOutputGetBalancesIN(retryNbre, waitFailure, taskName);
			wim.completeWorkItem(wi.getId(), resultsMap);
		}

	}

}
