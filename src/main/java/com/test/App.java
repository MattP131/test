package com.test;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	FetchMail thread = new FetchMail(4000);
  
    	//send email smtp test...make cool methods later, msg factory meyhaps
    	Properties properties = new Properties();
    	properties.put("mail.smtp.auth", "true");
    	properties.put("mail.smtp.starttls.enable", "true");
    	properties.put("mail.smtp.host", "smtp.gmail.com");
    	properties.put("mail.smtp.port", "587");
    	
    	
    	
    	       Client client = Client.create();
    	       client.addFilter(new HTTPBasicAuthFilter("api",
    	                       "key-3ax6xnjp29jd6fds4gc373sgvjxteol0"));
    	       WebResource webResource =
    	               client.resource("https://api.mailgun.net/v2/samples.mailgun.org" +
    	                               "/mailboxes");
    	       MultivaluedMapImpl formData = new MultivaluedMapImpl();
    	       formData.add("mailbox", "sergeyo@samples.mailgun.org");
    	       formData.add("password", "secret");
    	       webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
    	               post(ClientResponse.class, formData);
    	


    	/*Session session = Session.getInstance(properties, new Authenticator(){
    		protected PasswordAuthentication getPassword
    	}
    	);*/
    	/*
    	Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api",
                        "key-90-smej035grxiriwiwe0yqlx8iwse15"));
        WebResource webResource =
                client.resource("https://api.mailgun.net/v2/minoe.mailgun.org" +
                                "/messages");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", "admin <kingsway@minoe.mailgun.org>");
        formData.add("to", "kingswayminoes@gmail.com");
        formData.add("subject", "Hello");
        formData.add("text", "Testing some Mailgun awesomness!");
        webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
        post(ClientResponse.class, formData);
        */
    	//fetching email thread
    	while(true){
    		if(!thread.isAlive()){
    			thread.run();
    		}
    	}
    }
}

class FetchMail extends Thread{
	public static final String TEXT_TYPE = "text/plain";
	public static final String MY_ADDRESS = "<kingsway@minoe.mailgun.org";
	int waitTime = 0;
	
	public FetchMail(int sleepTime){
		this.waitTime = sleepTime;
	}
	
	public FetchMail(){
		
	}
	
	public void run(){
		
		Properties properties = System.getProperties();
		properties.put("mail.host", "imap.mailgun.org");
		properties.put("mail.store.protocol", "imap");
		properties.put("mail.pop3s.auth", "true");
		properties.put("mail.pop3s.port", "995");
		
		try{			
			Session session = Session.getDefaultInstance(properties);
			Store store = session.getStore("imaps");
			System.out.println("Connecting...");
			store.connect("kingsway@minoe.mailgun.org", "superAwesome");
			System.out.println("Connected...");
			
			System.out.println("Oppening inbox");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
			
			Message messages[] = inbox.search(ft);
			System.out.println(messages.length);
			
			Object messageContent = null;
			String sender = "";
			for(Message message:messages){
				
				System.out.println("Subj: " + message.getSubject());
				
				sender = ((InternetAddress)message.getFrom()[0]).getPersonal();
				if(sender == null){
					sender = "";
				}
				System.out.println("From: " + message.getFrom()[0]);
				
				//determining email type
				messageContent = message.getContent();
				String emailBody = "";
				if(messageContent instanceof Multipart){					
					Multipart multipart = (Multipart)message.getContent();
					//looking for body
					Part part;
					String partContentType = "";
					for(int i = 0; i < multipart.getCount(); i++){
						part = multipart.getBodyPart(i);
						partContentType = part.getContentType();
						System.out.println("Type: " + partContentType);
						if(partContentType.toLowerCase().startsWith(TEXT_TYPE)){
							System.out.println("found body");
							emailBody = parseBody((String) part.getContent());
							System.out.println(emailBody);
							break;
						}
					}
				}else{
					if(message.getContentType().toLowerCase().startsWith(TEXT_TYPE)){
						System.out.println("found body");
						emailBody = parseBody((String)message.getContent());
						System.out.println(emailBody);
					}
				}
			}			
			
			System.out.println("END");
			inbox.close(true);
			store.close();
		}catch (NoSuchProviderException e){
			
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Sleeping for " + waitTime + " milis");
	    try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private String parseBody(String body){
		String[] splitBody = body.split("\n");
		String parsedBody = "";
		
		boolean delimeterFound = false;
		for(int i = 0; i < splitBody.length; i++){
			if(!delimeterFound){
				delimeterFound = checkDelimeter(splitBody[i]);
			}else{
				if(!splitBody[i].equals("") && !splitBody[i].equals(" ") && !splitBody[i].startsWith(">")){
					delimeterFound = false;
				}
			}
			
			if(!delimeterFound){
				parsedBody += splitBody[i] + "\n";
			}
			
			
		}
		
		return parsedBody;
	}
	
	private boolean checkDelimeter(String delimeter){
		if((delimeter.startsWith("On") || delimeter.startsWith("> On"))&& 
				(delimeter.contains(MY_ADDRESS + " wrote:") || delimeter.contains("admin" + " wrote:"))){
			return true;
		}
		
		return false;
	}
}
