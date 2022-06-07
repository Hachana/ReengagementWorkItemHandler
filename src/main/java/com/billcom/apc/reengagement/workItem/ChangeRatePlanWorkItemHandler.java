package com.billcom.apc.reengagement.workItem;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ChangeRatePlanResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * @author Fethi.Hachana
 *
 */

public class ChangeRatePlanWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(ChangeRatePlanWorkItemHandler.class);

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> ChangeRatePlanWorkItemHandler in:  Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		Map<String, Object> migrationMap = new HashMap<>();

		String taskName = "";
		this.logger.info("Reengagement in progress...Change RatePlan");
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			taskName = reengagementService.getConfig().getPropValues("ChangerateplanWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}
			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);

			// consume ws changeRatePlanReengagement
			ChangeRatePlanResponse response = reengagementService.changeRatePlanReengagement(
					(Long) contractMap.get("coId"),
					migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());
			this.logger.info("response : " + response.getIsSuccessful());
			if (response.getIsSuccessful().booleanValue()) {
				this.logger.info("Rate Plan changed");
				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputChangeRatePlan(waitFailure, taskName));
			} else {
				this.logger.info("Rate Plan failed to change");
				this.logger.info("response error : " + response.getBscsErrorCode() + " " + response.getComment());
				this.logger.info(
						"<= ChangeRatePlanWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputChangeRatePlan(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.info(
					"<= ChangeRatePlanWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputChangeRatePlan(retryNbre, waitFailure, taskName));
		}
	}

}
