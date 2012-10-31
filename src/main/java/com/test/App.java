package com.test;

import java.io.IOException;
import java.util.Properties;

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

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	FetchMail thread = new FetchMail();
  
    	
    	while(true){
    		if(!thread.isAlive()){
    			thread.run();
    		}
    	}
    }
}

class FetchMail extends Thread{
	public void run(){
		final String TEXT_TYPE = "text/plain";
		
		Properties properties = System.getProperties();
		properties.put("mail.host", "imap.gmail.com");
		properties.put("mail.store.protocol", "imap");
		properties.put("mail.pop3s.auth", "true");
		properties.put("mail.pop3s.port", "995");
		
		try{			
			Session session = Session.getDefaultInstance(properties);
			Store store = session.getStore("imaps");
			System.out.println("Connecting...");
			store.connect("matt@minoeworks.com", "Polska123");
			System.out.println("Connected...");
			
			System.out.println("Oppening inbox");
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			
			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), true);
			
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
							System.out.println(part.getContent());
							break;
						}
					}
				}else{
					if(message.getContentType().toLowerCase().startsWith(TEXT_TYPE)){
						System.out.println("found body");
						System.out.println(message.getContent());
					}
				}
			}
			/*
			MimeMultipart mp = (MimeMultipart)messages[23].getContent();
			String result = "";
			for(int i = 0; i < mp.getCount(); i++){
				MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(i);
				
				if(bp.getContent() instanceof String){
					result = ((String)bp.getContent());
					//System.out.println("B: " + result);
					break;
				}
				
				
			}
			
			String[] res = result.split("\n");
			for(int i = 0; i < res.length; i++){
				if(res[i].contains("<matt@minoeworks.com> wrote:")){
					break;
				}else{
					System.out.println(res[i]);
				}
			}*/
			
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
	}
}
