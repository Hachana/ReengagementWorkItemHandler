package com.billcom.apc.reengagement.workItem;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.ActivateServiceParamterResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Parameter;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.Service;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ServiceBeanCrm;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */

public class UpdateParametersWorkItemHandler implements WorkItemHandler {
	private Logger logger = Logger.getLogger(UpdateParametersWorkItemHandler.class);
	private String taskName;

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
/*
 * empty bloce
 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> UpdateParametersWorkItemHandler in: Process Id:: " + wi.getProcessInstanceId());
		this.logger.info("Reengagement in progress...Activation Services");
		 ReengagementService reengagementService = new ReengagementServiceImp();
			 Map<String, Object> contractMap;
			 List<Map<String, Object>> listToUpdate;
				 String waitFailure;
				 Integer retryNbre;
				 AutoRecycle autoRecycle;

		waitFailure = "0s";
		retryNbre = 0;
		autoRecycle = new AutoRecycle();

		try {
			// retryNbre
						retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			taskName = reengagementService.getConfig().getPropValues("UpdateParametersWIH");
			logger.info("taskName::" + taskName);
			listToUpdate = (List<Map<String, Object>>) wi.getParameter("listToUpdate");
			contractMap = (Map<String, Object>) wi.getParameter("contractMap");


			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			this.logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}


			Service[] services = new Service[listToUpdate.size()];
			for (int i = 0; i < listToUpdate.size(); i++) {

				ServiceBeanCrm serviceBean = new ObjectMapper().convertValue(listToUpdate.get(i), ServiceBeanCrm.class);
				Service service = new Service();

				Parameter param = new Parameter();
				if (serviceBean.getParameterDescription() != null) {
					param.setPrmDes(serviceBean.getParameterDescription());
					param.setPrmId(Long.parseLong(serviceBean.getParameterId()));
					param.setValue(serviceBean.getParameterValue());
					param.setValueDes(serviceBean.getValueDes());
					param.setPrmType(serviceBean.getPrmType());
					Parameter[] params = new Parameter[] { param };
					service.setParam(params);

				}

				service.setSncode(serviceBean.getSncode());
				service.setSpcode(serviceBean.getSpcode());

				services[i] = service;

			}

			ActivateServiceParamter request = new ActivateServiceParamter();

			request.setCoId((Long) contractMap.get("coId"));

			request.setCoIdPub((String) contractMap.get("contractCode"));

			request.setServices(services);

			ActivateServiceParamterResponse response = reengagementService.activateServiceParamter(request);

			this.logger.info("response : " + response.getIsSuccessful());

			if (response.getIsSuccessful().booleanValue()) {
				wim.completeWorkItem(wi.getId(),
						reengagementService.sucessOutputActivateServiceParamter(waitFailure, taskName));
			} else {
				this.logger.info("IsSuccessful " + response.getIsSuccessful());
				this.logger.info("BscsErrorCode " + response.getBscsErrorCode());
				this.logger.info("Comment " + response.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputActivateServiceParamter(retryNbre, waitFailure, taskName));
			}
		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputActivateServiceParamter(retryNbre, waitFailure, taskName));
		}
	}

}