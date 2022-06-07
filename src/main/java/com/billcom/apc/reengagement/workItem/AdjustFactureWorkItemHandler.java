package com.billcom.apc.reengagement.workItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.jboss.logging.Logger;

import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.APCHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.UnexpectedErrorFault;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.UpdateAdvChargeRequest;
import com.billcom.apc.reengagement.model.MigrationOutputBean;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.autorecycle.AutoRecycle;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Fethi Hachana
 *
 */
public class AdjustFactureWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(AdjustFactureWorkItemHandler.class);
	String taskName = null;
	String action = "";
	String remark = " ";

	public AdjustFactureWorkItemHandler() {
		/*
		 * constructeur vide
		 */
	}

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		/*
		 * empty bloc
		 */
	}

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {
		logger.info("=> AdjustFactureWorkItemHandler in:");
		ReengagementService reengagementService = new ReengagementServiceImp();

		Map<String, Object> resultsMap = new HashMap<>();
		Boolean updAdvChDone = false;
		Boolean adjFaFDone = false;

		logger.info("**************** Migration id : ******" + wi.getProcessInstanceId() + "***********");

		Map<String, Object> contractMap = new HashMap<>();
		Map<String, Object> migrationMap = new HashMap<>();

		boolean isSameGroup;
		String oldCoId = "";

		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		try {
			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			String dateMig = (String) wi.getParameter("dueDate");

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			migrationMap = (Map<String, Object>) wi.getParameter("migrationMap");
			isSameGroup = (Boolean) wi.getParameter("isSameGroup");
			oldCoId = (String) wi.getParameter("oldCoId");

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);

			taskName = reengagementService.getConfig().getPropValues("AdjustFactureWIH");

			logger.debug("waitFailure from config = " + waitFailure);
			APCHandlingSoapBindingStub binding = reengagementService.getConfig().consumeAPCHandling();
			String dateUpdateAdvCharge = reengagementService.getConfig().getPropValues("dateUpdateAdvCharge").trim();
			logger.info("=> Update Advanced Charge in:");
			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			// Update Advanced Charge
			MigrationOutputBean migrationOutputBean = new ObjectMapper().convertValue(migrationMap,
					MigrationOutputBean.class);

			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
			Date date = format.parse(dateMig);

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			logger.info("Update Adv Charge");
			UpdateAdvChargeRequest updateAdvChargeRequest = new UpdateAdvChargeRequest();
			if (!dateUpdateAdvCharge.equalsIgnoreCase(String.valueOf(0)) && dateUpdateAdvCharge != null
					&& dateUpdateAdvCharge != ""
					&& Integer.valueOf(dateUpdateAdvCharge) < calendar.getMaximum(Calendar.DAY_OF_MONTH)) {
				Date daf = new Date();
				daf.setDate(Integer.valueOf(dateUpdateAdvCharge));
				calendar.setTime(daf);
				updateAdvChargeRequest.setDateMig(calendar);
			} else {
				updateAdvChargeRequest.setDateMig(Calendar.getInstance());
			}

			updateAdvChargeRequest
					.setRpCodeSrc(migrationOutputBean.getResponseMigration().getOffreInitBscs().getTmcode());
			updateAdvChargeRequest
					.setRpCodeCible(migrationOutputBean.getResponseMigration().getOffreTargetBscs().getTmcode());
			if (isSameGroup) {

				if (contractMap.get("coId") != null)
					updateAdvChargeRequest.setCoIdSrc(Long.parseLong(contractMap.get("coId").toString()));
				if (contractMap.get("coId") != null)
					updateAdvChargeRequest.setCoIdTarget(Long.parseLong(contractMap.get("coId").toString()));
			} else {
				if (contractMap.get("coId") != null)
					updateAdvChargeRequest.setCoIdSrc(Long.parseLong(contractMap.get("coId").toString()));
				if (contractMap.get("coId") != null)
					updateAdvChargeRequest.setCoIdTarget(Long.parseLong(oldCoId));
			}

			if (updateAdvChargeRequest != null) {
				OperationResponse responseUpdateAdvCh = binding.updateAdvCharge(updateAdvChargeRequest);
				logger.info(+wi.getProcessInstanceId() + " : " + " --- response : "
						+ responseUpdateAdvCh.getIsSuccessful() + " | comment : " + responseUpdateAdvCh.getComment());

				if (responseUpdateAdvCh.getIsSuccessful().booleanValue()) {
					updAdvChDone = true;
					logger.info("<= Update Advanced Charge out:");

					logger.info(" AdjustFactureWorkItemHandler out: <=");
					wim.completeWorkItem(wi.getId(), reengagementService.successAdjustFacture(waitFailure, taskName));

				} else {
					updAdvChDone = false;
					logger.info(" AdjustFactureWorkItemHandler out with error :: <=");
					this.logger.info("IsSuccessful :" + responseUpdateAdvCh.getIsSuccessful());
					this.logger.info("BscsErrorCode : " + responseUpdateAdvCh.getBscsErrorCode());
					this.logger.info("Comment : " + responseUpdateAdvCh.getComment());
					wim.completeWorkItem(wi.getId(),
							reengagementService.failAdjustFacture(retryNbre, waitFailure, taskName));
				}
			}

		} catch (UnexpectedErrorFault e) {
			logger.error("An exception was thrown", e);
			logger.info(wi.getProcessInstanceId() + " : " + "C1 ==> Update Adv Charge statut : " + updAdvChDone
					+ " Adjust FaF statut : " + adjFaFDone);

			if (!adjFaFDone.booleanValue() && updAdvChDone.booleanValue())
				action = "adjustFaF";
			else
				action = "updateAdvancedCharge";
			if (e.getMessage() != null)
				remark = e.getMessage();
			logger.info(" AdjustFactureWorkItemHandler out with error : <=");
			wim.completeWorkItem(wi.getId(), reengagementService.failAdjustFacture(retryNbre, waitFailure, taskName));

		} catch (Exception e) {
			logger.error("An exception was thrown", e);
			logger.info(wi.getProcessInstanceId() + " : " + "C2 ==> Update Adv Charge statut : " + updAdvChDone
					+ " Adjust FaF statut : " + adjFaFDone);
			if (!adjFaFDone.booleanValue() && updAdvChDone.booleanValue())
				action = "adjustFaF";
			else
				action = "updateAdvancedCharge";
			resultsMap.put("success", false);
			if (e.getMessage() != null)
				remark = e.getMessage();

			logger.info(" AdjustFactureWorkItemHandler out with error : <=");

			wim.completeWorkItem(wi.getId(), reengagementService.failAdjustFacture(retryNbre, waitFailure, taskName));
		}

	}
}
