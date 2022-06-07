package com.billcom.apc.reengagement.workItem;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */
public class DesactivateServicesWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(DesactivateServicesWorkItemHandler.class);
	private String taskName;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty code
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		this.logger.info("=> DesactivateServicesWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());

		this.logger.info("Reengagement in progress...Desactivate Services");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> contractMap;
		List<Map<String, Object>> listToDelete;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("DesactivateServicesWIH");
			logger.info("taskName::" + taskName);
			listToDelete = (List<Map<String, Object>>) wi.getParameter("listToDelete");
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			ContractDetails contractDetail = new ObjectMapper().convertValue(contractMap, ContractDetails.class);
			long[] sncodes = new long[listToDelete.size()];

			for (int i = 0; i < listToDelete.size(); i++) {

				sncodes[i] = (Long) listToDelete.get(i).get("sncode");

			}

			DesactivateServicesRequest request = new DesactivateServicesRequest();

			request.setCoId(contractDetail.getCoId());

			request.setSnCodes(sncodes);

			DesactivateServicesResponse response = reengagementService.desactivateServices(request);

			this.logger.info("response : " + response.getIsSuccessful());
			if (response.getIsSuccessful().booleanValue()) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputDesactivateServices(waitFailure, taskName));
			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				this.logger.info("Remark " + response.getRemark());
				this.logger.info("<= DesactivateServicesWorkItemHandler with error out: Process Id::  "
						+ wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputDesactivateServices(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.info(
					"<= DesactivateServicesWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputDesactivateServices(retryNbre, waitFailure, taskName));
		}
	}

}
