package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("serial")
public class ACLMessage implements Serializable {

	private Performative performative;
	private AID sender;
	private AID[] receivers;
	private AID replyTo;
	private String content;
	private Object ContentObj;
	private HashMap<String, Object> userArgs = new HashMap<String, Object>();
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	private Long replyBy;
	
	public ACLMessage(AID sender, AID[] receivers, AID replyTo, String content, Object contentObj,
			HashMap<String, Object> userArgs, String language, String encoding, String ontology, String protocol,
			String conversationId, String replyWith, String inReplyTo, Long replyBy) {
		super();
		this.sender = sender;
		this.receivers = receivers;
		this.replyTo = replyTo;
		this.content = content;
		ContentObj = contentObj;
		this.userArgs = userArgs;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}
	
	public ACLMessage(Performative performative, AID sender, AID[] receivers, AID replyTo, String content,
			Object contentObj, HashMap<String, Object> userArgs, String language, String encoding, String ontology,
			String protocol, String conversationId, String replyWith, String inReplyTo, Long replyBy) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.receivers = receivers;
		this.replyTo = replyTo;
		this.content = content;
		ContentObj = contentObj;
		this.userArgs = userArgs;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.replyWith = replyWith;
		this.inReplyTo = inReplyTo;
		this.replyBy = replyBy;
	}


	public ACLMessage(ACLMessage mess, int i) {
		this.performative = mess.getPerformative();
		this.sender = mess.getSender();
		this.receivers = new AID[] {mess.getReceivers()[i]};
		this.replyTo = mess.getReplyTo();
		this.content = mess.getContent();
		ContentObj = mess.getContentObj();
		this.userArgs = mess.getUserArgs();
		this.language = mess.getLanguage();
		this.encoding = mess.getEncoding();
		this.ontology = mess.getOntology();
		this.protocol = mess.getProtocol();
		this.conversationId = mess.getConversationId();
		this.replyWith = mess.getReplyWith();
		this.inReplyTo = mess.getInReplyTo();
		this.replyBy = mess.getReplyBy();
	}
	
	public ACLMessage() {
		super();
	}

	public AID getSender() {
		return sender;
	}
	
	public void setSender(AID sender) {
		this.sender = sender;
	}
	
	public AID[] getReceivers() {
		return receivers;
	}
	
	public void setReceivers(AID[] receivers) {
		this.receivers = receivers;
	}
	
	public AID getReplyTo() {
		return replyTo;
	}
	
	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Object getContentObj() {
		return ContentObj;
	}
	
	public void setContentObj(Object contentObj) {
		ContentObj = contentObj;
	}
	
	public HashMap<String, Object> getUserArgs() {
		return userArgs;
	}
	
	public void setUserArgs(HashMap<String, Object> userArgs) {
		this.userArgs = userArgs;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getOntology() {
		return ontology;
	}
	
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getConversationId() {
		return conversationId;
	}
	
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	public String getReplyWith() {
		return replyWith;
	}
	
	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}
	
	public String getInReplyTo() {
		return inReplyTo;
	}
	
	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}
	
	public Long getReplyBy() {
		return replyBy;
	}
	
	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}

	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	@Override
	public String toString() {
		return "ACLMessage [sender=" + sender + ", receivers=" + Arrays.toString(receivers) + ", replyTo=" + replyTo
				+ ", content=" + content + ", ContentObj=" + ContentObj + ", userArgs=" + userArgs + ", language="
				+ language + ", encoding=" + encoding + ", ontology=" + ontology + ", protocol=" + protocol
				+ ", conversationId=" + conversationId + ", replyWith=" + replyWith + ", inReplyTo=" + inReplyTo
				+ ", replyBy=" + replyBy + "]";
	}
}
