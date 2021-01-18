package br.com.bruno.desafio;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;


@SpringBootApplication
public class DesafioEstagioApplication {
		//sheets service to access google sheets
		private static Sheets tableCalculationSheet;
		private static Sheets classTotalSheet;
		
		//Name application
		private static String APPLICATION_NAME = "Desafio de Estagio";
		
		//The spreadsheet on google sheet
		private static String SPREADSHEET = "1SGp1_gxEcYxCh31-a-OO8g7AMXuy30AH1bMaUVWCTjU";

		//create an authorization method for our application to access google sheets
		private static Credential authorize() throws IOException, GeneralSecurityException {
			//create an input stream to access the json file with the credentials
			InputStream in = DesafioEstagioApplication.class.getResourceAsStream("/credentials.json");
			//creating a GoogleClientSecrets object, and reading the input stream
			GoogleClientSecrets secret = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
					new InputStreamReader(in));
			//list of scopes that have access to spreadsheet
			List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);
			//create a GoogleAuthorizationCodeFlow who has access to secret and scopes and setting to offline 
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secret, scopes)
							.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
							.setAccessType("offline").build();
			//use the flow to create a credential
			Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
			//return the credential
			return credential;
		}

		//create the sheets service 
		private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
			//Credential recive methode authorize
			Credential credential = authorize();
			//return Sheet
			return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
					credential).setApplicationName(APPLICATION_NAME).build();
		}

		//main function to read the example
		public static void main(String[] args) throws IOException, GeneralSecurityException {
			SpringApplication.run(DesafioEstagioApplication.class, args);
			
			//get the sheet service
			tableCalculationSheet = getSheetsService();
			classTotalSheet = getSheetsService();
			
			//range to read the values in the spreedsheets
			String tableCalculationRange = "A4:F27";
			//use the range representation to create a values as response
			ValueRange tableCalculationResponse = tableCalculationSheet.spreadsheets().values().get(SPREADSHEET, tableCalculationRange).execute();
			//recive a list of list whith the values
			List<List<Object>> tableCalculationValues = tableCalculationResponse.getValues();
			//range to read the value Total Alunos
			String classTotalRange = "A2";
			//use the range representation to create a values as response
			ValueRange classTotalResponse = classTotalSheet.spreadsheets().values().get(SPREADSHEET, classTotalRange).execute();
			//recive a list of list whith the values
			List<List<Object>> classTotalValues = classTotalResponse.getValues();
			//converts the value of Object to int and keeps only the number characters
			int calcTotalClass = converter(classTotalValues.toString().substring(30, 32));
			//counter
			int i =4;
			//checks if the value is null
			if (tableCalculationValues == null || tableCalculationValues.isEmpty()) {
				System.out.println("No data in the table");
			} else {
				//loop over each row in values
				for (List<?> row : tableCalculationValues) {
					//take the number of absences from the table and convert to integer
					int absences = converter(row.get(2));
					//calculates the percentage of absences
					Double percentageAbsences = (calcTotalClass * 0.25);
					//takes test result and converts it to a integer
					int p1 = converter(row.get(3));
					//takes test result and converts it to a integer
					int p2 = converter(row.get(4));
					//takes test result and converts it to a integer
					int p3 = converter(row.get(5));
					//calculate the average
					int m =  (p1+p2+p3)/3;
					//calculate the nfa
					int nfa = (m + 70)/2;
					//if the percentage of absences is less than the number of absences the student is failed
					if(percentageAbsences < absences) {
						System.out.println("O aluno " + row.get(1)+ " foi reprovado por falta!");
						update("Reprovado por Falta", "G"+i);
						update("0", "H"+i);
					}
					//if the average is less than 50 the student failed
					else if(m < 50) {
						System.out.println("O aluno " + row.get(1)+ " foi reprovado!");
						update("Reprovado", "G"+i);
						update("0", "H"+i);
					//if the average is between 50 and less 70 the student is in the final exam
					}else if(m <= 50 || m <70){
						System.out.println("O aluno " + row.get(1)+ " foi para o exame final!");
						update("Exame final", "G"+i);
						String novo = Integer.toString(nfa);
						update(novo, "H"+i);
						//if the average is greater than 70 the student is approved
					}else {
						System.out.println("O aluno " + row.get(1)+ " foi aprovado!");
						update("Aprovado", "G"+i);
						update("0", "H"+i);
					}
					i++;
				}
			}
		}
		
		//update the shreedsheets
		private static void update(String um, String dois) throws IOException {
			//set a value to appendBody
			ValueRange appendBody = new ValueRange().setValues(Arrays.asList(
					Arrays.asList(um)));
			//update the war of spreedsheets with the appendBody
			UpdateValuesResponse update = tableCalculationSheet.spreadsheets().values()
				.update(SPREADSHEET, dois, appendBody).setValueInputOption("RAW")
				.execute();
		}

		//converts the value of Object to int
		private static int converter(Object numero) {
			return Integer.parseInt((String) numero);
		}
}
