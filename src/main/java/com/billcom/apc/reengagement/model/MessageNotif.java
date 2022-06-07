package com.billcom.apc.reengagement.model;



import java.math.BigDecimal;

public class MessageNotif {
        private int idMessage;
        private String messageDef;
        private String typeMessage;
        private String dn_num;
        private String ccemail;
        private BigDecimal request_id;
        
        
        
        
		public BigDecimal getRequest_id() {
			return request_id;
		}
		public void setRequest_id(BigDecimal request_id) {
			this.request_id = request_id;
		}
		public String getCcemail() {
			return ccemail;
		}
		public void setCcemail(String ccemail) {
			this.ccemail = ccemail;
		}
		public String getDn_num() {
			return dn_num;
		}
		public void setDn_num(String dn_num) {
			this.dn_num = dn_num;
		}
		public String getTypeMessage() {
			return typeMessage;
		}
		public void setTypeMessage(String typeMessage) {
			this.typeMessage = typeMessage;
		}
		
		public MessageNotif (int idMessage,String messageDef, String typeMessage,String dn_num,String ccemail ){
			   this.idMessage= idMessage;
			   this.messageDef= messageDef;
			   this.typeMessage=typeMessage;
			   this.dn_num=dn_num;
			   this.ccemail=ccemail;
		}
		public int getIdMessage() {
			return idMessage;
		}
		public void setIdMessage(int idMessage) {
			this.idMessage = idMessage;
		}
		public String getMessageDef() {
			return messageDef;
		}
		public void setMessageDef(String messageDef) {
			this.messageDef = messageDef;
		}
   
}

