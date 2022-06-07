package com.billcom.apc.reengagement.workItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.GetCategoryResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class GetMigrationGroupWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(GetMigrationGroupWorkItemHandler.class);

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetMigrationGroupWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> migrationMap = new HashMap<>();
		String taskName = "";
		this.logger.info("Reengagement in progress...Get Migration Group");
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		Boolean isSameGroup = false;
		Boolean isSameTmcode = false;
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			taskName = reengagementService.getConfig().getPropValues("GetMigrationGroupWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			MigrationOutputBean migrationBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);
			if (migrationBean.getResponseMigration().getOffreInitBscs().getTmcode()
					.equals(migrationBean.getResponseMigration().getOffreTargetBscs().getTmcode()))
				isSameTmcode = true;
			// consume ws getMigrationGroup
			GetCategoryResponse response = reengagementService
					.getMigrationGroup(new ObjectMapper().convertValue(migrationMap, MigrationOutputBean.class));

			this.logger.info("response : " + response.getIsSuccessful());

			if (response.getIsSuccessful().booleanValue()) {
				this.logger.info("Migration group =" + response.getCategory());
				if (response.getCategory().equals("Intra")) {
					this.logger.info("=> Intra ");

					isSameGroup = true;
				}

				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputGetMigrationGroup(waitFailure,
						isSameGroup, taskName, isSameTmcode));
			} else {
				this.logger.info("respone=" + response.getIsSuccessful());
				this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
				this.logger.info("comment= " + response.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputGetMigrationGroup(isSameGroup, retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputGetMigrationGroup(isSameGroup, retryNbre, waitFailure, taskName));
		}
	}

}
