package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;

import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.CheckEligibleRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.CheckEligibleResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.FaFHandlingSoapBindingStub;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otn.dsi.ws.ListFaFBO;

/**
 * @author Fethi Hachana
 *
 */

public class GetListFaFWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(GetListFaFWorkItemHandler.class);

	private String taskName;
	ArrayList<Long> result = null;
	boolean subscriberFriends = false;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetListFaFWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		this.logger.info("Reengagement in progress...Get List FaF");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		Map<String, Object> migrationMap;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		boolean toSuccess = true;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("GetListFaFWIH");
			logger.info("taskName::" + taskName);
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws listFaF
			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);
			FaFHandlingSoapBindingStub bindingEligible = reengagementService.getConfig().consumeFaFHandling();
			CheckEligibleRequest checkEligibleRequest = new CheckEligibleRequest();
			checkEligibleRequest.setShortPhoneNumber((String) contractMap.get("msisdn"));
			CheckEligibleResponse responseEligible = bindingEligible.checkEligible(checkEligibleRequest);

			if (responseEligible.getIsEligible() != null && responseEligible.getIsEligible()) {
				this.logger.info("=> responseEligible.getIsEligible() ::" + responseEligible.getIsEligible());

				ListFaFBO response = reengagementService.listFaF(
						migrationOutputBean.getResponseMigration().getOffreInitBscs().getShdesOffer(),
						Long.parseLong((String) contractMap.get("msisdn")));
				if (response.getResponse().getIsSuccessful().booleanValue()) {
					// get List Subscriber Friends
					if (response.getSubscriberFriends() != null) {
						if (response.getSubscriberFriends().length > 0) {
							subscriberFriends = true;
							result = new ArrayList<>(response.getSubscriberFriends().length);
							for (int i = 0; i < response.getSubscriberFriends().length; i++) {
								result.add(response.getSubscriberFriends()[i]);
								this.logger.info("SubscriberFriend ::" + response.getSubscriberFriends()[i]);

							}
							this.logger.info("listSubscriberFriends ::" + result);
						} else {
							subscriberFriends = false;
						}
					}
					toSuccess = true;

				} else {
					toSuccess = false;
					this.logger.info("IsSuccessful " + response.getResponse().getIsSuccessful());
					this.logger.info("ErrorCode " + response.getResponse().getErrorCode());
					this.logger.info("Comment " + response.getResponse().getComment());
				}
			} else { // Corrective éligible FAF
				toSuccess = true;
				subscriberFriends = false;

				this.logger.info("Numéro pas eligible FAF ");
				this.logger.info("Comment : " + responseEligible.getComment());
				this.logger.info("BscsErrorCode : " + responseEligible.getBscsErrorCode());

			}

			if (toSuccess) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputListFaF(waitFailure, taskName, result, subscriberFriends));
			} else {
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputListFaF(retryNbre, waitFailure, taskName));

			}
		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failOutputListFaF(retryNbre, waitFailure, taskName));

		}
	}

}
