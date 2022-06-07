package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.*;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ComLhsWsBeansV2Contract_services_addServicesBeanIn;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ContractDetails;
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

public class CreateContractWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(CreateContractWorkItemHandler.class);
	private Map<String, Object> contractMap;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		logger.info("=> CreateContractWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		List<Map<String, Object>> listCugs;
		String oldCoId;
		String oldCoIdPub;
		String taskName = "";
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		List<Map<String, Object>> listServiceRessource;
		Map<String, Object> cugMap;
		Map<String, Object> mapServiceToActivate;
		Map<String, Object> migrationMap;
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		ReengagementService reengagementService = new ReengagementServiceImp();

		try {

			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("CreateContractWIH");
			logger.info("taskName::" + taskName);
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			listServiceRessource = (List<Map<String, Object>>) wi.getParameter("listServiceRessource");
			cugMap = (Map<String, Object>) wi.getParameter("cugMap");

			mapServiceToActivate = (Map<String, Object>) wi.getParameter("mapServiceToActivate");
			listCugs = (List<Map<String, Object>>) wi.getParameter("listCugs");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			logger.info("listCugs=" + listCugs);
			ContractDetails contractDetail = new ObjectMapper().convertValue(contractMap, ContractDetails.class);
			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);

			ServiceToactivateContratResponse responseContract = new ObjectMapper().convertValue(
					mapServiceToActivate.get(String.valueOf(contractDetail.getCoId())),
					ServiceToactivateContratResponse.class);

			ServiceBean[] respServices = responseContract.getServicesForContract();
			Service[] services = new Service[respServices.length];
			Service[] servicesAfterCheck = new Service[respServices.length];

			this.logger.info("respServices size =" + respServices.length);
			int k = 0;

			for (ServiceBean serviceBean : respServices) {
				Service service = new Service();
				this.logger.info("k =" + k);

				if ((cugMap != null) && (serviceBean.getSncode().equals(cugMap.get("sncode")))) {
					this.logger.info(" ==>  cugMap IN ");

					service.setSncode(serviceBean.getSncode());
					service.setSpcode(serviceBean.getSpcode());

					List<Parameter> listparm = new ArrayList<>();
					for (int i = 0; i < listCugs.size(); i++) {
						com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean cugBeanIn = new ObjectMapper()
								.convertValue(listCugs.get(i),
										com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean.class);

						if (null != cugBeanIn.getMemberShipList() && cugBeanIn.getMemberShipList().length > 0) {
							if (cugBeanIn.getSncode() != null)
								logger.info("******contractCUG sncode *******\t" + cugBeanIn.getSncode());
							if (cugBeanIn.getSpCode() != null)
								logger.info("******contractCUG spcode *******\t" + cugBeanIn.getSpCode());
							if (cugBeanIn.getNumParams() != null)
								logger.info("******contractCUG numParams  *******\t" + cugBeanIn.getNumParams().length);
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

					}

					service.setParam(listparm.toArray(new Parameter[] {}));

					this.logger.info("service.getSncode() CUG = " + service.getSncode());
					this.logger.info("service.getSpcode() CUG = " + service.getSpcode());
					services[k] = service;
					servicesAfterCheck[k] = service;
					k++;

				} else {

					Parameter[] params = new Parameter[serviceBean.getParam().length];

					if ((serviceBean.getParam().length > 0)) {

						for (int i = 0; i < serviceBean.getParam().length; i++) {

							this.logger.info("serviceBean.getParameterDescription() != null");
							this.logger.info("serviceBean ParameterDescription ="
									+ serviceBean.getParam()[i].getParameterDescription());

							Parameter param = new Parameter();

							if (serviceBean.getParam()[i].getParameterDescription() != null) {
								param.setPrmDes(serviceBean.getParam()[i].getParameterDescription());
							}

							if (serviceBean.getParam()[i].getParameterId() != null) {
								param.setPrmId(Long.parseLong(serviceBean.getParam()[i].getParameterId()));
							}
							if (serviceBean.getParam()[i].getParameterValue() != null) {
								param.setValue(serviceBean.getParam()[i].getParameterValue());
							}
							if (serviceBean.getParam()[i].getValueDes() != null) {
								param.setValueDes(serviceBean.getParam()[i].getValueDes()); // a verifier

							}

							if (serviceBean.getParam()[i].getPrmType() != null) {
								param.setPrmType(serviceBean.getParam()[i].getPrmType()); // a verifier

							}

							params[i] = param;
							this.logger.info("params des = " + params[i].getPrmDes());
							this.logger.info("paramsId = " + params[i].getPrmId());

						}
						service.setParam(params);
					}

					service.setSncode(serviceBean.getSncode());
					service.setSpcode(serviceBean.getSpcode());
					this.logger.info("service.getSncode() = " + service.getSncode());
					this.logger.info("service.getSpcode() = " + service.getSpcode());
					services[k] = service;
					servicesAfterCheck[k] = service;
					k++;
				}
			}

			CreateContractRequest request = new CreateContractRequest();
			request.setCoId(contractDetail.getCoId());
			this.logger.info("coid injected = " + contractDetail.getCoId());

			request.setRpCode(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());
			this.logger.info(
					"tmcode injected = " + migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());

			// Traitement services ressources
			if (listServiceRessource != null) {

				Long profileId = (long) 0;
				int g = 0; // increment services_AfterCheck
				ComLhsWsBeansV2Contract_services_addServicesBeanIn[] servicesRess = new ComLhsWsBeansV2Contract_services_addServicesBeanIn[listServiceRessource
						.size()];
				for (int i = 0; i < listServiceRessource.size(); i++) {
					ServiceBean serviceBeanRess = new ObjectMapper().convertValue(listServiceRessource.get(i),
							ServiceBean.class);
					ComLhsWsBeansV2Contract_services_addServicesBeanIn serviceRess = new ComLhsWsBeansV2Contract_services_addServicesBeanIn();

					serviceRess.setSncode(serviceBeanRess.getSncode());
					logger.info("snCode ressource=" + serviceBeanRess.getSncode());
					serviceRess.setSpcode(serviceBeanRess.getSpcode());
					logger.info("Spcode ressource=" + serviceBeanRess.getSpcode());
					serviceRess.setProfileId(profileId);
					servicesRess[i] = serviceRess;
				}
				this.logger.info("services ressources size = " + servicesRess.length);

				List<Service> listServiceAfterCheck = new ArrayList<>();

				for (int i = 0; i < services.length; i++) {
					listServiceAfterCheck.add(services[i]);

				}

				for (int i = 0; i < services.length; i++) {

					for (int j = 0; j < servicesRess.length; j++) {
						logger.info("services sncode = " + services[i].getSncode());
						logger.info("services ressources sncode = " + servicesRess[j].getSncode());

						if ((services[i].getSncode()).equals(servicesRess[j].getSncode())
								|| ((services[i].getSncode()).equals(servicesRess[j].getSncode()))) {
							listServiceAfterCheck.remove(services[i]);
							this.logger.info("service removed = " + services[i].getSncode());

						}
					}

				}

				servicesAfterCheck = new Service[listServiceAfterCheck.size()];

				for (Service s : listServiceAfterCheck) {
					servicesAfterCheck[g] = s;
					g++;
				}

			}
			this.logger.info("services before check size = " + services.length);

			this.logger.info("services after check size = " + servicesAfterCheck.length);

			// Fin traitement services ressources
			request.setServices(servicesAfterCheck);
			this.logger.info("services injected size = " + servicesAfterCheck.length);

			logger.info("request anc= " + new ObjectMapper().convertValue(request, Map.class));

			CreateContractResponse response = reengagementService.createContract(request);

			if (response.getIsSuccessful().booleanValue()) {

				logger.info("response= " + new ObjectMapper().convertValue(response, Map.class));
				oldCoId = String.valueOf(contractDetail.getCoId());
				oldCoIdPub = String.valueOf(contractDetail.getContractCode());

				contractMap.put("tmcode", migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());
				contractMap.put("coId", response.getCoId());
				contractMap.put("contractCode", response.getCoIdPub());

				wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputCreateContract(waitFailure, taskName,
						contractMap, oldCoId, oldCoIdPub));
			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				this.logger.info("<= CreateContract with error out: Process Id::  " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputCreateContract(retryNbre, waitFailure, taskName, contractMap));
			}
		} catch (Exception e) {
			this.logger.info("<= CreateContract with error out: Process Id::  " + wi.getProcessInstanceId());

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputCreateContract(retryNbre, waitFailure, taskName, contractMap));
		}
	}

}
