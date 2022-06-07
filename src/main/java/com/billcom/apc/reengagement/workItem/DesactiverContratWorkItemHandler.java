package com.billcom.apc.reengagement.workItem;

import java.util.Map;

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

public class DesactiverContratWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(DesactiverContratWorkItemHandler.class);
	private String taskName;

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		logger.info("=> DesactiverContratWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());

		logger.info("Reengagement in progress...Deactivate Contract");
		ReengagementService reengagementService = new ReengagementServiceImp();
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		Map<String, Object> contractMap;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			taskName = reengagementService.getConfig().getPropValues("DesactivateContractWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// consume ws activateOnHoldService
			OperationResponse response = reengagementService.deactivateContract((Long) contractMap.get("coId"));
			this.logger.info("desactiver Contrat : " + response.getIsSuccessful());
			if (response.getIsSuccessful().booleanValue()) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputDeactivateContract(waitFailure, taskName));
			} else {
				this.logger.info("desactiver Contrat BscsErrorCode : " + response.getBscsErrorCode());
				this.logger.info("desactiver Contrat Comment : " + response.getComment());
				this.logger.info("<= DesactiverContratWorkItemHandler with error out: Process Id::  "
						+ wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputDeactivateContract(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.info(
					"<= DesactiverContratWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputDeactivateContract(retryNbre, waitFailure, taskName));
		}

	}

}
