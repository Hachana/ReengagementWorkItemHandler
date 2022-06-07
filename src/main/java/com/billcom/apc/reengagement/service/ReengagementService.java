package com.billcom.apc.reengagement.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;

import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.GetBalanceAndDateBeanOutputV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.OptionV2;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.AdjustBalanceResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.AddFaFResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.RemoveFaFResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.PercistReengagementOrderResponse;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementBean;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.UpdateReengagementOrderResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamterResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.AddCugRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ChangeRatePlanResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CheckRateplanResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CreateContractRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.CreateContractResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.DesactivateServicesResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.GetCategoryResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.HasPendingRequestResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.LimitAddResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.AddGrhRequestResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.AssignPromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.DeletePromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.GetCustomerPromoResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetBscsOfferAndServciesFromCrmDemandeResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetListOfServiceToactivateContratResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.GetOrcreateCugForcustomerResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBeanCrm;
import com.billcom.apc.generatedSOAPReengagement.gps.AddUpdateJackpotResponse;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.utils.ReengagementConfigHandler;
import com.otn.dsi.ws.AddFaFBO;
import com.otn.dsi.ws.ListFaFBO;
import com.otn.dsi.ws.RemoveFaFBO;

/**
 * @author arij.dhahbi
 *
 */

public interface ReengagementService {

	public ReengagementConfigHandler getConfig();

	public Integer getRetryNumber(Integer inputRetryNumber) throws IOException;

	public void printWorkItem(Logger logger, WorkItem wi);// print all inputs of workItem

	// verifyPending
	public HasPendingRequestResponse verifyPending(Long coId) throws Exception;

	public Map<String, Object> sucessOutputVerifyPending(String waitFailure, Boolean isPending, String taskName);

	public Map<String, Object> sucessOutputVerifyPendingTrue(Integer retryNbre, String waitFailure, Boolean isPending,
			String taskName);

	public Map<String, Object> failOutputVerifyPending(Integer retryNbre, String waitFailure, String taskName);

	// DeactivateContract
	public OperationResponse deactivateContract(Long coId) throws Exception;

	public Map<String, Object> sucessOutputDeactivateContract(String waitFailure, String taskName);

	public Map<String, Object> failOutputDeactivateContract(Integer retryNbre, String waitFailure, String taskName);

	// changeRatePlan
	public ChangeRatePlanResponse changeRatePlanReengagement(Long coId, Long tmcode) throws Exception;

	public Map<String, Object> sucessOutputChangeRatePlan(String waitFailure, String taskName);

	public Map<String, Object> failOutputChangeRatePlan(Integer retryNbre, String waitFailure, String taskName);

	// GetMigrationGroup
	public GetCategoryResponse getMigrationGroup(MigrationOutputBean migrationOutputBean) throws Exception;

	public Map<String, Object> sucessOutputGetMigrationGroup(String waitFailure, Boolean isSameGroup, String taskName,
			Boolean isSameTmcode);

	public Map<String, Object> failOutputGetMigrationGroup(Boolean isSameGroup, Integer retryNbre, String waitFailure,
			String taskName);

	// ActivateOnHoldService
	public OperationResponse activateOnHoldService(Long coId) throws Exception;

	public Map<String, Object> sucessOutputActivateOnHoldService(String waitFailure, String taskName);

	public Map<String, Object> failOutputActivateOnHoldService(Integer retryNbre, String waitFailure, String taskName);

	// GetCustomerPromo
	public GetCustomerPromoResponse getCustomerPromo(Long csId) throws Exception;

	public Map<String, Object> sucessOutputGetCustomerPromo(String waitFailurePromotion, String taskName,
			List<Map<String, Object>> listPromotions, String retriesConfigNbre);

	public Map<String, Object> failOutputGetCustomerPromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName, String retriesConfigNbre);

	// DeletePromo
	public DeletePromoResponse deletePromo(Long assignSeqNo, String canalName, long csId, Calendar deleteDate,
			long packId, String userName) throws Exception;

	public Map<String, Object> sucessOutputDeletePromo(String waitFailurePromotion, String taskName);

	public Map<String, Object> failOutputDeletePromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName);

	// AssignPromo
	public AssignPromoResponse assignPromo(AssignPromoRequest request) throws Exception;

	public Map<String, Object> sucessOutputAssignPromo(String waitFailurePromotion, String taskName);

	public Map<String, Object> failOutputAssignPromo(Integer retryNbrePromotion, String waitFailurePromotion,
			String taskName);

	// GetListFaF
	public ListFaFBO listFaF(String rpCodePub, Long internationNumberPhone) throws Exception;

	public Map<String, Object> sucessOutputListFaF(String waitFailure, String taskName, ArrayList<Long> result,
			boolean subscriberFriends);

	public Map<String, Object> failOutputListFaF(Integer retryNbre, String waitFailure, String taskName);

	// CheckMaxFaF
	public LimitAddResponse checkMaxFaF(String shortPhoneNumber) throws Exception;

	public Map<String, Object> sucessOutputCheckMaxFaF(String waitFailure, String taskName, Integer maxAdded,
			Boolean isSameTmcode);

	public Map<String, Object> failOutputCheckMaxFaF(Integer retryNbre, String waitFailure, String taskName);

	// AdjustFaF
	public AddFaFBO addFaF(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber) throws Exception;

	public AddFaFResponse addFaFIN(ArrayList<Long> listSubscriberFriends, String shortPhoneNumber, String tmcode)
			throws Exception;

	public RemoveFaFBO removeFaF(ArrayList<Long> listSubscriberFriends, String msisdn) throws Exception;

	public RemoveFaFResponse removeFaFIN(ArrayList<Long> listSubscriberFriends, String msisdn) throws Exception;

	public Map<String, Object> sucessOutputAdjustFaF(String waitFailure, String taskName, Boolean isListRemoveFaF);

	public Map<String, Object> failOutputAdjustFaF(Integer retryNbre, String waitFailure, String taskName);

	// AddJackPot
	public AddUpdateJackpotResponse addUpdateJackpot(String osmCode, String user, String custnum, BigDecimal amountHt,
			BigDecimal facilityAmount, Long commitment) throws Exception;

	public Map<String, Object> sucessOutputAddUpdateJackpot(String waitFailureGPS, String taskName);

	public Map<String, Object> failOutputAddUpdateJackpot(Integer retryNbreGPS, String waitFailureGPS, String taskName);

	// Get Or create Cug For customer
	public GetOrcreateCugForcustomerResponse getOrcreateCugForcustomer(Long custId) throws Exception;

	public Map<String, Object> sucessOutputGetOrcreateCugForcustomer(String waitFailure, String taskName,
			List<Map<String, Object>> listCugs);

	public Map<String, Object> failOutputGetOrcreateCugForcustomer(Integer retryNbre, String waitFailure,
			String taskName);

	// Desactivate Services
	public DesactivateServicesResponse desactivateServices(DesactivateServicesRequest request) throws Exception;

	public Map<String, Object> sucessOutputDesactivateServices(String waitFailure, String taskName);

	public Map<String, Object> failOutputDesactivateServices(Integer retryNbre, String waitFailure, String taskName);

	// DesactivateNonCommonServices
	public DesactivateServicesResponse desactivateNonCommonServices(Long coId, long[] sncodeToDesactivate)
			throws Exception;

	public Map<String, Object> sucessOutputDesactivateNonCommonServices(String waitFailure, String taskName,
			List<Map<String, Object>> listToDeleteAfterCheck);

	public Map<String, Object> failOutputDesactivateNonCommonServices(Integer retryNbre, String waitFailure,
			String taskName, List<Map<String, Object>> listToDelete);

	// ActivateServiceParamterResponse
	public ActivateServiceParamterResponse activateServiceParamter(ActivateServiceParamter request) throws Exception;

	public Map<String, Object> sucessOutputActivateServiceParamter(String waitFailure, String taskName);

	public Map<String, Object> failOutputActivateServiceParamter(Integer retryNbre, String waitFailure,
			String taskName);

	// AddGRH
	public AddGrhRequestResponse addGrh(Logger logger, Map<String, Object> grhMap, ContractDetails contractDetails,
			Long custId, String user, Long reqId) throws Exception;

	public Map<String, Object> sucessOutputModifierDFE(String waitFailureDFE, String taskName, String exist);

	public Map<String, Object> failOutputModifierDFE(Integer retryNbreDFE, String waitFailureDFE, String taskName,
			String exist);

	// CheckRateplan
	public CheckRateplanResponse checkRateplan(Map<String, Object> migrationMap, Map<String, Object> contractMap)
			throws Exception;

	public Map<String, Object> sucessOutputCheckRateplan(String waitFailure, String taskName, List<Long> listSncode,
			int sizeDesactivateServices, ArrayList<Service> missingServices, ArrayList<Service> communServices);

	public Map<String, Object> failOutputCheckRateplan(Integer retryNbre, String waitFailure, String taskName);

	// SmsNotif
	public Map<String, Object> sucessOutputSmsNotif(String waitFailure, String taskName);

	public Map<String, Object> failOutputSmsNotif(Integer retryNbre, String waitFailure, String taskName);

	// CreerContrat
	public CreateContractResponse createContract(CreateContractRequest request) throws Exception;

	public Map<String, Object> sucessOutputCreateContract(String waitFailure, String taskName,
			Map<String, Object> contractMap, String oldCoId, String oldCoIdPub);

	public Map<String, Object> failOutputCreateContract(Integer retryNbre, String waitFailure, String taskName,
			Map<String, Object> contractMap);

	// adjustBalanceIN
	public AdjustBalanceResponse adjustBalanceIn(Map<String, Object> reengagementMap, Map<String, Object> contractMap,
			Long cust_id, Long mainBalanceAmount, Long oldCoId) throws Exception;

	public Map<String, Object> successAdjustBalanceIN(String waitFailure, String taskName);

	public Map<String, Object> failAdjustBalanceIN(Integer retryNbre, String waitFailure);

	public AdjustBalanceResponse adjustBalanceBscs(Map<String, Object> migrationMap, Map<String, Object> contractMap,
			Long cust_id, Long mainBalanceAmount, Long oldCoId) throws Exception;

	// getBscsOfferAndServciesFromCrmDemande
	public GetBscsOfferAndServciesFromCrmDemandeResponse getBscsOfferAndServciesFromCrmDemande(
			GetBscsOfferAndServciesFromCrmDemandeRequest request) throws Exception;

	public Map<String, Object> successGetBscsOfferAndServciesFromCrmDemande(String waitFailure, String taskName,
			List<Map<String, Object>> listMigrationMapOutput, List<Map<String, Object>> listUpdateOptionMapOutput,
			List<Map<String, Object>> listContractOfGrh, Integer sizeListContractOfGrh, String retriesConfigNbre,
			String retryWaiting, String pendingWaiting, List<Long> listcontracts, Integer orderId,
			List<Map<String, Object>> listContractOfGrhWithoutMigration, Integer sizeListContractOfGrh_Without_Mig,
			String waitingLot, Integer nbreContratLot);

	public Map<String, Object> failGetBscsOfferAndServciesFromCrmDemande(Integer retryNbre, String waitFailure,
			String retriesConfigNbre, String retryWaiting, String pendingWaiting);

	// percistReengagementOrder
	public PercistReengagementOrderResponse percistReengagementOrder(List<ReengagementBean> reengagementBeans)
			throws Exception;

	public Map<String, Object> sucessOutputPercistReengagementOrder(String waitFailure, String taskName,
			boolean isMigration, boolean isUpdateOption, boolean isInjectionRBF, boolean isInjectionGPS,
			String dueDateTimer);

	public Map<String, Object> failOutputPercistReengagementOrder(Integer retryNbre, String waitFailure,
			String taskName);

	// getListOfServiceToactivateContrat
	public GetListOfServiceToactivateContratResponse getListOfServiceToactivateContratRequest(
			GetListOfServiceToactivateContratRequest request) throws Exception;

	public Map<String, Object> sucessOutputGetListOfServiceToactivateContrat(String waitFailure,
			Map<String, Object> mapServiceToActivate, String taskName, Boolean isListCugs, Map<String, Object> cugMap,
			Boolean isListRessources, List<Map<String, Object>> listServiceRessource);

	public Map<String, Object> failOutputGetListOfServiceToactivateContrat(Integer retryNbre, String waitFailure,
			String taskName);

	// PrepareMigration
	public Map<String, Object> successPrepareMigration(String waitFailure, String taskName,
			Integer sizeListContractOfMigration, List<Map<String, Object>> listContractOfMigration);

	public Map<String, Object> failPrepareMigration(Integer retryNbre, String waitFailure, String taskName);

	// GetAction
	public Map<String, Object> successGetAction(String waitFailure, String taskName,
			Integer sizeListContractOfUpdateOption, List<Map<String, Object>> listContractOfUpdateOption,
			boolean isListToDelete, boolean isListToAdd, boolean isListToUpdate, boolean isListCugs,
			boolean isListRessources, List<Map<String, Object>> listToDelete, List<Service> listToAdd,
			List<Map<String, Object>> listToUpdate, List<Map<String, Object>> listServiceCug,
			List<Service> listServiceRessource, List<ServiceBeanCrm> listToAddService,
			List<ServiceBeanCrm> listToDeleteService, Map<String, Object> cugMapIntra);

	public Map<String, Object> failGetAction(Integer retryNbre, String waitFailure, String taskName);

	// updateReengagementOrder
	public UpdateReengagementOrderResponse updateReengagementOrder(ReengagementBean reengagement) throws Exception;

	public Map<String, Object> sucessOutputUpdateReengagementOrder(String waitFailure, String taskName);

	public Map<String, Object> failOutputUpdateReengagementOrder(Integer retryNbre, String waitFailure,
			String taskName);

	// GetBalancesIN
	public GetBalanceAndDateBeanOutputV2 getBalanceAndDate(Map<String, Object> contractMap) throws Exception;

	public OperationResponse insertBalanceHist(Map<String, Object> contractMap, String balanceType, String unit,
			double remAmount, Long orderId) throws Exception;

	public Map<String, Object> successOutputGetBalancesIN(String waitFailure, String taskName, boolean insertBalance,
			List<OptionV2> listOptions, String mainBalanceAmount);

	public Map<String, Object> failOutputGetBalancesIN(Integer retryNbre, String waitFailure, String taskName);

	public Map<String, Object> successOutputInserttBalancesIN(String waitFailure, String taskName,
			List<OptionV2> listOptions);

	public Map<String, Object> failOutputInsertBalancesIN(Integer retryNbre, String waitFailure,
			List<OptionV2> listOptions, Integer j);


	public Map<String, Object> sucessOutputSetContractOrder(String waitFailure, String taskName, Integer coid);

	public Map<String, Object> failOutputSetContractOrder(Integer retryNbre, String waitFailure, String taskName,
			Integer coid);

	// AddCug
	public OperationResponse addCug(AddCugRequest request) throws Exception;

	public Map<String, Object> sucessOutputAddCug(String waitFailure, String taskName);

	public Map<String, Object> failOutputAddCug(Integer retryNbre, String waitFailure, String taskName);

	// AdjustFacture
	public Map<String, Object> successAdjustFacture(String waitFailure, String taskName);

	public Map<String, Object> failAdjustFacture(Integer retryNbre, String waitFailure, String taskName);

	// ActivateServices
	public Map<String, Object> sucessOutputactivateServices(String waitFailure, String taskName);

	public Map<String, Object> failOutputactivateServices(Integer retryNbre, String waitFailure, String taskName);

	// ActivateServicesCores
	public OperationResponse activateServicesCores(Map<String, Object> contractMap, List<Service> listOfMissingServices,
			List<Service> listToAdd, List<Service> listServiceRessource, List<Map<String, Object>> listCugs)
			throws Exception;

	public Map<String, Object> sucessOutputactivateServicesCores(String waitFailure, String taskName,
			List<Service> listToAdd_AfterCheck, List<Service> listServiceRessource, List<Map<String, Object>> listCugs,
			Boolean isListCugs);

	public Map<String, Object> failOutputactivateServicesCores(Integer retryNbre, String waitFailure, String taskName,
			List<Service> listToAdd_AfterCheck, List<Service> listServiceRessource, List<Map<String, Object>> listCugs,
			Boolean isListCugs);

	// ContractServicesAddBeanIn
	public boolean contractServicesAdd(Map<String, Object> contractMap, Long profileId,
			List<Map<String, Object>> listServiceRessource) throws Exception;

	public Map<String, Object> sucessOutputContractServicesAdd(String waitFailure, String taskName);

	public Map<String, Object> failOutputContractServicesAdd(Integer retryNbre, String waitFailure, String taskName);

}
