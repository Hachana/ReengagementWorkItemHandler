package com.billcom.apc.reengagement.workItem;

import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * @author Fethi Hachana
 *
 */
public class ActivateServiceRessourceWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(ActivateServiceRessourceWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * bloc empty
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		logger.info("=> ActivateServiceRessourceWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap;

		List<Map<String, Object>> listServiceRessource;

		String taskName = "";

		String waitFailure;

		Integer retryNbre;

		AutoRecycle autoRecycle;
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		try {
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("ActivateServiceRessourceWIH");
			logger.info("taskName::" + taskName);

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			listServiceRessource = (List<Map<String, Object>>) wi.getParameter("listServiceRessource");

			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			logger.debug("waitFailure from config = " + waitFailure);
			for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>) wi.getParameters().entrySet())
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			Long profileId = Long.valueOf(0L);
			boolean response = reengagementService.contractServicesAdd(contractMap, profileId, listServiceRessource);
			if (response) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputContractServicesAdd(waitFailure, taskName));
			} else {
				logger.error("<= ActivateServiceRessourceWorkItemHandler with error  out: Process Id::  "
						+ wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputContractServicesAdd(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			logger.error("<= ActivateServiceRessourceWorkItemHandler with error  out: Process Id::  "
					+ wi.getProcessInstanceId() + e);

			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputContractServicesAdd(retryNbre, waitFailure, taskName));
		}
	}
}
