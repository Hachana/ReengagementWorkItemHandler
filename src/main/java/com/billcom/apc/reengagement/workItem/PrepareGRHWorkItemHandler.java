package com.billcom.apc.reengagement.workItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */
public class PrepareGRHWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(PrepareGRHWorkItemHandler.class);
	Map<String, Object> resultsMap = new HashMap<>();

	@Override
	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		logger.info("=> PrepareGRHWorkItemHandler IN : Process Id::  " + wi.getProcessInstanceId());
		List<Map<String, Object>> listContractOfGrh;
		Map<String, Object> contractMap;
		String oldCoId;

		listContractOfGrh = (List<Map<String, Object>>) wi.getParameter("listContractOfGrh");
		contractMap = (Map<String, Object>) wi.getParameter("contractMap");
		oldCoId = (String) wi.getParameter("oldCoId");

		logger.info("oldCoid =  " + oldCoId);

		logger.info("listContractOfGrh size = " + listContractOfGrh.size());

		if (listContractOfGrh != null) {

			for (Map<String, Object> map : listContractOfGrh) {

				if (oldCoId != null && map.get("coId").equals(oldCoId)) {
					logger.info("coId from  listContractOfGrh = " + map.get("coId"));
					map.put("coId", contractMap.get("coId"));
					logger.info("coId changed by  : " + map.get("coId"));

				}

			}
		}
		logger.info("<= PrepareGRHWorkItemHandler OUT : Process Id::  " + wi.getProcessInstanceId());
		resultsMap.put("listContractOfGrh", listContractOfGrh);
		wim.completeWorkItem(wi.getId(), resultsMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

		/*
		 * empty bloc
		 */
	}

}
