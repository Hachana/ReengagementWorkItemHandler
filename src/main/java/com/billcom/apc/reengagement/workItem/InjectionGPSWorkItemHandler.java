package com.billcom.apc.reengagement.workItem;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAP.PBI_JBPM.jbpmOrderReengagement.InjectGpsBean;

import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.CustomerHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.CustomerReference;
import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.GetCustomerDetailsRequest;
import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.GetCustomerDetailsResponse;
import com.billcom.apc.generatedSOAPReengagement.gps.AddUpdateJackpotResponse;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */
public class InjectionGPSWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(InjectionGPSWorkItemHandler.class);

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		this.logger.info("=> InjectionGPSWorkItemHandler in: Process Id::  " + wi.getProcessInstanceId());
		ReengagementService reengagementService = new ReengagementServiceImp();
		InjectGpsBean gpsBean = new InjectGpsBean();

		String taskName = "";
		String custNum = "";
		String user = "";
		String custId = "";
		this.logger.info("Reengagement in progress...injection GPS Reengagement");
		String waitFailureGPS = "0s";
		Integer retryNbreGPS = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		try {
			// retryNbre
			retryNbreGPS = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbreGPS"));

			gpsBean = new ObjectMapper().convertValue(wi.getParameter("gpsMap"), InjectGpsBean.class);
			taskName = reengagementService.getConfig().getPropValues("injectionGPSWIH");
			logger.info("taskName=" + taskName);

			user = reengagementService.getConfig().getPropValues("user").trim();
			custId = (String) wi.getParameter("custId");

			// waitfailure
			waitFailureGPS = autoRecycle.palierwaittime(waitFailureGPS, retryNbreGPS);
			this.logger.debug("waitFailure from config = " + waitFailureGPS);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}
			this.logger.info("GPS amount to inject : " + gpsBean.getAmount());
			CustomerHandlingSoapBindingStub bindingCustomer = reengagementService.getConfig().consumeCustomerHandling();

			GetCustomerDetailsRequest customerRequest = new GetCustomerDetailsRequest();
			CustomerReference customerReference = new CustomerReference();
			customerReference.setCsId(Long.parseLong(custId));
			customerRequest.setCustomerReference(customerReference);
			this.logger.info("=> getCustomerDetails for customer : " + custId);

			GetCustomerDetailsResponse customerResponse = bindingCustomer.getCustomerDetails(customerRequest);
			if (customerResponse.getIsSuccessful().booleanValue()) {
				if (customerResponse.getCustomer().getCsIdPub() != null) {
					custNum = customerResponse.getCustomer().getCsIdPub();
					this.logger.info("=> custNum : " + custNum);

				}
				// consume ws addUpdateJackpot
				AddUpdateJackpotResponse response = reengagementService.addUpdateJackpot(gpsBean.getOsmReference(),
						user, custNum, new BigDecimal(gpsBean.getAmount()),
						new BigDecimal(gpsBean.getCagnotteHtFacilite()), gpsBean.getCommitmentPeriod());

				this.logger.info("Response Injection GPS : " + response.isIsSuccessful());

				if (response.isIsSuccessful()) {
					wim.completeWorkItem(wi.getId(),
							reengagementService.sucessOutputAddUpdateJackpot(waitFailureGPS, taskName));
				} else {
					this.logger.info("Injection GPS BscsErrorCode : " + response.getErrorCode());
					this.logger.info("Injection GPS Comment : " + response.getComment());
					wim.completeWorkItem(wi.getId(),
							reengagementService.failOutputAddUpdateJackpot(retryNbreGPS, waitFailureGPS, taskName));
				}
			}

			else {
				this.logger.info("Customer Details BscsErrorCode : " + customerResponse.getBscsErrorCode());
				this.logger.info("Customer Details Comment : " + customerResponse.getComment());
				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputAddUpdateJackpot(retryNbreGPS, waitFailureGPS, taskName));
			}
		} catch (Exception e) {
			this.logger.error("", e);
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputAddUpdateJackpot(retryNbreGPS, waitFailureGPS, taskName));
		}
	}

}
