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
			System.out.println("错误：请将此程序放置在与启动器同目录下!");
			waitingInput();
			return;
		}
		System.out.println("游戏日志文件自动上传工具 v1.0   By:Ghost_chu！");
		System.out.println("请稍等，正在检查您的网络环境...");
		try {
		URL testURL = new URL("https://paste.ubuntu.com/");
		URLConnection testConnection = testURL.openConnection();
		testConnection.connect();
		}catch (Exception e) {
			System.out.println("网络不可用，请稍后再试。");
			waitingInput();
		}
		System.out.println("正在自动扫描需要上传的文件，请耐心等待...");
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
			builder.append("崩溃报告：\n"+crash_report_link+"\n");
		}else {
			builder.append("崩溃报告：\n未能找到任何有效崩溃日志文件"+"\n");
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
			builder.append("日志文件：\n"+logs_link+"\n");
		}else {
			builder.append("日志文件：\n未能找到任何有效日志文件"+"\n");
		}
		String result = null;
		System.out.println("正在生成结果...");
		try {
			result = getPastebinLink(builder.toString(), "result.log");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("上传报告过程中出现了网络错误，请重试");
			waitingInput();
		}
		if(result!=null) {
			System.out.println("请将下面的链接完整复制并发送给给你这个软件的人，若无法复制，软件目录下有result.txt，可以自行打开复制");
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
			//忽略掉
			waitingInput();
		}
	}
	private static String getLogsLink(File logFolder) throws IOException {
		File[] logs = logFolder.listFiles();
		Map<String,String> logsLinks = new HashMap<>();
		for (File file : logs) {
			if(file.isDirectory()||!(file.getName().endsWith(".log")||file.getName().endsWith(".txt")))
				continue;
			System.out.println("正在上传文件："+file.getName());
			logsLinks.put(file.getName(), getPastebinLink(readFile(file),file.getName()));
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> finalStrings  : logsLinks.entrySet()) {
			builder.append(finalStrings.getKey()+"："+finalStrings.getValue()+"\n");
		}
		return builder.toString();
	}
	private static String getCrashReportLink(File folder) throws IOException {
		File[] reports = folder.listFiles();
		Map<String,String> crashLinks = new HashMap<>();
		for (File file : reports) {
			if(file.isDirectory()||!(file.getName().endsWith(".log")||file.getName().endsWith(".txt")))
				continue;
			System.out.println("正在上传文件： "+file.getName());
			crashLinks.put(file.getName(), getPastebinLink(readFile(file),file.getName()));
		}
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> finalStrings  : crashLinks.entrySet()) {
			builder.append(finalStrings.getKey()+"："+finalStrings.getValue()+"\n");
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
		System.out.println("按任意键继续....");
		try {
			new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			//What!?
		}
	}
	protected static String readFile(File file) {
		StringBuilder result = new StringBuilder();
		try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
	}
}
