package talkdraw;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.concurrent.Task;
import javafx.scene.layout.StackPane;
import talkdraw.componet.SpeechMessageFloatingPane;
import talkdraw.misc.ConsoleColors;

public class PythonSocketServer extends Task<Void>{
    private String Voice_Msg = "";
    /** 語音輸入的暫存 Buffer */
    private DataOutputStream out;
    /** 是否要開啟 [語音輸入] 功能 */
    private boolean voiceInput = true;
    /** 是否要開啟 [語音輸出] 功能*/
    private boolean voiceOutput = false;
    private Process process;
    private int recivedDelayCount = 0;
    private final int MAX_DELAY = 20;

    //-------------------------------------------------------------
    /** 垃圾訊息的前綴字元 */
    private final char SPAM_CHARATER = '#';
    /** 語音輸入訊息的前綴字元 */
    private final char INFO_CHARATER = '@';
    /** 語音輸入訊息視窗 */
    private SpeechMessageFloatingPane resultInfoPane;
    //-------------------------------------------------------------
    public PythonSocketServer(){
        reStartVoiceProgram();

        //初始化 Socket 訊息 Floating Pane
        resultInfoPane = new SpeechMessageFloatingPane();
        StackPane.setAlignment( resultInfoPane, null );
    }

    @Override
    protected Void call() throws Exception {
        //建立 ServerSocketChannel 物件，並設定埠號50007
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //設定是否要阻塞式 Socket
        ssc.configureBlocking(true);
        //設定 ServerSocket 的連接埠
        ssc.socket().bind( new InetSocketAddress(50007) );

        //顯示 初始化訊息在 cmd 上
        printInitialMsg();
        

        try {
            
            //Client提出請求，accept()會傳回一個Socket物件，並讓 socket 指向它
            Socket socket = ssc.socket().accept();  

            //設定 Socket 的輸出 Stream
            out = new DataOutputStream( socket.getOutputStream() );

            //當 Client 連進來來時顯示訊息在 cmd 上   
            printClientConnectMsg();

            //當Socket持續在連接時，就做下面的事
            while( socket.isConnected() ){         
                //讀取 Python 的輸入字串
                String msg = new BufferedReader( new InputStreamReader( socket.getInputStream(),"UTF-8") ).readLine();
                
                 //當有訊息時 將讀出得訊息給予至 Voice_Msg 變數
                if( msg != null ){
                    if( msg.charAt(0) == INFO_CHARATER ){
                        resultInfoPane.changeMessage( msg.substring( 1 ) );
                    }
                    else{
                        Voice_Msg = (msg.endsWith(" ")) ? msg.substring( 0 , msg.length() - 1 ) : msg;       
                    }
                }
                //當Client disconnect時，readline會傳回null
                else break;
            }

            System.out.println( ConsoleColors.ERROR + "Python Clinet did Not connected！");
        } 
        catch (Exception e) {
            System.out.println( ConsoleColors.SEPERA_LINE_ERROR );
            System.out.println( ConsoleColors.ERROR + "Ptython to Java Socket Occur Exception！");
            System.out.println( ConsoleColors.ERROR + "Maybe Python Cline is Disconnect" );
            e.printStackTrace();
        }
        finally{
            ssc.close();
        }
        return null;
    }
    private void reStartVoiceProgram(){
        if( process != null )process.destroy();
        try{
            //印出初始化訊息
            System.out.println( ConsoleColors.SEPERA_LINE );
            System.out.println( ConsoleColors.INFO + "Start to Initialize Speech Diver....");
            
            //設定檔案位置
           /* String exeFile = "speechdriver\\dist\\ASR2.exe";
            String dirFile = "speechdriver\\";
            process = Runtime.getRuntime().exec( exeFile, null, new File( dirFile ));
            System.out.println( ConsoleColors.SUCCESS + "Initializing Speech Diver Successfully" );*/
        }
        catch( Exception e ){
            System.out.println( ConsoleColors.SEPERA_LINE_ERROR );
            System.out.println( ConsoleColors.ERROR + "Initialize Speech Diver Occur Error！");
            e.printStackTrace();
        }
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      功能區(Function Area)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定是否要監聽語音
    * @param value {@code true = 開啟} | {@code false = 關閉}*/
    public void setinputOpen(boolean value){
        voiceInput = value;
        resultInfoPane.setVisible( value );
    }
    /** 設定是否要語音輸出
    * @param value  {@code true = 開啟} | {@code false = 關閉}*/
    public void setoutputOpen(boolean value){
        voiceOutput = value;
    }
    /** 判斷是否有接收到 Voice Msg 
     *  @return {@code true = 有接收到} | {@code false = 無接收到} {@code [Boolean]}*/
    public boolean isReceived(){ 
        recivedDelayCount += 1;
        if( !Voice_Msg.isBlank() )recivedDelayCount = 0;
        if( recivedDelayCount > MAX_DELAY ){
            //reStartVoiceProgram();
        }

        return voiceInput ? !( Voice_Msg.isBlank() ) : false; 
    }
    /** <p>取得從 Python 傳出的字串</p> 
     *  <p>也就是取得使用者的 {@code 語音輸入}</p>
     *  @return 為分割的語音字串 {@code [List<String>]}*/
    public List<String> getVoiceMsg(){
        String temp[] = new String[ Voice_Msg.split(" ").length ];
        if( temp.length > 1 ){
            temp = Voice_Msg.split(" ");
        }
        else temp[0] = Voice_Msg;
        Voice_Msg = "";
        return Arrays.asList( temp );
    }

    /** 將要輸出至 Python 的文字給傳送過去 
     *  @param msg 欲送過去的字串*/
    public void sendToClient(String msg){
        try{
            if( voiceOutput && out != null ) out.writeUTF( msg );
        }
        catch( IOException e ){
            System.out.println( ConsoleColors.SEPERA_LINE_ERROR );
            System.out.println( ConsoleColors.ERROR + "Java To Python Socket Occur Exception！");
            e.printStackTrace();
        }
    }
    /** 傳出結束執行給 Python Socket*/
    public void sendEnd(){
        try{
            if( out != null ) out.writeUTF( "結束執行" );
        }
        catch( IOException e ){
            System.out.println( ConsoleColors.SEPERA_LINE_ERROR );
            System.out.println( ConsoleColors.ERROR + "Java To Python Socket Occur Exception！");
            e.printStackTrace();
        }
    }

    /** <p>判斷從 {@code SocketClinet} 接收的是否是垃圾訊息</p>
     *  <p>假如第一個字元是 {@code @} 就代表他是垃圾訊息</p>
     *  @return {@code true = 是垃圾訊息} | {@code false = 不是垃圾訊息}*/
    public boolean isSpamMsg(){
        return ( Voice_Msg.charAt(0) == SPAM_CHARATER ) ? true : false;
    }


    /** 取得經處理過的整個 {@code 格式化} 字串 
     *  @param 語音字串 {@code [String]}*/
    public String getFormatWholeMsg(){
        String temp = isSpamMsg() ? Voice_Msg.substring(1) : Voice_Msg;
        Voice_Msg = "";
        return String.join(" ", "語音輸入： ",
                                temp        ,
                                "          ",
                                new SimpleDateFormat("HH:mm:ss").format( new Date() ) );
    }

    public void sendStatus( String cmd ){
        try{
            if( out != null )
                out.writeUTF( "#" + cmd );
        }
        catch( IOException e ){
            System.out.println( ConsoleColors.SEPERA_LINE_ERROR );
            System.out.println( ConsoleColors.ERROR + "Java To Python Socket Occur Exception！");
            e.printStackTrace();
        }
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得 {@Socket Result} 的資訊欄 
     *  @return 資訊回饋欄 {@code [SpeechMessageFloatingPane]}*/
    public SpeechMessageFloatingPane getFloatingPane(){
        return resultInfoPane;
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      顯示 Debug Msg       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 顯示 初始化訊息在 cmd 上 */
    private void printInitialMsg(){
        System.out.println( ConsoleColors.SEPERA_LINE );
        System.out.println( ConsoleColors.INFO + "Initializing Socket Server......");   
        System.out.println( ConsoleColors.INFO + "Initializing Socket Port At 50007");   
        System.out.println( ConsoleColors.INFO + "Waiting For The Python Client......");   
    }
    /** 顯示 初始化訊息在 cmd 上 */
    private void printClientConnectMsg(){
        System.out.println( ConsoleColors.SEPERA_LINE );
        System.out.println( ConsoleColors.SUCCESS + "Python Clinet connected！");
        System.out.println( ConsoleColors.INFO + "JavaFX is Ready to Recived the Voice Data"); 
    }
}

