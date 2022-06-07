package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Parameter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBeanCrm;
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

public class GetActionWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(GetActionWorkItemHandler.class);
	private Map<String, Object> migrationMap;
	private List<Map<String, Object>> listToUpdate;
	private List<Service> listToAdd;
	private List<Map<String, Object>> listToDelete;
	private List<Map<String, Object>> listServiceCug;
	private List<Service> listServiceRessource;

	//
	private Integer sizeListContractOfUpdateOption;
	private boolean isListToDelete;
	private boolean isListToAdd;
	private boolean isListToUpdate;
	private boolean isListCugs;
	private boolean isListRessources = false;
	private String taskName;
	private Map<String, Object> cugMapIntra = new HashMap<>();

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> GetActionWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		logger.info("Reengagement in progress... Get Action");

		ReengagementService reengagementService = new ReengagementServiceImp();
		Map<String, Object> updateOptionMap;
		List<Map<String, Object>> listContractOfUpdateOption;
		List<ServiceBeanCrm> listToAddService;
		List<ServiceBeanCrm> listToDeleteService;

		listToUpdate = new ArrayList<>();
		listToAdd = new ArrayList<>();
		listToDelete = new ArrayList<>();
		listServiceCug = new ArrayList<>();
		listServiceRessource = new ArrayList<>();
		listContractOfUpdateOption = new ArrayList<>();
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		String sncodesRessouce;
		String sncodesCug;
		Boolean isSameTmcode;
		String isUpdate;

		//
		listToAddService = new ArrayList<>();
		listToDeleteService = new ArrayList<>();
		//
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("GetActionWIH");
			sncodesRessouce = reengagementService.getConfig().getPropValues("SNCODE_RESSOURCE").trim();
			sncodesCug = reengagementService.getConfig().getPropValues("SNCODE_CUG").trim();
			logger.info("taskName::" + taskName);
			updateOptionMap = (Map<String, Object>) wi.getParameter("updateOptionMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			isUpdate = (String) wi.getParameter("isUpdate");
			isSameTmcode = (boolean) wi.getParameter("isSameTmcode");

			logger.info("isSameTmcode in getAction :" + isSameTmcode);
			logger.info("isUpdate in getAction :" + isUpdate);

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}
			if (((isUpdate == null) || (isUpdate.equals(""))) && (MapUtils.isNotEmpty(migrationMap))) {

				MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
						MigrationOutputBean.class);

				ServiceBeanCrm[] serviceBeansToAdd = migrationOutputBean.getResponseMigration().getOptionToAddBscs();
				ServiceBeanCrm[] serviceBeansToDelete = migrationOutputBean.getResponseMigration()
						.getOptionsTodeleteBscs();
				traitement(sncodesCug, sncodesRessouce, serviceBeansToAdd, serviceBeansToDelete, isSameTmcode);

			}

			if (MapUtils.isNotEmpty(updateOptionMap)) {
				UpdateOutputBean updateOutputBean = new ObjectMapper().convertValue(updateOptionMap,
						UpdateOutputBean.class);

				ServiceBeanCrm[] serviceBeansToAdd = updateOutputBean.getResponseUpdateOption().getOptionToAddBscs();
				ServiceBeanCrm[] serviceBeansToDelete = updateOutputBean.getResponseUpdateOption()
						.getOptionsTodeleteBscs();

				for (int i = 0; i < serviceBeansToAdd.length; i++) {
					logger.info("serviceBeansToAdd=" + serviceBeansToAdd[i]);
				}

				for (int i = 0; i < serviceBeansToDelete.length; i++) {
					logger.info("serviceBeansToDelete=" + serviceBeansToDelete[i]);
				}

				traitement(sncodesCug, sncodesRessouce, serviceBeansToAdd, serviceBeansToDelete, isSameTmcode);

				Map<String, Object> responseUpdateOption = (Map<String, Object>) updateOptionMap
						.get("responseUpdateOption");
				listContractOfUpdateOption = (List<Map<String, Object>>) responseUpdateOption.get("contractDetails");

			}

			if (!CollectionUtils.isEmpty(listToAdd)) {
				isListToAdd = true;
			}
			if (!CollectionUtils.isEmpty(listToDelete)) {
				isListToDelete = true;
			}
			if (!CollectionUtils.isEmpty(listToUpdate)) {
				isListToUpdate = true;
			}
			if (!CollectionUtils.isEmpty(listServiceCug)) {
				isListCugs = true;
			}
			if (!CollectionUtils.isEmpty(listServiceRessource)) {
				isListRessources = true;

			}

			sizeListContractOfUpdateOption = listContractOfUpdateOption.size();

		} catch (Exception e) {
			this.logger.info("<= GetActionWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failGetAction(retryNbre, waitFailure, taskName));
		}

		wim.completeWorkItem(wi.getId(),
				reengagementService.successGetAction(waitFailure, taskName, sizeListContractOfUpdateOption,
						listContractOfUpdateOption, isListToDelete, isListToAdd, isListToUpdate, isListCugs,
						isListRessources, listToDelete, listToAdd, listToUpdate, listServiceCug, listServiceRessource,
						listToAddService, listToDeleteService, cugMapIntra));
		this.logger.info("=> GetActionWorkItemHandler out: Process Id::  " + wi.getProcessInstanceId());
	}

	private void traitement(String sncodes, String sncodes2, ServiceBeanCrm[] serviceBeansToAdd,
			ServiceBeanCrm[] serviceBeansToDelete, Boolean isSameTmcode) {
		logger.info("=> traitement");
		logger.info("isSameTmcode :::" + isSameTmcode);

		List<ServiceBeanCrm> result = new ArrayList<>();

		for (ServiceBeanCrm service : serviceBeansToAdd) {
			if (contains(sncodes, ",", Long.toString(service.getSncode()))) {
				listServiceCug.add(new ObjectMapper().convertValue(service, Map.class));
				cugMapIntra.put("sncode", service.getSncode());
				cugMapIntra.put("spcode", service.getSpcode());
			} else if (contains(sncodes2, ",", Long.toString(service.getSncode()))) {
				Service s = new Service();
				s.setSncode(service.getSncode());
				s.setSpcode(service.getSpcode());
				listServiceRessource.add(s);
			} else {
				result.add(service);

			}
		}

		for (ServiceBeanCrm serviceToDelete : serviceBeansToDelete) {
			boolean found = false;
			logger.info(serviceBeansToDelete);
			Iterator<ServiceBeanCrm> iterator = result.iterator();

			while (iterator.hasNext()) {

				ServiceBeanCrm serviceToAdd = iterator.next();

				logger.info("DescriptionAdd" + serviceToAdd.getParameterDescription());
				logger.info("DescriptionDelete" + serviceToDelete.getParameterDescription());
				logger.info("SpcodeAdd" + serviceToAdd.getSpcode());
				logger.info("SpcodeDelete" + serviceToDelete.getSpcode());
				logger.info("SncodeAdd" + serviceToAdd.getSncode());
				logger.info("SncodeDelete" + serviceToDelete.getSncode());

				if (serviceToAdd.getParameterDescription() != null
						&& serviceToDelete.getParameterDescription() != null) {
					logger.info("Block0:: service avec parameter");
					if (serviceToAdd.getSpcode().equals(serviceToDelete.getSpcode())
							&& serviceToAdd.getSncode().equals(serviceToDelete.getSncode()) && serviceToAdd
									.getParameterDescription().equals(serviceToDelete.getParameterDescription())) {
						logger.info("Block1:: update parameter" + serviceToAdd.getSpcode() + serviceToDelete.getSpcode()
								+ serviceToAdd.getSncode() + serviceToDelete.getSncode()
								+ serviceToAdd.getParameterDescription() + serviceToDelete.getParameterDescription());

						listToUpdate.add(new ObjectMapper().convertValue(serviceToAdd, Map.class));
						logger.info("listToUpdate=" + listToUpdate);
						iterator.remove();
						found = true;
						break;

					}

					if (serviceToAdd.getSpcode().equals(serviceToDelete.getSpcode())
							&& serviceToAdd.getSncode().equals(serviceToDelete.getSncode())) {
						logger.info("Block2:: Activate Service + Desactivate Service" + serviceToAdd.getSpcode()
								+ serviceToDelete.getSpcode() + serviceToAdd.getSncode() + serviceToDelete.getSncode()
								+ serviceToAdd.getParameterDescription() + serviceToDelete.getParameterDescription());
						//
						Service s = new Service();
						s.setSncode(serviceToAdd.getSncode());
						s.setSpcode(serviceToAdd.getSpcode());
						if (serviceToAdd.getParameterId() != null) {
							Parameter p = new Parameter();
							List<Parameter> paramList = new ArrayList<>();
							p.setPrmId(Long.parseLong(serviceToAdd.getParameterId()));

							if (serviceToAdd.getParameterDescription() != null) {
								p.setPrmDes(serviceToAdd.getParameterDescription());
							}
							if (serviceToAdd.getPrmType() != null) {
								p.setPrmType(serviceToAdd.getPrmType());
							}
							if (serviceToAdd.getValueDes() != null) {
								p.setValueDes(serviceToAdd.getValueDes());
							}
							if (serviceToAdd.getParameterValue() != null) {
								p.setValue(serviceToAdd.getParameterValue());
							}
							paramList.add(p);
							s.setParam(paramList.toArray(new Parameter[] {}));

						}
						//
						listToAdd.add(s);
						logger.info("listToAdd=" + listToAdd);
						iterator.remove();
						listToDelete.add(new ObjectMapper().convertValue(serviceToDelete, Map.class));
						logger.info("listToDelete=" + listToDelete);
						found = true;
						break;

					}

				}

			}
			if (!found) {
				logger.info("Block3");
				listToDelete.add(new ObjectMapper().convertValue(serviceToDelete, Map.class));
			}
		}

		for (ServiceBeanCrm serviceToAdd : result) {
			//
			Service s = new Service();
			s.setSncode(serviceToAdd.getSncode());
			s.setSpcode(serviceToAdd.getSpcode());

			if (serviceToAdd.getParameterId() != null) {
				Parameter p = new Parameter();
				List<Parameter> paramList = new ArrayList<>();
				p.setPrmId(Long.parseLong(serviceToAdd.getParameterId()));

				if (serviceToAdd.getParameterDescription() != null) {
					p.setPrmDes(serviceToAdd.getParameterDescription());
				}
				if (serviceToAdd.getPrmType() != null) {
					p.setPrmType(serviceToAdd.getPrmType());
				}
				if (serviceToAdd.getValueDes() != null) {
					p.setValueDes(serviceToAdd.getValueDes());
				}
				if (serviceToAdd.getParameterValue() != null) {
					p.setValue(serviceToAdd.getParameterValue());
				}
				paramList.add(p);
				s.setParam(paramList.toArray(new Parameter[] {}));

			}
			//
			listToAdd.add(s);

		}

		//// cas same tmcode add new pareter
		if (isSameTmcode != null && isSameTmcode) {
			logger.info("Same Tmcode => listToUpdate ");

			ServiceBeanCrm serviceToAddWithSameTmcode = new ServiceBeanCrm();

			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);
			serviceToAddWithSameTmcode.setParameterDescription(
					migrationOutputBean.getResponseMigration().getOffreTargetBscs().getShdesParameter());
			logger.info("ParameterDescription ="
					+ migrationOutputBean.getResponseMigration().getOffreTargetBscs().getShdesParameter());

			serviceToAddWithSameTmcode
					.setValueDes(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getValueDes());
			logger.info("ValueDes =" + migrationOutputBean.getResponseMigration().getOffreTargetBscs().getValueDes());

			serviceToAddWithSameTmcode.setParameterId(
					migrationOutputBean.getResponseMigration().getOffreTargetBscs().getParameterId() + "");
			logger.info(
					"ParameterId =" + migrationOutputBean.getResponseMigration().getOffreTargetBscs().getParameterId());

			serviceToAddWithSameTmcode.setParameterValue(
					migrationOutputBean.getResponseMigration().getOffreTargetBscs().getParameterValue());
			logger.info("ParameterValue ="
					+ migrationOutputBean.getResponseMigration().getOffreTargetBscs().getParameterValue());

			serviceToAddWithSameTmcode
					.setSncode(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getSncode());
			logger.info("Sncode =" + migrationOutputBean.getResponseMigration().getOffreTargetBscs().getSncode());

			serviceToAddWithSameTmcode
					.setSpcode(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getSpcode());
			logger.info("Spcode =" + migrationOutputBean.getResponseMigration().getOffreTargetBscs().getSpcode());

			listToUpdate.add(new ObjectMapper().convertValue(serviceToAddWithSameTmcode, Map.class));
		}
		///

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
