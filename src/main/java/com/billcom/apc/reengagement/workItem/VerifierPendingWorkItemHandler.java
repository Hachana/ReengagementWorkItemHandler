package com.billcom.apc.reengagement.workItem;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.HasPendingRequestResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.apc.reengagement.utils.ReengagementConfigHandler;
import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */

public class VerifierPendingWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(VerifierPendingWorkItemHandler.class);
	private String taskName;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		logger.info("=> VerifierPendingWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());

		logger.info("Reengagement in progress...Verify Pending");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("VerifypendingWIH");
			logger.info("taskName::" + taskName);
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws verifyPending

			HasPendingRequestResponse response = reengagementService.verifyPending((Long) contractMap.get("coId"));

			if (response.getIsSuccessful().booleanValue()) {

				this.logger.info("has pending : " + response.isPended());
				if (response.isPended())
					wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputVerifyPendingTrue(retryNbre,
							waitFailure, response.isPended(), taskName));
				else
					wim.completeWorkItem(wi.getId(),
							reengagementService.sucessOutputVerifyPending(waitFailure, response.isPended(), taskName));

			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputVerifyPending(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.error("", e);
			this.logger.error("<= VerifierPendingWorkItemHandler with error ");

			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputVerifyPending(retryNbre, waitFailure, taskName));
		}

	}

}
