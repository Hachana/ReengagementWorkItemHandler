package com.billcom.apc.reengagement.model;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


import org.jboss.logging.Logger;

import org.json.JSONObject;

import com.billcom.apc.reengagement.service.ReengagementService;
import com.billcom.apc.reengagement.service.ReengagementServiceImp;
import com.billcom.apc.reengagement.utils.ReengagementConfigHandler;

import com.google.gson.Gson;


public class Client {

	private ReengagementService reengagementService = new ReengagementServiceImp();
	
    private String from= reengagementService.getConfig().getPropValues("SMS_FROM");
    
    private String priority=reengagementService.getConfig().getPropValues("SMS_priority");
    
    private String validity=reengagementService.getConfig().getPropValues("SMS_validity");
    
    private String text=reengagementService.getConfig().getPropValues("SMS_text");
    
    private String canal=reengagementService.getConfig().getPropValues("SMS_canal");
    
    private String bid=reengagementService.getConfig().getPropValues("SMS_bid");
    
    private int type=Integer.parseInt(reengagementService.getConfig().getPropValues("SMS_type")); 

    private List<Action> actions = null;

    

//    public Client(String from, String priority, String validity, String text, List<Action> actions) {
//		super();
//		this.from = from;
//		this.priority = priority;
//		this.validity = validity;
//		this.text = text;
//		this.actions = actions;
//		
//	}


	public Client() {
		// TODO Auto-generated constructor stub
	}


	public Client(String from, String priority, String validity, String text, String canal, String bid, int type,
			List<Action> actions) {
		super();
		this.from = from;
		this.priority = priority;
		this.validity = validity;
		this.text = text;
		this.canal = canal;
		this.bid = bid;
		this.type = type;
		this.actions = actions;
	}


	public String getFrom() {
        return from;
    }


    public void setFrom(String from) {
        this.from = from;
    }


    public String getPriority() {
        return priority;
    }


    public void setPriority(String priority) {
        this.priority = priority;
    }


    public String getValidity() {
        return validity;
    }


    public void setValidity(String validity) {
        this.validity = validity;
    }


    public String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
    }


    public List<Action> getActions() {
        return actions;
    }


    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
    
    
    
    
public String getCanal() {
		return canal;
	}


	public void setCanal(String canal) {
		this.canal = canal;
	}


	public String getBid() {
		return bid;
	}


	public void setBid(String bid) {
		this.bid = bid;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


public static boolean smsNotification(MessageNotif messageNotif,long order_id) {
	Logger logger = Logger.getLogger(Client.class);

		try {
			
			ReengagementConfigHandler config = new  ReengagementServiceImp().getConfig();
			Client c = new Client();   

			c.setFrom(config.getPropValues("senderSms"));
			logger.info("senderSms : "+config.getPropValues("senderSms"));

			c.setPriority(config.getPropValues("prioritySms"));
			logger.info("prioritySms : "+config.getPropValues("prioritySms"));

			c.setBid(c.getBid()+order_id);
			
			c.setValidity(config.getPropValues("validitySms"));
			logger.info("validitySms : "+config.getPropValues("validitySms"));

			List<Action> listAction = new ArrayList<Action>();
			Action a =  new Action();
			a.setIdAction(1);	
			a.setTo(messageNotif.getDn_num());
			a.setMsg(messageNotif.getMessageDef());
			listAction.add(a);
			c.setActions(listAction);
			JSONObject jsonObject = new JSONObject(c);
		
		
		logger.info(jsonObject.toString());

		
		
			String urb = config.getPropValues("kannelUrl");
			logger.info("kannelUrl : "+config.getPropValues("kannelUrl"));

			String user_id = config.getPropValues("KannelUserId");
			logger.info("KannelUserId : "+config.getPropValues("KannelUserId"));

			logger.info(urb);
			URL url = new URL(urb);
			URLConnection connection =   url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", user_id);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(jsonObject.toString());
			out.close();
			//Receive Gateway SMS response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line="";
			while ((line=in.readLine()) != null) {
				logger.info("Line : "+line);
				Gson gson = new Gson();
			
				
				Clientserver clientResponse =  gson.fromJson(line, Clientserver.class);
				logger.info("Status  : "+clientResponse.getStatus());

				if(clientResponse.getStatus().equalsIgnoreCase("200")){
				logger.info("\n Concentrateur Service Invoked Successfully.."+clientResponse.getStatus());
				
				
				logger.info("\n Concentrateur Service Invoked Successfully.."+clientResponse.getMessage()+"\t"+clientResponse.getStatus());
			
							
				
					return true;
					
			
				}else{
					logger.info("\n Concentrateur REST Service Failed.."+clientResponse.getMessage()+"\t"+clientResponse.getStatus());
					
					return false;
				}
				
			}
			
			logger.info("\n Concentrateur REST Service Invoked Successfully..");
			in.close();
			
			
		} catch (Exception e) {
			logger.info("\nError while calling Crunchify REST Service");
			logger.info(e.getMessage());
			
			return false;
		}
		return false;

		
	}


}
