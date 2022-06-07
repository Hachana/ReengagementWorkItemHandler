/**
 * 
 */
package com.billcom.apc.reengagement.workItem;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */
public class PrepareMigrationWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(PrepareMigrationWorkItemHandler.class);

	private String taskName;
	private String waitFailure;
	private Integer retryNbre;

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
/*
 * empty bloc
 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> PrepareMigrationWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		 ReengagementService reengagementService = new ReengagementServiceImp();
			 AutoRecycle autoRecycle;
				 Map<String, Object> migrationMap;
				 List<Map<String, Object>> listContractOfMigration;
				 Integer sizeListContractOfMigration;



		try {
			taskName =reengagementService.getConfig().getPropValues("filterDataWIH");
			logger.info("taskName::" + taskName);
			logger.info("Reengagement in progress... " + taskName);
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			waitFailure = "0s";
			retryNbre = null;
			autoRecycle = new AutoRecycle();
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			Map<String, Object> responseMigration = (Map<String, Object>) migrationMap.get("responseMigration");
			listContractOfMigration = (List<Map<String, Object>>) responseMigration.get("contractDetails");

			if (!CollectionUtils.isEmpty(listContractOfMigration)) {
				sizeListContractOfMigration = listContractOfMigration.size();
				wim.completeWorkItem(wi.getId(), reengagementService.successPrepareMigration(waitFailure, taskName,
						sizeListContractOfMigration, listContractOfMigration));
			} else {
				// consume ws getBscsOfferAndServciesFromCrmDemande
				this.logger.info("ERROR");
				wim.completeWorkItem(wi.getId(),
						reengagementService.failPrepareMigration(retryNbre, waitFailure, taskName));
			}

		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failPrepareMigration(retryNbre, waitFailure, taskName));
		}
	}



}
