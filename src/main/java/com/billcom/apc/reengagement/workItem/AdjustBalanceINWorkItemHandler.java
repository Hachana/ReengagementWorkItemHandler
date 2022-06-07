package com.billcom.apc.reengagement.workItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.AdjustBalanceResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi.Hachana
 *
 */

public class AdjustBalanceINWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(AdjustBalanceINWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		Long oldCoId;
		boolean toSuccess = true;
		this.logger.info("=> AdjustBalanceINWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		logger.info("Reengagement in progress...Get Balances IN");

		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		Map<String, Object> contractMap = new HashMap<>();
		Map<String, Object> migrationMap = new HashMap<>();
		ReengagementService reengagementService = new ReengagementServiceImp();

		try {

			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			Long mainBalanceAmount = Long.parseLong((String) wi.getParameter("mainBalanceAmount"));
			Long custId = Long.parseLong((String) wi.getParameter("custId"));

			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);
			String shdes1 = migrationOutputBean.getResponseMigration().getOffreInitBscs().getShdesOffer();
			String shdes2 = migrationOutputBean.getResponseMigration().getOffreTargetBscs().getShdesOffer();
			String offre1 = shdes1.substring(0, 3);
			String offre2 = shdes2.substring(0, 3);
			String migrationCategory = offre1 + "2" + offre2;
			logger.info("MigrationCategory ::" + migrationCategory);
			if (wi.getParameter("oldCoId") != null)
				oldCoId = Long.parseLong((String) wi.getParameter("oldCoId"));
			else
				oldCoId = null;
			String taskName = reengagementService.getConfig().getPropValues("adjustBalanceINWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			if (mainBalanceAmount == null || mainBalanceAmount == 0) {
				logger.info("Main Balance = 0 ===> No Adjust balance IN");

				this.logger.info("<= AdjustBalanceINWorkItemHandler out: Process Id::  " + wi.getProcessInstanceId());
				toSuccess = true;
			} else {

				if (migrationCategory.equalsIgnoreCase("PRE2POS") || migrationCategory.equalsIgnoreCase("HYB2POS")) {
					AdjustBalanceResponse responseAsjustBalance = reengagementService.adjustBalanceBscs(migrationMap,
							contractMap, custId, mainBalanceAmount, oldCoId);
					logger.info("+ Adjust balance IN : " + responseAsjustBalance.isFinished());
					if (responseAsjustBalance.isFinished().booleanValue()) {

						this.logger.info(
								"<= AdjustBalanceINWorkItemHandler out: Process Id::  " + wi.getProcessInstanceId());
						toSuccess = true;

					} else {
						logger.info("Adjust Balance Error code : " + responseAsjustBalance.getErrorCode());
						logger.info("Adjust Balance Comment : " + responseAsjustBalance.getComment());
						logger.info(" Error Adjust balance IN : Error Calling Reachrge WS ");
						this.logger.info("<= AdjustBalanceINWorkItemHandler with error out: Process Id::  "
								+ wi.getProcessInstanceId());
						toSuccess = false;

					}
				} else if (migrationCategory.equalsIgnoreCase("POS2POS")) {
					this.logger.info("No Adjust Balance Call !!  " + wi.getProcessInstanceId());

					this.logger
							.info("<= AdjustBalanceINWorkItemHandler out: Process Id:  " + wi.getProcessInstanceId());
					toSuccess = true;

				} else {

					AdjustBalanceResponse responseAsjustBalance = reengagementService.adjustBalanceIn(migrationMap,
							contractMap, custId, mainBalanceAmount, oldCoId);
					logger.info("+ Adjust balance IN : " + responseAsjustBalance.isFinished());
					if (responseAsjustBalance.isFinished().booleanValue()) {

						this.logger.info(
								"<= AdjustBalanceINWorkItemHandler out : Process Id::  " + wi.getProcessInstanceId());
						toSuccess = true;

					} else {
						logger.info("Adjust Balance Error code : " + responseAsjustBalance.getErrorCode());
						logger.info("Adjust Balance Comment : " + responseAsjustBalance.getComment());
						logger.info(" Error Adjust balance IN : Error Calling Reachrge WS ");
						this.logger.info("<= AdjustBalanceINWorkItemHandler with error out: Process Id::  "
								+ wi.getProcessInstanceId());
						toSuccess = false;

					}
				}
			}
			if (toSuccess) {
				wim.completeWorkItem(wi.getId(), reengagementService.successAdjustBalanceIN(waitFailure, taskName));

			} else {
				wim.completeWorkItem(wi.getId(), reengagementService.failAdjustBalanceIN(retryNbre, waitFailure));

			}

		} catch (Exception e) {
			this.logger.error("", e);
			this.logger.info(
					"<= AdjustBalanceINWorkItemHandler with error out : Process Id::  " + wi.getProcessInstanceId());

			wim.completeWorkItem(wi.getId(), reengagementService.failAdjustBalanceIN(retryNbre, waitFailure));
		}

	}

}
