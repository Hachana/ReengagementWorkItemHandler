package com.billcom.apc.reengagement.workItem;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamterResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi Hachana
 *
 */

public class ActivateServicesWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(ActivateServicesWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * bloc empty
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		this.logger.info("=> ActivateServicesWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		this.logger.info("Reengagement in progress...Activation Services");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		List<Service> listToAdd;
		String taskName;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("ActivateServicesWIH");
			logger.info("taskName::" + taskName);
			listToAdd = (List<Service>) wi.getParameter("listToAdd");
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			Service[] services = new Service[listToAdd.size()];

			for (int i = 0; i < listToAdd.size(); i++) {

				services[i] = listToAdd.get(i);
			}

			ActivateServiceParamter request = new ActivateServiceParamter();

			request.setCoId((Long) contractMap.get("coId"));

			request.setCoIdPub((String) contractMap.get("contractCode"));

			request.setServices(services);

			ActivateServiceParamterResponse response = reengagementService.activateServiceParamter(request);

			this.logger.info("response : " + response.getIsSuccessful());

			if (response.getIsSuccessful().booleanValue()) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputActivateServiceParamter(waitFailure, taskName));
			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				this.logger.error("<= ActivateServicesWorkItemHandler with error  out: Process Id::  "
						+ wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputActivateServiceParamter(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.error("<= ActivateServicesWorkItemHandler with error  out: Process Id::  "
					+ wi.getProcessInstanceId() + e);

			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputActivateServiceParamter(retryNbre, waitFailure, taskName));
		}
	}

}