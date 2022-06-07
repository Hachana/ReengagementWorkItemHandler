package com.billcom.apc.reengagement.workItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.logging.Logger;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.BalanceV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.OptionV2;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.OperationResponse;
import com.billcom.apc.reengagement.model.ContractBalance;
import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;

import com.billcom.autorecycle.AutoRecycle;

/**
 * @author Fethi.Hachana
 *
 */
public class InsertBalanceWorkItemHandler implements WorkItemHandler {

	private Logger logger = Logger.getLogger(InsertBalanceWorkItemHandler.class);
	private boolean toSuccess = true;

	public void executeWorkItem(WorkItem wi, WorkItemManager wim) {

		Map<String, Object> contractMap = new HashMap<>();
		List<OptionV2> listOptions = new ArrayList<OptionV2>();
		Long contractOrderId = wi.getProcessInstanceId();

		Integer jwf;

		logger.info("Reengagement migration in progress...Insert Balances IN");
		ReengagementService reengagementService = new ReengagementServiceImp();

		String waitFailure = "0s";
		Integer retryNbre = 0;
		AutoRecycle autoRecycle = new AutoRecycle();
		String balanceType;
		Integer i = 0;
		Integer j = 0;

		reengagementService.printWorkItem(this.logger, wi);

		try {

			// retryNbre
			retryNbre = reengagementService.getRetryNumber((Integer) wi.getParameter("retryNbre"));

			contractMap = (Map<String, Object>) wi.getParameter("contractMap");
			listOptions = (List<OptionV2>) wi.getParameter("listOptions");

			jwf = (Integer) wi.getParameter("j");

			logger.info("j_wf " + jwf);

			if (jwf != null)
				j = jwf;

			// waitfailure
			waitFailure = autoRecycle.palierwaittime(waitFailure, retryNbre);
			logger.debug("waitFailure from config = " + waitFailure);

			// INPUT
			for (Entry<String, Object> entry : wi.getParameters().entrySet()) {
				logger.info("[INPUT] " + entry.getKey() + " : " + entry.getValue());
			}

			String taskName = reengagementService.getConfig().getPropValues("getBalanceResINWIH");

			ArrayList<ContractBalance> iNbalances = new ArrayList<ContractBalance>();
			if (listOptions != null) {

				logger.info("listOptions size : " + listOptions.size());

				for (OptionV2 inBalance : listOptions) {
					logger.info("=> for 1 in ");

					logger.info("Option name : " + inBalance.getOptionName());
					if (inBalance.getOptionName().equalsIgnoreCase("MAIN_BALANCE"))
						balanceType = "MA";
					else if (inBalance.getOptionName().equalsIgnoreCase("UCIP_DA_17"))
						balanceType = "DA";
					else
						balanceType = "OTHER";
					logger.info("balance Type : " + balanceType);

					logger.info("inBalance.getBalances().getItem().size() " + inBalance.getBalances().getItem().size());

					if (null != inBalance.getBalances() && !inBalance.getBalances().getItem().isEmpty()) {
						List<BalanceV2> balancesClone = new ArrayList<BalanceV2>();
						List<BalanceV2> balances = inBalance.getBalances().getItem();

						for (BalanceV2 balance : balances) {
							logger.info("=> for 2 in ");

							ContractBalance contractBalance = new ContractBalance();
							contractBalance.setCanal("IN");
							contractBalance.setCO_ID(((Long) contractMap.get("coId")).intValue());
							contractBalance.setTMCODE(((Long) contractMap.get("tmcode")).intValue());
							if (balance.getEndDate() != null)
								contractBalance.setDateEnd(balance.getEndDate().toString());
							if (balance.getExpirationDate() != null)
								contractBalance.setDateExpiration(balance.getExpirationDate().toString());
							if (balance.getStartDate() != null)
								contractBalance.setDateStart(balance.getStartDate().toString());
							if (balance.getRemainingAmount() != null)
								contractBalance.setVALUE(balance.getRemainingAmount().toString());
							if (balance.getUnit() != null)
								contractBalance.setUNIT(balance.getUnit());

							logger.info("restant: " + balance.getRemainingAmount());
							logger.info("unité: " + balance.getUnit());
							logger.info("start date: " + balance.getStartDate());
							logger.info("end date: " + balance.getEndDate());
							logger.info("expery date: " + balance.getExpirationDate());
							logger.info("description: " + balance.getDescription());

							// var
							Double remAmount = (double) 0;
							String unit; // OTHER for null
							if (balance.getRemainingAmount() != null) {
								remAmount = balance.getRemainingAmount().doubleValue();
							}
							if (balance.getUnit() != null && !balance.getUnit().equals(""))
								unit = balance.getUnit();
							else
								unit = "OTHER";

							OperationResponse response = reengagementService.insertBalanceHist(contractMap, balanceType,
									unit, remAmount, contractOrderId);
							if (!response.getIsSuccessful().booleanValue()) {
								toSuccess = false;
								logger.info("wsdl call insertBalanceHist is  " + response.getIsSuccessful());
								logger.info("ErrorCode insertBalanceHist is  " + response.getBscsErrorCode());
								logger.info("Comment insertBalanceHist is  " + response.getComment());
								logger.info("<= insertBalanceHist out: " + wi.getProcessInstanceId());
								logger.info("=> j " + j);

							}
							iNbalances.add(contractBalance);

							balancesClone.remove(balance);
							j++;
						}
						balances = balancesClone;
					}
					listOptions.remove(i);

					i++;
				}
			}
			if (toSuccess) {
				logger.info("<= GetBalancesINWorkItemHandler out " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.successOutputInserttBalancesIN(waitFailure, taskName, listOptions));

			} else {
				logger.info("<= GetBalancesINWorkItemHandler out with error " + wi.getProcessInstanceId());

				wim.completeWorkItem(wi.getId(),
						reengagementService.failOutputInsertBalancesIN(retryNbre, waitFailure, listOptions, j));

			}

		} catch (Exception e) {
			// ko
			logger.error("", e);
			logger.error("<= GetBalancesINWorkItemHandler out with error " + wi.getProcessInstanceId());
			wim.completeWorkItem(wi.getId(),
					reengagementService.failOutputInsertBalancesIN(retryNbre, waitFailure, listOptions, j));
		}

	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		/*
		 * empty bloc
		 */
	}

}
