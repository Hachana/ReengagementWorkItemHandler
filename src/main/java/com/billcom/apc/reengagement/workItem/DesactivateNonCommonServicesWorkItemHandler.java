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

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesResponse;

import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */
public class DesactivateNonCommonServicesWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(DesactivateNonCommonServicesWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger
				.info("=> DesactivateNonCommonServicesWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		List<Long> listSncode = new ArrayList<>();
		List<Map<String, Object>> listToDelete = new ArrayList<>();
		List<Map<String, Object>> listToDeleteAfterCheck = new ArrayList<>();

		String taskName = reengagementService.getConfig().getPropValues("DesactivateNonCommonServicesWIH");
		logger.info("taskName::" + taskName);
		this.logger.info("Reengagement in progress...Desactivate NonCommon Services");
		reengagementService.printWorkItem(this.logger, wi);

		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			listSncode = (List<Long>) wi.getParameter("listSncode");
			listToDelete = (List<Map<String, Object>>) wi.getParameter("listToDelete");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			long[] snCodes = new long[listSncode.size()];
			for (int j = 0; j < listSncode.size(); j++) {
				logger.info("services to desactivate is  " + listSncode.get(j));
				snCodes[j] = listSncode.get(j);
				logger.info("list snCodes Size::  " + snCodes.length);
			}

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			DesactivateServicesResponse response = reengagementService
					.desactivateNonCommonServices((Long) contractMap.get("coId"), snCodes);

			if (response.getIsSuccessful().booleanValue()) {

				if (listToDelete != null) {

					for (Map<String, Object> map : listToDelete) {
						listToDeleteAfterCheck.add(map);
					}

					for (Map<String, Object> map : listToDelete) {

						for (int i = 0; i < snCodes.length; i++) {

							if ((Long) map.get("sncode") == snCodes[i]) {
								logger.info("sncode : " + snCodes[i] + " removed from listToDelete");
								listToDeleteAfterCheck.remove(map);
							}
						}
					}
				}
				wim.completeWorkItem(wi.getId(), reengagementService
						.sucessOutputDesactivateNonCommonServices(waitFailure, taskName, listToDeleteAfterCheck));

			} else {
				this.logger.info("<= DesactivateNonCommonServicesWorkItemHandler with error out: Process Id::  "
						+ wi.getProcessInstanceId());
				this.logger.info("respone=" + response.getIsSuccessful());
				this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
				this.logger.info("comment= " + response.getComment());

				wim.completeWorkItem(wi.getId(), reengagementService.failOutputDesactivateNonCommonServices(retryNbre,
						waitFailure, taskName, listToDelete));

			}

		} catch (Exception e) {
			this.logger.info("<= DesactivateNonCommonServicesWorkItemHandler with exception out: Process Id::  "
					+ wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failOutputDesactivateNonCommonServices(retryNbre,
					waitFailure, taskName, listToDelete));
		}
	}

}
