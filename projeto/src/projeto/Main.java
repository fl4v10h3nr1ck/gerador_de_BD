

package projeto;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Main  extends JDialog {


private static final long serialVersionUID = 1L;

	
	
private JTextField tf_url;
private JTextField tf_user;
private JTextField tf_bd;
private JPasswordField tf_pass;


private JTextArea resultado;






	public Main(){
	
	super();
		
	this.setTitle("Gerador de base da dados");
	this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	this.setSize(500 , 500);
	this.setLocationRelativeTo(null);
	this.setLayout(new GridBagLayout());
	this.setModal(true);
	this.getContentPane().setBackground(Color.WHITE);  
		
	adicionarComponentes();
	}
	
	
	
	
	
	public void adicionarComponentes(){

		
	GridBagConstraints cons = new GridBagConstraints();  
	cons.fill = GridBagConstraints.BOTH;
	cons.gridwidth = 1;
	cons.weightx = 1;
	cons.weighty =1;
	JPanel p1 = new JPanel();
	p1.setLayout(new GridBagLayout());
	add(p1, cons);	
	
	cons.fill = GridBagConstraints.HORIZONTAL;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	cons.weightx = 1;
	cons.weighty =0;
	cons.insets = new Insets(10, 2, 0, 2);
	p1.add(new JLabel("<html>URL:<font color=red>*</font></html>"), cons);	
	cons.insets = new Insets(5, 2, 2, 2);
	tf_url = new JTextField("localhost");
	cons.insets = new Insets(2, 2, 0, 2);
	p1.add(tf_url, cons);
	
	cons.insets = new Insets(2, 2, 0, 2);
	p1.add(new JLabel("<html>Usuário:<font color=red>*</font></html>"), cons);
	cons.insets = new Insets(5, 2, 2, 2);
	tf_user = new JTextField("root");
	p1.add(tf_user, cons);
	
	
	cons.insets = new Insets(2, 2, 0, 2);
	p1.add(new JLabel("<html>Banco de Dados:<font color=red>*</font></html>"), cons);
	cons.insets = new Insets(5, 2, 2, 2);
	tf_bd= new JTextField("");
	p1.add(tf_bd, cons);
	
	cons.insets = new Insets(2, 2, 0, 2);
	p1.add(new JLabel("<html>Senha:</html>"), cons);
	cons.insets = new Insets(5, 2, 2, 2);
	tf_pass = new JPasswordField();
	p1.add(tf_pass, cons);
	
	
	cons.fill = GridBagConstraints.NONE;
	cons.gridwidth = GridBagConstraints.REMAINDER;
	cons.anchor = GridBagConstraints.EAST;
	cons.weightx = 0;
	cons.weighty =0;
	cons.ipadx = 25;
	cons.insets = new Insets(20, 20, 20, 15);
	JButton bt_salvar = new JButton("Carregar Arquivo e Gerar");
	p1.add(bt_salvar, cons);

	
	cons.fill = GridBagConstraints.BOTH;
	cons.gridwidth = 1;
	cons.weightx = 1;
	cons.weighty =1;
	cons.insets = new Insets(2, 2, 2, 2);
	this.resultado = new JTextArea();
	p1.add(new JScrollPane(resultado), cons);
	
	
	
		bt_salvar.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent event ){
		   
			resultado.setForeground(Color.black);
			resultado.setText("Aguarde...");		
				
			gerar();
			}
	
		});
	this.getRootPane().setDefaultButton(bt_salvar);
	}
	
	
	
	
	
	
	private void gerar(){
	
	if(!valida())
	return;
	
	int linha = 0;
	String ult_instrucao = "";
	
	JFileChooser fc = new JFileChooser();
	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	
	fc.setFileFilter(new javax.swing.filechooser.FileFilter(){
	      public boolean accept(File f){
	    	 
	    	  if (f.isDirectory()) {return true;}	
	    	  
	      return f.getName().toLowerCase().endsWith(".txt") || f.getName().toLowerCase().endsWith(".sql");
	      }

	      public String getDescription() {
	      return "Arquivos Texto (.txt/.sql)";
	      }});
	
	int returnVal = fc.showOpenDialog(this);

    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	
    	File file = fc.getSelectedFile();		
    	Connection connector = null;
	
			try{	
			
			Class.forName("com.mysql.jdbc.Driver");		
			connector = DriverManager.getConnection("jdbc:mysql://"+this.tf_url.getText()+"/"+this.tf_bd.getText()+"?user="+this.tf_user.getText()+"&password="+String.valueOf(this.tf_pass.getPassword()));		
					
			}
			catch(ClassNotFoundException driveNaoEncontrado ){
				
			this.resultado.setForeground(Color.RED);
			this.resultado.setText("ERRO\n\n(ClassNotFoundException) conector não encontrado.");
			return;
			}
			catch(SQLException erroSQL){ 
					
			this.resultado.setForeground(Color.RED);
			this.resultado.setText("ERRO\n\n(SQLException) instrução SQL inválida na conexão.");	
			return;
			}

		PreparedStatement stmt = null;				
		
			try {
			
			Scanner scan = new Scanner(file,"UTF-8");  
			    while (scan.hasNextLine()) {  
			    	
			    ult_instrucao =  scan.nextLine();

				    if(ult_instrucao==null || ult_instrucao.length()==0){
				    
				    linha++;  
				    continue;
				    }
			    
				//System.out.println(ult_instrucao);
				stmt = connector.prepareStatement( ult_instrucao);
				stmt.executeUpdate();	
				linha++; 
			    }  
			scan.close();  	
			stmt.close();
			connector.close();
			} 
			catch (IOException e) {
			
			this.resultado.setForeground(Color.RED);
			this.resultado.setText("ERRO\n\n(IOException) Arquivo não pôde ser lido.");
			return;
			} catch (SQLException e) {
			
			this.resultado.setForeground(Color.RED);
			this.resultado.setText("ERRO\n\n(SQLException) instrução SQL inválida na gravação. Linha "+linha+"\nINSTRUCAO: "+ult_instrucao);	
			return;
			}
    	}
    	
    this.resultado.setForeground(new Color(43, 107, 36));
	this.resultado.setText("SUCESSO\n\nOperação realizada com sucesso. número de instruções processadas: "+linha);	
	}
	
	
	
	
	
	
	
	
	private boolean valida(){
		
		if(this.tf_url.getText().length() == 0){
		
		this.resultado.setForeground(Color.RED);
		this.resultado.setText("ERRO\n\nInforme a URL do banco de dados.");
		return false;	
		}
					
		if(this.tf_user.getText().length() == 0){
			
		this.resultado.setForeground(Color.RED);	
		this.resultado.setText("ERRO\n\nInforme o USUÁRIO do banco de dados.");
		return false;	
		}
			
		if(this.tf_url.getText().length() == 0){
			
		this.resultado.setForeground(Color.RED);
		this.resultado.setText("ERRO\n\nInforme o NOME do banco de dados.");
		return false;	
		}
		
		
	return true;
	}
	
	
	
	
	
	
	
	public static void main(String[] args){
		
	Main main = new Main();
	main.setVisible(true);		
	}
	
	
	
	
	
	
	
}

