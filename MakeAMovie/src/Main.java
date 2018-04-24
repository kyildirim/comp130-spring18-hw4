import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import acm.graphics.*;
import acm.program.GraphicsProgram;

public class Main extends GraphicsProgram {

	ArrayList<String> grammar = new ArrayList<String>();
	String grammarAllowed = "color#image#time#from#to";
	String grammarConstants ="color##image##time#for$2#from#from$1#to#to$1";
	String timeConstants = "seconds#milliseconds";
	int grammarSize = grammarAllowed.split("#").length;
	String currFileName = "";
	File currFile;
	String movieTitle = "";
	ArrayList<String> imgs = new ArrayList<String>();
	ArrayList<String> scenes = new ArrayList<String>();
	HashMap<String, String> cmdDefs = new HashMap<String, String>();
	HashMap<String, Dimension> locs = new HashMap<String, Dimension>();
	String cmdDefsRaw = "q#quit#e#quit#exit#quit#quit#quit#"
			+ "grammar#setGrammar#setGrammar#setGrammar#setgrammar#setGrammar#"
			+ "showgrammar#showGrammar#grammar#showGrammar#showGrammar#showGrammar#"
			+ "list#listScenes#listscenes#listScenes#listScenes#listScenes#"
			+ "add#addScene#addscene#addScene#addScene#addScene#"
			+ "removescene#removeScene#removeScene#removeScene#"
			+ "settitle#setTitle#setTitle#setTitle#"
			+ "create#create#open#open#close#close#save#save#"
			+ "run#animate#play#animate#animate#animate";
	
	public void run(){
		constructCommandDefinitions();
		initDefaultLocations();
		initCLI();
	}
	
	public void initDefaultLocations(){
		locs.put("left", new Dimension(0, getHeight()/2));
		locs.put("top", new Dimension(getWidth()/2, 0));
		locs.put("right", new Dimension(getWidth(), getHeight()/2));
		locs.put("bottom", new Dimension(getWidth()/2, getHeight()));
	}
	
	public void constructCommandDefinitions(){
		String cmdSplit[] = cmdDefsRaw.split("#");
		if(cmdSplit.length%2==1)throw new RuntimeException("Invalid Raw Command Definitions");
		for(int i = 0;i<cmdSplit.length;i+=2)cmdDefs.put(cmdSplit[i], cmdSplit[i+1]);
	}
	
	public void cliLoop(){
		if(!currFileName.equals(""))print(currFileName.substring(0, currFileName.lastIndexOf(FILE_TYPE))+"@");
		print("MakeAMovie -> ");
		String cmd = readLine();
		String cmdTemp = cmd;
		cmd = cmdDefs.get(cmd);
		Method call;
		try {
			call = this.getClass().getMethod(cmd);
			try {
				call.invoke(this);
			} catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException|SecurityException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			println("Not a valid command <" + cmdTemp + ">");
		}
		
	}
	
	public void initCLI(){
		println("Welcome to Make a Movie!");
		println("Start by either creating a new project or opening an existing one.");
		while(true)cliLoop();
	}
	
	//Grammar Operations
	public void setGrammar(){
		println("Please specify the order of elements.");
		println("[color, image, from, to, time]"); //Connect to grammarAllowed
		String str[] = readLine().split(" ");
		grammar.clear();
		if(str.length == grammarSize){
			for (int i = 0; i < grammarSize; i++) {
				grammar.add(str[i].toLowerCase());
			}
		}else if(str.length == 1){
			while(grammar.size()<grammarSize){
				grammar.add(str[0]);
				if(grammar.size()==grammarSize)break;
				str = readLine().split(" ");
				if(str.length>1){
					println("Invalid input.");
				}
			}
		}else{
			println("Invalid input.");
		}
		boolean flag = true;
		for(int i=0;i<grammarSize;i++){
			for(String s : grammarAllowed.split("#")){
				if(s.equals(grammar.get(i)))flag=false;
			}
			if(flag)break;
			if(i==grammarSize-1)break;
			flag=true;
		}
		if(flag)setGrammar();
	}
	
	public void showGrammar(){
		if(grammar.size()==0){
			println("No grammar specified.");
			return;
		}
		for(int i=0; i<grammarSize; i++){
			if(i==0)print("[");
			print(grammar.get(i));
			if(i==grammarSize-1){
				println("]");
			}else{
				println(", ");
			}
		}
	}
	//End of Grammar Operations
	
	//File Operations
	public void create(){
		String fileName = readLine("Specify file name: ");
		fileName += FILE_TYPE;
		currFile = new File(fileName);
		try {
			currFile.createNewFile();
		} catch (IOException e) {
			println("Cannot create file.");
			return;
		}
		currFileName = fileName;
	}
	
	public void open(){
		String fileName = readLine("Specify file name: ");
		fileName += FILE_TYPE;
		currFile = new File(fileName);
		if(currFile.exists()){
			currFileName = fileName;
			println("File " + fileName + " opened successfully.");
			loadFile();
		}else{
			println("File does not exist.");
			return;
		}
	}
	
	public void close(){
		if(currFileName.equals("")){
			println("No open file.");
			return;
		}
		println("Closing file " + currFileName);
		currFileName = "";
		currFile = null;
	}
	
	public void loadFile(){
		try {
			Scanner sc = new Scanner(new FileInputStream(currFile));
			String line;
			while(sc.hasNextLine()){
				line = sc.nextLine();
				String cmd = line.split(" ")[0];
				if(cmd.equals("Title")){
					movieTitle=line.substring(6);
				}else if(cmd.equals("Images")){
					imgs = new ArrayList<String>();
					for(String str : line.substring(7).split(" ")){
						imgs.add(str);
					}
				}else if(cmd.equals("Grammar")){
					grammar = new ArrayList<String>();
					for(String str : line.substring(8).split(" ")){
						grammar.add(str);
					}
				}else if(cmd.equals("Scenes")){
					scenes = new ArrayList<String>();
					while(sc.hasNextLine()){
						line = sc.nextLine();
						scenes.add(line);
					}
				}else{
					throw new RuntimeException("Invalid file format.");
				}
			}	
			sc.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Load file fatal error.");
		}
	}
	
	public void save(){
		try (BufferedWriter bf = new BufferedWriter(new FileWriter(currFileName))) {
            bf.close();
        } catch (IOException e) {
        	throw new RuntimeException("BufferedWriter fatal error.");
		}
		if(currFileName.equals("")){
			println("No open file found.");
			return;
		}
		write("Title " + movieTitle + "\n");
		write("Images");
		for(String str : imgs)write(" "+str);
		write("\nGrammar");
		for(String str : grammar)write(" "+str);
		write("\nScenes\n");
		for(String str : scenes)write(str+"\n");
		println("Saved to "+currFileName);
	}
	//End of File Operations
	
	//Movie Operations
	public void setTitle(){
		if(currFileName.equals("")){
			println("No open file.");
			return;
		}
		String title = readLine("Enter new title: ");
		if(title.equals("")){
			println("Title cannot be empty.");
			return;
		}
		movieTitle = title;
	}
	
	public void addScene(){
		if(currFileName.equals("")){
			println("No open file.");
			return;
		}
		println("Describe the new scene:");
		String sceneStr = readLine();
		String[] sceneDesc = sceneStr.split(" ");
		int j=0;
		String img = "";
		for(int i=0; i<sceneDesc.length; j++){
			String currStr = sceneDesc[i].toLowerCase();
			String graConst = getGrammarConstants(grammar.get(j));
			if(graConst.equals("")){
				if(grammar.get(j).equals("image"))img=currStr;
				i++;
			}else if(graConst.equals("for$2")){
				i+=3;
			}else if(graConst.equals("from$1")||graConst.equals("to$1")){
				i+=2;
			}
		}
		if(j==grammarSize){
			scenes.add(sceneStr);
			if(!imgs.contains(img))imgs.add(img);
		}
	}
	
	public void listScenes(){
		if(currFileName.equals("")){
			println("No open file.");
			return;
		}
		println("Current scenes: ");
		for(int i = 0; i<scenes.size(); i++){
			println((i+1)+") "+scenes.get(i));
		}
	}
	
	public void removeScene(){
		if(currFileName.equals("")){
			println("No open file.");
			return;
		}
		int n = readInt("Enter scene number to remove: ");
		if(n<1){
			println("Invalid scene number:"+n);
			return;
		}
		if(n>scenes.size()){
			println("No scene numbered: "+n);
			return;
		}
		n--;
		println("Scene removed:");
		println(scenes.get(n));
		scenes.remove(n);
	}
	//End of Movie Operations
	
	//Utilities
	public void write(String str){
		try (BufferedWriter bf = new BufferedWriter(new FileWriter(currFileName, true))) {
            bf.write(str);
            bf.close();
        } catch (IOException e) {
        	throw new RuntimeException("BufferedWriter fatal error.");
		}
	}
	
	public String getGrammarConstants(String str){
		int index = grammarConstants.indexOf(str);
		if(index==-1)return "";
		return grammarConstants.substring(index, grammarConstants.length()).split("#")[1];
	}
	//End of Utilities
	
	//Animation Operations
	public void animate(){
		
		for(int i=0;i<scenes.size();i++){
			removeAll();
			String imgFile="";
			double duration=0;
			Color color = Color.BLACK; 
			Dimension from = null, to=null;
			String scene = scenes.get(i);
			String sceneDesc[] = scene.split(" ");
			int j=0;
			for(int k=0; k<sceneDesc.length; j++){
				String currStr = sceneDesc[k].toLowerCase();
				String graConst = getGrammarConstants(grammar.get(j));
				if(graConst.equals("")){
					if(grammar.get(j).equals("image"))imgFile=currStr;
					if(grammar.get(j).equals("color"))color=Color.getColor(currStr);
					k++;
				}else if(graConst.equals("for$2")){
					duration = Integer.parseInt(sceneDesc[k+1]);
					if(sceneDesc[k+2].toLowerCase().substring(0,6).equals("second"))duration*=1000;
					k+=3;
				}else if(graConst.equals("from$1")){
					from = locs.get(sceneDesc[k+1].toLowerCase());
					k+=2;
				}else if(graConst.equals("to$1")){
					to = locs.get(sceneDesc[k+1].toLowerCase());
					k+=2;
				}
			}
			GImage image = new GImage(imgFile+IMAGE_TYPE);
			image = recolorImage(image, color);
			add(image, from.getWidth(), from.getHeight());
			double dX = (to.getWidth()-from.getWidth())/((double)duration);
			double dY = (to.getHeight()-from.getHeight())/((double)duration);
			for(int t = 0; t<duration; t++){
				image.move(dX, dY);
				pause(1);
			}
		}
	}
	
	public GImage recolorImage(GImage img, Color c){
		return img;
	}
	
	public void clearScreen(){
		removeAll();
	}
	//End of Animation Operations
	
	public void quit(){
		exit();
	}
	
	public static String FILE_TYPE = ".txt";
	public static String IMAGE_TYPE = ".png";
	public static int ANIMATION_STEP = 10;
	
}

//TO-DO check create/open when a file is already open, may lead to instability.
//TO-DO ask to create file if none open.
//TO-DO move open file checks to a method in utilities. Maybe not, how to return if?
