/**
 * AmbientTalk/2 Project
 * (c) Programming Technology Lab, 2006 - 2007
 * Authors: Tom Van Cutsem & Stijn Mostinckx
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package demo.instantmessenger;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author jessie
 */
public class IMGUI extends Frame {
    private TextArea inTextArea_;
    private TextArea outTextArea_;
    private TextField inputArea_;
	private TextField receiver_;
    private Button sendMessageBtn_;
    private String lineSeparator_;
    private ATInstantMessenger atIm_;
    
    public interface ATInstantMessenger {
      // used by the GUI to notify the instant messenger that the user changed the username
      public void setUsername(String username) throws Exception;
      // used by the GUI to notify the instant messenger that a message should be sent
      public void talk(String to, String content) throws Exception;
    }

    public IMGUI(ATInstantMessenger atIm) {
        super("Instant Messenger");
        this.lineSeparator_ = "\n";
        this.setSize(250,350);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; 
        c.weightx = 1;
        c.gridwidth = 1;
        c.insets.left = 2;
        c.insets.right = 2;// = new Insets(0,2,0,2);
        c.insets.bottom = 2;
        this.setLayout(new GridBagLayout());
        
        Container topcontainer = new Container();
        c.ipady = 20;      //make this component tall
        c.ipadx = 30;      //make this component tall
        c.gridx = 0;
        c.gridy = 0;
        this.add(topcontainer,c);
        
        topcontainer.setLayout(new BorderLayout(2,2));
        inTextArea_ = new TextArea(2,20);
        topcontainer.add(inTextArea_, BorderLayout.CENTER);
		
        receiver_ = new TextField(16);
		topcontainer.add(receiver_, BorderLayout.NORTH);
		receiver_.setText("receiver");
        
/*      c.ipady = 20;      //make this component tall
        c.ipadx = 30;      //make this component tall
        c.gridx = 0;
        c.gridy = 0;
        topcontainer.add(inTextArea_, c, TextArea.SCROLLBARS_BOTH);*/

        atIm_ = atIm;
        
        sendMessageBtn_ = new Button("Send");
        c.ipady = 0;
        c.ipadx = 0;
        c.gridx = 0;
        c.gridy = 1;

        this.add(sendMessageBtn_,c);

        sendMessageBtn_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                IMGUI.this.sendButtonPressed();
            }           
        });
        
        Container bottomcontainer = new Container();

        c.ipady = 30;      //make this component tall
        c.ipadx = 30;      //make this component tall
        c.gridx = 0;
        c.gridy = 2;
        this.add(bottomcontainer,c);
        
        bottomcontainer.setLayout(new BorderLayout(2,2));
        outTextArea_ = new TextArea(7,20);
        bottomcontainer.add(outTextArea_);
        
        addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
              setVisible(false); 
              dispose(); 
              System.exit(0); 
            } 
        });
        
        initMenu();
        this.setVisible(true);
        askForUsername();
    }
    
    public void sendButtonPressed() {
        String toEval;
        if (inTextArea_.getSelectionStart() == inTextArea_.getSelectionEnd())
            toEval = inTextArea_.getText();
        else
            toEval = inTextArea_.getSelectedText();
		String receiver = receiver_.getText();
        IMGUI.this.talk(receiver, toEval);
    }           
    
    public void initMenu() {
        MenuBar mb = new MenuBar();
        Menu m = new Menu("Actions");
        mb.add(m);
        MenuItem mi = new MenuItem("Open & Send");
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                IMGUI.this.openFile();
                IMGUI.this.sendButtonPressed();
            }           
        });
        m.add(mi);      
        mi = new MenuItem("Open"); 
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
              IMGUI.this.openFile();
            }           
        });
        m.add(mi);
        mi = new MenuItem("Send"); 
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
              IMGUI.this.sendButtonPressed();
            }
        });
        m.add(mi);
        
        mi = new MenuItem("Quit"); 
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                IMGUI.this.setVisible(false);
                IMGUI.this.dispose();
                System.exit(0);
            }           
        });
        m.add(mi);

        this.setMenuBar(mb);
    }
    
    public void talk(String to, String text) {
        try {
          atIm_.talk(to, text);
        } catch (Exception e) {
          e.printStackTrace(System.out);
        }
    }
    
    public void openFile() {
        FileDialog dialog = new FileDialog(this, "Select file to open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setDirectory(System.getProperty("user.dir"));
        dialog.setVisible(true);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dialog.getDirectory() + dialog.getFile()));
            String s;
            StringBuffer buffer = new StringBuffer();
            while ((s = reader.readLine()) != null) {
                buffer.append(s).append(lineSeparator_);
            }
            reader.close();
            inTextArea_.setText(buffer.toString());
        } catch (IOException e) {
        }
    }
    
    
    /**
     * Called by the process to display a value to the screen.
     * @param output the string to be displayed.
     *
     * The screen scrolls down when excessive output is displayed.
     */
    public void display(String output) {
       int begapp = outTextArea_.getText().length();
       outTextArea_.append(output + "\n");
       if (this.isVisible()) {
         int endapp = outTextArea_.getText().length();
         outTextArea_.setCaretPosition(endapp);
         outTextArea_.setSelectionStart(begapp);
         outTextArea_.setSelectionEnd(endapp);
       }
      // this.repaint();
			this.pack();
    }
    
    /**
     * Ask the user for its user name upon startup.
     */
    private void askForUsername() {
        Button dbtn;
        final Dialog d = new Dialog(this, "What is your name?", false);
        d.setSize(150,150);
        d.add(new Label("What is your name?"), BorderLayout.NORTH); 
        inputArea_ = new TextField(16);
        d.add(inputArea_,BorderLayout.CENTER);
        d.add(dbtn = new Button("OK"), BorderLayout.SOUTH); 
        
        dbtn.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
              try {
            	String username = inputArea_.getText();
            	setTitle(getTitle() + " - " + username);
                atIm_.setUsername(username);
              } catch (Exception e2) {
                e2.printStackTrace(System.out);
              }
              d.dispose();
            }
        });
        d.setVisible(true);
    }
    
}
