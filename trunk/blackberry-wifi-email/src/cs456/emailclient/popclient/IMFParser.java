package cs456.emailclient.popclient;

import java.util.Vector;

import cs456.emailclient.models.Log;
import cs456.emailclient.models.Message;
import cs456.emailclient.models.MessageBox;

public class IMFParser
{
	/**
	 * 1) Parses the input and puts it into the Message object using the IMF standard (RFC 2822 and RFC 822)
	 * 2) Passes the message object
	 * @param lines
	 */
	public static void ParseIMF(Vector lines)
	{
		Message msg = new Message();
		Vector header = new Vector();
		Vector body = new Vector();
		
		int counter = 0;
		
		//HEADER
		//BLANK line indicates end of header
		while(counter < lines.size() && !lines.elementAt(counter).equals(""))
		{
			header.addElement(lines.elementAt(counter));
			counter++;
		}
		counter++; //ignore the blank line
		//BODY
		while(counter < lines.size())
		{
			body.addElement(lines.elementAt(counter));
			counter++;	
		}
		
		
		parseHeader(msg, header);
		parseBody(msg, body);
		
		MessageBox.AddMessage(msg);
	}
	
	/**
	 * parses the header and puts the results into the msg
	 * @param msg
	 * @param header
	 */
	private static void parseHeader(Message msg, Vector header)
	{
		header = foldHeader(header);
		
//		Log.info("******folded header******");
//		for(int x = 0; x < header.size(); x++)
//		{
//			Log.info((String)header.elementAt(x));
//		}		
		
		for(int x = 0; x < header.size(); x++)
		{
			parseHeaderLine(msg, (String)header.elementAt(x));
		}
		Log.info("Subject: " + msg.subject);
		Log.info("To: " + msg.to);
		Log.info("From: " + msg.from);
		Log.info("Date: " + msg.date);
		Log.info("ContentType: " + msg.contentType);
		Log.info("Boundary: " + msg.boundary);
	}
	/**
	 * Determines the type of the line.
	 * Takes out the value and puts it into the message.
	 * @param msg
	 * @param line
	 */
	private static void parseHeaderLine(Message msg, String line)
	{
		if(line.startsWith("To:"))
		{
			msg.to = line.substring(3).trim();
		}
		else if(line.startsWith("From:"))
		{
			msg.from = line.substring(5).trim();
		}
		else if(line.startsWith("Date:"))
		{
			msg.date =  line.substring(5).trim();
		}
		else if(line.startsWith("Subject:"))
		{
			msg.subject = line.substring(8).trim();
		}
		else if(line.startsWith("Content-Type:"))
		{
			msg.contentType = line.substring(8).trim();
			if(msg.contentType.indexOf("multipart") >= 0)
			{
				msg.isMultPart = true;
				int boundaryStart = msg.contentType.indexOf("boundary=");
				msg.boundary = msg.contentType.substring(boundaryStart + 9);
				if(msg.boundary.startsWith("\""))
				{
					//remove the quotes
					msg.boundary = msg.boundary.substring(0, msg.boundary.length() - 1);
					msg.boundary = msg.boundary.substring(1);
				}
				msg.boundary = "--" + msg.boundary;
			}
			else
			{
				msg.isMultPart = false;
			}
		}
		else if(line.startsWith("Content-Transfer-Encoding:"))
		{
			//do nothing
		}
		else
		{
			//Log.error("Unknown header: " + line);
		}
		//the message has been parsed
	}
	/**
	 * collapses headers spread out on multiple lines
	 * into a single line
	 * @param header the header lines
	 * @return
	 */
	private static Vector foldHeader(Vector header)
	{
		Vector foldedHeader = new Vector();
		String foldedLine = "";
		String currentLine = "";
		for(int x = 0; x < header.size(); x++)
		{
			currentLine = (String)header.elementAt(x);
			if(currentLine.startsWith(" ") || currentLine.startsWith("\t"))
			{
				foldedLine = foldedLine + " " + currentLine.trim();
			}
			else
			{
				if(!foldedLine.equals(""))
					foldedHeader.addElement(foldedLine);
				foldedLine = currentLine;
			}
		}
		
		if(!foldedLine.equals(""))
			foldedHeader.addElement(foldedLine);
		
		return foldedHeader;
	}
	
	/**
	 * parses the body and puts the results into the msg
	 * @param msg
	 * @param body
	 */
	private static void parseBody(Message msg, Vector body)
	{
		if(msg.isMultPart)
		{
			extractPlainTextFromBody(msg, body);
		}
		else
		{
			simplePlainTextExtract(msg, body);
		}
		
		Log.info("***BODY***");
		Log.info(msg.getBody());
		Log.info("**********");
	}
	private static void simplePlainTextExtract(Message msg, Vector body)
	{
		String bodyString = "";
		
		for(int x = 0; x < body.size(); x++)
		{
			bodyString += (String)body.elementAt(x) + "\n";
		}
		
		msg.setBody(bodyString);
	}
	/**
	 * assumes the the counter points to the boundary line
	 * @param counter
	 * @param msg
	 * @param body
	 * @return
	 */
	private static int parseBodyPartType(int counter, Message msg, Vector body)
	{
		counter++;
		String currentLine = (String)body.elementAt(counter);
		while(counter < body.size() && !currentLine.equals(""))
		{
			//Log.info("[header]" + currentLine);
			parseHeaderLine(msg, currentLine);
			counter++;
			currentLine = (String)body.elementAt(counter);
		}
		return counter;
	}
	private static void extractPlainTextFromBody(Message msg, Vector body)
	{
		int counter = 0;
		String bodySoFar = "";
		String currentLine;
		boolean foundTextPart = false;
		
		while(counter < body.size())
		{
			currentLine = (String)body.elementAt(counter);
			if(foundTextPart)
			{
				if(currentLine.startsWith(msg.boundary))
				{
					//Log.info("[endboundary]" + currentLine);
					break; //we reached the end of the plain text body
				}
				else
				{
					//Log.info("[goodtext]" + currentLine);
					bodySoFar = bodySoFar + currentLine + "\n";
				}
				counter++;
			}
			else if(currentLine.startsWith(msg.boundary))
			{
				//Log.info("[startboundary]" + currentLine);

				counter = parseBodyPartType(counter, msg, body);
				
				//Log.info("[contentType]" + msg.contentType);
				
				if(msg.contentType.indexOf("text/plain") >= 0)
					foundTextPart = true;
			}
			else
			{
				//Log.info("[badtext]" + currentLine);
				counter++;
			}
		}
		msg.setBody(bodySoFar);
	}
}
