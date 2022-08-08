package parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import analytics.Analyzor;
import dump.DumpGenerator;
import org.json.simple.parser.ParseException;
import parser.exception.InvalidQueryException;
import reverseEngineering.DrawERD;
import reverseEngineering.ErdExecutor;
import reverseEngineering.ReverseEngineering;

public class Console {

	static String username="";
	 static LoginSignup ls;
	boolean picker(int no) throws IOException, ParseException {


		if (no == 1) {
			WriteQueries wq = new WriteQueries(username);

			boolean chk = wq.manager();
			if (!chk) {
				System.out.println("Something went wrong");
			}
			return true;
		}
		if (no == 2) {
			DumpGenerator dumpGenerator=new DumpGenerator();
			dumpGenerator.createBothDump();
			return true;
		}
		if (no == 3) {
			ErdExecutor erdExecutor = new ErdExecutor();

			erdExecutor.doReverseEngineering();
			return true;
		}
		if (no == 4) {
			Analyzor analyzor=new Analyzor();
			analyzor.printAnalyse(username);

			return true;
		}

		return false;
	}

	void userInput() throws IOException, ParseException {


		Scanner sc = new Scanner(System.in);
		System.out.println();

		System.out.println("======MENU======");
		System.out.println();

		System.out.println("1. Write Queries");
		System.out.println("2. Export");
		System.out.println("3. Data Model");
		System.out.println("4. Analysis");
		int no = sc.nextInt();
		Boolean success = picker(no);
		// if(!success){
		// System.out.println("Invalid selection, select again");
		userInput();
		// }

	}

	void auth(Boolean passed) throws IOException, ParseException {

		LoginSignup ls = new LoginSignup();
		if (passed) {
			Console con = new Console();
			Boolean flag = true;
			while (flag) {
				Scanner sc = new Scanner(System.in);
				/*
				 * String quit="quit"; System.out.println("press 'q' to exit");
				 * 
				 * if(quit.equalsIgnoreCase(sc.next())){ flag=false; }s
				 */
				con.userInput();

				sc.close();

			}
			System.out.println("Quitted!!");
		} else {
			System.out.println("Try Again!!");
			passed = ls.runHere();
			auth(passed);
		}
	}

	public static void main(String[] string) throws IOException, ParseException {
		 ls = new LoginSignup();
		Boolean passed = ls.runHere();
		username=ls.getUserName();
		Console con = new Console();
		con.auth(passed);

	}


}
