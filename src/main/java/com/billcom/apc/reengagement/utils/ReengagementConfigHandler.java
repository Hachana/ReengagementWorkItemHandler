package com.billcom.apc.reengagement.utils;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.SOAPHeaderElement;
import org.jboss.logging.Logger;

import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.BalanceManagerV2;
import com.billcom.apc.generatedSOAPReengagement.BalanceManagerV2.BalanceManagerV2Service;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.APCManager;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.APCManagerService;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.FaFManager;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.FaFManager.FaFManagerService;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementManager;
import com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementManagerService;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.APCHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.apcHandling.APCHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.CommitmentHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.commitementHandling.CommitmentHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ContractServicesAddServiceSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.contractServicesAdd.ContractServicesAddService_ServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.CustomerHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.customerHandling.CustomerHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.FaFHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.fafHandling.FaFHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.GrhHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.grhHandling.GrhHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.promotionHandling.PromotionHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ReengagementHandlingServiceLocator;
import com.billcom.apc.generatedSOAPReengagement.bscs.reengagementHandling.ReengagementHandlingSoapBindingStub;
import com.billcom.apc.generatedSOAPReengagement.gps.JackpotWebServiceWebServicePortBindingStub;
import com.billcom.apc.generatedSOAPReengagement.gps.JackpotWebService_ServiceLocator;
import com.otn.dsi.ws.FaFManagerPortBindingStub;
import com.otn.dsi.ws.OtnWsFaFManagerLocator;

/**
 * @author arij.dhahbi
 *
 */

public class ReengagementConfigHandler {
	private Logger logger = Logger.getLogger(ReengagementConfigHandler.class);

	Properties prop = null;
	String propFileName = "";
	String propertyFile = null;
	File file = null;
	String result = "";

	//

	public static Properties configData = null;
	public static ReengagementConfigHandler instance = null;

	public ReengagementConfigHandler() {
		initializeData();
	}

	public static ReengagementConfigHandler getInstance() {
		if (instance == null) {
			instance = new ReengagementConfigHandler();
		}
		return instance;
	}

	public void initializeData() {
		if (configData == null) {
			try {

				logger.info(" First load of configData config_reengagement.properties");

				Properties properties = new Properties();
				file = new File(System.getProperty("config_reengagement.properties"));
				properties.load(new FileInputStream(this.file));
				configData = properties;
			

			} catch (Exception e) {

				e.printStackTrace();

			}
		}
	}

	public String getPropValues(String key) {
		return configData.getProperty(key);
	}

	//

	public APCHandlingSoapBindingStub consumeAPCHandling() {
		this.logger.info("=> consumeAPCHandling in");
		APCHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";
		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = getPropValues("wsdlUserSec");
			wsdlUsername = getPropValues("wsdlUsername");
			wsdlPassword = getPropValues("wsdlPassword");
			URL url = new URL(getPropValues("billcomWsUrl"));
			this.logger.info("Binding to WS APCHandling : " + getPropValues("billcomWsUrl"));
			binding = (APCHandlingSoapBindingStub) (new APCHandlingServiceLocator()).getAPCHandling(url);
			SOAPHeaderElement header = new SOAPHeaderElement(
					(Name) new PrefixedQName(getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);
			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);
			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);
			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception:: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception : " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception :: " + e);
		} catch (IOException e) {
			this.logger.error("Exception = " + e);
		}
		this.logger.info("<= consumeAPCHandling out");
		return binding;
	}

	public BalanceManagerV2 consumeWsdlWsPortal() {
		BalanceManagerV2 binding = null;
		try {
			URL url = new URL(getPropValues("otnWsPortalUrl"));
			this.logger.info("***** binding to WS :wsPortal-> " + getPropValues("otnWsPortalUrl") + "************");
			BalanceManagerV2Service service = new BalanceManagerV2Service(url,
					new QName("http://v2.manager.ws.lucent.alcatel.com", "BalanceManagerV2Service"));
			binding = service.getBalanceManagerV2();
			return binding;
		} catch (Exception e) {
			this.logger.error("An exception was thrown = ", e);
			return binding;
		}
	}

	// GRH
	public GrhHandlingSoapBindingStub consumeGrhHandling() {
		logger.info("=> consumeGrhHandling in");
		java.net.URL url;
		GrhHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("GrhHandlingWsUrl"));

			logger.info("Binding to WS GrhHandling : " + this.getPropValues("GrhHandlingWsUrl"));

			binding = (GrhHandlingSoapBindingStub) new GrhHandlingServiceLocator().getGrhHandling(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);

			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error(" Exception : " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		logger.info("<= consumeGrhHandling out");
		return binding;
	}

	// GPS
	public JackpotWebServiceWebServicePortBindingStub consumeGpsWsdl() {
		logger.info("=> consumeGpsWsdl in");
		java.net.URL url;
		JackpotWebServiceWebServicePortBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = getPropValues("wsdlGpsUsername");
			wsdlPassword = getPropValues("wsdlGpsPassword");
			url = new URL(this.getPropValues("GpsApcWsUrl"));
			logger.info("Binding to WS GpsApc : " + this.getPropValues("GpsApcWsUrl"));
			binding = (JackpotWebServiceWebServicePortBindingStub) new JackpotWebService_ServiceLocator()
					.getJackpotWebServiceWebServicePort(url);
			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);
			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (Exception e) {
			logger.error("An exception was thrown", e);
		}
		logger.info("<= consumeGpsWsdl out");
		return binding;
	}

	// Promotion
	public PromotionHandlingSoapBindingStub consumePromotionHandling() {
		logger.info("=> consumePromotionHandling in");
		java.net.URL url;
		PromotionHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("PromotionHandlingWsUrl"));

			logger.info("Binding to WS PromotionHandling : " + this.getPropValues("PromotionHandlingWsUrl"));

			binding = (PromotionHandlingSoapBindingStub) new PromotionHandlingServiceLocator()
					.getPromotionHandling(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);

			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		logger.info("<= consumePromotionHandling out");
		return binding;
	}

	// FaFHandling
	public FaFHandlingSoapBindingStub consumeFaFHandling() {
		logger.info("=> consumeFaFHandling in");
		java.net.URL url;
		FaFHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("FaFHandlingWsUrl"));

			logger.info("Binding to WS FaFHandling : " + this.getPropValues("FaFHandlingWsUrl"));

			binding = (FaFHandlingSoapBindingStub) new FaFHandlingServiceLocator().getFaFHandling(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);

			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		logger.info("<= consumeFaFHandling out");
		return binding;
	}

	public FaFManager consumeWsdlPBICMSFAF() {
		logger.info("=> consumeFaFManager in");
		java.net.URL url;
		FaFManager binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {

			wsdlUserSec = this.getPropValues("wsdlPbiCMSUserSec");
			wsdlUsername = this.getPropValues("wsdlPbiCMSUsername");
			wsdlPassword = this.getPropValues("wsdlPbiCMSPassword");
			int timeoutPBICMS = Integer.parseInt(this.getPropValues("timeoutPBICMS"));
			url = new URL(this.getPropValues("billcomWsUrlPBICMSFAF"));
			logger.info("***** binding to WS :PBICMS FAF User-> " + wsdlUsername + "******");
			logger.info("***** binding to WS :PBICMS FAF wsdlPassword-> " + wsdlPassword + "*******");

			FaFManagerService service = new FaFManagerService(url,
					new QName("http://manager.ws.FaF.billcom.com/", "FaFManagerService"));
			WSSUsernameTokenSecurityHandler wSSUsernameTokenSecurityHandler = new WSSUsernameTokenSecurityHandler(
					wsdlUsername, wsdlPassword);
			binding = service.getFaFManagerPort();
			BindingProvider bp = (BindingProvider) binding;
			List<Handler> handlerList = new ArrayList<>();
			handlerList.add(wSSUsernameTokenSecurityHandler);
			bp.getBinding().setHandlerChain(handlerList);
			bp.getRequestContext().put(bp.ENDPOINT_ADDRESS_PROPERTY, this.getPropValues("billcomWsUrlPBICMSFAF"));
			bp.getRequestContext().put(bp.USERNAME_PROPERTY, wsdlUsername);
			bp.getRequestContext().put(bp.PASSWORD_PROPERTY, wsdlPassword);
			logger.info("timeoutPBICMS : " + timeoutPBICMS);

			bp.getRequestContext().put("com.sun.xml.ws.developer.JAXWSProperties.CONNECT_TIMEOUT", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.internal.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.request.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeoutPBICMS);

			return binding;
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.info("<= consumeFaFManager out");
		return binding;

	}

	public ReengagementHandlingSoapBindingStub consumeReengagementHandling() {
		this.logger.info("=> consumeReengagementHandling in");
		ReengagementHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";
		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = getPropValues("wsdlUserSec");
			wsdlUsername = getPropValues("wsdlUsername");
			wsdlPassword = getPropValues("wsdlPassword");
			URL url = new URL(getPropValues("reengagementWsUrl"));
			this.logger.info("Binding to WS ReengagementHandling : " + getPropValues("reengagementWsUrl"));
			binding = (ReengagementHandlingSoapBindingStub) (new ReengagementHandlingServiceLocator())
					.getReengagementHandling(url);
			SOAPHeaderElement header = new SOAPHeaderElement(
					(Name) new PrefixedQName(getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);
			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);
			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);
			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		this.logger.info("<= consumeReengagementHandling out");
		return binding;
	}

	public APCManager consumeWsdlPBICMS() {
		logger.info("=> consumeAPCManager in");
		java.net.URL url;
		APCManager binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {

			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("billcomWsUrlPBICMSAPC"));
			int timeoutPBICMS = Integer.parseInt(this.getPropValues("timeoutPBICMS"));

			logger.info("***** binding to WS :PBICMS-> " + this.getPropValues("billcomWsUrlPBICMSAPC") + "********");
			com.billcom.apc.generatedSOAPReengagement.PBI_CMS.APCManager.APCManagerService service = new APCManagerService(
					url, new QName("http://manager.ws.apc.billcom.com/", "APCManagerService"));

			WSSUsernameTokenSecurityHandler wSSUsernameTokenSecurityHandler = new WSSUsernameTokenSecurityHandler(
					wsdlUsername, wsdlPassword);
			binding = service.getAPCManagerPort();

			BindingProvider bp = (BindingProvider) binding;
			List<Handler> handlerList = new ArrayList<>();
			handlerList.add(wSSUsernameTokenSecurityHandler);
			bp.getBinding().setHandlerChain(handlerList);
			bp.getRequestContext().put(bp.ENDPOINT_ADDRESS_PROPERTY, this.getPropValues("billcomWsUrlPBICMSAPC"));
			bp.getRequestContext().put(bp.USERNAME_PROPERTY, wsdlUsername);
			bp.getRequestContext().put(bp.PASSWORD_PROPERTY, wsdlPassword);
			// timeout

			logger.info("timeoutPBICMS : " + timeoutPBICMS);

			bp.getRequestContext().put("com.sun.xml.ws.developer.JAXWSProperties.CONNECT_TIMEOUT", timeoutPBICMS); // min
			bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.internal.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.request.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeoutPBICMS);

			return binding;
		} catch (Exception e) {
			logger.error("An exception was thrown", e);
		}
		logger.info("<= consumeAPCManager out");
		return binding;

	}

	// ReengagementManager
	public ReengagementManager consumeWsdlPBICMSReengagement() {
		logger.info("=> ReengagementManager in");
		java.net.URL url;
		ReengagementManager binding = null;
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {

			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("billcomWsUrlPBICMSReengagement"));
			int timeoutPBICMS = Integer.parseInt(this.getPropValues("timeoutPBICMS"));

			logger.info("***** binding to WS :PBICMS-> " + this.getPropValues("billcomWsUrlPBICMSReengagement")
					+ "********");
			com.billcom.apc.generatedSOAPReengagement.PBI_CMS.ReengagementManager.ReengagementManagerService service = new ReengagementManagerService(
					url, new QName("http://ws.reengagement.billcom.com/", "ReengagementManagerService"));

			WSSUsernameTokenSecurityHandler wSSUsernameTokenSecurityHandler = new WSSUsernameTokenSecurityHandler(
					wsdlUsername, wsdlPassword);
			binding = service.getReengagementManagerPort();

			BindingProvider bp = (BindingProvider) binding;
			List<Handler> handlerList = new ArrayList<>();
			handlerList.add(wSSUsernameTokenSecurityHandler);
			bp.getBinding().setHandlerChain(handlerList);
			bp.getRequestContext().put(bp.ENDPOINT_ADDRESS_PROPERTY,
					this.getPropValues("billcomWsUrlPBICMSReengagement"));
			bp.getRequestContext().put(bp.USERNAME_PROPERTY, wsdlUsername);
			bp.getRequestContext().put(bp.PASSWORD_PROPERTY, wsdlPassword);
			// timeout

			logger.info("timeoutPBICMS : " + timeoutPBICMS);

			bp.getRequestContext().put("com.sun.xml.ws.developer.JAXWSProperties.CONNECT_TIMEOUT", timeoutPBICMS); // min
			bp.getRequestContext().put("com.sun.xml.ws.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.internal.connect.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.ws.request.timeout", timeoutPBICMS);
			bp.getRequestContext().put("com.sun.xml.internal.ws.request.timeout", timeoutPBICMS);

			return binding;
		} catch (Exception e) {
			logger.error("An exception was thrown", e);
		}
		logger.info("<= ReengagementManager out");
		return binding;

	}

	public APCHandlingSoapBindingStub consumeAPCHandlingForCreateContract() {
		this.logger.info("=> consumeAPCHandling in");
		APCHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";
		try {
			int timeoutBscs_CreateContract = Integer.parseInt(getPropValues("timeoutBscs_CreateContract"));
			wsdlUserSec = getPropValues("wsdlUserSec");
			wsdlUsername = getPropValues("wsdlUsername");
			wsdlPassword = getPropValues("wsdlPassword");
			URL url = new URL(getPropValues("billcomWsUrl"));
			this.logger.info("Binding to WS APCHandling : " + getPropValues("billcomWsUrl"));
			binding = (APCHandlingSoapBindingStub) (new APCHandlingServiceLocator()).getAPCHandling(url);
			SOAPHeaderElement header = new SOAPHeaderElement(
					(Name) new PrefixedQName(getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);
			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);
			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);
			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs_CreateContract);
			binding.setHeader(header);
			//
			this.logger.info("timeoutBscs_CreateContract: " + timeoutBscs_CreateContract);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		this.logger.info("<= consumeAPCHandling out");
		return binding;
	}

	// Contract Services Add
	public ContractServicesAddServiceSoapBindingStub consumeContractServicesAddService() {
		this.logger.info("=> consumeContractServicesAddService in");
		ContractServicesAddServiceSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";
		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = getPropValues("wsdlUserSec");
			wsdlUsername = getPropValues("wsdlUsername");
			wsdlPassword = getPropValues("wsdlPassword");
			URL url = new URL(getPropValues("ContractServicesAddWsUrl"));
			this.logger.info("Binding to WS ContractServicesAdd : " + getPropValues("ContractServicesAddWsUrl"));
			binding = (ContractServicesAddServiceSoapBindingStub) (new ContractServicesAddService_ServiceLocator())
					.getContractServicesAddService(url);
			SOAPHeaderElement header = new SOAPHeaderElement(
					(Name) new PrefixedQName(getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);
			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);
			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);
			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		this.logger.info("<= consumeContractServicesAddService out");
		return binding;
	}

	public CommitmentHandlingSoapBindingStub consumeCommitmentWsdl() {

		java.net.URL url;
		CommitmentHandlingSoapBindingStub binding = null;
		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {

			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("CommitmentHandlingWsUrl"));

			this.logger.info(
					"***** binding to WS :BSCSWS-> " + this.getPropValues("CommitmentHandlingWsUrl") + "********");

			binding = (CommitmentHandlingSoapBindingStub) new CommitmentHandlingServiceLocator()
					.getCommitmentHandling(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);

			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);

			binding.setHeader(header);

		} catch (Exception e) {
			logger.error("An exception was thrown", e);
		}
		return binding;
	}

	public FaFManagerPortBindingStub consumeOMTFaFManager() {

		FaFManagerPortBindingStub binding_FaFpbicms = null;

		try {


			String wsdlUsername = this.getPropValues("OMTUsername");
			String wsdlPassword = this.getPropValues("OMTPassword");
			URL url = new URL(this.getPropValues("OMTWsFaFUrl"));
		
			

			binding_FaFpbicms = (FaFManagerPortBindingStub) new OtnWsFaFManagerLocator().getFaFManagerPort(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", "wsse"));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", "wsse");

			SOAPElement username = usernameToken.addChildElement("Username", "wsse");
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", "wsse");

			password.addTextNode(wsdlPassword);

			binding_FaFpbicms.setHeader(header);
		} catch (Exception e) {
			logger.error("An exception was thrown", e);
			return null;
		}

		return binding_FaFpbicms;
	}

	// CustomerHandling
	public CustomerHandlingSoapBindingStub consumeCustomerHandling() {
		logger.info("=> consumeCustomerHandling in");
		java.net.URL url;
		CustomerHandlingSoapBindingStub binding = null;

		String wsdlUserSec = "";
		String wsdlUsername = "";
		String wsdlPassword = "";

		try {
			int timeoutBscs = Integer.parseInt(getPropValues("timeoutBscs"));
			wsdlUserSec = this.getPropValues("wsdlUserSec");
			wsdlUsername = this.getPropValues("wsdlUsername");
			wsdlPassword = this.getPropValues("wsdlPassword");
			url = new URL(this.getPropValues("CustomerHandlingUrl"));

			logger.info("Binding to WS CustomerHandling : " + this.getPropValues("CustomerHandlingUrl"));

			binding = (CustomerHandlingSoapBindingStub) new CustomerHandlingServiceLocator().getCustomerHandling(url);

			SOAPHeaderElement header = new SOAPHeaderElement(
					new PrefixedQName(this.getPropValues("SecQname"), "Security", wsdlUserSec));
			SOAPElement usernameToken = header.addChildElement("UsernameToken", wsdlUserSec);

			SOAPElement username = usernameToken.addChildElement("Username", wsdlUserSec);
			username.addTextNode(wsdlUsername);

			SOAPElement password = usernameToken.addChildElement("Password", wsdlUserSec);

			password.addTextNode(wsdlPassword);
			binding.setTimeout(timeoutBscs);
			binding.setHeader(header);
			//
			this.logger.info("Timeout Bscs: " + timeoutBscs);
		} catch (SOAPException e) {
			this.logger.error("Exception: " + e);
		} catch (ServiceException e) {
			this.logger.error("Exception: " + e);
		} catch (MalformedURLException e) {
			this.logger.error("Exception: " + e);
		} catch (IOException e) {
			this.logger.error("Exception: " + e);
		}
		logger.info("<= consumeCustomerHandling out");
		return binding;
	}
}
