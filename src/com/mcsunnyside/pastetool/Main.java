package com.mcsunnyside.pastetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	public static void main(String[] args) throws IOException {
		File runningFolder = new File("./");
		File[] files = runningFolder.listFiles();
		boolean haveMinecraftFolder = false;
		for (File file : files) {
			if(file.isDirectory()&&file.getName().equals(".minecraft")) {
				haveMinecraftFolder=true;
				break;
			}
		}
		if(!haveMinecraftFolder) {
			System.out.println("�����뽫�˳����������������ͬĿ¼��!");
			waitingInput();
			return;
		}
		System.out.println("��Ϸ��־�ļ��Զ��ϴ����� v1.0   By:Ghost_chu��");
		System.out.println("���Եȣ����ڼ���������绷��...");
		try {
		URL testURL = new URL("https://paste.ubuntu.com/");
		URLConnection testConnection = testURL.openConnection();
		testConnection.connect();
		}catch (Exception e) {
			System.out.println("���粻���ã����Ժ����ԡ�");
			waitingInput();
		}
		System.out.println("�����Զ�ɨ����Ҫ�ϴ����ļ��������ĵȴ�...");
		doWork();
		return;
	}
	private static void doWork() {
		StringBuilder builder = new StringBuilder();
		String crash_report_link = null;
		File crashFolder = new File("./.minecraft/crash-report");
		if(crashFolder.exists()) {
			try {
				crash_report_link=getCrashReportLink(crashFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(crash_report_link!=null) {
			builder.append("�������棺\n"+crash_report_link+"\n");
		}else {
			builder.append("�������棺\nδ���ҵ��κ���Ч������־�ļ�"+"\n");
		}
		String logs_link = null;
		File logFolder = new File("./.minecraft/logs");
		if(logFolder.exists()) {
			try {
				logs_link=getLogsLink(logFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(logs_link!=null) {
			builder.append("��־�ļ���\n"+logs_link+"\n");
		}else {
			builder.append("��־�ļ���\nδ���ҵ��κ���Ч��־�ļ�"+"\n");
		}
		String result = null;
		System.out.println("�������ɽ��...");
		try {
			result = getPastebinLink(builder.toString(), "result.log");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("�ϴ���������г������������������");
			waitingInput();
		}
		if(result!=null) {
			System.out.println("�뽫����������������Ʋ����͸��������������ˣ����޷����ƣ����Ŀ¼����result.txt���������д򿪸���");
			System.out.println(result);
			File resultTXT = new File("./result.txt");
			resultTXT.delete();
			try {
				resultTXT.createNewFile();
				Files.write(Paths.get(resultTXT.getPath()), result.getBytes(),StandardOpenOption.CREATE);
			} catch (IOException e) {
				e.printStackTrace();
			}
			waitingInput();
		}else {
			//���Ե�
			waitingInput();
		}
	}
	private static String getLogsLink(File logFolder) throws IOException {
		File[] logs = logFolder.listFiles();
		Map<String,String> logsLinks = new HashMap<>();
		for (File file : logs) {
			if(file.isDirectory()||!(file.getName().endsWith(".log")||file.getName().endsWith(".txt")))
				continue;
			System.out.println("�����ϴ��ļ���"+file.getName());
			logsLinks.put(file.getName(), getPastebinLink(readFile(file),file.getName()));
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> finalStrings  : logsLinks.entrySet()) {
			builder.append(finalStrings.getKey()+"��"+finalStrings.getValue()+"\n");
		}
		return builder.toString();
	}
	private static String getCrashReportLink(File folder) throws IOException {
		File[] reports = folder.listFiles();
		Map<String,String> crashLinks = new HashMap<>();
		for (File file : reports) {
			if(file.isDirectory()||!(file.getName().endsWith(".log")||file.getName().endsWith(".txt")))
				continue;
			System.out.println("�����ϴ��ļ��� "+file.getName());
			crashLinks.put(file.getName(), getPastebinLink(readFile(file),file.getName()));
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> finalStrings  : crashLinks.entrySet()) {
			builder.append(finalStrings.getKey()+"��"+finalStrings.getValue()+"\n");
		}
		return builder.toString();
	}
	private static String getPastebinLink(String text,String filename) throws IOException {
		 URL url = new URL("https://paste.ubuntu.com/");
		 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		 conn.setInstanceFollowRedirects(false);
		 conn.setRequestMethod("POST");
		 conn.setUseCaches(false);
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setDoOutput(true);
         conn.setDoInput(true);
         conn.setReadTimeout(1200000);
         conn.setConnectTimeout(600000);
         PrintWriter out = new PrintWriter(conn.getOutputStream());
         String param = "poster=";
         param+="LogsUploader";
         param+="&syntax=text";
         param+="&content=";
         param+=URLEncoder.encode(text, "UTF-8");
         out.write(param);
         out.flush();
         String url302 = null;
        	 url302 = conn.getHeaderField("Location");
        	 if(url302==null)
        		 url302=conn.getHeaderField("location");
         if(out!=null){
             out.close();
         }
         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         String line;
         while ((line = reader.readLine()) != null){
             System.out.println(line);
         }
         return url302;
	}
  
	public static void waitingInput() {
		System.out.println("�����������....");
		try {
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			//What!?
		}
	}
	protected static String readFile(File file) {
		StringBuilder result = new StringBuilder();
		try{
            BufferedReader br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
            String s = null;
            while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}
}
