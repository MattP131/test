package com.test;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

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
			Folder inbox = store.getFolder("inbox");
			inbox.open(Folder.READ_ONLY);
			Message messages[] = inbox.getMessages();
			System.out.println(messages.length);
			for(Message message:messages){
				//System.out.println(message);
			}
			
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
	}
}
