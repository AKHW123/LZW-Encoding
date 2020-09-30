//Original code by Jasmine and Sally
//Decoder by Akseli and Lyon
//Optimization by Anneliese and Maya :)

//import all required tools
import java.io.*;
import java.util.*;

public class Encoder {
	
	//initialize dictionary and previous, current, and previous + current variables
	private ArrayList <String> dictionary = new ArrayList <String> (); 
	private String p = "";
	private char c = 0;
	private String pc = "";

	private boolean dictionaryFull = false; //this is a boolean that we can use to prevent the program from throwing the full dictionary error more than once

	public Encoder ()
	{
	}


	public void encode (String fileName) throws IOException
	{
		try {
			//reading in a text file and creating print writer
			FileReader fr = new FileReader (fileName);
			BufferedReader br = new BufferedReader(fr);
			PrintWriter pw = new PrintWriter ("encoded.txt");
			String encodedContent = ""; //large string holding everything before printed
			while (br.ready())
			{
				c = (char)br.read();
				pc = p+c;
				//dictionary excludes characters 0-255 in the ascii table
				//if pc is already in the dictionary or if it's in the ascii table
				if (dictionary.indexOf(pc) >= 0 || pc.length() == 1)
				{
					p = pc;
				}
				//print out value for previous character
				else
				{
					//if p is already in the ascii table
					if (p.length()==1)
					{
						encodedContent += ((int)p.charAt(0) + " ");
					}
					//if only in dictionary
					else 
					{
						encodedContent += (256+dictionary.indexOf(p) + " ");
					}
					if (dictionary.size()<512) //Changed --> 512 is the maximum
					{
						dictionary.add(pc);
					}
					else if (dictionaryFull == false){
						dictionaryFull = true;
						System.out.println ("Dictionary is full. :( File will probably not encode or decode correctly. This will not work.");
					}
					p= "" + c;
				}

			}
			//edge case
			//if previous is just one character then convert it to an int
			if (p.length() == 1 )
			{
				encodedContent += ((int)p.charAt(0)+ " ");
			}
			//if previous is a longer String, then find it in the dictionary
			else
			{
				encodedContent += (256+dictionary.indexOf(p) + " ");
			}
			// Include the dictionary at the end of the encoded file
			// Print an Ŕ to represent the end of the code and the start of the dictionary
			encodedContent += ('Ŕ');
			// print each the index of each dictionary entry, then the length of the entry so when reading it in, it is easy to know when to stop, then print the entry itself
			// these are delimited by a ":" between the index and the length and a "-" between the length and the entry itself
			for (int i = 0; i < dictionary.size(); i++) {
				encodedContent += ("" + (i + 256) + ":" + dictionary.get(i).length() + "-" + dictionary.get(i));
			}

			//actually printing stuff
			//print dictionary first
			pw.print(encodedContent.substring((encodedContent.indexOf('Ŕ')+1), encodedContent.length()));

			//print an Ŕ to separate the dictionary from the encoded message
			pw.print((char)('Ŕ'));

			//then print the rest of the stuff
			pw.print(encodedContent.substring(0, (encodedContent.indexOf('Ŕ'))));

			//close all writers and readers
			pw.close();
			br.close();
			fr.close();
		}
		catch (IOException e)
		{
			System.out.println ("IOException.");
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////////////


	public void decode () throws IOException
	{
		// New String for Message
		String decodedMessage = "";
				
		try
		{
			// First, the dictionary alone is read from the encoded file
			// File Reader for Encoded Text
			FileReader fr = new FileReader("encoded.txt");

			// Buffered Reader for File
			BufferedReader br = new BufferedReader(fr);

			// PrintWriter for New Decoded Text File
			PrintWriter pw = new PrintWriter ( "decoded.txt");
			
			// Int a (just to store each character being read in)
			int a;
			
			// Boolean for whether or not the Letter Ŕ has been read in yet, which indicates the start of the dictionary
			boolean foundR = false;
			
			// String for the Current String being constructed
			String currentString = "";
			
			// HashMap for reconstructed Dictionary
			HashMap <Integer, String> map = new HashMap < Integer, String > ();
			
			// Int for the Current Code
			int thisCode = 0;
			
			// Int for Current Counter (counts the number of characters read so far for each dictionary entry)
			int lengthCounter = 0;
			
			// Int for Current Length (stores the correct length of each dictionary entry)
			int codeLength = 0;
			
			// Boolean for Reading (whether or not the characters currently being read are part of a combination of characters that is stored in the dictionary)
			boolean startedReading = false;
			
			// If the letter Ŕ has not been found yet
			while (((a = br.read()) != -1) && (foundR == false))
			{
				// If a = Ŕ, set foundR to true
				if ((char)a == ('Ŕ'))
				{
					//set foundR to true
					foundR = true;
				}

				else if (foundR == false)
				{
					// If We Have Started Reading a combination that is in the dictionary
					if (startedReading == true)
					{
						// Add the Char Version of the Letter from the Buffered Reader to the current String that is being constructed
						currentString += ((char) a);
						// counter for the length of the String constructed so far Increases by One
						lengthCounter++;
						//if the current String has hit the specified length of the dictionary entry
						if (lengthCounter == codeLength)
						{
							// Add to Dictionary
							map.put (thisCode, currentString);
							// Reset currentString
							currentString = "";
							// Reset lengthCounter
							lengthCounter = 0;
							// Reset startedReading
							startedReading = false;
						}
					}
					else
					{
						// If Statement for Delimiter ":", which represents the end of the index of the dictionary entry and the start of its length
						if ((char)a == ':')
						{
							thisCode = Integer.parseInt(currentString);
							currentString = "";
						}
						// If Statement for Delimiter "-", which indicates the end of the length of the dictionary entry and the start of the actual character combination
						else if ((char)a == '-')
						{
							//parse the length of the entry to an int
							codeLength = Integer.parseInt(currentString);
							//set startedReading to true
							startedReading = true;
							//reset currentString
							currentString = "";
						}
						else
						{
							//append the current character to currentString
							currentString += ((char)a);
						}
					}
				}
			}

			
			
			// Now that the dictionary is fully reconstructed, the file is read again to translate the encoded section
			// String Character to store the current character
			String thisCharacter = "";
			
			// String Current Code to store the current code
			String currentCode = "";
			
			// int to store the currentCode after it is parsed to an integer
			int code = 0;
			
			// While the end of the file hasn't been reached
			while ((a != -1))
			{
				// thisCharacter Becomes the current character
				thisCharacter = String.valueOf ((char)a);
				
				// If Statement for Delimiter " ", which indicates the end of the current code
				if (thisCharacter.equals (" "))
				{
					// parse the current code to an integer 
					code = Integer.parseInt(currentCode);
					
					// If the code represents a single ASCII character
					if (code <= 255)
					{
						// the character is added to the message
						decodedMessage += (char)code;
					}
					else
					// If the code does not represent a single character, Use Dictionary We Created to find its value
					{
						// add the decoded combination to the decoded message
						decodedMessage += map.get(code);	
					}
					//reset the currentCode after each individual code has been decoded
					currentCode = "";
				}
				else
				{
					// Add Character to Current Code
					currentCode += thisCharacter;
				}
				a = br.read();
			}
			
			// Print Decoded Message to File
			pw.print(decodedMessage);
			// close the print writer
			pw.close();

			//close the file reader and the buffered reader
			fr.close();
			br.close();
		}
		
		// Catch for Errors
		catch (Exception e)
		{
			System.out.println(e);
		}
	}
}
