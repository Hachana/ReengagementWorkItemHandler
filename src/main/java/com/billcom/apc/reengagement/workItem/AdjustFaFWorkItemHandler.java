package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.AddFaFResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.CheckEligibleRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.CheckEligibleResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.FaFHandlingSoapBindingStub;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otn.dsi.ws.AddFaFBO;
import com.otn.dsi.ws.RemoveFaFBO;

/**
 * @author Fethi Hachana
 *
 */
public class AdjustFaFWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(AdjustFaFWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * bloc empty
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		boolean toSuccess = true;
		boolean adjust = false;
		this.logger.info("=> AdjustFaFWorkItemHandler in:  Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		Map<String, Object> migrationMap = new HashMap<>();

		reengagementService.printWorkItem(this.logger, wi);
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		String taskName = "";
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			ArrayList<Long> listSubscriberFriends = (ArrayList<Long>) wi.getParameter("listSubscriberFriends");

			Integer maxAdded = (Integer) wi.getParameter("maxAdded");
			Boolean isSameGroup = (Boolean) wi.getParameter("isSameGroup");
			taskName = reengagementService.getConfig().getPropValues("AdjustFaFWIH");
			logger.info("taskName::" + taskName);
			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);
			Boolean isListRemoveFaF = false;
			String shdes1 = migrationOutputBean.getResponseMigration().getOffreInitBscs().getShdesOffer();
			String shdes2 = migrationOutputBean.getResponseMigration().getOffreTargetBscs().getShdesOffer();
			String offre1 = shdes1.substring(0, 3);
			String offre2 = shdes2.substring(0, 3);
			String migrationCategory = offre1 + "2" + offre2;
			logger.info("MigrationCategory ::" + migrationCategory);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// List Subscriber Friends

			if (isSameGroup.booleanValue()) {

				if (maxAdded < listSubscriberFriends.size()) {
					FaFHandlingSoapBindingStub bindingEligible = reengagementService.getConfig().consumeFaFHandling();

					CheckEligibleRequest checkEligibleRequest = new CheckEligibleRequest();
					checkEligibleRequest.setShortPhoneNumber((String) contractMap.get("msisdn"));
					CheckEligibleResponse responseEligible = bindingEligible.checkEligible(checkEligibleRequest);

					if (responseEligible.getIsEligible() != null && responseEligible.getIsEligible()) {
						this.logger.info("=> responseEligible.getIsEligible() ::: " + responseEligible.getIsEligible());

						RemoveFaFBO response = reengagementService.removeFaF(listSubscriberFriends,
								(String) contractMap.get("msisdn"));
						if (response.getResponse().getIsSuccessful().booleanValue()) {
							isListRemoveFaF = true;
							toSuccess = true;
							adjust = true;
						} else {
							this.logger.info("IsSuccessful  " + response.getResponse().getIsSuccessful());
							this.logger.info("BscsErrorCode  " + response.getResponse().getErrorCode());
							this.logger.info("Comment  " + response.getResponse().getComment());
							toSuccess = false;

						}
					} else { // Corrective éligible FAF
						toSuccess = true;

						this.logger.info("Numéro pas eligible FAF ! ");
						this.logger.info("Comment :: " + responseEligible.getComment());
						this.logger.info("BscsErrorCode :: " + responseEligible.getBscsErrorCode());

					}

				} else {
					toSuccess = true;

				}

			} else {
				if (maxAdded >= listSubscriberFriends.size()) {
					if (migrationCategory.equalsIgnoreCase("POS2POS") || migrationCategory.equalsIgnoreCase("HYB2POS")
							|| migrationCategory.equalsIgnoreCase("PRE2POS")) {
						FaFHandlingSoapBindingStub bindingEligible = reengagementService.getConfig()
								.consumeFaFHandling();

						CheckEligibleRequest checkEligibleRequest = new CheckEligibleRequest();

						checkEligibleRequest.setShortPhoneNumber((String) contractMap.get("msisdn"));
						CheckEligibleResponse responseEligible = bindingEligible.checkEligible(checkEligibleRequest);

						if (responseEligible.getIsEligible() != null && responseEligible.getIsEligible()) {
							this.logger
									.info("=> responseEligible.getIsEligible() ::" + responseEligible.getIsEligible());

							AddFaFBO response = reengagementService.addFaF(listSubscriberFriends,
									(String) contractMap.get("msisdn"));
							if (response.getResponse().getIsSuccessful().booleanValue()) {
								toSuccess = true;
								adjust = true;

							} else {
								this.logger.info("IsSuccessful " + response.getResponse().getIsSuccessful());
								this.logger.info("BscsErrorCode " + response.getResponse().getErrorCode());
								this.logger.info("Comment " + response.getResponse().getComment());
								toSuccess = false;

							}
						} else { // Corrective éligible FAF
							toSuccess = true;

							this.logger.info("Numéro pas eligible FAF ");
							this.logger.info("Comment : " + responseEligible.getComment());
							this.logger.info("BscsErrorCode : " + responseEligible.getBscsErrorCode());

						}

					}

					if (migrationCategory.equalsIgnoreCase("POS2PRE") || migrationCategory.equalsIgnoreCase("POS2HYB")
							|| migrationCategory.equalsIgnoreCase("HYB2PRE")
							|| migrationCategory.equalsIgnoreCase("HYB2HYB")) {

						FaFHandlingSoapBindingStub bindingEligible = reengagementService.getConfig()
								.consumeFaFHandling();

						CheckEligibleRequest checkEligibleRequest = new CheckEligibleRequest();

						checkEligibleRequest.setShortPhoneNumber((String) contractMap.get("msisdn"));
						CheckEligibleResponse responseEligible = bindingEligible.checkEligible(checkEligibleRequest);

						if (responseEligible.getIsEligible() != null && responseEligible.getIsEligible()) {
							this.logger
									.info("=> responseEligible.getIsEligible() ::" + responseEligible.getIsEligible());
							AddFaFResponse responseAddFaFIN = reengagementService.addFaFIN(listSubscriberFriends,
									(String) contractMap.get("msisdn"), contractMap.get("tmcode").toString());
							if (responseAddFaFIN.getResponse().isIsSuccessful().booleanValue()) {
								toSuccess = true;
								adjust = true;

							} else {
								this.logger.info("IsSuccessful " + responseAddFaFIN.getResponse().isIsSuccessful());
								this.logger.info("BscsErrorCode " + responseAddFaFIN.getResponse().getError());
								this.logger.info("Comment " + responseAddFaFIN.getResponse().getComment());
								toSuccess = false;

							}
						} else { // Corrective éligible FAF
							toSuccess = true;

							this.logger.info("Numéro pas eligible FAF ");
							this.logger.info("Comment : " + responseEligible.getComment());
							this.logger.info("BscsErrorCode : " + responseEligible.getBscsErrorCode());

						}

					}
				} else {
					isListRemoveFaF = true;
					toSuccess = true;

					wim.completeWorkItem(wi.getId(),
							reengagementService.sucessOutputAdjustFaF(waitFailure, taskName, isListRemoveFaF));
				}

			}

			if (toSuccess && !adjust) {
				this.logger.info("NO FAF TO ADJUST ");
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputAdjustFaF(waitFailure, taskName, isListRemoveFaF));
			} else if (toSuccess && adjust) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputAdjustFaF(waitFailure, taskName, isListRemoveFaF));

			} else if (!toSuccess) {

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputAdjustFaF(retryNbre, waitFailure, taskName));

			}

		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failOutputAdjustFaF(retryNbre, waitFailure, taskName));
		}

	}

}
