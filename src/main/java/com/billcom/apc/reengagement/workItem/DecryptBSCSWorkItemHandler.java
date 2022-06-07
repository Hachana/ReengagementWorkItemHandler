package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.ContractListBean;
import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.MigrationBean;
import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.OptionsToAdd;
import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.OptionsTodelete;
import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.UpdateGrhBean;
import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.UpdateOption;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Arij dhahbi updated by Fethi Hachana
 *
 */
public class DecryptBSCSWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(DecryptBSCSWorkItemHandler.class);

	private List<Map<String, Object>> listContractOfGrh;
	private List<Map<String, Object>> listContractOfGrhWithoutMigration;

	public void abortWorkItem(WorkItem wi, WorkItemManager wim) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> DecryptBSCSWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();
		List<Long> listcontracts;
		Integer sizeListContractOfGrh = 0;
		Integer sizeListContractOfGrhWithoutMig = 0;
		List<Map<String, Object>> listMigrationMap;
		List<Map<String, Object>> listUpdateOptionMap;
		List<Map<String, Object>> listMigrationMapOutput;
		List<Map<String, Object>> listUpdateOptionMapOutput;
		Map<String, Object> grhMap;
		String retriesConfigNbre = null;
		boolean isMigration;
		boolean isUpdateOption;
		boolean isGrh;
		String taskName = "";
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		String retryWaiting = "";
		String pendingWaiting = "";
		String waitingLot;

		Integer orderId;
		Integer nbreContratLot;

		List<ContractDetails> listMig = new ArrayList<>();
		List<ContractDetails> listGRH = new ArrayList<>();
		boolean toSuccessMig = true;
		boolean toSuccessUpdateOpt = true;
		boolean toSuccessGrh = true;

		retriesConfigNbre = reengagementService.getConfig().getPropValues("retriesConfigNbre");

		logger.info("Reengagement in progress... Decrypt BSCS");

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("DecryptWIH").trim();
			retryWaiting = reengagementService.getConfig().getPropValues("retryWaiting").trim();
			pendingWaiting = reengagementService.getConfig().getPropValues("pendingWaiting").trim();
			waitingLot = reengagementService.getConfig().getPropValues("waitingLot").trim();
			nbreContratLot = Integer.parseInt(reengagementService.getConfig().getPropValues("nbreContratLot").trim());
			logger.info("taskName::" + taskName);

			listMigrationMap = (List<Map<String, Object>>) wi.getParameter("listMigrationMap");
			listUpdateOptionMap = (List<Map<String, Object>>) wi.getParameter("listUpdateOptionMap");
			grhMap = (Map<String, Object>) wi.getParameter("grhMap");
			listMigrationMapOutput = new ArrayList<>();
			listUpdateOptionMapOutput = new ArrayList<>();
			listcontracts = new ArrayList<>();
			orderId = (int) wi.getProcessInstanceId();

			if (CollectionUtils.isEmpty(listMigrationMap)) {
				isMigration = false;
			} else {
				isMigration = true;
			}
			if (CollectionUtils.isEmpty(listUpdateOptionMap)) {
				isUpdateOption = false;
			} else {
				isUpdateOption = true;
			}
			if (MapUtils.isEmpty(grhMap)) {
				isGrh = false;
			} else {
				isGrh = true;
			}

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// consume ws getBscsOfferAndServciesFromCrmDemande

			if (isMigration) {

				for (Map<String, Object> map : listMigrationMap) {

					GetBscsOfferAndServciesFromCrmDemandeRequest request = new GetBscsOfferAndServciesFromCrmDemandeRequest();

					MigrationBean migrationBean = new ObjectMapper().convertValue(map, MigrationBean.class);

					request.setOffreInit(migrationBean.getOffreInit());
					request.setOffreTarget(migrationBean.getOffreTarget());
					request.setListCoCode(getListCocode(migrationBean.getListContractListBean()));
					if (migrationBean.getListOptionsToAdd() != null && migrationBean.getListOptionsToAdd().length > 0) {
						this.logger
								.info("migrationBean.getListOptionsToAdd() : " + migrationBean.getListOptionsToAdd());

						request.setOptionsToAdd(getOption(migrationBean.getListOptionsToAdd()));
					}

					if (migrationBean.getListOptionsToDelete() != null
							&& migrationBean.getListOptionsToDelete().length > 0) {
						this.logger.info(
								"migrationBean.getListOptionsToDelete() : " + migrationBean.getListOptionsToDelete());

						request.setOptionsTodelete(getOptionDelete(migrationBean.getListOptionsToDelete()));
					}

					this.logger.info("request Migration =" + new ObjectMapper().convertValue(request, Map.class));

					GetBscsOfferAndServciesFromCrmDemandeResponse response = reengagementService
							.getBscsOfferAndServciesFromCrmDemande(request);

					this.logger.info("responseMigration : " + response.getIsSuccessful());
					if (response.getIsSuccessful().booleanValue()) {
						toSuccessMig = true;

						Map<String, Object> outputMap = new HashMap<>();
						outputMap.put("inputMigration", map);
						outputMap.put("responseMigration", new ObjectMapper().convertValue(response, Map.class));

						listMigrationMapOutput.add(outputMap);

						this.logger.info("listMigrationMapOutput=" + listMigrationMapOutput);
						for (ContractDetails contractDetail : response.getContractDetails()) {
							listcontracts.add(contractDetail.getCoId());
							listMig.add(contractDetail);

						}

					} else {
						toSuccessMig = false;

						this.logger.info("respone =" + response.getIsSuccessful());
						this.logger.info("bscsErrorCode  = " + response.getBscsErrorCode());
						this.logger.info("comment = " + response.getComment());

					}

				}
			}

			if (isUpdateOption) {

				for (Map<String, Object> map : listUpdateOptionMap) {

					GetBscsOfferAndServciesFromCrmDemandeRequest request = new GetBscsOfferAndServciesFromCrmDemandeRequest();

					UpdateOption updateOption = new ObjectMapper().convertValue(map, UpdateOption.class);

					request.setOffreTarget(updateOption.getOffreTarget());
					request.setListCoCode(getListCocode(updateOption.getListContractListBean()));
					if (updateOption.getListOptionsToAdd() != null && updateOption.getListOptionsToAdd().length > 0) {
						this.logger.info("updateOption.getListOptionsToAdd()=" + updateOption.getListOptionsToAdd());

						request.setOptionsToAdd(getOption(updateOption.getListOptionsToAdd()));
					}
					if (updateOption.getListOptionsToDelete() != null
							&& updateOption.getListOptionsToDelete().length > 0)

					{
						this.logger
								.info("updateOption.getListOptionsToDelete()=" + updateOption.getListOptionsToDelete());

						request.setOptionsTodelete(getOptionDelete(updateOption.getListOptionsToDelete()));
					}

					this.logger.info("request updateOption=" + new ObjectMapper().convertValue(request, Map.class));

					GetBscsOfferAndServciesFromCrmDemandeResponse response = reengagementService
							.getBscsOfferAndServciesFromCrmDemande(request);

					if (response.getIsSuccessful().booleanValue()) {
						toSuccessUpdateOpt = true;

						Map<String, Object> outputMap = new HashMap<>();
						outputMap.put("inputUpdateOption", map);
						outputMap.put("responseUpdateOption", new ObjectMapper().convertValue(response, Map.class));

						listUpdateOptionMapOutput.add(outputMap);

						this.logger.info("listUpdateOptionMapOutput=" + listUpdateOptionMapOutput);

						for (ContractDetails contractDetail : response.getContractDetails()) {
							listcontracts.add(contractDetail.getCoId());
						}

					} else {
						toSuccessUpdateOpt = false;

						this.logger.info("respone=" + response.getIsSuccessful());
						this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
						this.logger.info("comment= " + response.getComment());

					}

				}

			}

			if (isGrh) {

				listContractOfGrh = new ArrayList<>();
				listContractOfGrhWithoutMigration = new ArrayList<>();
				Boolean exist = false;
				GetBscsOfferAndServciesFromCrmDemandeRequest request = new GetBscsOfferAndServciesFromCrmDemandeRequest();

				request.setListCoCode(getListCocode(
						new ObjectMapper().convertValue(grhMap, UpdateGrhBean.class).getListContractListBean()));

				this.logger.info("request=" + new ObjectMapper().convertValue(request, Map.class));

				GetBscsOfferAndServciesFromCrmDemandeResponse response = reengagementService
						.getBscsOfferAndServciesFromCrmDemande(request);

				if (response.getIsSuccessful().booleanValue()) {
					toSuccessGrh = true;

					ContractDetails[] contractDetails = response.getContractDetails();

					for (ContractDetails contractDetail : contractDetails) {
						listContractOfGrh.add(new ObjectMapper().convertValue(contractDetail, Map.class));
						listcontracts.add(contractDetail.getCoId());
						listGRH.add(contractDetail);
					}

					if (isMigration) {

						for (ContractDetails elemGRH : listGRH) {

							for (ContractDetails c : listMig) {

								if (c.getCoId().equals(elemGRH.getCoId())) {
									exist = true;
								}
							}

							if (!exist.booleanValue()) {
								listContractOfGrhWithoutMigration
										.add(new ObjectMapper().convertValue(elemGRH, Map.class));
							}

							exist = false;
						}

					} else {
						for (ContractDetails elemGRH : listGRH) {
							listContractOfGrhWithoutMigration.add(new ObjectMapper().convertValue(elemGRH, Map.class));

						}

					}

					sizeListContractOfGrh = listContractOfGrh.size();
					sizeListContractOfGrhWithoutMig = listContractOfGrhWithoutMigration.size();
					this.logger.info("listContractOfGrh=" + listContractOfGrh);
					this.logger.info("listContractOfGrhWithoutMigration size =" + sizeListContractOfGrhWithoutMig);

					this.logger.info("listContractOfGrhWithoutMigration=" + listContractOfGrhWithoutMigration);

				} else {
					toSuccessGrh = false;

					this.logger.info("respone=" + response.getIsSuccessful());
					this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
					this.logger.info("comment= " + response.getComment());

				}

			}

			if (toSuccessGrh && toSuccessMig && toSuccessUpdateOpt) {
				this.logger.info("<= DecryptBSCSWorkItemHandler out: Process Id:: " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.successGetBscsOfferAndServciesFromCrmDemande(waitFailure, taskName,

								listMigrationMapOutput, listUpdateOptionMapOutput, listContractOfGrh,
								sizeListContractOfGrh, retriesConfigNbre, retryWaiting, pendingWaiting, listcontracts,
								orderId, listContractOfGrhWithoutMigration, sizeListContractOfGrhWithoutMig, waitingLot,
								nbreContratLot));
			} else {
				this.logger.info(
						"<= DecryptBSCSWorkItemHandler out with error : Process Id:: " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(), reengagementService.failGetBscsOfferAndServciesFromCrmDemande(
						retryNbre, waitFailure, retriesConfigNbre, retryWaiting, pendingWaiting));
			}

		} catch (Exception e) {
			this.logger
					.info("<= DecryptBSCSWorkItemHandler out with error : Process Id:: " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failGetBscsOfferAndServciesFromCrmDemande(retryNbre,
					waitFailure, retriesConfigNbre, retryWaiting, pendingWaiting));
		}

	}

	private String[] getListCocode(ContractListBean[] listContractListBean) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < listContractListBean.length; i++) {
			result.add(listContractListBean[i].getCocode());
		}
		return result.toArray(new String[0]);
	}

	public String[] getOption(OptionsToAdd[] listOptionsToAdd) {
		List<String> listOption = new ArrayList<>();
		for (int i = 0; i < listOptionsToAdd.length; i++) {
			listOption.add(listOptionsToAdd[i].getOption());
		}
		return listOption.toArray(new String[0]);
	}

	public String[] getOptionDelete(OptionsTodelete[] listOptionsToDelete) {
		List<String> listOptionDelete = new ArrayList<>();
		for (int i = 0; i < listOptionsToDelete.length; i++) {
			listOptionDelete.add(listOptionsToDelete[i].getOption());
		}
		return listOptionDelete.toArray(new String[0]);
	}

}