package com.billcom.apc.reengagement.workItem;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementBean;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.UpdateReengagementOrderResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.model.UpdateOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class UpdateReengagementCommandeWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(UpdateReengagementCommandeWorkItemHandler.class);

	private String taskName;
	private boolean orderUpdated = false;

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		logger.info("=> UpdateOrderWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		logger.info("Reengagement in progress... Update Order");
		Map<String, Object> contractMap;

		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		Integer contractOrderId;
		Integer orderId;
		String exist;
		String start;
		String end;
		String oldCoId;
		String oldCoIdPub;
		boolean migrationUpdated = false;
		boolean optionUpdated = false;
		boolean grhUpdated = false;
		boolean gpsUpdated = false;
		boolean rBFUpdated = false;
		Map<String, Object> migrationMap;
		Map<String, Object> updateOptionMap;
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = (String) wi.getParameter("taskName");
			logger.info("taskName::" + taskName);
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			orderId = (Integer) wi.getParameter("orderId");
			exist = (String) wi.getParameter("exist");
			start = (String) wi.getParameter("start");
			end = (String) wi.getParameter("end");
			oldCoId = (String) wi.getParameter("oldCoId");
			oldCoIdPub = (String) wi.getParameter("oldCoIdPub");
			updateOptionMap = (Map<String, Object>) wi.getParameter("updateOptionMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			contractOrderId = (int) wi.getProcessInstanceId();

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			if (exist.equalsIgnoreCase("Grh")) {
				logger.info("=> update Grh IN");

				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setCOID(String.valueOf(contractMap.get("coId")));
				reengagementBean.setDFEDESCRIPTION("DFE");
				reengagementBean.setDFESTATUS(2);
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setOPTIONSTATUS(0);
				if (oldCoId != null) {
					reengagementBean.setOLDCOID(oldCoId);
				}
				if (oldCoIdPub != null) {
					reengagementBean.setOLDCOCODE(oldCoIdPub);
				}
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setCONTRACTSTATUS("Completed");
				reengagementBean.setORDERID(orderId);
				reengagementBean.setCONTRACTORDERID(0);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setSTATUTORDER(0);
				reengagementBean.setTask(taskName);
				reengagementBean.setDATEEND(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				reengagementBean.setStatus(Integer.valueOf(2));

				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					grhUpdated = true;
					logger.info("reengagement are updated");
				} else {
					logger.info("respone Grh=" + orderResponse.isFinished());
					logger.info("Grh Error=" + orderResponse.getResponse().getError());
					logger.info("Grh Error Comment=" + orderResponse.getResponse().getComment());
					logger.info("updateOrder grh failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}

			}

			if (exist.equalsIgnoreCase("Migration")) {
				logger.info("=> update Migration IN");

				MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
						MigrationOutputBean.class);
				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setStatus(1);
				reengagementBean.setCOID(String.valueOf(contractMap.get("coId")));
				reengagementBean.setCOCODE(String.valueOf(contractMap.get("contractCode")));
				reengagementBean.setMIGRATIONDESCRIPTION("Migration");
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setOPTIONSTATUS(0);
				if (start.equalsIgnoreCase("startMigration")) {
					reengagementBean.setMIGRATIONSTATUS(1);
					reengagementBean.setCONTRACTSTATUS("Inprogress");
				}
				if (end.equalsIgnoreCase("endMigration")) {
					reengagementBean.setMIGRATIONSTATUS(2);
					reengagementBean.setCONTRACTSTATUS("Completed");
					reengagementBean.setDATEEND(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					reengagementBean.setStatus(Integer.valueOf(2));

				}
				if (oldCoId != null) {
					reengagementBean.setOLDCOID(oldCoId);
				}
				if (oldCoIdPub != null) {
					reengagementBean.setOLDCOCODE(oldCoIdPub);
				}
				reengagementBean.setCONTRACTORDERID(contractOrderId);
				reengagementBean.setORDERID(orderId);

				reengagementBean.setTMCODEINIT(
						migrationOutputBean.getResponseMigration().getOffreInitBscs().getTmcode().intValue());
				reengagementBean.setTMCODETARGET(
						migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode().intValue());
				reengagementBean.setSTATUTORDER(0);
				reengagementBean.setTask(taskName);
				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					migrationUpdated = true;
					logger.info("reengagementOrder are updated");
				} else {
					logger.info("response Migration=" + orderResponse.isFinished());
					logger.info("Migration Error=" + orderResponse.getResponse().getError());
					logger.info("Migration Error comment=" + orderResponse.getResponse().getComment());
					logger.info("update reengagementOrder migration failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}
			}
			if (exist.equalsIgnoreCase("UpdateOption")) {
				logger.info("=> update UpdateOption IN");

				UpdateOutputBean updateOutputBean = new ObjectMapper().convertValue(updateOptionMap,
						UpdateOutputBean.class);
				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setStatus(1);
				reengagementBean.setCOID(String.valueOf(contractMap.get("coId")));
				reengagementBean.setOPTIONDESCRIPTION("Update Option");
				if (start.equalsIgnoreCase("startUpdate")) {
					reengagementBean.setOPTIONSTATUS(1);
					reengagementBean.setCONTRACTSTATUS("Inprogress");
				}
				if (end.equalsIgnoreCase("endUpdate")) {
					reengagementBean.setOPTIONSTATUS(2);
					reengagementBean.setCONTRACTSTATUS("Completed");
					reengagementBean.setDATEEND(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					reengagementBean.setStatus(Integer.valueOf(2));

				}
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setCONTRACTORDERID(contractOrderId);
				reengagementBean.setORDERID(orderId);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(
						updateOutputBean.getResponseUpdateOption().getOffreTargetBscs().getTmcode().intValue());
				reengagementBean.setSTATUTORDER(0);
				reengagementBean.setTask(taskName);
				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					optionUpdated = true;
					logger.info("reengagementOrder are updated");

				} else {
					logger.info("response UpdateOption=" + orderResponse.isFinished());
					logger.info("UpdateOption Error=" + orderResponse.getResponse().getError());
					logger.info("UpdateOption Error comment=" + orderResponse.getResponse().getComment());
					logger.info("update reengagementOrder updateOption failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}

			}

			if (exist.equalsIgnoreCase("Gps")) {
				logger.info("=> update Gps IN");

				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setCAGNOTTEDESCRIPTION("GPS");
				reengagementBean.setCAGNOTTESTATUS(2);
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setOPTIONSTATUS(0);
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setORDERID(orderId);
				reengagementBean.setCONTRACTORDERID(0);
				reengagementBean.setCONTRACTSTATUS(null);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setSTATUTORDER(0);
				reengagementBean.setTask(taskName);
				reengagementBean.setStatus(Integer.valueOf(2));

				reengagementBean.setDATEEND(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					gpsUpdated = true;
					logger.info("reengagementOrder are updated");

				} else {
					logger.info("response GPS" + orderResponse.isFinished());
					logger.info("GPS Error=" + orderResponse.getResponse().getError());
					logger.info("GPS Error comment=" + orderResponse.getResponse().getComment());
					logger.info("update reengagementOrder GPS failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}

			}
			if (exist.equalsIgnoreCase("RBF")) {
				logger.info("=> update RBF IN");

				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setRBFDESCRIPTION("RBF");
				reengagementBean.setRBFSTATUS(2);
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setOPTIONSTATUS(0);
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setORDERID(orderId);
				reengagementBean.setCONTRACTORDERID(0);
				reengagementBean.setCONTRACTSTATUS(null);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setSTATUTORDER(0);
				reengagementBean.setTask(taskName);
				reengagementBean.setDATEEND(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				reengagementBean.setStatus(Integer.valueOf(2));

				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					rBFUpdated = true;
					logger.info("reengagementOrder are updated");

				} else {
					logger.info("response RBF" + orderResponse.isFinished());
					logger.info("RBF Error=" + orderResponse.getResponse().getError());
					logger.info("RBF Error Comment=" + orderResponse.getResponse().getComment());
					logger.info("update reengagementOrder RBF failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}
			}

			if (exist.equalsIgnoreCase("order")) {
				logger.info("=> update order IN");

				ReengagementBean reengagementBean = new ReengagementBean();
				reengagementBean.setORDERDESCRIPTION("Reengagement Commande");
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setOPTIONSTATUS(0);
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setORDERID(orderId);
				reengagementBean.setCONTRACTORDERID(0);
				reengagementBean.setCONTRACTSTATUS(null);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setSTATUTORDER(2);

				reengagementBean.setStatus(0);

				UpdateReengagementOrderResponse orderResponse = reengagementService
						.updateReengagementOrder(reengagementBean);

				if (orderResponse.isFinished()) {
					orderUpdated = true;
					logger.info("reengagementOrder are updated");

				} else {
					logger.info("response RBF" + orderResponse.isFinished());
					logger.info("RBF Error=" + orderResponse.getResponse().getError());
					logger.info("RBF Error Comment=" + orderResponse.getResponse().getComment());
					logger.info("update reengagementOrder RBF failed ");
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
				}

			}
			if (rBFUpdated || gpsUpdated || optionUpdated || migrationUpdated || grhUpdated || orderUpdated) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputUpdateReengagementOrder(waitFailure, taskName));
			}
		} catch (Exception e) {
			logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputUpdateReengagementOrder(retryNbre, waitFailure, taskName));
		}
	}

}
