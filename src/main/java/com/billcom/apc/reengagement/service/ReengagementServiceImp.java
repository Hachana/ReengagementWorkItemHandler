package com.billcom.apc.reengagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.UpdateGrhBean;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.BalanceManagerV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.GetBalanceAndDateBeanInputV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.GetBalanceAndDateBeanOutputV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.OptionV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.SubscriberV2;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.APCManager;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.AdjustBalanceRequest;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.AdjustBalanceResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.AddFaFRequest;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.AddFaFResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.FaFManager;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.RemoveFaFRequest;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.RemoveFaFResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.Subscriber;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.PercistReengagementOrderRequest;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.PercistReengagementOrderResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementBean;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementManager;

import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.UpdateReengagementOrderRequest;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.UpdateReengagementOrderResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.APCHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamterResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.AddCugRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.BalanceHistRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ChangeRatePlanRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ChangeRatePlanResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CheckRateplanRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CheckRateplanResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CoIdRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CreateContractRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CreateContractResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.GetCategoryRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.GetCategoryResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.HasPendingRequestResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Parameter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.UnexpectedErrorFault;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ComLhsWsBeansV2Contract_services_addServicesBeanIn;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ContractServicesAddBeanIn;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ContractServicesAddBeanOut;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ContractServicesAddServiceSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.In0;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.FaFHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.LimitAddRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.LimitAddResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.AddGrhRequestRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.AddGrhRequestResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.CommitmentEntityType;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.CommitmentRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.GrhHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.GrhRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.OperationType;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.ProcessingMode;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.RequestAll;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.StatusDefinition;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.DeletePromoRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.DeletePromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.GetCustomerPromoRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.GetCustomerPromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetOrcreateCugForcustomerRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetOrcreateCugForcustomerResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ReengagementHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBeanCrm;
import com.billcom.apc.generatedSOAPReengagement.gps.AddUpdateJackpotRequest;
import com.billcom.apc.generatedSOAPReengagement.gps.AddUpdateJackpotResponse;
import com.billcom.apc.generatedSOAPReengagement.gps.AuthenticationData;
import com.billcom.apc.generatedSOAPReengagement.gps.JackpotWebServiceWebServicePortBindingStub;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.utils.ReengagementConfigHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otn.dsi.ws.AddFaFBI;
import com.otn.dsi.ws.AddFaFBO;
import com.otn.dsi.ws.FaFManagerPortBindingStub;
import com.otn.dsi.ws.ListFaFBI;
import com.otn.dsi.ws.ListFaFBO;
import com.otn.dsi.ws.RemoveFaFBI;
import com.otn.dsi.ws.RemoveFaFBO;

/**
 * @author arij.dhahbi
 *
 */

public class ReengagementServiceImp implements ReengagementService {

	private final ReengagementConfigHandler config = new ReengagementConfigHandler();

	private Logger logger = Logger.getLogger(ReengagementServiceImp.class);

	public ReengagementConfigHandler getConfig() {
		return config.getInstance();
	}

	public void printWorkItem(Logger logger, WorkItem wi) {

		Map<String, Object> paraList = wi.getParameters();
		logger.info("Inputs :: " + wi.getProcessInstanceId());
		for (Map.Entry<String, Object> entry : paraList.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (!(value instanceof java.util.ArrayList) && !key.equals("reengagementMap")) {
				logger.info(key + " = " + value.toString());
				continue;
			}
			logger.debug(key + " = " + value.toString().replace("},", "},\n"));
		}
	}

	// retryNbre, set 0 if null or greater than retryConfigNumber
	public Integer getRetryNumber(Integer inputRetryNumber) throws IOException {
		Integer retryNbre = 0;
		String retriesConfigNbre = config.getPropValues("retriesConfigNbre");
		logger.info("=> retriesConfigNbre = " + retriesConfigNbre);
		if (inputRetryNumber != null) // && inputRetryNumber <= Integer.valueOf(retriesConfigNbre)
			retryNbre = inputRetryNumber;
		logger.info("Retry Tentative N° : " + retryNbre);
		return retryNbre;
	}

	/* Verify Pending methods */

	public HasPendingRequestResponse verifyPending(Long coId) throws Exception {
		logger.info("=> verifyPending coid = " + coId);
		APCHandlingSoapBindingStub binding = config.consumeAPCHandling();
		CoIdRequest request = new CoIdRequest(coId);
		HasPendingRequestResponse response = new HasPendingRequestResponse();
		try {
			response = binding.hasPendingRequest(request);
		} catch (com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.UnexpectedErrorFault e) {
			logger.error("Axis Fault : " + e.getFaultString());
			logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			logger.error("<= verifyPending coid = " + coId);
			throw e;
		} catch (Exception e) {
			logger.error("<= verifyPending coid with error = " + coId);
			throw e;
		}
		logger.info("<= verifyPending coid   " + coId);
		return response;
	}

	public Map<String, Object> sucessOutputVerifyPending(String waitFailure, Boolean isPending, String taskName) {
		logger.info("=> sucessOutputVerifyPending In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("hasPending", isPending);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("success", true);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputVerifyPending Out ");
		return resultsMap;
	}

	public Map<String, Object> sucessOutputVerifyPendingTrue(Integer retryNbre, String waitFailure, Boolean isPending,
			String taskName) {
		logger.info("=> sucessOutputVerifyPendingTrue");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("hasPending", isPending);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputVerifyPendingTrue");

		return resultsMap;
	}

	public Map<String, Object> failOutputVerifyPending(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputVerifyPending In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("hasPending", false);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("success", false);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputVerifyPending Out ");
		return resultsMap;
	}

	/* Deactivate Contract methods */

	public OperationResponse deactivateContract(Long coId) throws Exception {
		this.logger.info("=> deactivateContract IN");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		CoIdRequest request = new CoIdRequest(coId);
		OperationResponse response = new OperationResponse();
		try {
			response = binding.desactivateContract(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= deactivateContract with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= deactivateContract with error");

			throw e;
		}
		this.logger.info("<= deactivateContract ");
		return response;
	}

	public Map<String, Object> sucessOutputDeactivateContract(String waitFailure, String taskName) {
		logger.info("=> sucessOutputDeactivateContract In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputDeactivateContract Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputDeactivateContract(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputDeactivateContract In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputDeactivateContract Out ");
		return resultsMap;
	}

	/* Change RatePlan methods */

	public ChangeRatePlanResponse changeRatePlanReengagement(Long coId, Long tmcode) throws Exception {
		this.logger.info("=> changeRatePlan IN: co_id=" + coId + ", tmcode " + tmcode);
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		ChangeRatePlanRequest request = new ChangeRatePlanRequest();
		request.setCoId(coId.longValue());
		request.setRpCode(tmcode.longValue());
		ChangeRatePlanResponse response = new ChangeRatePlanResponse();
		try {
			response = binding.changeRateplan(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			this.logger.error("<= changeRatePlan with error");
			throw e;
		}
		this.logger.info("<= changeRatePlan out:" + response.isFinished());
		return response;
	}

	public Map<String, Object> sucessOutputChangeRatePlan(String waitFailure, String taskName) {
		logger.info("=> sucessOutputChangeRatePlan In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputChangeRatePlan Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputChangeRatePlan(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputChangeRatePlan In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputChangeRatePlan Out ");
		return resultsMap;
	}

	/* Get Migration Group methods */

	public GetCategoryResponse getMigrationGroup(MigrationOutputBean migrationOutputBean) throws Exception {
		GetCategoryResponse response;
		this.logger.info("=> getMigrationGroup");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		GetCategoryRequest request = new GetCategoryRequest();
		request.setRpCodeInit(migrationOutputBean.getResponseMigration().getOffreInitBscs().getTmcode());
		request.setRpCodeTarget(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());
		try {
			response = binding.getCatMigration(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= getMigrationGroup with error ");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= getMigrationGroup with error");
			throw e;
		}
		this.logger.info("<= getMigrationGroup ");
		return response;
	}

	public Map<String, Object> sucessOutputGetMigrationGroup(String waitFailure, Boolean isSameGroup, String taskName,
			Boolean isSameTmcode) {
		logger.info("=> sucessOutputGetMigrationGroup In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("isSameGroup", isSameGroup);
		resultsMap.put("success", true);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("isSameTmcode", isSameTmcode);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputGetMigrationGroup Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputGetMigrationGroup(Boolean isSameGroup, Integer retryNbre, String waitFailure,
			String taskName) {
		logger.info("=> failOutputGetMigrationGroup In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("isSameGroup", isSameGroup);
		resultsMap.put("success", false);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputGetMigrationGroup Out ");
		return resultsMap;
	}
	/* Activate OnHold Service methods */

	public OperationResponse activateOnHoldService(Long coId) throws Exception {
		this.logger.info("=> activateOnHoldService IN");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		CoIdRequest request = new CoIdRequest(coId);

		OperationResponse response = new OperationResponse();
		try {
			response = binding.activateOnHoldService(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= activateOnHoldService with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= activateOnHoldService with error");
			throw e;
		}
		this.logger.info("<= activateOnHoldService");
		return response;
	}

	public Map<String, Object> sucessOutputActivateOnHoldService(String waitFailure, String taskName) {
		logger.info("=> sucessOutputActivateOnHoldService In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputActivateOnHoldService Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputActivateOnHoldService(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputActivateOnHoldService In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputActivateOnHoldService Out ");
		return resultsMap;
	}

	// ActivateServices

	public Map<String, Object> sucessOutputactivateServices(String waitFailure, String taskName) {
		logger.info("=> sucessOutputactivateServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputactivateServices Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputactivateServices(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputactivateServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputactivateServices Out ");
		return resultsMap;
	}
	/* GetCustomerPromo methods */

	public GetCustomerPromoResponse getCustomerPromo(Long csId) throws Exception {
		this.logger.info("=> getCustomerPromo IN");
		PromotionHandlingSoapBindingStub binding = this.config.consumePromotionHandling();
		GetCustomerPromoRequest request = new GetCustomerPromoRequest();
		request.setCsId(csId);
		GetCustomerPromoResponse response = new GetCustomerPromoResponse();
		try {
			response = binding.getCustomerPromo(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= getCustomerPromo with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= getCustomerPromo with error");
			throw e;
		}
		this.logger.info("<= getCustomerPromo ");
		return response;
	}

	public Map<String, Object> sucessOutputGetCustomerPromo(String waitFailurePromotion, String taskName,
			List<Map<String, Object>> listPromotions, String retriesConfigNbre) {
		logger.info("=> sucessOutputGetCustomerPromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", 0);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", true);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("listPromotions", listPromotions);
		if (listPromotions != null) {
			resultsMap.put("listPromotionsSize", listPromotions.size());
		} else {
			resultsMap.put("listPromotionsSize", 0);

		}

		resultsMap.put("retriesConfigNbre", retriesConfigNbre);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputGetCustomerPromo Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputGetCustomerPromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName, String retriesConfigNbre) {
		logger.info("=> failOutputGetCustomerPromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", retryNbrePromotion + 1);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", false);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("retriesConfigNbre", retriesConfigNbre);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputGetCustomerPromo Out ");
		return resultsMap;
	}

	/* DeletePromo methods */

	public DeletePromoResponse deletePromo(Long assignSeqNo, String canalName, long csId, Calendar deleteDate,
			long packId, String userName) throws Exception {
		this.logger.info("=> deletePromo IN");
		PromotionHandlingSoapBindingStub binding = this.config.consumePromotionHandling();
		DeletePromoRequest request = new DeletePromoRequest();
		request.setAssignSeqNo(assignSeqNo);
		request.setCanalName(canalName);
		request.setCsId(csId);
		request.setDeleteDate(deleteDate);
		request.setPackId(packId);
		request.setUserName(userName);
		DeletePromoResponse response = new DeletePromoResponse();
		try {
			response = binding.deletePromo(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= deletePromo");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= deletePromo with exception");
			throw e;
		}
		this.logger.info("<= deletePromo ");
		return response;
	}

	public Map<String, Object> sucessOutputDeletePromo(String waitFailurePromotion, String taskName) {
		logger.info("=> sucessOutputDeletePromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", 0);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", true);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputDeletePromo Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputDeletePromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName) {
		logger.info("=> failOutputDeletePromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", retryNbrePromotion + 1);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", false);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputDeletePromo Out ");
		return resultsMap;
	}

	/* AssignPromo methods */

	public AssignPromoResponse assignPromo(AssignPromoRequest request) throws Exception {
		this.logger.info("=> assignPromo");
		PromotionHandlingSoapBindingStub binding = this.config.consumePromotionHandling();
		AssignPromoResponse response = new AssignPromoResponse();
		try {
			response = binding.assignPromo(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= assignPromo");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= assignPromo with exception");
			throw e;
		}
		this.logger.info("<= assignPromo");
		return response;
	}

	public Map<String, Object> sucessOutputAssignPromo(String waitFailurePromotion, String taskName) {
		logger.info("=> sucessOutputAssignPromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", 0);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", true);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputAssignPromo Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputAssignPromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName) {
		logger.info("=> failOutputAssignPromo In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbrePromotion", retryNbrePromotion + 1);
		resultsMap.put("waitFailurePromotion", waitFailurePromotion);
		resultsMap.put("successPromotion", false);
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputAssignPromo Out ");
		return resultsMap;
	}

	public ListFaFBO listFaF(String rpCodePub, Long internationNumberPhone) throws Exception {
		this.logger.info("=> getListFaF");
		FaFManagerPortBindingStub binding = this.config.consumeOMTFaFManager();
		ListFaFBI request = new ListFaFBI();

		request.setShortPhoneNumber(internationNumberPhone.toString());
		ListFaFBO response = new ListFaFBO();
		try {
			response = binding.listFaF(request);
		} catch (Exception e) {
			this.logger.info("<= getListFaF with exception");
			throw e;
		}
		this.logger.info("<= getListFaF");
		return response;
	}

	public Map<String, Object> sucessOutputListFaF(String waitFailure, String taskName, ArrayList<Long> result,
			boolean subscriberFriends) {
		logger.info("=> sucessOutputListFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listSubscriberFriends", result);
		resultsMap.put("subscriberFriends", subscriberFriends);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputListFaF Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputListFaF(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputListFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputListFaF Out ");
		return resultsMap;
	}

	/* Check Max FaF methods */

	public LimitAddResponse checkMaxFaF(String shortPhoneNumber) throws Exception {
		LimitAddResponse response;
		this.logger.info("=> CheckMaxFaF");
		FaFHandlingSoapBindingStub binding = this.config.consumeFaFHandling();
		LimitAddRequest request = new LimitAddRequest();
		request.setShortPhoneNumber(shortPhoneNumber);
		try {
			response = binding.checkMaxAdd(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= CheckMaxFaF");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= CheckMaxFaF with exception");
			throw e;
		}
		this.logger.info("<= CheckMaxFaF ");
		return response;
	}

	public Map<String, Object> sucessOutputCheckMaxFaF(String waitFailure, String taskName, Integer maxAdded,
			Boolean isSameTmcode) {
		logger.info("=> sucessOutputCheckMaxFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", Integer.valueOf(0));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("maxAdded", maxAdded);
		resultsMap.put("isSameTmcode", isSameTmcode);
		logger.info("==> isSameTmcode " + isSameTmcode);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputCheckMaxFaF Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputCheckMaxFaF(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputCheckMaxFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", Integer.valueOf(retryNbre.intValue() + 1));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);// false
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputCheckMaxFaF Out ");
		return resultsMap;
	}

	/* Adjust FaF methods */

	public RemoveFaFBO removeFaF(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber) throws Exception {
		this.logger.info("=> removeFaF with OMT");

		long[] listFaF = new long[listSubscriberFriends.size()];
		int index = 0;
		for (Long value : listSubscriberFriends) {
			listFaF[index] = value;
			this.logger.info("numéro FAF to remove :" + listFaF[index]);

			index++;
		}

		RemoveFaFBO response;
		FaFManagerPortBindingStub binding = this.config.consumeOMTFaFManager();
		RemoveFaFBI request = new RemoveFaFBI();
		request.setSubscriberFriend(listFaF);
		request.setShortPhoneNumber(shortPhoneNumber);
		try {
			response = binding.removeFaF(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= removeFaF with OMT");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= removeFaFPost with exception");
			throw e;
		}
		this.logger.info("<= removeFaF with OMT ");
		return response;
	}

	public RemoveFaFResponse removeFaFIN(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber)
			throws Exception {
		this.logger.info("=> removeFaFPost");

		long[] listFaF = new long[listSubscriberFriends.size()];
		int index = 0;
		for (Long value : listSubscriberFriends) {
			listFaF[index] = value;
		}

		RemoveFaFResponse response;
		FaFManager binding = this.config.consumeWsdlPBICMSFAF();
		RemoveFaFRequest request = new RemoveFaFRequest();
		Subscriber subscriberFriend = new Subscriber();
		subscriberFriend.setSubscriberNumberNAI(Integer.parseInt(shortPhoneNumber));
		request.setSubscriber(subscriberFriend);
		for (Long value : listSubscriberFriends) {
			request.getSubscriberFriend().add(value);
		}
		try {
			response = binding.removeFaFPre(request);
		} catch (Exception e) {
			this.logger.error("<= removeFaFPost with exception");
			throw e;
		}
		this.logger.info("<= removeFaFPost ");
		return response;
	}

	public AddFaFBO addFaF(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber) throws Exception {

		this.logger.info("=> addFaFPos");
		this.logger.info("=> shortPhoneNumber = " + shortPhoneNumber);

		long[] listFaF = new long[listSubscriberFriends.size()];
		int index = 0;
		for (Long value : listSubscriberFriends) {
			listFaF[index] = value;
			this.logger.info("=> numFaF to add : " + listFaF[index]);

			index++;
		}

		AddFaFBO response;
		FaFManagerPortBindingStub binding = this.config.consumeOMTFaFManager();
		AddFaFBI request = new AddFaFBI();
		request.setSubscriberFriend(listFaF);
		request.setShortPhoneNumber(shortPhoneNumber);
		try {
			response = binding.addFaF(request);
		} catch (Exception e) {
			this.logger.error("<= addFaFPos with exception");
			throw e;
		}
		this.logger.info("<= addFaFPos ");
		return response;
	}

	public AddFaFResponse addFaFIN(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber, String tmcode)
			throws Exception {

		this.logger.info("=> addFaF");
		this.logger.info("=> tmcode = " + tmcode);
		this.logger.info("=> shortPhoneNumber = " + shortPhoneNumber);

		this.logger.info("=> listFaF size  = " + listSubscriberFriends.size());

		AddFaFResponse response;
		FaFManager binding = this.config.consumeWsdlPBICMSFAF();
		AddFaFRequest request = new AddFaFRequest();
		Subscriber subscriberFriend = new Subscriber();
		subscriberFriend.setTmCode(Integer.parseInt(tmcode));
		subscriberFriend.setInternationNumberPhone(Long.parseLong(shortPhoneNumber));

		request.setSubscriber(subscriberFriend);
		request.getSubscriberFriend().addAll(listSubscriberFriends);

		try {

			response = binding.addFaFPre(request);
		} catch (Exception e) {
			this.logger.error("<= raddFaFt with exception");
			throw e;
		}
		this.logger.info("<= addFaFt ");
		return response;
	}

	public Map<String, Object> sucessOutputAdjustFaF(String waitFailure, String taskName, Boolean isListRemoveFaF) {
		logger.info("=> sucessOutputAdjustFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", Integer.valueOf(0));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("isListRemoveFaF", isListRemoveFaF);// taskName

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputAdjustFaF Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputAdjustFaF(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputAdjustFaF In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", Integer.valueOf(retryNbre.intValue() + 1));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);// false
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputAdjustFaF Out ");
		return resultsMap;
	}
	/* add Update Jackpot methods */

	public AddUpdateJackpotResponse addUpdateJackpot(String osmCode, String user, String custnum, BigDecimal amountHt,
			BigDecimal facilityAmount, Long commitment) throws Exception {
		this.logger.info("=> AddUpdateJackpot");
		JackpotWebServiceWebServicePortBindingStub binding = this.config.consumeGpsWsdl();
		AddUpdateJackpotRequest request = new AddUpdateJackpotRequest();
		this.logger.info("=> osmCode :" + osmCode);

		request.setOsmCode(osmCode);
		this.logger.info("=> user :" + user);

		request.setUser(user);
		this.logger.info("=> custnum :" + custnum);

		request.setCustnum(custnum);
		DecimalFormat df = new DecimalFormat("0.000");

		this.logger.info("=> amountHt :" + new BigDecimal(df.format(amountHt)));

		request.setAmountHt(new BigDecimal(df.format(amountHt)));
		this.logger.info("=> facilityAmount :" + new BigDecimal(df.format(facilityAmount)));

		request.setFacilityAmount(new BigDecimal(df.format(facilityAmount)));
		this.logger.info("=> commitment :" + commitment);

		request.setCommitment(commitment);
		AuthenticationData authenticationData = new AuthenticationData(config.getPropValues("wsdlGpsUsername"),
				config.getPropValues("wsdlGpsPassword")); // a revoir
		AddUpdateJackpotResponse response = new AddUpdateJackpotResponse();
		try {
			response = binding.addUpdateJackpot(authenticationData, request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= AddUpdateJackpot");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= AddUpdateJackpot with exception");
			throw e;
		}
		this.logger.info("<= AddUpdateJackpot ");
		return response;
	}

	public Map<String, Object> sucessOutputAddUpdateJackpot(String waitFailureGPS, String taskName) {
		logger.info("=> sucessOutputAddUpdateJackpot In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbreGPS", Integer.valueOf(0));
		resultsMap.put("waitFailureGPS", waitFailureGPS);
		resultsMap.put("successGPS", true);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputAddUpdateJackpot Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputAddUpdateJackpot(Integer retryNbreGPS, String waitFailureGPS,
			String taskName) {
		logger.info("=> failOutputAddUpdateJackpot In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbreGPS", Integer.valueOf(retryNbreGPS.intValue() + 1));
		resultsMap.put("waitFailureGPS", waitFailureGPS);
		resultsMap.put("successGPS", false);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputAddUpdateJackpot Out ");
		return resultsMap;

	}

	/* Get Or create Cug For customer methods */

	public GetOrcreateCugForcustomerResponse getOrcreateCugForcustomer(Long custId) throws Exception {
		this.logger.info("=> GetOrcreateCugForcustomer");
		ReengagementHandlingSoapBindingStub binding = this.config.consumeReengagementHandling();
		GetOrcreateCugForcustomerRequest request = new GetOrcreateCugForcustomerRequest();
		request.setCustomerId(custId);
		this.logger.info("=> custId :" + custId);

		GetOrcreateCugForcustomerResponse response = new GetOrcreateCugForcustomerResponse();
		try {
			response = binding.getOrcreateCugForcustomer(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= GetOrcreateCugForcustomer with error ");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= GetOrcreateCugForcustomer with exception");
			throw e;
		}
		this.logger.info("<= GetOrcreateCugForcustomer ");
		return response;
	}

	public Map<String, Object> sucessOutputGetOrcreateCugForcustomer(String waitFailure, String taskName,
			List<Map<String, Object>> listCugs) {
		logger.info("=> sucessOutputGetOrcreateCugForcustomer In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listCugs", listCugs);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputGetOrcreateCugForcustomer Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputGetOrcreateCugForcustomer(Integer retryNbre, String waitFailure,
			String taskName) {
		logger.info("=> failOutputGetOrcreateCugForcustomer In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);// false
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputGetOrcreateCugForcustomer Out ");
		return resultsMap;
	}

	/* Desactivate Services methods */

	public DesactivateServicesResponse desactivateServices(DesactivateServicesRequest request) throws Exception {
		DesactivateServicesResponse response;
		this.logger.info("=> DesactivateServices");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		try {
			response = binding.desactivateServices(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= DesactivateServices");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= DesactivateServices with error");
			throw e;
		}
		this.logger.info("<= DesactivateServices");
		return response;
	}

	public Map<String, Object> sucessOutputDesactivateServices(String waitFailure, String taskName) {
		logger.info("=> sucessOutputDesactivateServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", Integer.valueOf(0));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputDesactivateServices Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputDesactivateServices(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputDesactivateServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", Integer.valueOf(retryNbre.intValue() + 1));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputDesactivateServices Out ");
		return resultsMap;
	}

	// DesactivateNonCommonServices

	public DesactivateServicesResponse desactivateNonCommonServices(Long coId, long[] sncodeToDesactivate)
			throws Exception {
		DesactivateServicesResponse response;
		this.logger.info("=> DesactivateServices");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		DesactivateServicesRequest request = new DesactivateServicesRequest();
		request.setCoId(coId);
		request.setSnCodes(sncodeToDesactivate);
		try {

			response = binding.desactivateServices(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.info("<= DesactivateServices");
			throw e;
		} catch (Exception e) {
			this.logger.info("<= DesactivateServices with exception");
			throw e;
		}
		this.logger.info("<= DesactivateServices ");
		return response;
	}

	public Map<String, Object> sucessOutputDesactivateNonCommonServices(String waitFailure, String taskName,
			List<Map<String, Object>> listToDeleteAfterCheck) {
		logger.info("=> sucessOutputDesactivateNonCommonServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		Boolean isListToDelete = false;
		if (listToDeleteAfterCheck != null && listToDeleteAfterCheck.size() != 0) {

			isListToDelete = true;

		}
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", Integer.valueOf(0));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listToDelete", listToDeleteAfterCheck);
		resultsMap.put("isListToDelete", isListToDelete);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputDesactivateNonCommonServices Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputDesactivateNonCommonServices(Integer retryNbre, String waitFailure,
			String taskName, List<Map<String, Object>> listToDelete) {
		logger.info("=> failOutputDesactivateNonCommonServices In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", Integer.valueOf(retryNbre.intValue() + 1));
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listToDelete", listToDelete);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputDesactivateNonCommonServices Out ");
		return resultsMap;
	}

	/* ModifierDFE (GRH) methods */

	public AddGrhRequestResponse addGrh(Logger logger, Map<String, Object> grhMap, ContractDetails contractDetails,
			Long custId, String user, Long reqId) throws Exception {
		logger.info("=> addGrh");
		// binding GRH
		GrhHandlingSoapBindingStub bindingGrh = config.consumeGrhHandling();

		UpdateGrhBean updateGrhBean = new ObjectMapper().convertValue(grhMap, UpdateGrhBean.class);

		// requests
		AddGrhRequestRequest addGrhRequestRequest = new AddGrhRequestRequest();
		GrhRequest grhRequest = new GrhRequest();
		CommitmentRequest commitmentRequest = new CommitmentRequest();
		RequestAll requestAll = new RequestAll();

		// grhOperationType = réengagement (id = 3)
		Integer grhOperationType = Integer.valueOf(config.getPropValues("grhOperationTypeReengagement"));

		logger.info("grhOperationType = " + grhOperationType);
		logger.info("User = " + user);
		logger.info("RequestPriority = " + Integer.valueOf(config.getPropValues("grhRequestPriority")));

		// set requestAll
		requestAll.setCoId(contractDetails.getCoId());
		requestAll.setCoTmCode(contractDetails.getTmcode().intValue());
		requestAll.setCustomerId(custId);
		requestAll.setOperationType(new OperationType(null, grhOperationType));
		requestAll.setPriority(Integer.valueOf(config.getPropValues("grhRequestPriority")));
		requestAll.setProcessingMode(new ProcessingMode(null, 1));
		requestAll.setSourceId(2L);
		requestAll.setStatusDefinition(new StatusDefinition(null, 2));
		requestAll.setRequestPostUser(user);

		requestAll.setCoNewTrialStartDate(updateGrhBean.getCommitmentStartDate());
		// set commitmentRequest
		commitmentRequest.setEntryDate(Calendar.getInstance());
		commitmentRequest.setCommitmentEntityType(new CommitmentEntityType(null, 2));
		commitmentRequest.setPriority(1);
		commitmentRequest.setPackStartDate_BaselineRule("OVERRIDE");
		commitmentRequest.setPackEndDate_BaselineRule("OVERRIDE");

		// TO DO
		if (reqId != null)
			commitmentRequest.setRequestId(reqId); // web service commitment

		commitmentRequest.setCommitmentDuration(updateGrhBean.getCommitmentPeriod());

		// set grhRequest
		grhRequest.setRequestAll(requestAll);
		grhRequest.setCommitmentRequest(commitmentRequest);

		// set addGrhRequestRequest
		addGrhRequestRequest.setGrhRequest(grhRequest);

		logger.info("grhRequest=" + new ObjectMapper().convertValue(grhRequest, Map.class));

		AddGrhRequestResponse response = new AddGrhRequestResponse();

		try {
			response = bindingGrh.addGrhRequest(addGrhRequestRequest);
		} catch (com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.UnexpectedErrorFault e) {
			logger.error("Axis Fault : " + e.getFaultString());
			logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			logger.info("<= addGrh");
			throw e;
		} catch (Exception e) {
			logger.info("<= addGrh with exception");
			throw e;
		}
		logger.info("<= addGrh ");
		return response;
	}

	public Map<String, Object> sucessOutputModifierDFE(String waitFailureDFE, String taskName, String exist) {
		logger.info("=> sucessOutputModifierDFE In ");
		exist = "Grh";
		logger.info("=> exist  " + exist);

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbreDFE", 0);// retryNbre
		resultsMap.put("waitFailureDFE", waitFailureDFE);// waitFailure
		resultsMap.put("successDFE", true);
		resultsMap.put("exist", exist);
		resultsMap.put("taskName", taskName);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputModifierDFE Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputModifierDFE(Integer retryNbreDFE, String waitFailureDFE, String taskName,
			String exist) {
		logger.info("=> failOutputModifierDFE In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbreDFE", retryNbreDFE + 1);// retryNbre
		resultsMap.put("waitFailureDFE", waitFailureDFE);// waitFailure
		resultsMap.put("successDFE", false);
		resultsMap.put("taskName", taskName);
		resultsMap.put("exist", exist);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputModifierDFE Out ");
		return resultsMap;
	}

	/* SmsNotif methods */

	public Map<String, Object> sucessOutputSmsNotif(String waitFailure, String taskName) {
		logger.info("=> sucessOutputSmsNotif In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputSmsNotif Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputSmsNotif(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputSmsNotif In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputSmsNotif Out ");
		return resultsMap;
	}

	/* CheckRateplan methods */

	public CheckRateplanResponse checkRateplan(Map<String, Object> migrationMap, Map<String, Object> contractMap)
			throws Exception {
		CheckRateplanResponse response;
		this.logger.info("=> CheckRateplan");
		MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
				MigrationOutputBean.class);

		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		CheckRateplanRequest request = new CheckRateplanRequest();

		request.setCoId((Long) contractMap.get("coId"));
		Long templateId = null;
		logger.info("templateId " + templateId);
		if (templateId != null && templateId != 0) {
			request.setTemplateId(templateId);
		}
		request.setDes(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getShdesOffer()); // a revoir
		request.setRpcode(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());

		request.setValue(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getParameterValue());
		try {
			response = binding.checkRateplan(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= CheckRateplan");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= CheckRateplan with error");
			throw e;
		}
		this.logger.info("<= CheckRateplan ");
		return response;
	}

	public Map<String, Object> sucessOutputCheckRateplan(String waitFailure, String taskName, List<Long> listSncode,
			int sizeDesactivateServices, ArrayList<Service> missingServices, ArrayList<Service> communServices) {
		logger.info("=> sucessOutputCheckRateplan In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listSncode", listSncode);
		resultsMap.put("sizeDesactivateServices", sizeDesactivateServices);
		resultsMap.put("missingServices", missingServices);
		resultsMap.put("communServices", communServices);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputCheckRateplan Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputCheckRateplan(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputCheckRateplan In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputCheckRateplan Out ");
		return resultsMap;
	}

	/* AdjustBalanceIn methods */

	public AdjustBalanceResponse adjustBalanceIn(Map<String, Object> migrationMap, Map<String, Object> contractMap,
			Long custId, Long mainBalanceAmount, Long oldCoId) throws Exception {
		this.logger.info("=> adjustBalanceIn");
		APCManager bindingPBICMSAPC = config.consumeWsdlPBICMS();
		AdjustBalanceRequest adjustBalanceRequest = new AdjustBalanceRequest();
		MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
				MigrationOutputBean.class);
		GetBscsOfferAndServciesFromCrmDemandeResponse res = migrationOutputBean.getResponseMigration();
		adjustBalanceRequest.setAmount(mainBalanceAmount);

		if (oldCoId == null) {
			adjustBalanceRequest.setCoId(Long.parseLong(contractMap.get("coId").toString()));
			this.logger.info("coId injected =" + Long.parseLong(contractMap.get("coId").toString()));

		} else {
			adjustBalanceRequest.setCoId(oldCoId);
			this.logger.info("coId injected =" + oldCoId);

		}

		adjustBalanceRequest.setCsId(custId);
		if (res.getOffreInitBscs().getValueDes() != null) {
			adjustBalanceRequest.setDesSource(res.getOffreInitBscs().getValueDes());
			this.logger.info("DesSource = " + res.getOffreInitBscs().getValueDes());

		}
		if (res.getOffreTargetBscs().getValueDes() != null) {

			adjustBalanceRequest.setDesTarget(res.getOffreTargetBscs().getValueDes());
			this.logger.info("DesTarget = " + res.getOffreTargetBscs().getValueDes());
		}

		adjustBalanceRequest.setRpCodeSource(res.getOffreInitBscs().getTmcode());
		adjustBalanceRequest.setRpCodeCible(res.getOffreTargetBscs().getTmcode());
		adjustBalanceRequest.setMsisdn(contractMap.get("msisdn").toString());
		if (res.getOffreInitBscs().getParameterValue() != null)
			adjustBalanceRequest.setValueSource(res.getOffreInitBscs().getParameterValue());
		else
			adjustBalanceRequest.setValueSource("");

		if (res.getOffreTargetBscs().getParameterValue() != null)
			adjustBalanceRequest.setValueTarget(res.getOffreTargetBscs().getParameterValue());
		else
			adjustBalanceRequest.setValueTarget("");

		AdjustBalanceResponse responseAsjustBalance = new AdjustBalanceResponse();
		try {
			responseAsjustBalance = bindingPBICMSAPC.adjustBalanceIn(adjustBalanceRequest);
		} catch (Exception e) {

			this.logger.error("<= adjustBalanceIn with error");

			e.printStackTrace();
			throw e;
		}

		this.logger.info("<= adjustBalanceIn ");
		return responseAsjustBalance;
	}

	public AdjustBalanceResponse adjustBalanceBscs(Map<String, Object> migrationMap, Map<String, Object> contractMap,
			Long custId, Long mainBalanceAmount, Long oldCoId) throws Exception {
		this.logger.info("=> adjustBalanceBscs");
		APCManager bindingPBICMSAPC = config.consumeWsdlPBICMS();
		AdjustBalanceRequest adjustBalanceRequest = new AdjustBalanceRequest();
		MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
				MigrationOutputBean.class);
		GetBscsOfferAndServciesFromCrmDemandeResponse res = migrationOutputBean.getResponseMigration();

		if (oldCoId == null) {
			adjustBalanceRequest.setCoId(Long.parseLong(contractMap.get("coId").toString()));
			this.logger.info("coId injected =" + Long.parseLong(contractMap.get("coId").toString()));

		} else {
			adjustBalanceRequest.setCoId(oldCoId);
			this.logger.info("old coId injected =" + oldCoId);

		}
		adjustBalanceRequest.setAmount(mainBalanceAmount);
		this.logger.info("mainBalanceAmount = " + mainBalanceAmount);

		adjustBalanceRequest.setCsId(custId);
		this.logger.info("custId = " + custId);

		if (res.getOffreInitBscs().getValueDes() != null) {
			adjustBalanceRequest.setDesSource(res.getOffreInitBscs().getValueDes());
			this.logger.info("DesSource = " + res.getOffreInitBscs().getValueDes());

		}
		if (res.getOffreTargetBscs().getValueDes() != null) {

			adjustBalanceRequest.setDesTarget(res.getOffreTargetBscs().getValueDes());
			this.logger.info("DesTarget = " + res.getOffreTargetBscs().getValueDes());
		}
		adjustBalanceRequest.setRpCodeSource(res.getOffreInitBscs().getTmcode());
		this.logger.info("tmCode source = " + res.getOffreInitBscs().getTmcode());

		adjustBalanceRequest.setRpCodeCible(res.getOffreTargetBscs().getTmcode());
		this.logger.info("tmCode cible = " + res.getOffreTargetBscs().getTmcode());

		adjustBalanceRequest.setMsisdn(contractMap.get("msisdn").toString());
		this.logger.info("msisdn = " + contractMap.get("msisdn").toString());

		if (res.getOffreInitBscs().getParameterValue() != null)
			adjustBalanceRequest.setValueSource(res.getOffreInitBscs().getParameterValue());
		else
			adjustBalanceRequest.setValueSource("");

		this.logger.info("ValueSource = " + adjustBalanceRequest.getValueSource());

		if (res.getOffreTargetBscs().getParameterValue() != null)
			adjustBalanceRequest.setValueTarget(res.getOffreTargetBscs().getParameterValue());
		else
			adjustBalanceRequest.setValueTarget("");

		this.logger.info("ValueTarget = " + adjustBalanceRequest.getValueTarget());

		AdjustBalanceResponse responseAsjustBalance = new AdjustBalanceResponse();
		try {
			responseAsjustBalance = bindingPBICMSAPC.adjustBalanceBscs(adjustBalanceRequest);
		} catch (Exception e) {

			this.logger.error("<= adjustBalanceIn with error");

			e.printStackTrace();
			throw e;
		}

		this.logger.info("<= adjustBalanceBscs ");
		return responseAsjustBalance;
	}

	public Map<String, Object> successAdjustBalanceIN(String waitFailure, String taskName) {
		logger.info("=> successAdjustBalance In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successAdjustBalanceIN Out ");
		return resultsMap;
	}

	public Map<String, Object> failAdjustBalanceIN(Integer retryNbre, String waitFailure) {
		logger.info("=> failAdjustBalanceIN In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failAdjustBalance Out ");
		return resultsMap;

	}

	/* DecryptBSCS methods */

	public GetBscsOfferAndServciesFromCrmDemandeResponse getBscsOfferAndServciesFromCrmDemande(
			GetBscsOfferAndServciesFromCrmDemandeRequest request) throws Exception {
		this.logger.info("=> getBscsOfferAndServciesFromCrmDemande");

		ReengagementHandlingSoapBindingStub binding = this.config.consumeReengagementHandling();
		GetBscsOfferAndServciesFromCrmDemandeResponse response = new GetBscsOfferAndServciesFromCrmDemandeResponse();

		try {
			response = binding.getBscsOfferAndServciesFromCrmDemande(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= getBscsOfferAndServciesFromCrmDemande with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= getBscsOfferAndServciesFromCrmDemande with error");
			throw e;
		}

		this.logger.info("<= getBscsOfferAndServciesFromCrmDemande ");
		return response;

	}

	public Map<String, Object> successGetBscsOfferAndServciesFromCrmDemande(String waitFailure, String taskName,
			List<Map<String, Object>> listMigrationMapOutput, List<Map<String, Object>> listUpdateOptionMapOutput,
			List<Map<String, Object>> listContractOfGrh, Integer sizeListContractOfGrh, String retriesConfigNbre,
			String retryWaiting, String pendingWaiting, List<Long> listcontracts, Integer orderId,
			List<Map<String, Object>> listContractOfGrhWithoutMigration, Integer sizeListContractOfGrhWithoutMig,
			String waitingLot, Integer nbreContratLot) {
		logger.info("=> successGetBscsOfferAndServciesFromCrmDemande In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listMigrationMapOutput", listMigrationMapOutput);
		resultsMap.put("listUpdateOptionMapOutput", listUpdateOptionMapOutput);
		resultsMap.put("listContractOfGrh", listContractOfGrh);
		resultsMap.put("sizeListContractOfGrh", sizeListContractOfGrh);
		resultsMap.put("retriesConfigNbre", retriesConfigNbre);
		resultsMap.put("retryWaiting", retryWaiting);
		resultsMap.put("pendingWaiting", pendingWaiting);
		resultsMap.put("listcontracts", listcontracts);
		resultsMap.put("listContractOfGrhWithoutMigration", listContractOfGrhWithoutMigration);
		resultsMap.put("sizeListContractOfGrh_Without_Mig", sizeListContractOfGrhWithoutMig);
		resultsMap.put("waitingLot", waitingLot);
		resultsMap.put("nbreContratLot", nbreContratLot);

		resultsMap.put("orderId", orderId);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successGetBscsOfferAndServciesFromCrmDemande Out ");
		return resultsMap;
	}

	public Map<String, Object> failGetBscsOfferAndServciesFromCrmDemande(Integer retryNbre, String waitFailure,
			String retriesConfigNbre, String retryWaiting, String pendingWaiting) {
		logger.info("=> failGetBscsOfferAndServciesFromCrmDemande In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("retriesConfigNbre", retriesConfigNbre);
		resultsMap.put("retryWaiting", retryWaiting);
		resultsMap.put("pendingWaiting", pendingWaiting);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failGetBscsOfferAndServciesFromCrmDemande Out ");
		return resultsMap;

	}

	/* PercistReengagementOrder methods */

	public PercistReengagementOrderResponse percistReengagementOrder(List<ReengagementBean> reengagementBeans)
			throws Exception {
		logger.info("=> PercistReengagementOrder IN");
		ReengagementManager binding = config.consumeWsdlPBICMSReengagement();
		PercistReengagementOrderRequest request = new PercistReengagementOrderRequest();
		request.getReengagements().addAll(reengagementBeans);
		PercistReengagementOrderResponse response = new PercistReengagementOrderResponse();
		try {
			response = binding.percisteReengagement(request);
			logger.info("RESPONSE= " + response);
		} catch (Exception e) {
			logger.error("ERROR PERCIST" + e);
			logger.error("<= PercistReengagementOrder with error");
			throw e;
		}
		logger.info("<= PercistReengagementOrder OUT");

		return response;

	}

	public Map<String, Object> sucessOutputPercistReengagementOrder(String waitFailure, String taskName,
			boolean isMigration, boolean isUpdateOption, boolean isInjectionRBF, boolean isInjectionGPS,
			String dueDateTimer) {
		logger.info("=> sucessOutputPercistReengagementOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);
		resultsMap.put("isMigration", isMigration);
		resultsMap.put("isUpdateOption", isUpdateOption);
		resultsMap.put("isInjectionRBF", isInjectionRBF);
		resultsMap.put("isInjectionGPS", isInjectionGPS);
		resultsMap.put("dueDateTimer", dueDateTimer);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputPercistReengagementOrder Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputPercistReengagementOrder(Integer retryNbre, String waitFailure,
			String taskName) {
		logger.info("=> failOutputPercistReengagementOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);// false
		resultsMap.put("retryNbre", retryNbre + 1);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputPercistReengagementOrder Out ");
		return resultsMap;
	}

	/* getListOfServiceToactivateContrat methods */

	public GetListOfServiceToactivateContratResponse getListOfServiceToactivateContratRequest(
			GetListOfServiceToactivateContratRequest request) throws Exception {
		this.logger.info("=> getListOfServiceToactivateContrat IN");
		ReengagementHandlingSoapBindingStub binding = this.config.consumeReengagementHandling();
		GetListOfServiceToactivateContratResponse response = new GetListOfServiceToactivateContratResponse();

		try {
			response = binding.getListOfServiceToactivateContrat(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= getListOfServiceToactivateContrat with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= getListOfServiceToactivateContrat with error");
			throw e;
		}

		this.logger.info("<= getListOfServiceToactivateContrat  ");
		return response;

	}

	public Map<String, Object> sucessOutputGetListOfServiceToactivateContrat(String waitFailure,
			Map<String, Object> mapServiceToActivate, String taskName, Boolean isListCugs, Map<String, Object> cugMap,
			Boolean isListRessources, List<Map<String, Object>> listServiceRessource) {
		logger.info("=> sucessOutputGetListOfServiceToactivateContrat In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("mapServiceToActivate", mapServiceToActivate);
		resultsMap.put("isListCugs", isListCugs);
		resultsMap.put("cugMap", cugMap);
		resultsMap.put("isListRessources", isListRessources);
		resultsMap.put("listServiceRessource", listServiceRessource);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputGetListOfServiceToactivateContrat Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputGetListOfServiceToactivateContrat(Integer retryNbre, String waitFailure,
			String taskName) {
		logger.info("=> failOutputGetListOfServiceToactivateContrat In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);// false
		resultsMap.put("retryNbre", retryNbre + 1);// retryNbre
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputGetListOfServiceToactivateContrat Out ");
		return resultsMap;
	}

	/* prepare Migration methods */

	public Map<String, Object> successPrepareMigration(String waitFailure, String taskName,
			Integer sizeListContractOfMigration, List<Map<String, Object>> listContractOfMigration) {
		logger.info("=> successPrepareMigration In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);

		resultsMap.put("sizeListContractOfMigration", sizeListContractOfMigration);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("listContractOfMigration", listContractOfMigration);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successPrepareMigration Out ");
		return resultsMap;
	}

	public Map<String, Object> failPrepareMigration(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failPrepareMigration In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failPrepareMigration Out ");
		return resultsMap;
	}

	/* prepare Migration methods */

	/* Get Action methods */ public Map<String, Object> successGetAction(String waitFailure, String taskName,
			Integer sizeListContractOfUpdateOption, List<Map<String, Object>> listContractOfUpdateOption,
			boolean isListToDelete, boolean isListToAdd, boolean isListToUpdate, boolean isListCugs,
			boolean isListRessources, List<Map<String, Object>> listToDelete, List<Service> listToAdd,
			List<Map<String, Object>> listToUpdate, List<Map<String, Object>> listServiceCug,
			List<Service> listServiceRessource, List<ServiceBeanCrm> listToAddService,
			List<ServiceBeanCrm> listToDeleteService, Map<String, Object> cugMapIntra) {
		logger.info("=> successGetAction In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("sizeListContractOfUpdateOption", sizeListContractOfUpdateOption);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("listContractOfUpdateOption", listContractOfUpdateOption);
		resultsMap.put("isListToDelete", isListToDelete);
		resultsMap.put("isListToAdd", isListToAdd);
		resultsMap.put("isListToUpdate", isListToUpdate);
		resultsMap.put("isListCugs", isListCugs);
		resultsMap.put("isListRessources", isListRessources);
		resultsMap.put("listToDelete", listToDelete);
		resultsMap.put("listToAdd", listToAdd);
		resultsMap.put("listToUpdate", listToUpdate);
		resultsMap.put("listServiceCug", listServiceCug);
		resultsMap.put("listToAddService", listToAddService);
		resultsMap.put("listToDeleteService", listToDeleteService);
		resultsMap.put("listServiceRessource", listServiceRessource);
		resultsMap.put("cugMapIntra", cugMapIntra);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successGetAction Out ");
		return resultsMap;
	}

	public Map<String, Object> failGetAction(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failGetAction In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failGetAction Out ");
		return resultsMap;
	}
	/* updateReengagement Order Methods */

	public UpdateReengagementOrderResponse updateReengagementOrder(ReengagementBean reengagement) throws Exception {
		logger.info("=> updateReengagementOrder IN");
		ReengagementManager binding = config.consumeWsdlPBICMSReengagement();
		UpdateReengagementOrderRequest request = new UpdateReengagementOrderRequest();
		logger.info("req= " + new ObjectMapper().convertValue(reengagement, Map.class));
		request.setReengagementBean(reengagement);
		UpdateReengagementOrderResponse response = new UpdateReengagementOrderResponse();
		try {
			response = binding.updateReengagement(request);
		} catch (Exception e) {
			logger.error("error= " + e + "  <= updateReengagementOrder with error");
			throw e;
		}
		logger.info("<= updateReengagementOrder");
		return response;
	}

	public Map<String, Object> sucessOutputUpdateReengagementOrder(String waitFailure, String taskName) {
		logger.info("=> sucessOutputUpdateReengagementOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputUpdateReengagementOrder Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputUpdateReengagementOrder(Integer retryNbre, String waitFailure,
			String taskName) {

		logger.info("=> failOutputUpdateReengagementOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputUpdateReengagementOrder Out ");
		return resultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.billcom.apc.reengagement.service.ReengagementService#
	 * activateServiceParamter(com.billcom.apc.generatedSOAPReengagement.bscs.
	 * apcHandling.ActivateServiceParamter)
	 */
	public ActivateServiceParamterResponse activateServiceParamter(ActivateServiceParamter request) throws Exception {

		ActivateServiceParamterResponse response;
		this.logger.info("=> ActivateServiceParamter IN");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		try {
			response = binding.activateAndSetParameterServices(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= ActivateServiceParamter with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= ActivateServiceParamter with error");
			throw e;
		}
		this.logger.info("<= ActivateServiceParamter ");
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.billcom.apc.reengagement.service.ReengagementService#
	 * sucessOutputActivateServiceParamter(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> sucessOutputActivateServiceParamter(String waitFailure, String taskName) {
		logger.info("=> sucessOutputActivateServiceParamter In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputActivateServiceParamter Out ");
		return resultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.billcom.apc.reengagement.service.ReengagementService#
	 * failOutputActivateServiceParamter(java.lang.Integer, java.lang.String,
	 * java.lang.String)
	 */
	public Map<String, Object> failOutputActivateServiceParamter(Integer retryNbre, String waitFailure,
			String taskName) {
		logger.info("=> failOutputActivateServiceParamter In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputActivateServiceParamter Out ");
		return resultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.billcom.apc.reengagement.service.ReengagementService#createContract(com.
	 * billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CreateContractRequest)
	 */
	public CreateContractResponse createContract(CreateContractRequest request) throws Exception {

		CreateContractResponse response;
		this.logger.info("=> createContract IN");

		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandlingForCreateContract();
		try {
			response = binding.createContract(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= createContract with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= createContract with error");
			throw e;
		}
		this.logger.info("<= createContract ");
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.billcom.apc.reengagement.service.ReengagementService#
	 * sucessOutputCreateContract(java.util.Map, java.lang.String, java.lang.String)
	 */
	public Map<String, Object> sucessOutputCreateContract(String waitFailure, String taskName,
			Map<String, Object> contractMap, String oldCoId, String oldCoIdPub) {
		logger.info("=> sucessOutputCreateContract In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("contractMap", contractMap);
		resultsMap.put("oldCoId", oldCoId);
		resultsMap.put("oldCoIdPub", oldCoIdPub);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputCreateContract Out ");
		return resultsMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.billcom.apc.reengagement.service.ReengagementService#
	 * failOutputCreateContract(java.util.Map, java.lang.Integer, java.lang.String,
	 * java.lang.String)
	 */
	public Map<String, Object> failOutputCreateContract(Integer retryNbre, String waitFailure, String taskName,
			Map<String, Object> contractMap) {
		logger.info("=> failOutputCreateContract In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);// false
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("contractMap", contractMap);// taskName

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputCreateContract Out ");
		return resultsMap;
	}

	/* Get balances IN methods */

	public GetBalanceAndDateBeanOutputV2 getBalanceAndDate(Map<String, Object> contractMap) throws Exception {
		this.logger.info("=> getBalanceAndDate IN");
		BalanceManagerV2 binding = this.config.consumeWsdlWsPortal();
		GetBalanceAndDateBeanInputV2 getBalanceAndDateBeanInputV2 = new GetBalanceAndDateBeanInputV2();
		SubscriberV2 subscriberV2 = new SubscriberV2();
		subscriberV2.setInternationNumberPhone(Long.valueOf(Long.parseLong(contractMap.get("msisdn").toString())));
		getBalanceAndDateBeanInputV2.setSubscriber(subscriberV2);
		getBalanceAndDateBeanInputV2.setTmCode(Integer.valueOf(Integer.parseInt(contractMap.get("tmcode").toString())));
		GetBalanceAndDateBeanOutputV2 response = new GetBalanceAndDateBeanOutputV2();
		try {
			response = binding.getBalanceAndDate(getBalanceAndDateBeanInputV2);
		} catch (Exception e) {
			this.logger.error("<=getBalanceAndDate with error");
			throw e;
		}

		this.logger.info("<=getBalanceAndDate with success");

		return response;
	}

	public OperationResponse insertBalanceHist(Map<String, Object> contractMap, String balanceType, String unit,
			double remAmount, Long orderId) throws Exception {
		this.logger.info("=> insertBalanceHist IN");
		BalanceHistRequest insertBalanceHistRequest = new BalanceHistRequest("IN",
				Long.valueOf(Long.parseLong(contractMap.get("coId").toString())), orderId, null,
				Long.valueOf(Long.parseLong(contractMap.get("tmcode").toString())), balanceType, unit,
				Double.valueOf(remAmount));
		APCHandlingSoapBindingStub bindingBscsAPC = this.config.consumeAPCHandling();
		OperationResponse balanceInsertResponse = new OperationResponse();
		try {
			balanceInsertResponse = bindingBscsAPC.insertBalanceHist(insertBalanceHistRequest);
		} catch (Exception e) {
			this.logger.error("<=insertBalanceHist with error ");
			throw e;
		}
		this.logger.info("<=insertBalanceHist  ");

		return balanceInsertResponse;
	}

	public Map<String, Object> successOutputGetBalancesIN(String waitFailure, String taskName, boolean insertBalance,
			List<OptionV2> listOptions, String mainBalanceAmount) {
		logger.info("=> successOutputGetBalancesIN In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("insertBalance", insertBalance);
		resultsMap.put("listOptions", listOptions);
		resultsMap.put("mainBalanceAmount", mainBalanceAmount);

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successOutputGetBalancesIN Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputGetBalancesIN(Integer retryNbre, String waitFailure, String taskName) {
		logger.info("=> failOutputGetBalancesIN In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);// taskName

		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputGetBalancesIN Out ");
		return resultsMap;
	}

	public Map<String, Object> successOutputInserttBalancesIN(String waitFailure, String taskName,
			List<OptionV2> listOptions) {
		logger.info("=> successOutputInserttBalancesIN");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listOptions", listOptions);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successOutputInserttBalancesIN");

		return resultsMap;
	}

	public Map<String, Object> failOutputInsertBalancesIN(Integer retryNbre, String waitFailure,
			List<OptionV2> listOptions, Integer j) {
		logger.info("=> failOutputInsertBalancesIN");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("listOptions", listOptions);
		resultsMap.put("j", j);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputInsertBalancesIN");

		return resultsMap;
	}

	public Map<String, Object> sucessOutputSetContractOrder(String waitFailure, String taskName, Integer coid) {
		logger.info("=> sucessOutputSetContractOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);
		resultsMap.put("coid", coid);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputSetContractOrder Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputSetContractOrder(Integer retryNbre, String waitFailure, String taskName,
			Integer coid) {

		logger.info("=> failOutputSetContractOrder In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		resultsMap.put("coid", coid);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputSetContractOrder Out ");
		return resultsMap;
	}

	// AddCug
	public OperationResponse addCug(AddCugRequest request) throws Exception {

		OperationResponse response;
		this.logger.info("=> addCUG IN");
		APCHandlingSoapBindingStub binding = this.config.consumeAPCHandling();
		try {
			response = binding.addCugContract(request);
		} catch (UnexpectedErrorFault e) {
			this.logger.error("Axis Fault : " + e.getFaultString());
			this.logger.error("UnexpectedErrorFault : " + e.getFaultstring());
			this.logger.error("<= addCUG with error");
			throw e;
		} catch (Exception e) {
			this.logger.error("<= addCUG with error");
			throw e;
		}
		this.logger.info("<= addCUG ");
		return response;
	}

	public Map<String, Object> sucessOutputAddCug(String waitFailure, String taskName) {
		logger.info("=> sucessOutputAddCug In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputAddCug Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputAddCug(Integer retryNbre, String waitFailure, String taskName) {

		logger.info("=> failOutputAddCug In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputAddCug Out ");
		return resultsMap;
	}

	/* Adjust Facture */

	public Map<String, Object> successAdjustFacture(String waitFailure, String taskName) {
		logger.info("=> successAdjustFacture IN");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= successAdjustFacture out");

		return resultsMap;
	}

	public Map<String, Object> failAdjustFacture(Integer retryNbre, String waitFailure, String taskName) {

		logger.info("=> failAdjustFacture IN");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failAdjustFacture out");

		return resultsMap;
	}

	// ActivateServicesCores

	public OperationResponse activateServicesCores(Map<String, Object> contractMap, List<Service> listOfMissingServices,
			List<Service> listToAdd, List<Service> listServiceRessource, List<Map<String, Object>> listCugs)
			throws Exception {
		this.logger.info("=> activateServicesCores IN");
		ActivateServiceParamter activateService = new ActivateServiceParamter();
		activateService.setCoId((Long) contractMap.get("coId"));
		this.logger.info("CoId = " + (Long) contractMap.get("coId"));

		activateService.setCoIdPub((String) contractMap.get("contractCode"));
		this.logger.info("CoCode = " + (String) contractMap.get("contractCode"));

		APCHandlingSoapBindingStub binding = config.consumeAPCHandling();
		OperationResponse response = new OperationResponse();
		int i = 0;
		String sncodesCUG = this.config.getPropValues("SNCODE_CUG").trim();
		GetOrcreateCugForcustomerResponse cugBean = new GetOrcreateCugForcustomerResponse();
		try {

			Service[] services = new Service[listOfMissingServices.size()];

			this.logger.info("services size  " + services.length);

			List<Service> servicesList = new ArrayList<>();
			List<Service> listServiceRessources = new ArrayList<>();
			listServiceRessources.addAll(listServiceRessource);
			this.logger.info("listOfMissingServices size  " + listOfMissingServices.size());
			this.logger.info("listToAdd size  " + listToAdd.size());

			if (listOfMissingServices.size() > 0) {
				this.logger.info("=> IN MissingServices ");

				for (Service s : listOfMissingServices) {
					this.logger.info("=> For 1 ");

					for (Service service : listToAdd) {
						this.logger.info("=> For 2 ");

						if (s.getSncode().equals(service.getSncode())) {
							this.logger.info(s.getSncode() + " ==  " + service.getSncode());

							if (service.getParam() != null) {
								this.logger.info("New Param " + service.getParam()[0].getPrmDes());

								s.setParam(service.getParam());

							}
						}

					}
					services[i] = s;
					servicesList.add(s);
					i++;

				}
				this.logger.info("<= Out ");

			}

			this.logger.info("listServiceRessource size  " + listServiceRessource.size());

			this.logger.info("servicesList size  " + servicesList.size());
			this.logger.info("services size  " + services.length);

			if (listServiceRessource.size() > 0) {

				this.logger.info(" => IN ressource : ");
				this.logger.info(" =>  listServiceRessources  : " + listServiceRessources);
				for (Service s : services) { // a verifier
					this.logger.info(" => services : " + s.getSncode());

					for (Service sr : listServiceRessources) {
						this.logger.info(" => services sr: " + sr.getSncode());

						if (s.getSncode().equals(sr.getSncode())) {
							this.logger.info(" =>  ");

							servicesList.remove(s);
							this.logger.info(" removed ");
							this.logger.info("  " + s.getSncode());

						}

					}
				}

			}

			this.logger
					.info("size list services core to activate after check ressource from CRM " + servicesList.size());
			Service[] servicesAfterCheck = new Service[servicesList.size()];
			int k = 0;
			for (Service s : servicesList) {
				if (contains(sncodesCUG, ",", Long.toString(s.getSncode()))) {

					cugBean = this.getOrcreateCugForcustomer((Long) contractMap.get("customerId"));

					if (cugBean.getIsSuccessful()) {
						logger.info(" getCugBeanForCustomer  " + cugBean.getIsSuccessful());
						Service service = new Service();
						service.setSncode(s.getSncode());
						service.setSpcode(s.getSpcode());

						List<Parameter> listparm = new ArrayList<>();
						if (cugBean.getCug()[0] != null) {

							com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean cugBeanIn = new ObjectMapper()
									.convertValue(cugBean.getCug()[0],
											com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean.class);

							if (null != cugBeanIn.getMemberShipList() && cugBeanIn.getMemberShipList().length > 0) {
								if (cugBeanIn.getSncode() != null)
									logger.info("******contractCUG sncode *******\t" + cugBeanIn.getSncode());
								if (cugBeanIn.getSpCode() != null)
									logger.info("******contractCUG spcode *******\t" + cugBeanIn.getSpCode());
								if (cugBeanIn.getNumParams() != null)
									logger.info(
											"******contractCUG numParams  *******\t" + cugBeanIn.getNumParams().length);
								if (cugBeanIn.getMemberShipList() != null)
									logger.info("******contractCUG MemberShipList   *******\t"
											+ cugBeanIn.getMemberShipList().length);
								for (com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CugMembershipsBeanOut cugMember : cugBeanIn
										.getMemberShipList()) {

									Parameter params = new Parameter();

									if (cugMember.getCugName() != null)
										params.setPrmDes("CUGNAME" + "_" + cugMember.getCugName());

									if (cugMember.getCugId() != null)
										params.setPrmId(cugMember.getCugId());
									params.setPrmType("");
									if (cugMember.getCugInterlockCode() != null)
										params.setValue(cugMember.getCugInterlockCode());

									if (cugMember.getCugInterlockCode() != null)
										params.setValueDes("CUGNAME" + "_" + cugMember.getCugInterlockCode());
									listparm.add(params);

								}
							}

							service.setParam(listparm.toArray(new Parameter[] {}));

							servicesAfterCheck[k] = service;
						}

					}
				} else {
					servicesAfterCheck[k] = s;

				}
				this.logger.info("service core to activate  ");
				this.logger.info("sncode  " + servicesAfterCheck[k].getSncode());
				this.logger.info("spcode  " + servicesAfterCheck[k].getSpcode());

				if (servicesAfterCheck[k].getType() != null) {
					this.logger.info("type  " + servicesAfterCheck[k].getType());
				}

				if (servicesAfterCheck[k].getDes() != null) {
					this.logger.info("des  " + servicesAfterCheck[k].getDes());
				}

				this.logger.info("param size  " + servicesAfterCheck[k].getParam().length);

				k++;
			}

			activateService.setServices(servicesAfterCheck);
			response = binding.activateAndSetParameterServices(activateService);
		} catch (Exception e) {
			this.logger.error("<=activateServicesCores with error   ");
			throw new Exception(e);
		}
		return response;
	}

	public Map<String, Object> sucessOutputactivateServicesCores(String waitFailure, String taskName,
			List<Service> listToAddAfterCheck, List<Service> listServiceRessource, List<Map<String, Object>> listCugs,
			Boolean isListCugs) {
		logger.info("=> sucessOutputactivateServicesCores IN");

		Map<String, Object> resultsMap = new HashMap<>();
		Boolean isListToAdd = false;
		if (listToAddAfterCheck != null && listToAddAfterCheck.size() != 0) {
			isListToAdd = true;
		}
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", 0);
		resultsMap.put("success", true);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listToAdd", listToAddAfterCheck);
		resultsMap.put("isListToAdd", isListToAdd);
		resultsMap.put("listServiceRessource", listServiceRessource);
		resultsMap.put("listServiceCug", listCugs);
		resultsMap.put("isListCugs", isListCugs);

		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputactivateServicesCores out");

		return resultsMap;
	}

	public Map<String, Object> failOutputactivateServicesCores(Integer retryNbre, String waitFailure, String taskName,
			List<Service> listToAddAfterCheck, List<Service> listServiceRessource, List<Map<String, Object>> listCugs,
			Boolean isListCugs) {
		logger.info("=> failOutputactivateServicesCores IN");

		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("retryNbre", retryNbre + 1);

		resultsMap.put("success", false);
		resultsMap.put("taskName", taskName);
		resultsMap.put("listServiceRessource", listServiceRessource);
		resultsMap.put("listToAdd", listToAddAfterCheck);
		resultsMap.put("listServiceCug", listCugs);
		resultsMap.put("isListCugs", isListCugs);
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputactivateServicesCores out");

		return resultsMap;
	}

	/* ContractServicesAddBeanIn Methods */

	public boolean contractServicesAdd(Map<String, Object> contractMap, Long profileId,
			List<Map<String, Object>> listServiceRessource) throws Exception {
		boolean successAdd = false;

		try {

			this.logger.info("=> contractServicesAdd IN");
			ContractServicesAddBeanIn newreq = new ContractServicesAddBeanIn();

			ComLhsWsBeansV2Contract_services_addServicesBeanIn[] services = new ComLhsWsBeansV2Contract_services_addServicesBeanIn[listServiceRessource
					.size()];

			for (int i = 0; i < listServiceRessource.size(); i++) {
				Service serviceBean = new ObjectMapper().convertValue(listServiceRessource.get(i), Service.class);
				ComLhsWsBeansV2Contract_services_addServicesBeanIn service = new ComLhsWsBeansV2Contract_services_addServicesBeanIn();

				service.setSncode(serviceBean.getSncode());
				logger.info("snCode=" + serviceBean.getSncode());
				service.setSpcode(serviceBean.getSpcode());
				logger.info("Spcode=" + serviceBean.getSpcode());
				service.setProfileId(profileId);
				logger.info("profileId=" + profileId);

				services[i] = service;
			}

			newreq.setServices(services);
			newreq.setCoId((Long) contractMap.get("coId"));
			logger.info("co_id=" + (Long) contractMap.get("coId"));

			ContractServicesAddServiceSoapBindingStub binding = this.config.consumeContractServicesAddService();
			ContractServicesAddBeanOut response = new ContractServicesAddBeanOut();

			In0 int0 = new In0();
			logger.info(newreq.getCoId());
			int0.setInput(newreq);
			response = binding.execute(int0);

		} catch (UnexpectedErrorFault e) {
			this.logger.error("<= UnexpectedErrorFault contractServicesAdd with exception" + e.getFaultstring());
			this.logger.error("<= UnexpectedErrorFault contractServicesAdd with Erreur Code" + e.getFaultcode());
			throw e;
		}

		catch (Exception e) {
			this.logger.error("<= Exception  contractServicesAdd with exception" + e.getCause());
			throw e;
		}

		successAdd = true;
		this.logger.info("<= contractServicesAdd ");
		return successAdd;
	}

	public Map<String, Object> sucessOutputContractServicesAdd(String waitFailure, String taskName) {
		logger.info("=> sucessOutputContractServicesAdd In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", true);
		resultsMap.put("retryNbre", 0);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= sucessOutputContractServicesAdd Out ");
		return resultsMap;
	}

	public Map<String, Object> failOutputContractServicesAdd(Integer retryNbre, String waitFailure, String taskName) {

		logger.info("=> failOutputContractServicesAdd In ");
		Map<String, Object> resultsMap = new HashMap<>();
		resultsMap.put("success", false);
		resultsMap.put("retryNbre", retryNbre + 1);
		resultsMap.put("waitFailure", waitFailure);// waitFailure
		resultsMap.put("taskName", taskName);// taskName
		// OUTPUT
		for (Entry<String, Object> entry : resultsMap.entrySet()) {
			logger.info("[OUTPUT] " + entry.getKey() + " : " + entry.getValue());
		}
		logger.info("<= failOutputContractServicesAdd Out ");
		return resultsMap;
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
