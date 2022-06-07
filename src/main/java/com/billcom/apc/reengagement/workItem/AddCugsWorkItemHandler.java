package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.*;

import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */
public class AddCugsWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(AddCugsWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		Map<String, Object> contractMap;
		List<Map<String, Object>> listCugs;
		String taskName = "";
		String waitFailure;
		Integer retryNbre;
		AutoRecycle autoRecycle;
		Map<String, Object> cugMapIntra;
		boolean found = false;
		this.logger.info("=> AddCugsWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		this.logger.info("Reengagement in progress...Add Cugs");
		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();
		ReengagementService reengagementService = new ReengagementServiceImp();

		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("AddCugsWIH");
			logger.info("taskName::" + taskName);

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			listCugs = (List<Map<String, Object>>) wi.getParameter("listServiceCug");
			cugMapIntra = (Map<String, Object>) wi.getParameter("cugMapIntra");
			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			ActivateServiceParamter request = new ActivateServiceParamter();

			request.setCoId((Long) contractMap.get("coId"));

			request.setCoIdPub((String) contractMap.get("contractCode"));
			List<Parameter> listparm = new ArrayList<>();

			if (CollectionUtils.isNotEmpty(listCugs)) {

				for (int i = 0; i < listCugs.size(); i++) {
					com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean cugBeanIn = new ObjectMapper()
							.convertValue(listCugs.get(i),
									com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean.class);

					if (cugMapIntra != null && (Long) cugMapIntra.get("sncode") == cugBeanIn.getSncode()) {
						found = true;
						this.logger.info("cugMapIntra.get(\"sncode\") = " + (Long) cugMapIntra.get("sncode"));
						this.logger.info("cugMapIntra.get(\"sncode\") IN ");

						this.logger.info("found =" + found);
						List<Service> services = new ArrayList<>();
						Service service = new Service();
						Parameter param = new Parameter();
						this.logger.info("cugBeanIn.getSncode() = " + cugBeanIn.getSncode());

						service.setSncode(cugBeanIn.getSncode());
						this.logger.info("cugBeanIn.getSpCode() = " + cugBeanIn.getSpCode());

						service.setSpcode(cugBeanIn.getSpCode());

						if (null != cugBeanIn.getMemberShipList() && cugBeanIn.getMemberShipList().length > 0) {

							this.logger.info("=> MemberShipList in ");
							this.logger.info("cugBeanIn.getCugId() = " + cugBeanIn.getMemberShipList()[0].getCugId());

							param.setPrmId(cugBeanIn.getMemberShipList()[0].getCugId());
							this.logger.info("cugBeanIn.getCugInterlockCode() = "
									+ cugBeanIn.getMemberShipList()[0].getCugInterlockCode());

							param.setValue(cugBeanIn.getMemberShipList()[0].getCugInterlockCode());

							this.logger
									.info("cugBeanIn.getCugName() = " + cugBeanIn.getMemberShipList()[0].getCugName());

							param.setValueDes("CUG_NAME_" + cugBeanIn.getMemberShipList()[0].getCugName());

							this.logger.info("param.setValueDes = " + param.getValueDes());

							listparm.add(param);
							service.setParam(listparm.toArray(new Parameter[] {}));
							services.add(service);
						}
						request.setServices(services.toArray(new Service[] {}));
					}
				}

				if (!found) {
					com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean cugBeanIn = new ObjectMapper()
							.convertValue(listCugs.get(0),
									com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.CUGBean.class);

					List<Service> services = new ArrayList<>();
					Service service = new Service();
					Parameter param = new Parameter();
					if (cugMapIntra != null)

					{
						this.logger.info("cugBeanIn.getSncode() = " + (Long) cugMapIntra.get("sncode"));

						service.setSncode((Long) cugMapIntra.get("sncode"));
						this.logger.info("cugBeanIn.getSpCode() = " + (Long) cugMapIntra.get("spcode"));

						service.setSpcode((Long) cugMapIntra.get("spcode"));
					}
					if (null != cugBeanIn.getMemberShipList() && cugBeanIn.getMemberShipList().length > 0) {

						this.logger.info("=> MemberShipList in ");
						this.logger.info("cugBeanIn.getCugId() = " + cugBeanIn.getMemberShipList()[0].getCugId());

						param.setPrmId(cugBeanIn.getMemberShipList()[0].getCugId());
						this.logger.info("cugBeanIn.getCugInterlockCode() = "
								+ cugBeanIn.getMemberShipList()[0].getCugInterlockCode());

						param.setValue(cugBeanIn.getMemberShipList()[0].getCugInterlockCode());

						this.logger.info("cugBeanIn.getCugName() = " + cugBeanIn.getMemberShipList()[0].getCugName());

						param.setValueDes("CUG_NAME_" + cugBeanIn.getMemberShipList()[0].getCugName());

						this.logger.info("param.setValueDes = " + param.getValueDes());

						listparm.add(param);
						service.setParam(listparm.toArray(new Parameter[] {}));
						services.add(service);
					}
					request.setServices(services.toArray(new Service[] {}));
				}

			}

			ActivateServiceParamterResponse response = reengagementService.activateServiceParamter(request);
			if (response.getIsSuccessful().booleanValue()) {
				this.logger.info("activateServiceParamter  IsSuccessful" + response.getIsSuccessful());

				this.logger.info("coId = " + (Long) contractMap.get("coId"));

				OperationResponse operationActivateOnHolde = reengagementService
						.activateOnHoldService((Long) contractMap.get("coId"));
				if (operationActivateOnHolde.getIsSuccessful().booleanValue()) {
					this.logger
							.info("activateOnHoldService  IsSuccessful" + operationActivateOnHolde.getIsSuccessful());

					wim.completeWorkItem(wi.getId(), reengagementService.sucessOutputAddCug(waitFailure, taskName));
				} else {
					this.logger.info("respone=" + operationActivateOnHolde.getIsSuccessful());
					this.logger.info("bscsErrorCode= " + operationActivateOnHolde.getBscsErrorCode());
					this.logger.info("comment= " + operationActivateOnHolde.getComment());
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputAddCug(retryNbre, waitFailure, taskName));
				}
			} else {
				this.logger.info("respone=" + response.getIsSuccessful());
				this.logger.info("bscsErrorCode= " + response.getBscsErrorCode());
				this.logger.info("comment= " + response.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputAddCug(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger
					.error("<= AddCugsWorkItemHandler with error  out: Process Id::  " + wi.getProcessInstanceId() + e);

			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(), reengagementService.failOutputAddCug(retryNbre, waitFailure, taskName));
		}

	}

}