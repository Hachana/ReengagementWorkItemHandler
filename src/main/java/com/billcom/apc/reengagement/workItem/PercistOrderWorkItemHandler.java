package com.billcom.apc.reengagement.workItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.InjectGpsBean;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.PercistReengagementOrderResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementBean;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.model.UpdateOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Arij dhahbi updated by Fethi Hachana
 */

public class PercistOrderWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(PercistOrderWorkItemHandler.class);
	List<ReengagementBean> reengagementBeans;

	private String taskName;
	private String dueDate;
	private String osmReference;
	private String custId;
	private Integer orderId;
	private String canal;
	private String group;
	private String jbpmReference;

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		logger.info("=> PercistOrderWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();
		List<Map<String, Object>> listMigrationMapOutput;
		List<Map<String, Object>> listUpdateOptionMapOutput;
		List<Map<String, Object>> listRbfMap;
		List<Map<String, Object>> listContractOfGrh;
		Map<String, Object> grhMap;
		Map<String, Object> gpsMap;
		boolean isMigration;
		boolean isUpdateOption;
		boolean isInjectionRBF;
		boolean isInjectionGPS;
		boolean isGrh;
		String execTime;
		String dueDateTimer;
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("percistOrderWIH");
			logger.info("taskName::" + taskName);
			dueDateTimer = "0s";
			execTime = reengagementService.getConfig().getPropValues("dueDateTimer").trim();

			group = reengagementService.getConfig().getPropValues("group").trim();
			logger.info("Reengagement in progress... Percist Order");
			listUpdateOptionMapOutput = (List<Map<String, Object>>) wi.getParameter("listUpdateOptionMapOutput");
			listMigrationMapOutput = (List<Map<String, Object>>) wi.getParameter("listMigrationMapOutput");
			listContractOfGrh = (List<Map<String, Object>>) wi.getParameter("listContractOfGrh");
			listRbfMap = (List<Map<String, Object>>) wi.getParameter("listRbfMap");
			grhMap = (Map<String, Object>) wi.getParameter("grhMap");
			gpsMap = (Map<String, Object>) wi.getParameter("gpsMap");

			if (CollectionUtils.isEmpty(listMigrationMapOutput)) {
				isMigration = false;
			} else {
				isMigration = true;
			}
			if (CollectionUtils.isEmpty(listUpdateOptionMapOutput)) {
				isUpdateOption = false;
			} else {
				isUpdateOption = true;
			}
			if (MapUtils.isEmpty(gpsMap)) {
				isInjectionGPS = false;
			} else {
				isInjectionGPS = true;
			}
			if (CollectionUtils.isEmpty(listRbfMap)) {
				isInjectionRBF = false;
			} else {
				isInjectionRBF = true;
			}
			if (MapUtils.isEmpty(grhMap) || CollectionUtils.isEmpty(listContractOfGrh)) {
				isGrh = false;
			} else {
				isGrh = true;
			}

			orderId = (int) wi.getProcessInstanceId();
			osmReference = (String) wi.getParameter("osmReference");
			dueDate = (String) wi.getParameter("dueDate");
			custId = (String) wi.getParameter("custId");
			canal = (String) wi.getParameter("canal");
			jbpmReference = (String) wi.getParameter("JBPM_Ref_Rengagement");
			logger.info("jbpmReference = " + jbpmReference);

			/*
			 * For Delayed Timer
			 */

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date date;
			String hour = execTime.split(":")[0];
			String min = execTime.split(":")[1];
			logger.info("execTime min = " + execTime);

			logger.info("execTime hour = " + Integer.parseInt(hour));
			logger.info("execTime min = " + Integer.parseInt(min));

			date = format.parse(dueDate);
			Calendar dueDateCal = Calendar.getInstance();
			dueDateCal.setTime(date);
			logger.info("dueDateCal = " + dueDateCal.getTime());
			Calendar dateNow = Calendar.getInstance();
			logger.info("dateNowCal = " + dateNow.getTime());
			if (dueDateCal.compareTo(dateNow) > 0) {
				boolean l = dueDateCal.compareTo(dateNow) > 0;
				logger.info("Planifié  : " + l);

				dueDateCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				dueDateCal.set(Calendar.MINUTE, Integer.parseInt(min));
				logger.info("new dueDate = " + dueDateCal.getTime());
				long remainingDays = ((dueDateCal.getTimeInMillis() - dateNow.getTimeInMillis()) / 1000);
				dueDateTimer = remainingDays + "s";
				logger.info("dueDateTimer = " + dueDateTimer);

			}

			/*
			 * 
			 * 
			 */

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			reengagementBeans = new ArrayList<>();

			if (isGrh) {

				for (Map<String, Object> map : listContractOfGrh) {

					ContractDetails contractDetails = new ObjectMapper().convertValue(map, ContractDetails.class);
					ReengagementBean reengagementBean = new ReengagementBean();
					setParams(reengagementBean);
					reengagementBean.setCOID(String.valueOf(contractDetails.getCoId()));
					reengagementBean.setCOCODE(contractDetails.getContractCode());

					reengagementBean.setMSISDN(contractDetails.getMsisdn());
					reengagementBean.setOFFERINIT(" ");
					reengagementBean.setOFFERTARGET(" ");
					reengagementBean.setDFEDESCRIPTION("DFE");
					reengagementBean.setDFESTATUS(1);
					reengagementBean.setMIGRATIONDESCRIPTION(" ");
					reengagementBean.setMIGRATIONSTATUS(0);
					reengagementBean.setOPTIONDESCRIPTION(" ");
					reengagementBean.setOPTIONSTATUS(0);
					reengagementBean.setTMCODEINIT(0);
					reengagementBean.setTMCODETARGET(0);
					reengagementBean.setCAGNOTTEDESCRIPTION(" ");
					reengagementBean.setCAGNOTTESTATUS(0);
					reengagementBean.setRBFDESCRIPTION(" ");
					reengagementBean.setRBFSTATUS(0);
					reengagementBean.setCUSTCODE(contractDetails.getCustomerCode());
					reengagementBean.setCONTRACTSTATUS("Inprogress");
					reengagementBeans.add(reengagementBean);
				}
			}

			if (isMigration) {
				for (Map<String, Object> map : listMigrationMapOutput) {
					MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(map,
							MigrationOutputBean.class);
					GetBscsOfferAndServciesFromCrmDemandeResponse response = migrationOutputBean.getResponseMigration();

					for (int i = 0; i < response.getContractDetails().length; i++) {

						ReengagementBean reengagementBean = new ReengagementBean();
						setParams(reengagementBean);
						reengagementBean.setCOID(String.valueOf(response.getContractDetails()[i].getCoId()));
						reengagementBean.setCOCODE(response.getContractDetails()[i].getContractCode());
						reengagementBean.setMSISDN(response.getContractDetails()[i].getMsisdn());
						reengagementBean.setOFFERINIT(
								migrationOutputBean.getResponseMigration().getOffreInitBscs().getOffreDes());
						reengagementBean.setOFFERTARGET(
								migrationOutputBean.getResponseMigration().getOffreTargetBscs().getOffreDes());
						reengagementBean.setMIGRATIONDESCRIPTION("Migration");
						reengagementBean.setMIGRATIONSTATUS(1);
						reengagementBean.setOPTIONDESCRIPTION(" ");
						reengagementBean.setOPTIONSTATUS(0);
						reengagementBean.setDFEDESCRIPTION(" ");
						reengagementBean.setDFESTATUS(0);
						reengagementBean.setTMCODETARGET(0);
						reengagementBean.setTMCODEINIT(response.getOffreInitBscs().getTmcode().intValue());
						reengagementBean.setTMCODETARGET(response.getOffreTargetBscs().getTmcode().intValue());
						reengagementBean.setCAGNOTTEDESCRIPTION(" ");
						reengagementBean.setCAGNOTTESTATUS(0);
						reengagementBean.setRBFDESCRIPTION(" ");
						reengagementBean.setRBFSTATUS(0);
						reengagementBean.setCUSTCODE(response.getContractDetails()[i].getCustomerCode());
						reengagementBean.setCONTRACTSTATUS("Inprogress");
						reengagementBeans.add(reengagementBean);
					}
				}
			}

			if (isUpdateOption) {
				for (Map<String, Object> map : listUpdateOptionMapOutput) {

					UpdateOutputBean updateOutputBean = new ObjectMapper().convertValue(map, UpdateOutputBean.class);
					GetBscsOfferAndServciesFromCrmDemandeResponse response = updateOutputBean.getResponseUpdateOption();
					for (int i = 0; i < response.getContractDetails().length; i++) {

						ReengagementBean reengagementBean = new ReengagementBean();
						setParams(reengagementBean);
						reengagementBean.setCOID(String.valueOf(response.getContractDetails()[i].getCoId()));
						reengagementBean.setCOCODE(response.getContractDetails()[i].getContractCode());

						reengagementBean.setMSISDN(response.getContractDetails()[i].getMsisdn());
						reengagementBean.setOFFERINIT(" ");
						reengagementBean.setOFFERTARGET(
								updateOutputBean.getResponseUpdateOption().getOffreTargetBscs().getOffreDes());
						reengagementBean.setOPTIONDESCRIPTION("Update Option");
						reengagementBean.setOPTIONSTATUS(1);
						reengagementBean.setMIGRATIONDESCRIPTION(" ");
						reengagementBean.setMIGRATIONSTATUS(0);
						reengagementBean.setDFEDESCRIPTION(" ");
						reengagementBean.setDFESTATUS(0);
						reengagementBean.setTMCODEINIT(0);
						reengagementBean.setTMCODETARGET(response.getOffreTargetBscs().getTmcode().intValue());
						reengagementBean.setCAGNOTTEDESCRIPTION(" ");
						reengagementBean.setCAGNOTTESTATUS(0);
						reengagementBean.setRBFDESCRIPTION(" ");
						reengagementBean.setRBFSTATUS(0);
						reengagementBean.setCUSTCODE(response.getContractDetails()[i].getCustomerCode());
						reengagementBean.setCONTRACTSTATUS("Inprogress");
						reengagementBeans.add(reengagementBean);
					}
				}
			}

			if (isInjectionGPS) {
				InjectGpsBean gpsBean;

				gpsBean = new ObjectMapper().convertValue(gpsMap, InjectGpsBean.class);
				ReengagementBean reengagementBean = new ReengagementBean();
				setParams(reengagementBean);
				reengagementBean.setCAGNOTTEDESCRIPTION("GPS");
				reengagementBean.setCAGNOTTESTATUS(1);
				reengagementBean.setCOID(" ");
				reengagementBean.setCOCODE(" ");

				reengagementBean.setMSISDN(" ");
				reengagementBean.setOFFERINIT(" ");
				reengagementBean.setOFFERTARGET(" ");
				reengagementBean.setOPTIONDESCRIPTION(" ");
				reengagementBean.setOPTIONSTATUS(0);
				reengagementBean.setMIGRATIONDESCRIPTION(" ");
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setDFEDESCRIPTION(" ");
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setRBFDESCRIPTION(" ");
				reengagementBean.setRBFSTATUS(0);
				reengagementBean.setCUSTCODE("");
				reengagementBean.setCONTRACTSTATUS(" ");
				if (gpsBean.getAmount() != null) {
					reengagementBean.setGpsAmount(gpsBean.getAmount());
				}
				reengagementBeans.add(reengagementBean);

			}
			if (isInjectionRBF) {
				ReengagementBean reengagementBean = new ReengagementBean();
				setParams(reengagementBean);
				reengagementBean.setRBFDESCRIPTION("RBF");
				reengagementBean.setRBFSTATUS(1);
				reengagementBean.setCOID(" ");
				reengagementBean.setCOCODE(" ");

				reengagementBean.setMSISDN(" ");
				reengagementBean.setOFFERINIT(" ");
				reengagementBean.setOFFERTARGET(" ");
				reengagementBean.setOPTIONDESCRIPTION(" ");
				reengagementBean.setOPTIONSTATUS(0);
				reengagementBean.setMIGRATIONDESCRIPTION(" ");
				reengagementBean.setMIGRATIONSTATUS(0);
				reengagementBean.setDFEDESCRIPTION(" ");
				reengagementBean.setDFESTATUS(0);
				reengagementBean.setTMCODEINIT(0);
				reengagementBean.setTMCODETARGET(0);
				reengagementBean.setCAGNOTTEDESCRIPTION(" ");
				reengagementBean.setCAGNOTTESTATUS(0);
				reengagementBean.setCUSTCODE("");
				reengagementBean.setCONTRACTSTATUS(" ");
				reengagementBeans.add(reengagementBean);
			}

			PercistReengagementOrderResponse response = reengagementService.percistReengagementOrder(reengagementBeans);

			if (response.isFinished()) {
				logger.info("All reengagements are percisted");
				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputPercistReengagementOrder(waitFailure,
						taskName, isMigration, isUpdateOption, isInjectionRBF, isInjectionGPS, dueDateTimer));
			} else {
				logger.info("percist failed ");
				this.logger.info("respone=" + response.isFinished());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputPercistReengagementOrder(retryNbre, waitFailure, taskName));
			}

		} catch (Exception e) {
			logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputPercistReengagementOrder(retryNbre, waitFailure, taskName));
		}
	}

	private void setParams(ReengagementBean reengagementBean) {

		reengagementBean.setDATESTART(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		reengagementBean.setORDERID(orderId);
		reengagementBean.setCONTRACTORDERID(0);
		reengagementBean.setUSERID(canal);
		reengagementBean.setUSERGROUP(group);
		reengagementBean.setReengComment("Migration suite réengagement");
		if (osmReference != null) {
			reengagementBean.setOSMREFERENCE(osmReference);
		} else {
			reengagementBean.setOSMREFERENCE(" ");
		}

		if (jbpmReference != null) {
			reengagementBean.setJBPMRefRengagement(jbpmReference);
		} else {
			reengagementBean.setJBPMRefRengagement(" ");

		}

		if (dueDate != null) {
			reengagementBean.setDUEDATE(dueDate);

		} else {
			reengagementBean.setDUEDATE(" ");
		}

		if (custId != null) {
			reengagementBean.setCUSTID(custId);
		} else {
			reengagementBean.setCUSTID(" ");
		}
		reengagementBean.setTask(taskName);
		reengagementBean.setORDERDESCRIPTION("Reengagement Commande");
		reengagementBean.setStatus(1);
		reengagementBean.setSTATUTORDER(1);
		reengagementBean.setOLDCOCODE(" ");
		reengagementBean.setOLDCOID(" ");
		reengagementBean.setDATEEND(" ");

	}

}
