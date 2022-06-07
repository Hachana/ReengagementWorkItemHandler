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

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CheckRateplanResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Parameter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi.Hachana
 *
 */

public class CheckRatePlanWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(CheckRatePlanWorkItemHandler.class);
	List<Map<String, Object>> listCugs = new ArrayList<>();
	Boolean isListCugs = false;
	String sncodesCUG = "";

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		List<Long> listSncode = null;
		List<Long> listSncodeService = null;
		ArrayList<Service> communServices = null;
		ArrayList<Service> desactivateServices = null;
		ArrayList<Service> missingServices = null;
		int sizeDesactivateServices = 0;
		this.logger.info("=> CheckRatePlanWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> contractMap = new HashMap<>();
		Map<String, Object> migrationMap = new HashMap<>();

		String taskName = "";
		this.logger.info("Reengagement in progress...Check Rate Plan");
		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();

		try {

			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			taskName = reengagementService.getConfig().getPropValues("CheckRatePlanWIH");
			logger.info("taskName::" + taskName);
			listCugs = (List<Map<String, Object>>) wi.getParameter("listServiceCug"); // Added for keys
			isListCugs = (Boolean) wi.getParameter("isListCugs"); // Added for keys

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}
			sncodesCUG = reengagementService.getConfig().getPropValues("SNCODE_CUG").trim();

			// consume ws checkRatePlan
			CheckRateplanResponse response = reengagementService.checkRateplan(migrationMap, contractMap);
			this.logger.info("response : " + response.getIsSuccessful());
			if (response.getIsSuccessful().booleanValue()) {
				logger.info("responseRpCheck  : " + response.getIsSuccessful());
				// IncompatibleServices
				Service[] listOfDesactivateServices = response.getIncompatibleServices();
				logger.info("sizeOfDesactivateServices" + listOfDesactivateServices.length);
				if (listOfDesactivateServices != null && listOfDesactivateServices.length > 0) {
					desactivateServices = new ArrayList<>();
					listSncode = new ArrayList<>();
					for (Service service : listOfDesactivateServices) {
						logger.info("DesactivateServices In");
						Service newService = new Service();

						listSncode.add(service.getSncode());
						newService.setDes(service.getDes());
						newService.setSncode(service.getSncode());
						newService.setSpcode(service.getSpcode());

						logger.info(newService.getDes());
						if (service.getParam() != null) {
							List<Parameter> listParam = new ArrayList<>();
							for (Parameter param : service.getParam()) {
								Parameter paramMig = new Parameter();
								paramMig.setPrmId(param.getPrmId());
								paramMig.setPrmDes(param.getPrmDes());
								paramMig.setValueDes(param.getValueDes());
								paramMig.setValue(param.getValue());
								logger.info(paramMig.getPrmDes());
								logger.info(paramMig.getValue());
								logger.info(paramMig.getValueDes());
								listParam.add(paramMig);
							}
							Parameter[] p = new Parameter[listParam.size()];
							newService.setParam(listParam.toArray(p));
						}
						logger.info(service.getSncode());
						desactivateServices.add(newService);
					}
					logger.info("DesactivateServices Out");
					logger.info("SnCodes::" + listSncode);
					sizeDesactivateServices = listSncode.size();
					logger.info("SnCodes Size::" + sizeDesactivateServices);

				}
				//
				Service[] listOfMissingServices = response.getMissingServices();
				logger.info("SizeOfMissingServices::" + listOfMissingServices.length);
				if (listOfMissingServices != null && listOfMissingServices.length > 0) {
					missingServices = new ArrayList<>();
					listSncodeService = new ArrayList<>();
					for (Service service : listOfMissingServices) {
						logger.info("listOfMissingServices In");
						Service newService = new Service();
						listSncodeService.add(service.getSncode());
						newService.setDes(service.getDes());
						newService.setSncode(service.getSncode());
						newService.setSpcode(service.getSpcode());

						logger.info(newService.getDes());
						if (service.getParam() != null) {
							List<Parameter> listParam = new ArrayList<>();

							for (Parameter param : service.getParam()) {

								String comb = (param.getPrmType() + param.getPrmId()).trim();

								if (!this.exist(comb, listParam)) {
									logger.info("=> comb : " + param.getPrmType() + param.getPrmId());

									Parameter paramMig = new Parameter();
									paramMig.setPrmId(param.getPrmId());
									paramMig.setPrmDes(param.getPrmDes());
									if (param.getValue() != null) {
										paramMig.setValueDes(param.getValueDes());
										paramMig.setValue(param.getValue());
									} else {
										paramMig.setValueDes("");
										paramMig.setValue("");
									}
									paramMig.setPrmType(param.getPrmType()); // added
									logger.info(paramMig.getPrmDes());
									logger.info(paramMig.getValue());
									logger.info(paramMig.getValueDes());
									listParam.add(paramMig);
								}

							}

							Parameter[] p = new Parameter[listParam.size()];
							newService.setParam(listParam.toArray(p));
						}
						logger.info(service.getSncode());

						missingServices.add(newService);

					}
					logger.info("listOfMissingServices Out");
				}
				//
				// CommunServices
				Service[] listOfCommunServices = response.getCommunServices();
				logger.info("SizeOfCommunServices::" + listOfCommunServices.length);
				if (listOfCommunServices != null && listOfCommunServices.length > 0) {
					communServices = new ArrayList<>();
					listSncodeService = new ArrayList<>();
					for (Service service : listOfCommunServices) {
						logger.info("listOfCommunServices In");
						Service newService = new Service();
						listSncodeService.add(service.getSncode());
						newService.setDes(service.getDes());
						newService.setSncode(service.getSncode());
						logger.info(newService.getDes());
						if (service.getParam() != null) {
							List<Parameter> listParam = new ArrayList<>();
							for (Parameter param : service.getParam()) {

								String comb = (param.getPrmType() + param.getPrmId()).trim();

								if (!this.exist(comb, listParam)) {
									logger.info("=> comb : " + param.getPrmType() + param.getPrmId());

									Parameter paramMig = new Parameter();
									paramMig.setPrmId(param.getPrmId());
									paramMig.setPrmDes(param.getPrmDes());
									if (param.getValue() != null) {
										paramMig.setValueDes(param.getValueDes());
										paramMig.setValue(param.getValue());
									} else {
										paramMig.setValueDes("");
										paramMig.setValue("");
									}
									paramMig.setPrmType(param.getPrmType()); // added
									logger.info(paramMig.getPrmDes());
									logger.info(paramMig.getValue());
									logger.info(paramMig.getValueDes());
									listParam.add(paramMig);
								}

							}
							Parameter[] p = new Parameter[listParam.size()];
							newService.setParam(listParam.toArray(p));
						}
						logger.info(newService.getSncode());
						communServices.add(newService);
					}
					logger.info("listOfCommunServices Out");
				}
				//
				if ((listOfMissingServices == null || listOfMissingServices.length == 0)
						&& (listOfCommunServices != null && listOfCommunServices.length > 0)) {
					logger.info("missingServices = communServices");
					missingServices = new ArrayList<>();
					missingServices.addAll(communServices);
				}

				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputCheckRateplan(waitFailure, taskName,
						listSncode, sizeDesactivateServices, missingServices, communServices));
			} else {
				this.logger.info(
						"<= CheckRatePlanWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());
				this.logger.info("IsSuccessful :" + response.getIsSuccessful());
				this.logger.info("BscsErrorCode : " + response.getBscsErrorCode());
				this.logger.info("Comment : " + response.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputCheckRateplan(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger
					.info("<= CheckRatePlanWorkItemHandler with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputCheckRateplan(retryNbre, waitFailure, taskName));
		}
	}

	public boolean exist(String varr, List<Parameter> paramList) {

		for (Parameter p : paramList) {

			if ((p.getPrmType() + p.getPrmId()).trim().equals(varr.trim())) {
				return true;
			}
		}
		return false;
	}

}
