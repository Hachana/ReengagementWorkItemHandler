package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */

public class ActivateServiceCoreWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(ActivateServiceCoreWorkItemHandler.class);
	private List<Map<String, Object>> listCugs;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * Method empty
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> ActivateServiceCoreWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		List<Service> listToAdd = new ArrayList<>();
		List<Service> listOfMissingServices = new ArrayList<>();
		List<Service> listOfMissingServicesAfterCheck = new ArrayList<>();

		List<Service> listToAddAfterCheck = new ArrayList<>();
		List<Service> listServiceRessource = new ArrayList<>();
		Boolean isListCugs = false;
		String sncodesRessources = "";

		String taskName = "";
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		try {
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			//
			// retryNbre
			taskName = reengagementService.getConfig().getPropValues("ActivateServiceCoreWIH");
			sncodesRessources = reengagementService.getConfig().getPropValues("SNCODE_RESSOURCE").trim();

			isListCugs = (Boolean) wi.getParameter("isListCugs");

			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}
			this.logger.info("listToAdd size " + listToAdd.size());

			logger.info("listOfMissingServices size ::" + listOfMissingServices.size());

			for (Service s : listOfMissingServices) {
				listOfMissingServicesAfterCheck.add(s);
			}

			for (Service service : listOfMissingServices) {
				logger.info("service  ::" + service.getSncode());

				if (contains(sncodesRessources, ",", Long.toString(service.getSncode()))) {
					logger.info("listOfMissingServices_AfterCheck size   ::" + listOfMissingServicesAfterCheck.size());

					listOfMissingServicesAfterCheck.remove(service);
					logger.info("service  removed ::" + service.getSncode());

				}

			}

			OperationResponse response = reengagementService.activateServicesCores(contractMap,
					listOfMissingServicesAfterCheck, listToAdd, listServiceRessource, listCugs);

			if (response.getIsSuccessful()) {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				if (listToAdd.isEmpty()) {
					this.logger.info("listToAdd not null ");

					listToAddAfterCheck.addAll(listToAdd);
					this.logger.info("listToAdd_AfterCheck size " + listToAddAfterCheck.size());

					for (Service s : listToAdd) {

						for (Service missingService : listOfMissingServices) {

							if (s.getSncode().equals(missingService.getSncode())) {
								listToAddAfterCheck.remove(missingService);
								this.logger.info(missingService.getSncode() + " removed successfully ! ");

							}
						}

					}
				}

				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputactivateServicesCores(waitFailure,
						taskName, listToAddAfterCheck, listServiceRessource, listCugs, isListCugs));

			} else {
				this.logger.info("IsSuccessful :" + response.getIsSuccessful());
				this.logger.info("BscsErrorCode : " + response.getBscsErrorCode());
				this.logger.info("Comment : " + response.getComment());

				wim.completeWorkItem(wi.getId(), reengagementService.failOutputactivateServicesCores(retryNbre,
						waitFailure, taskName, listToAdd, listServiceRessource, listCugs, isListCugs));

			}

		} catch (Exception e) {
			this.logger.error("<= ActivateServiceCoreWorkItemHandler with error  out: Process Id::  "
					+ wi.getProcessInstanceId() + e);

			wim.completeWorkItem(wi.getId(), reengagementService.failOutputactivateServicesCores(retryNbre, waitFailure,
					taskName, listToAddAfterCheck, listServiceRessource, listCugs, isListCugs));
		}

	}

	private boolean contains(String buffer, String separator, String value) {

		try {
			buffer = buffer.trim();
			value = value.trim();

			String[] valueList = buffer.split(separator);
			for (int j = 0; j < valueList.length; j++) {
				if (valueList[j].trim().equals(value)) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
