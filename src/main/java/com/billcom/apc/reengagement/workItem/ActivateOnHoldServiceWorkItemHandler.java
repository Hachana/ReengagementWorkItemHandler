package com.billcom.apc.reengagement.workItem;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */

public class ActivateOnHoldServiceWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(ActivateOnHoldServiceWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * Empty code
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		String taskName = "";
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		logger.info("=> ActivateOnHoldServiceWorkItemHandler in:  Process Id::  " + wi.getProcessInstanceId());

		logger.info("Reengagement in progress...Activate On Hold Service");
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			taskName = reengagementService.getConfig().getPropValues("ActivateOnHoldServiceWIH");
			logger.info("taskName::" + taskName);
			// retryNbre

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws activateOnHoldService
			OperationResponse response = reengagementService.activateOnHoldService((Long) contractMap.get("coId"));
			logger.info("Activate On Hold Service : " + response.getIsSuccessful());
			if (response.getIsSuccessful().booleanValue()) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputActivateOnHoldService(waitFailure, taskName));
			} else {
				logger.info("Activate On Hold Service BscsErrorCode : " + response.getBscsErrorCode());
				logger.info("Activate On Hold Service Comment : " + response.getComment());
				logger.info("<= ActivateOnHoldServiceWorkItemHandler out with error :  Process Id::  "
						+ wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputActivateOnHoldService(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			logger.info("<= ActivateOnHoldServiceWorkItemHandler out with error :  Process Id::  "
					+ wi.getProcessInstanceId() + e);

			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputActivateOnHoldService(retryNbre, waitFailure, taskName));
		}
	}

}
