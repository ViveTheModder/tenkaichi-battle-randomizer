package gui;
//Tenkaichi Randomizer v1.0, by ViveTheModder
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

public class MainApp 
{
	static final int WORLD_TOUR_NOON = 4, WORLD_TOUR_EVE = 27;
	//constants are in this order: number of characters, number of maps, number of songs, number of referees
	static final int[] CONST = {161, 35, 24, 7};
	static final int[] TIMER = {60, 90, 180, 240, -1}; //-1 represents Infinite Time (or No Timer)
	static final String CSV_PATH = "./csv/";
	static final String HTML = "<html><div style='text-align: center;'>";
	static final String WINDOW_TITLE = "Tenkaichi Randomizer";
	static final String[] COL_1P = {"Player 1 Team","Costume"};
	static final String[] COL_2P = {"Player 2 Team","Costume"};
	static final String[] COM_DIFF = {"Very Weak","Weak","Average","Strong","Very Strong"};
	static final File[] CSV_FILE = 
	{
		new File(CSV_PATH+"characters.csv"), new File(CSV_PATH+"maps.csv"), 
		new File(CSV_PATH+"bgm.csv"), new File(CSV_PATH+"referees.csv"), new File(CSV_PATH+"costumes.csv")
	};
	/* static variables, all of which I love dearly */
	static boolean[] selectedCharaIDs = new boolean[CONST[0]];
	static int matchCnt, teamSize1P, teamSize2P;
	static int[] restrictions = {255,255,255,255};
	static String[][] charaData1P = new String[5][5], charaData2P = new String[5][5];
	static String comDifficulty, duelTime;
	static String bgmName, mapName, refereeName;
	
	public static int getCostumeCount(int ID, File csv) throws FileNotFoundException
	{
		int costumeCnt=2, charaID, temp;
		Scanner sc = new Scanner(csv);
		sc.useDelimiter(",");
		while (sc.hasNext())
		{
			charaID = Integer.parseInt(sc.next());
			temp = Integer.parseInt(sc.nextLine().replace(",", ""));
			if (charaID == ID)
			{
				costumeCnt = temp; break;
			}
		}
		sc.close();
		return costumeCnt;
	}
	public static String getName(int num, File csv) throws FileNotFoundException
	{
		if (num == 255) return null;
		
		String input, name; int cnt=0;
		Scanner sc = new Scanner(csv);
		while (sc.hasNextLine())
		{
			input = sc.nextLine();
			String[] inputArray = input.split(",");
			if (cnt == num)
			{
				name = inputArray[1];
				//assign character restrictions, if any
				if (inputArray.length>2)
				{
					for (int i=2; i<(inputArray.length); i++)
						restrictions[i-2] = Integer.parseInt(inputArray[i]);
					for (int i=0; i<restrictions.length; i++)
					{
						if (restrictions[i]==255) continue;
						selectedCharaIDs[restrictions[i]] = true;
					}
				}
				sc.close(); return name;
			}
			cnt++;
		}
		sc.close(); //I hate myself for calling this method twice to prevent a "resource leak"
		return null;
	}
	public static String getMatch() throws FileNotFoundException
	{	
		matchCnt++;
		String output = "(Match "+matchCnt+")\n\n";
		int[] teamCharaIDs = new int[10];
		int[] teamCostumeIDs = new int[10];
		int currCharaID;
		
		//reset selected & restricted character IDs
		selectedCharaIDs = new boolean[CONST[0]];
		for (int j=0; j<4; j++) restrictions[j]=255;
		
		for (int i=0; i<10; i++)
		{
			if (i<teamSize1P || (i>=5 && i<teamSize2P+5))
			{
				currCharaID = (int) (Math.random()*CONST[0]);
				teamCharaIDs[i] = currCharaID;
				getName(teamCharaIDs[i],CSV_FILE[0]); //set character restrictions (part of getName method)
				//to prevent ID collisions, the ID has to deviate by a randomly generated number
				if (selectedCharaIDs[currCharaID] == true)
				{
                    int rng = (int) (Math.random()*CONST[0]);
                    while (rng<CONST[0]) //go through selected ID array & occupy 1st unselected character
                    {
                        if (selectedCharaIDs[rng]==false)
                        {
                            selectedCharaIDs[rng]=true; 
                            teamCharaIDs[i] = rng; break;
                        }
                        rng++;
                    }
				}
				else selectedCharaIDs[currCharaID] = true;
				teamCostumeIDs[i] = (int) (Math.random()*getCostumeCount(teamCharaIDs[i],CSV_FILE[4]));
				teamCostumeIDs[i]++;
			}
			else teamCharaIDs[i] = 255; //null value
		}

		output+="["+COL_1P[0]+"]\n";
		for (int i=0; i<10; i++)
		{
			if (i==5) output+="\n["+COL_2P[0]+"]\n";
			String name = getName(teamCharaIDs[i],CSV_FILE[0]);
			String costumeID = Integer.toString(teamCostumeIDs[i]);
			if (name == null) 
			{
				charaData1P[i%5][0]=""; charaData2P[i%5][0]="";
				charaData1P[i%5][1]=""; charaData2P[i%5][1]="";
				continue;
			}
			output+=name+", Costume "+teamCostumeIDs[i]+"\n";
			if (i<5) 
			{
				charaData1P[i%5][0]=name; charaData1P[i%5][1]=costumeID;
			}
			else
			{
				charaData2P[i%5][0]=name; charaData2P[i%5][1]=costumeID;
			}
		}
		
		duelTime = Integer.toString(TIMER[(int) (Math.random()*TIMER.length)])+" s";
		if (duelTime.equals("-1 s")) duelTime="Infinite";
		int mapID = (int) (Math.random()*CONST[1]);
		//World Tournament Stage cannot be selected in Team Battles, so map ID has to be deviated
		if ((mapID==WORLD_TOUR_NOON || mapID==WORLD_TOUR_EVE) && (teamSize1P>1 || teamSize2P>1))
			mapID+=(int) (Math.random()*5);

		int bgmID = (int) (Math.random()*CONST[2]);
		int refereeID = (int) (Math.random()*CONST[3]);
		comDifficulty = COM_DIFF[(int) (Math.random()*COM_DIFF.length)];
		mapName = getName(mapID,CSV_FILE[1]);
		bgmName = getName(bgmID,CSV_FILE[2]);
		refereeName = getName(refereeID,CSV_FILE[3]);
		
		output+="\n[Battle Settings]";
		output+="\nDuel Time: "+duelTime+"\nCOM Difficulty: "+comDifficulty+"\nReferee: "+refereeName;
		output+="\n\n[Map & BGM Select]\nMap: "+mapName+"\nBGM: "+bgmName;
		return output;
	}
	public static void setGUI()
	{
		ImageIcon icon = new ImageIcon("icon.png"); 
		JFrame frame = new JFrame(WINDOW_TITLE);
		JTable table1P = new JTable(charaData1P, COL_1P);
		JTable table2P = new JTable(charaData2P, COL_2P);
		//make tables read-only
		table1P.setDefaultEditor(Object.class, null);
		table2P.setDefaultEditor(Object.class, null); 
		//stylize tables (foreground = text)
		table1P.getTableHeader().setBackground(new Color(53,82,247));
		table1P.getTableHeader().setForeground(Color.WHITE);
	    table1P.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
	    table1P.setFont(new Font("Tahoma", Font.BOLD, 12));
		table2P.getTableHeader().setBackground(new Color(143,16,58));
		table2P.getTableHeader().setForeground(Color.WHITE);
	    table2P.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));
	    table2P.setFont(new Font("Tahoma", Font.BOLD, 12));
	    table1P.getColumn("Costume").setWidth(64);;

		JScrollPane pane1P = new JScrollPane(table1P);
		JScrollPane pane2P = new JScrollPane(table2P);
		JPanel panel = new JPanel();
		JPanel btnPanel = new JPanel();
		JButton button = new JButton("Generate");
		button.setFont(new Font("Tahoma",Font.BOLD,12));
		
		String labelTxt = HTML+"Duel Time: "+duelTime+"<br>COM Difficulty: "+comDifficulty+
		"<br>Referee: "+refereeName+"<br><br>Map: "+mapName+"<br>"+"BGM: "+bgmName;
		JLabel label = new JLabel(labelTxt);
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(label);
		btnPanel.add(button);
		
		button.addActionListener(new ActionListener()
		{
			@Override
	        public void actionPerformed(ActionEvent event)
			{
				try {System.out.println("\n"+getMatch());} 
				catch (FileNotFoundException ex) {ex.printStackTrace();}
				
				JTable newTable1P = new JTable(charaData1P, COL_1P);
				JTable newTable2P = new JTable(charaData2P, COL_2P);
				label.setText(HTML+"Duel Time: "+duelTime+"<br>COM Difficulty: "+comDifficulty+
				"<br>Referee: "+refereeName+"<br><br>Map: "+mapName+"<br>"+"BGM: "+bgmName);
				//make tables read-only
				newTable1P.setDefaultEditor(Object.class, null); 
				newTable2P.setDefaultEditor(Object.class, null);
				
				pane1P.add(newTable1P); pane2P.add(newTable2P);
				frame.add(pane1P, BorderLayout.WEST);
				frame.add(pane2P, BorderLayout.EAST);
				frame.add(btnPanel, BorderLayout.SOUTH);
	        }
	    });
		frame.add(pane1P, BorderLayout.WEST);
		frame.add(pane2P, BorderLayout.EAST);
		frame.add(panel, BorderLayout.CENTER);
		frame.add(btnPanel, BorderLayout.SOUTH);
		frame.setIconImage(icon.getImage());
		frame.setSize(1200, 256);
		frame.setLocationRelativeTo(null); //set location to center of the screen
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //successful termination
	}
	public static void main(String[] args) throws FileNotFoundException
	{
		if (args.length == 2) //check for command line arguments
		{	
			int arg1 = Integer.parseInt(args[0]);
			int arg2 = Integer.parseInt(args[1]);
			if (arg1>0 && arg2<=5 && arg2>0 && arg2<=5)
			{
				teamSize1P = arg1; teamSize2P = arg2;
				System.out.println(getMatch());
				System.exit(1); //alternate successful termination
			}
			else
			{
				System.out.println("Incorrect amount of players for at least one team.");
				System.exit(2); //termination due to params with incorrect range
			}
		}
		else if (args.length == 1)
		{
			System.out.println("Only one team size has been entered.");
			System.exit(3); //termination due to one params
		}
		else
		{
			String msg = HTML+"Please enter the number of teammates<br> for each team, separated by a space."
			+ "<br>Otherwise, each team will have 3 by default.";
			String userInput = JOptionPane.showInputDialog(null, msg, WINDOW_TITLE, JOptionPane.INFORMATION_MESSAGE);
			if (userInput == null) System.exit(4); //termination from cancel button
			String[] userInputArray = userInput.split(" ");
			if (userInputArray.length==2)
			{
				teamSize1P = Integer.parseInt(userInputArray[0]);
				teamSize2P = Integer.parseInt(userInputArray[1]);
			}
			else
			{
				teamSize1P = 3; teamSize2P = 3;
			}
			//if team size exceeds range 1-5, change it to min or max of that range
			if (teamSize1P>5) teamSize1P=5; if (teamSize2P>5) teamSize1P=5;
			if (teamSize1P<1) teamSize1P=1; if (teamSize2P<1) teamSize1P=1;
			System.out.println(getMatch());
			setGUI();
		}
	}
}