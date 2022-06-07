package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.MigrationBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBean;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.model.ServiceToactivateContratResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class ListActivateServicesWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(ListActivateServicesWorkItemHandler.class);
	private String taskName;
	private Boolean isListCugs = false;
	private Boolean isListRessources = false;
	private Map<String, Object> cugMap = new HashMap<>();
	private boolean toSuccess = true;
	private List<Map<String, Object>> listServiceRessource;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> ListActivateServicesWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());

		logger.info("Reengagement in progress... listActivateServices");
		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> migrationMap;
		Map<String, Object> mapServiceToActivate;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		String sncodesCug;
		String sncodesRessources;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		mapServiceToActivate = new HashMap<>();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("listActivateServicesWIH");
			logger.info("taskName::" + taskName);
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			sncodesCug = reengagementService.getConfig().getPropValues("SNCODE_CUG").trim();
			sncodesRessources = reengagementService.getConfig().getPropValues("SNCODE_RESSOURCE").trim();
			listServiceRessource = (List<Map<String, Object>>) wi.getParameter("listServiceRessource");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			if (MapUtils.isNotEmpty(migrationMap)) {

				MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
						MigrationOutputBean.class);

				GetBscsOfferAndServciesFromCrmDemandeResponse res = migrationOutputBean.getResponseMigration();
				MigrationBean resDecrypt = migrationOutputBean.getInputMigration();

				for (int i = 0; i < res.getContractDetails().length; i++) {

					GetListOfServiceToactivateContratRequest request = new GetListOfServiceToactivateContratRequest();

					request.setCoId(res.getContractDetails()[i].getCoId());
					request.setOffreInit(res.getOffreInitBscs());
					request.setOffreTarget(res.getOffreTargetBscs());

					Arrays.stream(res.getOptionToAddBscs())
							.forEach(x -> logger.info("getOptionToAddBscs[i]= " + x.getSncode()));

					if (resDecrypt.getListOptionsToAdd() != null && resDecrypt.getListOptionsToAdd().length > 0) {
						String[] optionToAddBscs = new String[resDecrypt.getListOptionsToAdd().length];

						for (int j = 0; j < optionToAddBscs.length; j++) {
							optionToAddBscs[j] = resDecrypt.getListOptionsToAdd()[j].getOption();
							logger.info("optionToAddBscs in request = " + optionToAddBscs[j]);
						}

						request.setOptionsToAdd(optionToAddBscs);
					} else {
						String[] optionToAddBscs = new String[0];

						request.setOptionsToAdd(optionToAddBscs);

					}

					this.logger.info("request= " + new ObjectMapper().convertValue(request, Map.class));

					GetListOfServiceToactivateContratResponse response = reengagementService
							.getListOfServiceToactivateContratRequest(request);

					if (response.getIsSuccessful().booleanValue()) {
						toSuccess = true;
						this.logger.info("response= [coId= " + res.getContractDetails()[i].getCoId() + "]= "
								+ new ObjectMapper().convertValue(response, Map.class));

						ServiceToactivateContratResponse serviceToactivateContratResponse = new ServiceToactivateContratResponse();

						serviceToactivateContratResponse.setCoId(res.getContractDetails()[i].getCoId());
						serviceToactivateContratResponse.setOffreInitBscs(response.getOffreInitBscs());
						serviceToactivateContratResponse.setOffreTargetBscs(response.getOffreInitBscs());
						serviceToactivateContratResponse.setServicesForContract(response.getServicesForContract());

						this.logger.info("response= [coId= " + res.getContractDetails()[i].getCoId() + "]= "
								+ new ObjectMapper().convertValue(serviceToactivateContratResponse, Map.class));

						if (MapUtils.isNotEmpty(migrationMap)) {

							ServiceToactivateContratResponse serviceToactivateContratResponseAfterCheck = new ServiceToactivateContratResponse();
							serviceToactivateContratResponseAfterCheck.setServicesForContract(traitement(sncodesCug,
									sncodesRessources, serviceToactivateContratResponse.getServicesForContract()));

							mapServiceToActivate.put(String.valueOf(res.getContractDetails()[i].getCoId()),
									new ObjectMapper().convertValue(serviceToactivateContratResponseAfterCheck,
											Map.class));

						}
					} else {
						toSuccess = false;
						this.logger.info("respone=" + response.getIsSuccessful());
						this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
						this.logger.info("comment= " + response.getComment());
					}

				}
			} else {
				this.logger.info("Migration Map is empty");
			}
			if (toSuccess) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputGetListOfServiceToactivateContrat(waitFailure,
								mapServiceToActivate, taskName, isListCugs, cugMap, isListRessources,
								listServiceRessource));

			} else {
				wim.completeWorkItem(wi.getId(), reengagementService
						.failOutputGetListOfServiceToactivateContrat(retryNbre, waitFailure, taskName));

			}

		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputGetListOfServiceToactivateContrat(retryNbre, waitFailure, taskName));
		}
	}

	private ServiceBean[] traitement(String sncodes, String sncodes2, ServiceBean[] serviceToactivateContratResponse) {
		logger.info("=> traitement");
		List<ServiceBean> listServiceToactivateContratResponse = new ArrayList<>();

		logger.info(" Array services size" + serviceToactivateContratResponse.length);

		for (ServiceBean service : serviceToactivateContratResponse) {

			listServiceToactivateContratResponse.add(service);
		}

		logger.info(" List services size after switch " + listServiceToactivateContratResponse.size());

		for (ServiceBean service : serviceToactivateContratResponse) {
			if (contains(sncodes, ",", Long.toString(service.getSncode()))) {
				isListCugs = true;
				cugMap.put("sncode", service.getSncode());
				cugMap.put("spcode", service.getSpcode());

			}

			else if (contains(sncodes2, ",", Long.toString(service.getSncode()))) {
				isListRessources = true;
				listServiceRessource.add(new ObjectMapper().convertValue(service, Map.class));
				listServiceToactivateContratResponse.remove(service);
				logger.info(" service ressource removed : " + service.getSncode());

			}
		}
		logger.info(" List services size after remove " + listServiceToactivateContratResponse.size());
		ServiceBean[] serviceToactivateContratResponseAfterCheck = new ServiceBean[listServiceToactivateContratResponse
				.size()];

		int i = 0;
		for (ServiceBean s : listServiceToactivateContratResponse) {
			serviceToactivateContratResponseAfterCheck[i] = s;
			i++;
		}

		logger.info(" Array services size after remove " + serviceToactivateContratResponseAfterCheck.length);

		return serviceToactivateContratResponseAfterCheck;

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
