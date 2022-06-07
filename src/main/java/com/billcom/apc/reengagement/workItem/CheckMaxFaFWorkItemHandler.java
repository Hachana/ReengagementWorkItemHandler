package com.billcom.apc.reengagement.workItem;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.LimitAddResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */

public class CheckMaxFaFWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(CheckMaxFaFWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> CheckMaxFaFWorkItemHandler in:  Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		String taskName;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		Integer maxAdded;
		Boolean isSameTmcode;
		reengagementService.printWorkItem(this.logger, wi);
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			isSameTmcode = (Boolean) wi.getParameter("isSameTmcode");
			taskName = reengagementService.getConfig().getPropValues("CheckMaxFaFWIH");
			logger.info("taskName::" + taskName);
			this.logger.info("Reengagement in progress...Check Max FaF");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws checkMaxFaF
			LimitAddResponse response = reengagementService.checkMaxFaF((String) contractMap.get("msisdn"));
			if (response.getIsSuccessful().booleanValue()) {
				maxAdded = response.getMaxAdded();
				this.logger.info("maxAdded :: " + maxAdded);
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputCheckMaxFaF(waitFailure, taskName, maxAdded, isSameTmcode));
			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				this.logger.info(
						"<= CheckMaxFaFWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputCheckMaxFaF(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger
					.info("<= CheckMaxFaFWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputCheckMaxFaF(retryNbre, waitFailure, taskName));
		}

	}

}
