package com.billcom.apc.reengagement.workItem;

import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.CommitmentHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.ContractReference;

import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.GetCommitmentHistoryRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.GetCommitmentHistoryResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.AddGrhRequestResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class MajDfeWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(MajDfeWorkItemHandler.class);
	private Long reqId;
	private String exist = "";
	private Boolean found = false;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		this.logger.info("=> MajDfeWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		Map<String, Object> grhMap;
		Map<String, Object> contractMapOfGrh;
		Map<String, Object> contractMap;
		List<Map<String, Object>> listContractOfGrh;

		String taskName = "";
		this.logger.info("Reengagement in progress...MAJ Dfe");
		ReengagementService reengagementService = new ReengagementServiceImp();

		reengagementService.printWorkItem(this.logger, wi);
		String waitFailureDFE = "0s";
		Integer retryNbreDFE = 0;
		AutoRecycle autoRecycle = new AutoRecycle();

		try {
			// retryNbre
			retryNbreDFE = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbreDFE"));

			grhMap = (Map<String, Object>) wi.getParameter("grhMap");
			contractMapOfGrh = (Map<String, Object>) wi.getParameter("contractMapOfGrh");
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			listContractOfGrh = (List<Map<String, Object>>) wi.getParameter("listContractOfGrh");

			Long custId = Long.parseLong((String) wi.getParameter("custId"));
			String user = reengagementService.getConfig().getPropValues("user").trim();
			taskName = reengagementService.getConfig().getPropValues("majDfeWIH");
			logger.info("taskName::" + taskName);

			// waitfailure
			waitFailureDFE = autoRecycle.palierwaittime(waitFailureDFE, retryNbreDFE);
			logger.debug("waitFailure from config = " + waitFailureDFE);
			ContractDetails contract;

			// consume ws addGrh

			this.logger.info("==> Get Commitement IN ");
			CommitmentHandlingSoapBindingStub commitmentHandlingSoapBindingStub = reengagementService.getConfig()
					.consumeCommitmentWsdl();
			GetCommitmentHistoryRequest getCommitmentHistoryRequest = new GetCommitmentHistoryRequest();
			ContractReference contractReference = new ContractReference();
			if (contractMapOfGrh != null)
				contractReference.setDirNum((String) contractMapOfGrh.get("msisdn"));
			else if (contractMap != null)
				contractReference.setDirNum((String) contractMap.get("msisdn"));

			getCommitmentHistoryRequest.setContractReference(contractReference);
			getCommitmentHistoryRequest.setLatestOccurenceFlag(true);
			GetCommitmentHistoryResponse getCommitmentHistoryResponse = commitmentHandlingSoapBindingStub
					.getCommitmentHistory(getCommitmentHistoryRequest);

			this.logger
					.info("<== Get Commitement OUT with response :" + getCommitmentHistoryResponse.getIsSuccessful());

			if (getCommitmentHistoryResponse.getIsSuccessful().booleanValue()) {

				if (getCommitmentHistoryResponse.getFullCommitmentServices() != null
						&& getCommitmentHistoryResponse.getFullCommitmentServices()[0].getGrhRequest() != null
						&& getCommitmentHistoryResponse.getFullCommitmentServices()[0].getGrhRequest()
								.getRequestAll() != null
						&& getCommitmentHistoryResponse.getFullCommitmentServices()[0].getGrhRequest().getRequestAll()
								.getRequestId() != null) {

					reqId = getCommitmentHistoryResponse.getFullCommitmentServices()[0].getGrhRequest().getRequestAll()
							.getRequestId();
				} else {
					reqId = null;
					this.logger.info("No Commitement getted !!");

				}
				this.logger.info(" Request Id = " + reqId);

			}

			if (contractMapOfGrh != null) {
				contract = new ObjectMapper().convertValue(contractMapOfGrh, ContractDetails.class);
				this.logger.info(" Call  GRH without Migration !");
				this.logger.info(" Contract : " + contract.getContractCode());

				AddGrhRequestResponse addGrhRequestResponse = reengagementService.addGrh(logger, grhMap, contract,
						custId, user, reqId);

				this.logger.info("response :: " + new ObjectMapper().convertValue(addGrhRequestResponse, Map.class));

				if (addGrhRequestResponse.getIsSuccessful().booleanValue()) {
					exist = "Grh";
					wim.completeWorkItem(wi.getId(),
							reengagementService.sucessOutputModifierDFE(waitFailureDFE, taskName, exist));

				} else {
					logger.info("error : " + addGrhRequestResponse.getBscsErrorCode() + " "
							+ addGrhRequestResponse.getComment());

					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputModifierDFE(retryNbreDFE, waitFailureDFE, taskName, exist));
				}

			}

			else if (contractMap != null) {

				contract = new ObjectMapper().convertValue(contractMap, ContractDetails.class);
				if (listContractOfGrh != null) {
					for (Map<String, Object> map : listContractOfGrh) {

						if (map.get("msisdn").equals(contract.getMsisdn())) {
							this.logger.info(" Call  GRH in Migration !");
							found = true;
							break;

						}

					}
					if (found.booleanValue()) {
						AddGrhRequestResponse addGrhRequestResponse = reengagementService.addGrh(logger, grhMap,
								contract, custId, user, reqId);

						this.logger.info(
								"response:: " + new ObjectMapper().convertValue(addGrhRequestResponse, Map.class));

						if (addGrhRequestResponse.getIsSuccessful().booleanValue()) {
							exist = "Grh";

							wim.completeWorkItem(wi.getId(),
									reengagementService.sucessOutputModifierDFE(waitFailureDFE, taskName, exist));

						} else {
							logger.info("error :: " + addGrhRequestResponse.getBscsErrorCode() + " "
									+ addGrhRequestResponse.getComment());
							wim.completeWorkItem(wi.getId(), reengagementService.failOutputModifierDFE(retryNbreDFE,
									waitFailureDFE, taskName, exist));
						}
					} else {
						this.logger.info("  call forced in Migration !" + contract.getCoId());
						AddGrhRequestResponse addGrhRequestResponse = reengagementService.addGrh(logger, grhMap,
								contract, custId, user, reqId);

						this.logger
								.info("response=" + new ObjectMapper().convertValue(addGrhRequestResponse, Map.class));

						if (addGrhRequestResponse.getIsSuccessful().booleanValue()) {
							exist = "Grh";

							wim.completeWorkItem(wi.getId(),
									reengagementService.sucessOutputModifierDFE(waitFailureDFE, taskName, exist));

						} else {
							logger.info("error: " + addGrhRequestResponse.getBscsErrorCode() + " "
									+ addGrhRequestResponse.getComment());
							wim.completeWorkItem(wi.getId(), reengagementService.failOutputModifierDFE(retryNbreDFE,
									waitFailureDFE, taskName, exist));
						}
					}
				}

				else {
					this.logger.info("  call forced in Migration !" + contract.getCoId());
					AddGrhRequestResponse addGrhRequestResponse = reengagementService.addGrh(logger, grhMap, contract,
							custId, user, reqId);

					this.logger.info("response=" + new ObjectMapper().convertValue(addGrhRequestResponse, Map.class));

					if (addGrhRequestResponse.getIsSuccessful().booleanValue()) {
						exist = "Grh";

						wim.completeWorkItem(wi.getId(),
								reengagementService.sucessOutputModifierDFE(waitFailureDFE, taskName, exist));

					} else {
						logger.info("error: " + addGrhRequestResponse.getBscsErrorCode() + " "
								+ addGrhRequestResponse.getComment());
						wim.completeWorkItem(wi.getId(), reengagementService.failOutputModifierDFE(retryNbreDFE,
								waitFailureDFE, taskName, exist));
					}

				}

			}

		} catch (Exception e) {
			logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputModifierDFE(retryNbreDFE, waitFailureDFE, taskName, exist));
		}
	}

}