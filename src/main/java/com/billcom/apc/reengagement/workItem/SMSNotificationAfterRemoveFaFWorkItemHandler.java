package com.billcom.apc.reengagement.workItem;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.reengagement.model.Client;
import com.billcom.apc.reengagement.model.MessageNotif;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi.Hachana
 *
 */

public class SMSNotificationAfterRemoveFaFWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(SMSNotificationAfterRemoveFaFWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		this.logger
				.info("=> SMSNotificationAfterRemoveFaFWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		String taskName = "";
		logger.info("Reengagement in progress... SMS Notification");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		Map<String, Object> migrationMap;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		int idMessage = 0;
		String messageDef = null;
		String typeMessage = null;
		String ccemail = null;

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			taskName = reengagementService.getConfig().getPropValues("NotificationsmsWIH");
			String mock = reengagementService.getConfig().getPropValues("mock").trim();
			String dnNum = reengagementService.getConfig().getPropValues("mockMsisdn").trim();

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);

			String initOffer = migrationOutputBean.getInputMigration().getOffreInit();
			String targetOffer = migrationOutputBean.getInputMigration().getOffreTarget();
			messageDef = reengagementService.getConfig().getPropValues("removeFAF").replace("#offre1#", initOffer)
					.replace("#offre2#", targetOffer);

			MessageNotif messageNotif = null;
			if (mock.equalsIgnoreCase("0")) {
				String msisdn = (String) contractMap.get("msisdn");
				messageNotif = new MessageNotif(idMessage, messageDef, typeMessage, "+216" + msisdn, ccemail);
			} else {
				messageNotif = new MessageNotif(idMessage, messageDef, typeMessage, "+216" + dnNum, ccemail);
			}
			if (Client.smsNotification(messageNotif, wi.getProcessInstanceId())) {
				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputSmsNotif(waitFailure, taskName));

			} else {
				logger.info("error send message");

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputSmsNotif(retryNbre, waitFailure, taskName));
			}

		} catch (Exception e) {
			this.logger.error("<= SMSNotificationAfterRemoveFaFWorkItemHandler out with error : Process Id::  "
					+ wi.getProcessInstanceId());

			logger.error("", e);

		}
		wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputSmsNotif(waitFailure, taskName));

		this.logger.info(
				"<= SMSNotificationAfterRemoveFaFWorkItemHandler out: Process Id::  " + wi.getProcessInstanceId());

	}

}
